package com.zlagi.domain.usecase.blog.search.suggestions

import com.zlagi.domain.repository.search.suggestions.SearchSuggestionsRepository
import javax.inject.Inject

class DeleteAllSearchSuggestionsUseCase @Inject constructor(
    private val searchSuggestionsRepository: SearchSuggestionsRepository
) {
    suspend operator fun invoke() {
        searchSuggestionsRepository.deleteAllSearchSuggestions()
    }
}
