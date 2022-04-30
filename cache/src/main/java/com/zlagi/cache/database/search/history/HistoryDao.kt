package com.zlagi.cache.database.search.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zlagi.cache.model.HistoryCacheModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query(
        """
        SELECT * FROM history 
        """
    )
    fun getHistory(): Flow<List<HistoryCacheModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuery(item: HistoryCacheModel)

    @Query(
        """
        Delete from history WHERE `query` LIKE '%' || :query
    """
    )
    suspend fun deleteQuery(query: String)

    @Query(
        """
        DELETE from history
    """
    )
    suspend fun clearHistory()
}
