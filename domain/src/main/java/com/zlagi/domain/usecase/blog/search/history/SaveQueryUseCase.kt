package com.zlagi.domain.usecase.blog.search.history

import com.zlagi.domain.model.HistoryDomainModel
import com.zlagi.domain.repository.search.history.HistoryRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SaveQueryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(
        query: String
    ) {
        val data = historyRepository.getHistory().first()
        val exists = data.find {
            it.query == query
        }
        if (exists == null) historyRepository.saveQuery(HistoryDomainModel(query = query))
    }
}
