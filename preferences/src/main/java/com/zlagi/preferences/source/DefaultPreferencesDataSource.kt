package com.zlagi.preferences.source

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.zlagi.common.utils.PreferencesConstants.KEY_ACCESS_TOKEN
import com.zlagi.common.utils.PreferencesConstants.KEY_REFRESH_TOKEN
import com.zlagi.data.model.TokensDataModel
import com.zlagi.data.source.preferences.PreferencesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultPreferencesDataSource @Inject constructor(
    @ApplicationContext context: Context,
) : PreferencesDataSource {

    companion object {
        const val PREFERENCES_NAME = "BLOGFY_PREFERENCES"
    }

    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun storeTokens(tokens: TokensDataModel) {
        edit {
            putString(KEY_ACCESS_TOKEN, tokens.accessToken)
            putString(KEY_REFRESH_TOKEN, tokens.refreshToken)
        }
    }

    private inline fun edit(block: SharedPreferences.Editor.() -> Unit) {
        with(preferences.edit()) {
            block()
            commit()
        }
    }

    override fun getAccessToken(): String {
        return preferences.getString(KEY_ACCESS_TOKEN, "").orEmpty()
    }

    override fun getRefreshToken(): String {
        return preferences.getString(KEY_REFRESH_TOKEN, "").orEmpty()
    }

    override fun deleteTokens() {
        edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
        }
    }
}