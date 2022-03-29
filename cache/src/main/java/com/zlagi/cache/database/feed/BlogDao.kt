package com.zlagi.cache.database.feed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zlagi.cache.model.BlogCacheModel
import kotlinx.coroutines.flow.Flow

@Dao
interface BlogDao {

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

    @Query("SELECT * FROM blog WHERE pk = :blogPK")
    fun fetchBlog(blogPK: Int): Flow<BlogCacheModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun storeBlog(blogCacheModel: BlogCacheModel)

    @Query(
        """
        UPDATE blog SET title = :blogTitle, description = :blogDescription, updated = :updated 
        WHERE pk = :blogPk
        """
    )
    suspend fun updateBlog(blogPk: Int, blogTitle: String, blogDescription: String, updated: String)

    @Query("DELETE FROM blog WHERE pk = :blogPk")
    suspend fun deleteBlog(blogPk: Int)

    @Query(
        """
        DELETE FROM blog
    """
    )
    suspend fun deleteAllBlogs()
}
