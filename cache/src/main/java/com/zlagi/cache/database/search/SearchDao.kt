package com.zlagi.cache.database.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zlagi.cache.model.SearchBlogCacheModel
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Query(
        """
        SELECT * FROM search_blog
        WHERE title LIKE '%' || :searchQuery || '%'
        ORDER BY pk ASC
        """
    )
    fun fetchBlogsOrderByTitleDESC(
        searchQuery: String
    ): Flow<List<SearchBlogCacheModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeBlogs(blogCacheModels: List<SearchBlogCacheModel>)

    @Query("DELETE FROM search_blog WHERE pk = :blogPk")
    suspend fun deleteBlog(blogPk: Int)

    @Query(
        """
        DELETE FROM search_blog
    """
    )
    suspend fun deleteAllBlogs()
}
