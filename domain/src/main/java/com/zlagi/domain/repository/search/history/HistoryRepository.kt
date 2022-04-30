package com.zlagi.domain.repository.search.history

import com.zlagi.domain.model.HistoryDomainModel
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getHistory(): Flow<List<HistoryDomainModel>>
    suspend fun saveQuery(item: HistoryDomainModel)
    suspend fun deleteQuery(query: String)
    suspend fun clearHistory()
}
