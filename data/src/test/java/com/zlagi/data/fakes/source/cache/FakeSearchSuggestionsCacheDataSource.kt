package com.zlagi.data.fakes.source.cache

import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.data.model.SearchSuggestionDataModel
import com.zlagi.data.source.cache.search.suggestions.SearchSuggestionsCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSearchSuggestionsCacheDataSource : SearchSuggestionsCacheDataSource {

    override suspend fun storeSearchSuggestion(searchQuery: SearchSuggestionDataModel) {
        //
    }

    override fun fetchSearchSuggestions(): Flow<List<SearchSuggestionDataModel>> {
        return flow {
            emit(FakeDataGenerator.searchSuggestions)
        }
    }

    override suspend fun deleteSearchSuggestion(searchSuggestionPk: Int) {
        //
    }

    override suspend fun deleteAllSearchSuggestions() {
        //
    }
}
