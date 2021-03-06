package com.blockchain.koin

import com.blockchain.account.DefaultAccountDataManager
import com.blockchain.accounts.AccountList
import com.blockchain.accounts.XlmAsyncAccountListAdapter
import com.blockchain.sunriver.HorizonProxy
import com.blockchain.sunriver.MemoMapper
import com.blockchain.sunriver.XlmDataManager
import com.blockchain.sunriver.XlmSecretAccess
import com.blockchain.sunriver.datamanager.XlmMetaDataInitializer
import com.blockchain.transactions.logMemoType
import com.blockchain.transactions.updateLastTxOnSend
import org.koin.dsl.bind
import org.koin.dsl.module

val sunriverModule = module {

    scope(payloadScopeQualifier) {

        factory { XlmSecretAccess(get()) }

        factory { XlmDataManager(get(), get(), get(), get(), get(), get(), get(), getProperty("HorizonURL")) }
            .bind(DefaultAccountDataManager::class)

        factory { get<XlmDataManager>().updateLastTxOnSend(get()).logMemoType(get()) }

        factory { HorizonProxy() }

        scoped { XlmMetaDataInitializer(get(), get(), get(), get()) }

        factory(xlm) {
            XlmAsyncAccountListAdapter(xlmDataManager = get())
        }.bind(AccountList::class)
    }

    factory { MemoMapper() }
}
