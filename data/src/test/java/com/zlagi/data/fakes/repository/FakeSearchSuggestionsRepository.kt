package com.zlagi.data.fakes.repository

import com.zlagi.data.fakes.FakeDataGenerator
import com.zlagi.domain.model.SearchSuggestionDomainModel
import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeSearchSuggestionsRepository @Inject constructor() : SearchSuggestionsRepository {
    override suspend fun storeSearchSuggestion(searchSuggestion: SearchSuggestionDomainModel) {
        //
    }

    override fun getSearchSuggestions(): Flow<List<SearchSuggestionDomainModel>> {
        return flow {
            emit(FakeDataGenerator.searchSuggestionsDomain)
        }
    }

    override suspend fun deleteSearchSuggestion(searchSuggestionPk: Int) {
        //
    }

    override suspend fun deleteAllSearchSuggestions() {
        //
    }
}
