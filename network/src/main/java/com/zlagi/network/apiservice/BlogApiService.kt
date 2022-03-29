package com.zlagi.network.apiservice

import com.zlagi.network.model.NetworkConstants
import com.zlagi.network.model.NetworkParameters
import com.zlagi.network.model.request.NotificationRequest
import com.zlagi.network.model.request.UpdateBlogRequest
import com.zlagi.network.model.response.BlogNetworkModel
import com.zlagi.network.model.response.GenericResponse
import com.zlagi.network.model.response.PaginatedBlogsNetworkModel
import retrofit2.http.*

interface BlogApiService {

    @GET(NetworkConstants.BLOGS_ENDPOINT)
    suspend fun getBlogs(
        @Query(NetworkParameters.SEARCH_QUERY) searchQuery: String,
        @Query(NetworkParameters.PAGE) page: Int,
        @Query(NetworkParameters.LIMIT) limit: Int,
    ): PaginatedBlogsNetworkModel

    @POST(NetworkConstants.BLOG_ENDPOINT)
    suspend fun createBlog(
        @Body updateBlogRequest: UpdateBlogRequest
    ): BlogNetworkModel

    @DELETE(NetworkConstants.DELETE_ENDPOINT)
    suspend fun deleteBlog(
        @Path(NetworkParameters.BLOG_PK) blogPk: Int
    ): GenericResponse

    @PUT(NetworkConstants.UPDATE_ENDPOINT)
    suspend fun updateBlog(
        @Path(NetworkParameters.BLOG_PK) blogPk: Int,
        @Body updateBlogRequest: UpdateBlogRequest
    ): BlogNetworkModel

    @GET(NetworkConstants.CHECK_AUTHOR_ENDPOINT)
    suspend fun checkBlogAuthor(
        @Path(NetworkParameters.BLOG_PK) blogPk: Int
    ): GenericResponse

    @POST(NetworkConstants.NOTIFICATION_ENDPOINT)
    suspend fun sendNotification(
        @Body notificationRequest: NotificationRequest
    ): GenericResponse
}