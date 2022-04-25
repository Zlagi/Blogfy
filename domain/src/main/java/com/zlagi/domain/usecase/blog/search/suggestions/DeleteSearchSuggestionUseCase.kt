package com.zlagi.domain.usecase.blog.search.suggestions

import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
import javax.inject.Inject

class DeleteSearchSuggestionUseCase @Inject constructor(
    private val searchSuggestionsRepository: SearchSuggestionsRepository
) {
    suspend operator fun invoke(searchSuggestionPk: Int) {
        searchSuggestionsRepository.deleteSearchSuggestion(searchSuggestionPk)
    }
}
