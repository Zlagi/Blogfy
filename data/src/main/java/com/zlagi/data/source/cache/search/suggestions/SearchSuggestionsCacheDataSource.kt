package com.zlagi.data.source.cache.search.suggestions

import com.zlagi.data.model.SearchSuggestionDataModel
import kotlinx.coroutines.flow.Flow

interface SearchSuggestionsCacheDataSource {
    suspend fun storeSearchSuggestion(searchQuery: SearchSuggestionDataModel)
    fun fetchSearchSuggestions(): Flow<List<SearchSuggestionDataModel>>
    suspend fun deleteSearchSuggestion(searchSuggestionPk: Int)
    suspend fun deleteAllSearchSuggestions()
}
