package com.zlagi.network.source

import com.zlagi.common.exception.NetworkException
import com.zlagi.common.mapper.ExceptionMapper
import com.zlagi.common.utils.wrapper.DataResult
import com.zlagi.data.connectivity.ConnectivityChecker
import com.zlagi.data.model.BlogDataModel
import com.zlagi.data.model.PaginatedBlogsDataModel
import com.zlagi.data.source.network.blog.BlogNetworkDataSource
import com.zlagi.network.apiservice.BlogApiService
import com.zlagi.network.mapper.BlogNetworkDataMapper
import com.zlagi.network.mapper.PaginationNetworkDataMapper
import com.zlagi.network.model.request.NotificationRequest
import com.zlagi.network.model.request.UpdateBlogRequest
import javax.inject.Inject

class DefaultBlogNetworkDataSource @Inject constructor(
    private val blogApiService: BlogApiService,
    private val blogNetworkDataMapper: BlogNetworkDataMapper,
    private val paginationNetworkDataMapper: PaginationNetworkDataMapper,
    private val connectivityChecker: ConnectivityChecker,
    private val exceptionMapper: ExceptionMapper
) : BlogNetworkDataSource {

    override suspend fun getBlogs(
        searchQuery: String,
        page: Int,
        pageSize: Int
    ): DataResult<PaginatedBlogsDataModel> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            val (blogList, pagination) = blogApiService.getBlogs(searchQuery, page, pageSize)
            DataResult.Success(
                PaginatedBlogsDataModel(
                    blogNetworkDataMapper.fromList(blogList.orEmpty()),
                    paginationNetworkDataMapper.from(pagination)
                )
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun createBlog(
        blogTitle: String,
        blogDescription: String,
        creationTime: String
    ): DataResult<BlogDataModel> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            DataResult.Success(
                blogApiService.createBlog(
                    UpdateBlogRequest(
                        title = blogTitle,
                        description = blogDescription,
                        creationTime = creationTime
                    )
                ).let {
                blogNetworkDataMapper.from(it)
            }
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun updateBlog(
        blogPk: Int,
        blogTitle: String,
        blogDescription: String,
        updateTime: String
    ): DataResult<BlogDataModel> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            DataResult.Success(
                blogApiService.updateBlog(
                    blogPk = blogPk,
                    updateBlogRequest = UpdateBlogRequest(
                        title = blogTitle,
                        description = blogDescription,
                        creationTime = updateTime
                    )
                ).let {
                    blogNetworkDataMapper.from(it)
                }
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun deleteBlog(blogPk: Int): DataResult<String> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            DataResult.Success(
                blogApiService.deleteBlog(blogPk).message
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun checkBlogAuthor(blogPk: Int): DataResult<String> {
        if (!connectivityChecker.hasInternetAccess()) {
            return DataResult.Error(NetworkException.NetworkUnavailable)
        }
        return try {
            DataResult.Success(
                blogApiService.checkBlogAuthor(blogPk).message
            )
        } catch (exception: Exception) {
            DataResult.Error(exceptionMapper.mapError(exception))
        }
    }

    override suspend fun sendNotification(title: String) {
        blogApiService.sendNotification(notificationRequest = NotificationRequest(title))
    }
}