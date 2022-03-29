package com.zlagi.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountCacheModel(
    @PrimaryKey(autoGenerate = false)
    val pk: Int,
    val email: String,
    val username: String
)
