package com.zlagi.cache.database.feed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zlagi.cache.model.FeedBlogCacheModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Query(
        """
        SELECT * FROM feed
        WHERE title LIKE '%' || :searchQuery || '%'
        ORDER BY pk ASC
        """
    )
    fun fetchBlogsOrderByTitleDESC(
        searchQuery: String
    ): Flow<List<FeedBlogCacheModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeBlogs(feedBlogCacheModels: List<FeedBlogCacheModel>)

    @Query("SELECT * FROM feed WHERE pk = :blogPK")
    fun fetchBlog(blogPK: Int): Flow<FeedBlogCacheModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeBlog(feedBlogCacheModel: FeedBlogCacheModel)

    @Query(
        """
        UPDATE feed SET title = :blogTitle, description = :blogDescription, updated = :updated 
        WHERE pk = :blogPk
        """
    )
    suspend fun updateBlog(blogPk: Int, blogTitle: String, blogDescription: String, updated: String)

    @Query("DELETE FROM feed WHERE pk = :blogPk")
    suspend fun deleteBlog(blogPk: Int)

    @Query(
        """
        DELETE FROM feed
    """
    )
    suspend fun deleteAllBlogs()
}
