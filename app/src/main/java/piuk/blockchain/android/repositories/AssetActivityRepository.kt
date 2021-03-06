package piuk.blockchain.android.repositories

import com.blockchain.swap.nabu.datamanagers.repositories.ExpiringRepository
import info.blockchain.balance.CryptoCurrency
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import piuk.blockchain.android.coincore.AccountGroup
import piuk.blockchain.android.coincore.ActivitySummaryItem
import piuk.blockchain.android.coincore.ActivitySummaryList
import piuk.blockchain.android.coincore.BlockchainAccount
import piuk.blockchain.android.coincore.Coincore
import piuk.blockchain.androidcore.data.access.AuthEvent
import piuk.blockchain.androidcore.data.rxjava.RxBus

class AssetActivityRepository(
    private val coincore: Coincore,
    private val rxBus: RxBus
) : ExpiringRepository<ActivitySummaryList>() {
    private val event = rxBus.register(AuthEvent.LOGOUT::class.java)

    init {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable += event
            .subscribe {
                doOnLogout()
            }
    }

    private val transactionCache = mutableListOf<ActivitySummaryItem>()

    fun fetch(
        account: BlockchainAccount,
        isRefreshRequested: Boolean
    ): Observable<ActivitySummaryList> {
        return Maybe.concat(
            getFromCache(),
            requestNetwork(isRefreshRequested)
        )
            .toObservable()
            .map { list ->
                list.filter { item ->
                    if (account is AccountGroup) {
                        account.includes(item.account)
                    } else {
                        account == item.account
                    }
                }.sorted()
            }
    }

    fun findCachedItem(cryptoCurrency: CryptoCurrency, txHash: String): ActivitySummaryItem? =
        transactionCache.find {
            it.cryptoCurrency == cryptoCurrency && it.txId == txHash
        }

    fun findCachedItemById(txHash: String): ActivitySummaryItem? =
        transactionCache.find {
            it.txId == txHash
        }

    private fun requestNetwork(refreshRequested: Boolean): Maybe<ActivitySummaryList> {
        return if (refreshRequested || isCacheExpired()) {
            getFromNetwork()
        } else {
            Maybe.empty()
        }
    }

    override fun getFromNetwork(): Maybe<ActivitySummaryList> =
        coincore.allWallets.activity.toMaybe().doOnSuccess { activityList ->
            // on error of activity returns onSuccess with empty list
            if (activityList.isNotEmpty()) {
                transactionCache.clear()
                transactionCache.addAll(activityList)
            }
            lastUpdatedTimestamp = System.currentTimeMillis()
        }.map { list ->
            // if network comes empty, but we have cache, return cache instead
            if (list.isEmpty() && transactionCache.isNotEmpty()) {
                transactionCache
            } else {
                list
            }
        }

    override fun getFromCache(): Maybe<ActivitySummaryList> {
        return if (transactionCache.isNotEmpty()) {
            Maybe.just(transactionCache)
        } else {
            Maybe.empty()
        }
    }

    private fun doOnLogout() {
        transactionCache.clear()
        rxBus.unregister(AuthEvent::class.java, event)
    }
}
