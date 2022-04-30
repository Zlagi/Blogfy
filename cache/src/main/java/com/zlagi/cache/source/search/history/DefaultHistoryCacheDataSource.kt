package com.zlagi.cache.source.search.history

import com.zlagi.cache.database.search.history.HistoryDao
import com.zlagi.cache.mapper.HistoryCacheDataMapper
import com.zlagi.data.model.HistoryDataModel
import com.zlagi.data.source.cache.search.history.HistoryCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultHistoryCacheDataSource @Inject constructor(
    private val historyDao: HistoryDao,
    private val historyCacheDataMapper: HistoryCacheDataMapper,
) : HistoryCacheDataSource {

    override fun getHistory(): Flow<List<HistoryDataModel>> =
        historyDao.getHistory().map {
            historyCacheDataMapper.fromList(it)
        }

    override suspend fun saveQuery(item: HistoryDataModel) {
        historyCacheDataMapper.to(item).let {
            historyDao.saveQuery(it)
        }
    }

    override suspend fun deleteQuery(query: String) {
        historyDao.deleteQuery(query)
    }

    override suspend fun clearHistory() {
        historyDao.clearHistory()
    }
}
