package com.zlagi.network.model

const val SIGN_IN_SUCCESS_RESPONSE_JSON = """
    {
        "status": "SUCCESS",
        "message": "Sign in successfully",
        "access_token": "a4U8_gH4HhFghqhuq7'4HgjhHhqFfhgjqhg",
        "refresh_token": "faaf33HHf3-6jJJfhFiK4j__FfHFHhf'4HgjhHhqFfhgjqhg"
    }
"""

const val SIGN_IN_FAIL_RESPONSE_JSON = """
    {
        "status": "UNAUTHORIZED",
        "message": "Authentication failed: Invalid credentials"
    }
"""