package com.zlagi.common.utils.validator

object AuthValidator {

    fun isValidEmail(email: String): Boolean =
        Regex(MAIL_REGEX).matches(email)

    fun String.isAlphaNumeric() = matches("[a-zA-Z0-9]+".toRegex())

    fun isValidPassword(password: String): Boolean =
        password.count() > 7

    fun passwordMatches(newPassword: String, confirmNewPassword: String): Boolean =
        newPassword == confirmNewPassword

    const val MAIL_REGEX = (
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
            "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
            "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
            "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|" +
            "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        )
}
