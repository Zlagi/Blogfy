package com.zlagi.cache.mapper

import com.zlagi.cache.model.SearchBlogCacheModel
import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.BlogDataModel
import javax.inject.Inject

/**
 * Mapper class for convert [SearchBlogCacheModel] to [BlogDataModel] and vice versa
 */
class SearchBlogCacheDataMapper @Inject constructor() : Mapper<SearchBlogCacheModel, BlogDataModel> {

    override fun from(i: SearchBlogCacheModel): BlogDataModel {
        return BlogDataModel(
            pk = i.pk,
            title = i.title,
            description = i.description,
            created = i.created,
            updated = i.updated,
            username = i.username
        )
    }

    override fun to(o: BlogDataModel): SearchBlogCacheModel {
        return SearchBlogCacheModel(
            pk = o.pk,
            title = o.title,
            description = o.description,
            created = o.created,
            updated = o.updated,
            username = o.username
        )
    }
}
