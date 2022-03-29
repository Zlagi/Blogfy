package com.zlagi.cache.mapper

import com.zlagi.cache.model.AccountCacheModel
import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.AccountDataModel
import javax.inject.Inject

/**
 * Mapper class for convert [AccountCacheModel] to [AccountDataModel] and vice versa
 */
class AccountCacheDataMapper @Inject constructor() :
    Mapper<AccountCacheModel, AccountDataModel> {

    override fun from(i: AccountCacheModel): AccountDataModel {
        return AccountDataModel(
            pk = i.pk,
            email = i.email,
            username = i.username
        )
    }

    override fun to(o: AccountDataModel): AccountCacheModel {
        return AccountCacheModel(
            pk = o.pk,
            email = o.email,
            username = o.username
        )
    }
}
