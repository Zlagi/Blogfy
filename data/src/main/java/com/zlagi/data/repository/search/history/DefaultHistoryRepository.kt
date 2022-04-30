package com.zlagi.data.repository.search.history

import com.zlagi.data.mapper.HistoryDataDomainMapper
import com.zlagi.data.source.cache.search.history.HistoryCacheDataSource
import com.zlagi.domain.model.HistoryDomainModel
import com.zlagi.domain.repository.search.history.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultHistoryRepository @Inject constructor(
    private val historyCacheDataSource: HistoryCacheDataSource,
    private val historyDataDomainMapper: HistoryDataDomainMapper,
) : HistoryRepository {

    override fun getHistory(): Flow<List<HistoryDomainModel>> =
        historyCacheDataSource.getHistory().map {
            historyDataDomainMapper.fromList(it)
        }

    override suspend fun saveQuery(item: HistoryDomainModel) {
        historyDataDomainMapper.to(item).let {
            historyCacheDataSource.saveQuery(it)
        }
    }

    override suspend fun deleteQuery(query: String) {
        historyCacheDataSource.deleteQuery(query)
    }

    override suspend fun clearHistory() {
        historyCacheDataSource.clearHistory()
    }
}
