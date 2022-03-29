package com.zlagi.cache.source

import com.zlagi.cache.database.search.suggestions.SearchSuggestionsDao
import com.zlagi.cache.mapper.SearchSuggestionCacheDataMapper
import com.zlagi.data.model.SearchSuggestionDataModel
import com.zlagi.data.source.cache.search.suggestions.SearchSuggestionsCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSearchSuggestionsCacheDataSource @Inject constructor(
    private val searchSuggestionsDao: SearchSuggestionsDao,
    private val searchSuggestionCacheDataMapper: SearchSuggestionCacheDataMapper,
) : SearchSuggestionsCacheDataSource {

    override suspend fun storeSearchSuggestion(searchQuery: SearchSuggestionDataModel) {
        searchSuggestionCacheDataMapper.to(searchQuery).let {
            searchSuggestionsDao.storeSearchSuggestion(it)
        }
    }

    override fun fetchSearchSuggestions(): Flow<List<SearchSuggestionDataModel>> =
        searchSuggestionsDao.fetchSearchSuggestions().map {
            searchSuggestionCacheDataMapper.fromList(it)
        }

    override suspend fun deleteSearchSuggestion(searchSuggestionPk: Int) {
        searchSuggestionsDao.deleteSearchSuggestion(searchSuggestionPk)
    }

    override suspend fun deleteAllSearchSuggestions() {
        searchSuggestionsDao.deleteAllSearchSuggestions()
    }
}
