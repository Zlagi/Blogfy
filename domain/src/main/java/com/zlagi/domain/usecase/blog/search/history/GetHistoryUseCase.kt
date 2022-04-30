package com.zlagi.domain.usecase.blog.search.history

import com.zlagi.common.exception.CacheException
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.domain.model.HistoryDomainModel
import com.zlagi.domain.repository.search.history.HistoryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(): DataResult<List<HistoryDomainModel>> {
        val data = historyRepository.getHistory().first()
            .takeLast(5).reversed()
        if (data.isEmpty()) {
            return DataResult.Error(CacheException.NoResults)
        }
        return DataResult.Success(data)
    }
}
