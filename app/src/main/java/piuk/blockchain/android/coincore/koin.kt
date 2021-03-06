package piuk.blockchain.android.coincore

import com.blockchain.koin.paxAccount
import com.blockchain.koin.payloadScopeQualifier
import com.blockchain.koin.usdtAccount
import info.blockchain.balance.CryptoCurrency
import org.koin.dsl.bind
import org.koin.dsl.module
import piuk.blockchain.android.coincore.alg.AlgoAsset
import piuk.blockchain.android.coincore.bch.BchAsset
import piuk.blockchain.android.coincore.btc.BtcAsset
import piuk.blockchain.android.coincore.erc20.pax.PaxAsset
import piuk.blockchain.android.coincore.erc20.usdt.UsdtAsset
import piuk.blockchain.android.coincore.eth.EthAsset
import piuk.blockchain.android.coincore.fiat.FiatAsset
import piuk.blockchain.android.coincore.stx.StxAsset
import piuk.blockchain.android.coincore.xlm.XlmAsset
import piuk.blockchain.android.repositories.AssetActivityRepository

val coincoreModule = module {

    scope(payloadScopeQualifier) {

        scoped {
            StxAsset(
                payloadManager = get(),
                exchangeRates = get(),
                historicRates = get(),
                currencyPrefs = get(),
                custodialManager = get(),
                crashLogger = get(),
                pitLinking = get(),
                labels = get()
            )
        }

        scoped {
            BtcAsset(
                exchangeRates = get(),
                environmentSettings = get(),
                historicRates = get(),
                currencyPrefs = get(),
                payloadDataManager = get(),
                custodialManager = get(),
                pitLinking = get(),
                crashLogger = get(),
                labels = get()
            )
        }

        scoped {
            BchAsset(
                bchDataManager = get(),
                exchangeRates = get(),
                historicRates = get(),
                currencyPrefs = get(),
                crashLogger = get(),
                stringUtils = get(),
                custodialManager = get(),
                environmentSettings = get(),
                pitLinking = get(),
                labels = get()
            )
        }

        scoped {
            XlmAsset(
                xlmDataManager = get(),
                exchangeRates = get(),
                historicRates = get(),
                currencyPrefs = get(),
                custodialManager = get(),
                pitLinking = get(),
                crashLogger = get(),
                labels = get()
            )
        }

        scoped {
            EthAsset(
                ethDataManager = get(),
                feeDataManager = get(),
                exchangeRates = get(),
                historicRates = get(),
                currencyPrefs = get(),
                crashLogger = get(),
                custodialManager = get(),
                pitLinking = get(),
                labels = get()
            )
        }

        scoped {
            PaxAsset(
                paxAccount = get(paxAccount),
                exchangeRates = get(),
                historicRates = get(),
                currencyPrefs = get(),
                custodialManager = get(),
                pitLinking = get(),
                crashLogger = get(),
                labels = get()
            )
        }

        scoped {
            AlgoAsset(
                exchangeRates = get(),
                historicRates = get(),
                currencyPrefs = get(),
                custodialManager = get(),
                pitLinking = get(),
                crashLogger = get(),
                labels = get()
            )
        }

        scoped {
            FiatAsset()
        }

        scoped {
            UsdtAsset(
                usdtAccount = get(usdtAccount),
                exchangeRates = get(),
                historicRates = get(),
                currencyPrefs = get(),
                custodialManager = get(),
                crashLogger = get(),
                labels = get(),
                pitLinking = get()
            )
        }

        scoped {
            Coincore(
                payloadManager = get(),
                fiatAsset = get<FiatAsset>(),
                assetMap = mapOf(
                    CryptoCurrency.BTC to get<BtcAsset>(),
                    CryptoCurrency.BCH to get<BchAsset>(),
                    CryptoCurrency.ETHER to get<EthAsset>(),
                    CryptoCurrency.XLM to get<XlmAsset>(),
                    CryptoCurrency.PAX to get<PaxAsset>(),
                    CryptoCurrency.STX to get<StxAsset>(),
                    CryptoCurrency.ALGO to get<AlgoAsset>(),
                    CryptoCurrency.USDT to get<UsdtAsset>()
                ),
                defaultLabels = get()
            )
        }

        scoped {
            AssetActivityRepository(
                coincore = get(),
                rxBus = get()
            )
        }

        scoped {
            AddressFactoryImpl(
                coincore = get()
            )
        }.bind(AddressFactory::class)
    }
}
