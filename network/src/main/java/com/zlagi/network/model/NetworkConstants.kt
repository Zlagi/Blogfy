package com.zlagi.network.model

object NetworkConstants {
    const val BASE_ENDPOINT = "https://blogfy-server.herokuapp.com/"
    const val SIGNIN_ENDPOINT = "auth/signin"
    const val SIGNUP_ENDPOINT = "auth/signup"
    const val GOOGLE_AUTHENTICATION_ENDPOINT = "auth/idp/google"
    const val REFRESH_TOKEN_ENDPOINT = "auth/token/refresh"
    const val REVOKE_TOKEN_ENDPOINT = "auth/token/revoke"
    const val BLOGS_ENDPOINT ="blog/list"
    const val NOTIFICATION_ENDPOINT ="blog/notification"
    const val BLOG_ENDPOINT ="blog"
    const val CHECK_AUTHOR_ENDPOINT = "blog/{blogId}/is_author"
    const val UPDATE_ENDPOINT = "blog/{blogId}"
    const val DELETE_ENDPOINT = "blog/{blogId}"
    const val ACCOUNT_ENDPOINT = "auth/account"
    const val PASSWORD_ENDPOINT = "auth/account/password"
}

object NetworkParameters {
    const val TOKEN_TYPE = "Bearer "
    const val AUTH_HEADER = "Authorization"
    const val CUSTOM_HEADER = "@"
    const val NO_AUTH = "NoAuth"
    const val SEARCH_QUERY = "search_query"
    const val BLOG_PK = "blogId"
    const val PAGE = "page"
    const val LIMIT = "limit"
}