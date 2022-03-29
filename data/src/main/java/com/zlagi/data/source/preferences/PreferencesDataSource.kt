package com.zlagi.data.source.preferences

import com.zlagi.data.model.TokensDataModel

interface PreferencesDataSource {
    fun storeTokens(tokens: TokensDataModel)
    fun getAccessToken(): String
    fun getRefreshToken(): String
    fun deleteTokens()
}
