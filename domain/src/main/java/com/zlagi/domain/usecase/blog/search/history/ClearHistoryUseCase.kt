package com.zlagi.domain.usecase.blog.search.history

import com.zlagi.domain.repository.search.history.HistoryRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke() {
        historyRepository.clearHistory()
    }
}
