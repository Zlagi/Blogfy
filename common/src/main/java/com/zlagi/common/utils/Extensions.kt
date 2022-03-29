package com.zlagi.common.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object Extensions {
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}
