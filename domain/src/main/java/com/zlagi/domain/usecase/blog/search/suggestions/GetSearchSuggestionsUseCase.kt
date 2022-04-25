package com.zlagi.domain.usecase.blog.search.suggestions

import com.zlagi.domain.model.SearchSuggestionDomainModel
import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchSuggestionsUseCase @Inject constructor(
    private val searchSuggestionsRepository: SearchSuggestionsRepository
) {
    operator fun invoke(): Flow<List<SearchSuggestionDomainModel>> {
        return searchSuggestionsRepository.getSearchSuggestions()
    }
}
