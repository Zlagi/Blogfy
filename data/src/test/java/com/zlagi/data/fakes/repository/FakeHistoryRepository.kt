package com.zlagi.data.fakes.repository

import com.zlagi.domain.model.HistoryDomainModel
import com.zlagi.domain.repository.search.history.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeHistoryRepository @Inject constructor() : HistoryRepository {

    private val cache = LinkedHashMap<String, HistoryDomainModel>()

    override suspend fun saveQuery(item: HistoryDomainModel) {
        cache[item.query] = item
    }

    override fun getHistory(): Flow<List<HistoryDomainModel>> {
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
