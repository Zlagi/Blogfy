package com.zlagi.data.repository.search.suggestions

import com.zlagi.data.mapper.SearchSuggestionDataDomainMapper
import com.zlagi.data.source.cache.search.suggestions.SearchSuggestionsCacheDataSource
import com.zlagi.domain.model.SearchSuggestionDomainModel
import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSearchSuggestionsRepository @Inject constructor(
    private val searchSuggestionsCacheDataSource: SearchSuggestionsCacheDataSource,
    private val searchSuggestionDataDomainMapper: SearchSuggestionDataDomainMapper,
) : SearchSuggestionsRepository {

    override suspend fun storeSearchSuggestion(searchSuggestion: SearchSuggestionDomainModel) {
        searchSuggestionDataDomainMapper.to(searchSuggestion).let {
            searchSuggestionsCacheDataSource.storeSearchSuggestion(it)
        }
    }

    override fun getSearchSuggestions(): Flow<List<SearchSuggestionDomainModel>> =
        searchSuggestionsCacheDataSource.fetchSearchSuggestions().map {
            searchSuggestionDataDomainMapper.fromList(it)
        }

    override suspend fun deleteSearchSuggestion(searchSuggestionPk: Int) {
        searchSuggestionsCacheDataSource.deleteSearchSuggestion(searchSuggestionPk)
    }

    override suspend fun deleteAllSearchSuggestions() {
        searchSuggestionsCacheDataSource.deleteAllSearchSuggestions()
    }
}
