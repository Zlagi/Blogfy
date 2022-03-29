package com.zlagi.data.fakes.source.preferences

import com.zlagi.common.utils.PreferencesConstants.KEY_ACCESS_TOKEN
import com.zlagi.common.utils.PreferencesConstants.KEY_REFRESH_TOKEN
import com.zlagi.data.model.TokensDataModel
import com.zlagi.data.source.preferences.PreferencesDataSource
import javax.inject.Inject

class FakePreferences @Inject constructor() : PreferencesDataSource {
    private val preferences = mutableMapOf<String, Any>()

    override fun storeTokens(tokens: TokensDataModel) {
        preferences[KEY_ACCESS_TOKEN] = tokens.accessToken
        preferences[KEY_REFRESH_TOKEN] = tokens.refreshToken
    }

    override fun getAccessToken(): String {
        return ""
    }

    override fun getRefreshToken(): String {
        return preferences[KEY_REFRESH_TOKEN] as String
    }

    override fun deleteTokens() {
        //
    }
}
