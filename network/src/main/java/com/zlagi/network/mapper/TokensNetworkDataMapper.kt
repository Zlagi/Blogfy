package com.zlagi.network.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.TokensDataModel
import com.zlagi.network.model.response.TokensNetworkModel
import javax.inject.Inject

/**
 * Mapper class for convert [TokensNetworkModel] to [TokensDataModel] and vice versa
 */
class TokensNetworkDataMapper @Inject constructor() : Mapper<TokensNetworkModel, TokensDataModel> {

    override fun from(i: TokensNetworkModel): TokensDataModel {
        return TokensDataModel(
            accessToken = i.access_token.orEmpty(),
            refreshToken = i.refresh_token.orEmpty()
        )
    }

    override fun to(o: TokensDataModel): TokensNetworkModel {
        return TokensNetworkModel(
            access_token = o.accessToken,
            refresh_token = o.refreshToken
        )
    }
}