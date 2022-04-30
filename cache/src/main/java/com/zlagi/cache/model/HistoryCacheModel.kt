package com.zlagi.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryCacheModel(
    @PrimaryKey(autoGenerate = false)
    val query: String
)
