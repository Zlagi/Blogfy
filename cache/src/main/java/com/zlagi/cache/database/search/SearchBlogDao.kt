package com.zlagi.cache.database.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zlagi.cache.model.BlogCacheModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchBlogDao {

    @Query(
        """
        SELECT * FROM blog
        WHERE title LIKE '%' || :searchQuery || '%'
        ORDER BY pk ASC
        """
    )
    fun fetchBlogsOrderByTitleDESC(
        searchQuery: String
    ): Flow<List<BlogCacheModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeBlogs(blogCacheModels: List<BlogCacheModel>)

    @Query("DELETE FROM blog WHERE pk = :blogPk")
    suspend fun deleteBlog(blogPk: Int)

    @Query(
        """
        DELETE FROM blog
    """
    )
    suspend fun deleteAllBlogs()
}
