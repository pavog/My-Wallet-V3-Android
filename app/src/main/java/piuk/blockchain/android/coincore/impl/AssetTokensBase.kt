package piuk.blockchain.android.coincore.impl

import com.blockchain.logging.CrashLogger

import com.blockchain.preferences.CurrencyPrefs
import com.blockchain.swap.nabu.datamanagers.CustodialWalletManager
import com.blockchain.wallet.DefaultLabels
import info.blockchain.balance.CryptoCurrency
import info.blockchain.balance.ExchangeRate
import info.blockchain.wallet.prices.TimeInterval
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import piuk.blockchain.android.coincore.AccountGroup
import piuk.blockchain.android.coincore.AssetFilter
import piuk.blockchain.android.coincore.BlockchainAccount
import piuk.blockchain.android.coincore.CryptoAsset
import piuk.blockchain.android.coincore.SingleAccount
import piuk.blockchain.android.coincore.SingleAccountList
import piuk.blockchain.android.thepit.PitLinking
import piuk.blockchain.androidcore.data.charts.ChartsDataManager
import piuk.blockchain.androidcore.data.charts.PriceSeries
import piuk.blockchain.androidcore.data.charts.TimeSpan
import piuk.blockchain.androidcore.data.exchangerate.ExchangeRateDataManager
import piuk.blockchain.androidcore.utils.extensions.then
import timber.log.Timber
import java.math.BigDecimal

internal abstract class CryptoAssetBase(
    protected val exchangeRates: ExchangeRateDataManager,
    private val historicRates: ChartsDataManager,
    protected val currencyPrefs: CurrencyPrefs,
    protected val labels: DefaultLabels,
    protected val custodialManager: CustodialWalletManager,
    private val pitLinking: PitLinking,
    protected val crashLogger: CrashLogger
) : CryptoAsset {

    private val accounts = mutableListOf<SingleAccount>()
    override val isEnabled: Boolean
        get() = !asset.hasFeature(CryptoCurrency.STUB_ASSET)

    // Init token, set up accounts and fetch a few activities
    override fun init(): Completable =
        initToken()
            .doOnError { throwable ->
                crashLogger.logException(throwable, "Coincore: Failed to load $asset wallet")
            }
            .then { loadAccounts() }
            .doOnComplete { Timber.d("Coincore: Init $asset Complete") }
            .doOnError { Timber.d("Coincore: Init $asset Failed") }

    private fun loadAccounts(): Completable =
        Completable.fromCallable { accounts.clear() }
            .then {
                loadNonCustodialAccounts(labels)
                    .doOnSuccess { accounts.addAll(it) }
                    .ignoreElement()
            }
            .then {
                loadCustodialAccount()
                    .doOnSuccess { accounts.addAll(it) }
                    .ignoreElement()
            }
            .then {
                loadInterestAccounts(labels)
                    .doOnSuccess { accounts.addAll(it) }
                    .ignoreElement()
            }
            .doOnError { Timber.e("Error loading accounts for ${asset.networkTicker}: $it") }

    abstract fun initToken(): Completable

    abstract fun loadNonCustodialAccounts(labels: DefaultLabels): Single<SingleAccountList>

    private fun loadInterestAccounts(labels: DefaultLabels): Single<SingleAccountList> =
        Single.fromCallable {
            listOf(
                CryptoInterestAccount(
                    asset,
                    labels.getDefaultInterestWalletLabel(asset),
                    custodialManager,
                    exchangeRates
                )
            )
        }

    override fun interestRate(): Single<Double> = custodialManager.getInterestAccountRates(asset)

    open fun loadCustodialAccount(): Single<SingleAccountList> =
        Single.just(
            listOf(CustodialTradingAccount(
                asset,
                labels.getDefaultCustodialWalletLabel(asset),
                exchangeRates,
                custodialManager
            ))
        )

    final override fun accountGroup(filter: AssetFilter): Single<AccountGroup> =
        Single.fromCallable {
            filterTokenAccounts(asset, labels, accounts, filter)
        }

    final override fun defaultAccount(): Single<SingleAccount> =
        Single.fromCallable {
            accounts.first { it.isDefault }
        }

    final override fun accounts(): List<SingleAccount> =
        accounts

    private fun getNonCustodialAccountList(): Single<SingleAccountList> =
        accountGroup(filter = AssetFilter.NonCustodial)
            .doOnSuccess { Timber.d("@@@@ got unfiltered list: $it") }
            .map { group -> group.accounts.mapNotNull { it as? SingleAccount } }
            .doOnSuccess { Timber.d("@@@@ got list: $it") }

    final override fun exchangeRate(): Single<ExchangeRate> =
        exchangeRates.fetchExchangeRate(asset, currencyPrefs.selectedFiatCurrency)
            .map {
                ExchangeRate.CryptoToFiat(
                    asset,
                    currencyPrefs.selectedFiatCurrency,
                    it
                )
            }

    final override fun historicRate(epochWhen: Long): Single<ExchangeRate> =
        exchangeRates.getHistoricPrice(asset, currencyPrefs.selectedFiatCurrency, epochWhen)
            .map {
                ExchangeRate.CryptoToFiat(
                    asset,
                    currencyPrefs.selectedFiatCurrency,
                    it.toBigDecimal()
                )
            }

    override fun historicRateSeries(period: TimeSpan, interval: TimeInterval): Single<PriceSeries> =
        historicRates.getHistoricPriceSeries(asset, currencyPrefs.selectedFiatCurrency, period)

    protected fun getPitLinkingAccount(): Maybe<SingleAccount> =
        pitLinking.isPitLinked().filter { it }
            .flatMap { custodialManager.getExchangeSendAddressFor(asset) }
            .map { address ->
                CryptoExchangeAccount(
                    cryptoCurrency = asset,
                    label = labels.getDefaultExchangeWalletLabel(asset),
                    address = address,
                    exchangeRates = exchangeRates
                )
            }

    final override fun canTransferTo(account: BlockchainAccount): Single<SingleAccountList> =
        when (account) {
            is CustodialTradingAccount -> getNonCustodialAccountList()
            is CryptoInterestAccount -> Single.just(emptyList())
            is CryptoExchangeAccount -> Single.just(emptyList())
            is CryptoNonCustodialAccount -> getPitLinkingAccount()
                    .map { listOf(it) }
                    .toSingle(emptyList())
            else -> Single.just(emptyList())
        }
}

fun ExchangeRateDataManager.fetchExchangeRate(
    cryptoCurrency: CryptoCurrency,
    currencyName: String
): Single<BigDecimal> =
    updateTickers()
        .andThen(Single.defer { Single.just(getLastPrice(cryptoCurrency, currencyName)) })
        .map { it.toBigDecimal() }
