package com.zlagi.cache.mapper

import com.zlagi.cache.model.FeedBlogCacheModel
import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.BlogDataModel
import javax.inject.Inject

/**
 * Mapper class for convert [FeedBlogCacheModel] to [BlogDataModel] and vice versa
 */
class FeedBlogCacheDataMapper @Inject constructor() : Mapper<FeedBlogCacheModel, BlogDataModel> {

    override fun from(i: FeedBlogCacheModel): BlogDataModel {
        return BlogDataModel(
            pk = i.pk,
            title = i.title,
            description = i.description,
            created = i.created,
            updated = i.updated,
            username = i.username
        )
    }

    override fun to(o: BlogDataModel): FeedBlogCacheModel {
        return FeedBlogCacheModel(
            pk = o.pk,
            title = o.title,
            description = o.description,
            created = o.created,
            updated = o.updated,
            username = o.username
        )
    }
}
