package com.zlagi.domain.repository.search.suggestions

import com.zlagi.domain.model.SearchSuggestionDomainModel
import kotlinx.coroutines.flow.Flow

interface SearchSuggestionsRepository {
    suspend fun storeSearchSuggestion(searchSuggestion: SearchSuggestionDomainModel)
    fun getSearchSuggestions(): Flow<List<SearchSuggestionDomainModel>>
    suspend fun deleteSearchSuggestion(searchSuggestionPk: Int)
    suspend fun deleteAllSearchSuggestions()
}