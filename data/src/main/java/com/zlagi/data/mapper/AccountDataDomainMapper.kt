package com.zlagi.data.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.AccountDataModel
import com.zlagi.domain.model.AccountDomainModel
import javax.inject.Inject

/**
 * Mapper class for convert [AccountDataModel] to [AccountDomainModel] and vice versa
 */
class AccountDataDomainMapper @Inject constructor() :
    Mapper<AccountDataModel, AccountDomainModel> {

    override fun from(i: AccountDataModel): AccountDomainModel {
        return AccountDomainModel(
            pk = i.pk,
            email = i.email,
            username = i.username
        )
    }

    override fun to(o: AccountDomainModel): AccountDataModel {
        return AccountDataModel(
            pk = o.pk,
            email = o.email,
            username = o.username
        )
    }
}
