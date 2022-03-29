package com.zlagi.cache.mapper

import com.zlagi.cache.model.BlogCacheModel
import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.BlogDataModel
import javax.inject.Inject

/**
 * Mapper class for convert [BlogCacheModel] to [BlogDataModel] and vice versa
 */
class BlogCacheDataMapper @Inject constructor() : Mapper<BlogCacheModel, BlogDataModel> {

    override fun from(i: BlogCacheModel): BlogDataModel {
        return BlogDataModel(
            pk = i.pk,
            title = i.title,
            description = i.description,
            created = i.created,
            updated = i.updated,
            username = i.username
        )
    }

    override fun to(o: BlogDataModel): BlogCacheModel {
        return BlogCacheModel(
            pk = o.pk,
            title = o.title,
            description = o.description,
            created = o.created,
            updated = o.updated,
            username = o.username
        )
    }
}
