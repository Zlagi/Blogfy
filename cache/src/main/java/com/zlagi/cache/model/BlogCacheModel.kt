package com.zlagi.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blog")
data class BlogCacheModel(
    @PrimaryKey(autoGenerate = false)
    val pk: Int,
    val title: String,
    val description: String,
    val created: String,
    val updated: String,
    val username: String
)
