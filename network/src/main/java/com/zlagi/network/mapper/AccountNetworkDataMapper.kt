package com.zlagi.network.mapper

import com.zlagi.common.exception.MappingException
import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.AccountDataModel
import com.zlagi.network.model.response.AccountNetworkModel
import javax.inject.Inject

/**
 * Mapper class for convert [AccountNetworkModel] to [AccountDataModel] and vice versa
 */
class AccountNetworkDataMapper @Inject constructor() : Mapper<AccountNetworkModel, AccountDataModel> {

    override fun from(i: AccountNetworkModel): AccountDataModel {
        return AccountDataModel(
            pk = i.id ?: throw MappingException("Account pk cannot be null"),
            email = i.email.orEmpty(),
            username = i.username.orEmpty()
        )
    }

    override fun to(o: AccountDataModel): AccountNetworkModel {
        return AccountNetworkModel(
            id = o.pk,
            email = o.email,
            username = o.username
        )
    }
}