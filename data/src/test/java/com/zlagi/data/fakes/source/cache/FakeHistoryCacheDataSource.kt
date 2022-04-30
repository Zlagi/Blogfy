package com.zlagi.data.fakes.source.cache

import com.zlagi.data.model.HistoryDataModel
import com.zlagi.data.source.cache.search.history.HistoryCacheDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeHistoryCacheDataSource : HistoryCacheDataSource {

    private val cache = LinkedHashMap<String, HistoryDataModel>()

    override suspend fun saveQuery(item: HistoryDataModel) {
        cache[item.query] = item
    }

    override fun getHistory(): Flow<List<HistoryDataModel>> {
        return flow {
            emit(value = cache.values.toList())
        }
    }

    override suspend fun deleteQuery(query: String) {
        with(cache) {
            remove(key = query)
        }
    }

    override suspend fun clearHistory() {
        cache.clear()
    }
}
