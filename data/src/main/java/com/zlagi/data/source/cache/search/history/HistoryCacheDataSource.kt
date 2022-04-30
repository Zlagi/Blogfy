package com.zlagi.data.source.cache.search.history

import com.zlagi.data.model.HistoryDataModel
import kotlinx.coroutines.flow.Flow

interface HistoryCacheDataSource {
    fun getHistory(): Flow<List<HistoryDataModel>>
    suspend fun saveQuery(item: HistoryDataModel)
    suspend fun deleteQuery(query: String)
    suspend fun clearHistory()
}
