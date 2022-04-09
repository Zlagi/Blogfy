package com.zlagi.domain.usecase.blog.dateformat

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DateFormatUseCase @Inject constructor() {
    operator fun invoke(): String {
        @SuppressLint("SimpleDateFormat")
        val formatter = SimpleDateFormat("'Date: 'yyyy-MM-dd' Time: 'HH:mm:ss")
        val now = Date()
        return formatter.format(now)
    }
}