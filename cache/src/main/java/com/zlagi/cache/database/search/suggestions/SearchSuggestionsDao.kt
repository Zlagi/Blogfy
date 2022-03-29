package com.zlagi.cache.database.search.suggestions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zlagi.cache.model.SearchSuggestionCacheModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchSuggestionsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeSearchSuggestion(searchSuggestionCacheModel: SearchSuggestionCacheModel)

    @Query(
        """
        SELECT * FROM search_suggestion 
        """
    )
    fun fetchSearchSuggestions(): Flow<List<SearchSuggestionCacheModel>>

    @Query(
        """
        Delete from search_suggestion WHERE pk LIKE '%' || :searchSuggestionPk
    """
    )
    suspend fun deleteSearchSuggestion(searchSuggestionPk: Int)

    @Query(
        """
        DELETE from search_suggestion
    """
    )
    suspend fun deleteAllSearchSuggestions()
}
