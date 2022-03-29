package com.zlagi.data.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.TokensDataModel
import com.zlagi.domain.model.TokensDomainModel
import javax.inject.Inject

/**
 * Mapper class for convert [TokensDataModel] to [TokensDomainModel] and vice versa
 */
class TokensDataDomainMapper @Inject constructor() : Mapper<TokensDataModel, TokensDomainModel> {

    override fun from(i: TokensDataModel): TokensDomainModel {
        return TokensDomainModel(
            accessToken = i.accessToken,
            refreshToken = i.refreshToken
        )
    }

    override fun to(o: TokensDomainModel): TokensDataModel {
        return TokensDataModel(
            accessToken = o.accessToken,
            refreshToken = o.refreshToken
        )
    }
}
