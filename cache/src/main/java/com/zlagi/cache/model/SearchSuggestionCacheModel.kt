package com.zlagi.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_suggestion")
data class SearchSuggestionCacheModel(
    @PrimaryKey(autoGenerate = true)
    val pk: Int,
    val query: String
)
