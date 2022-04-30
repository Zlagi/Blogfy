package com.zlagi.domain.usecase.blog.search.history

import com.zlagi.domain.repository.search.history.HistoryRepository
import javax.inject.Inject

class DeleteQueryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(query: String) {
        historyRepository.deleteQuery(query)
    }
}
