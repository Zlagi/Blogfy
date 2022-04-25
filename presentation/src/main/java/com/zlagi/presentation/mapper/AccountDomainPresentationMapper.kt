package com.zlagi.presentation.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.domain.model.AccountDomainModel
import com.zlagi.presentation.model.AccountPresentationModel
import javax.inject.Inject

/**
 * Mapper class for convert [AccountDomainModel] to [AccountPresentationModel] and vice versa
 */
class AccountDomainPresentationMapper @Inject constructor() :
    Mapper<AccountDomainModel, AccountPresentationModel> {

    override fun from(i: AccountDomainModel): AccountPresentationModel {
        return AccountPresentationModel(
            pk = i.pk,
            email = i.email,
            username = i.username
        )
    }

    override fun to(o: AccountPresentationModel): AccountDomainModel {
        return AccountDomainModel(
            pk = o.pk,
            email = o.email,
            username = o.username
        )
    }
}
