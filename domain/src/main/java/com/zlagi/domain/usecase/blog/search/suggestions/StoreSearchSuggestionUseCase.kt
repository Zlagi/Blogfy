package com.zlagi.domain.usecase.blog.search.suggestions

import com.zlagi.domain.model.SearchSuggestionDomainModel
import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
import javax.inject.Inject

class StoreSearchSuggestionUseCase @Inject constructor(private val searchSuggestionsRepository: SearchSuggestionsRepository) {
    operator suspend fun invoke(
        query: String
    ) {
        searchSuggestionsRepository.storeSearchSuggestion(SearchSuggestionDomainModel(0, query))
    }
}