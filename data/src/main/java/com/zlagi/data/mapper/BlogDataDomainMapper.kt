package com.zlagi.data.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.BlogDataModel
import com.zlagi.domain.model.BlogDomainModel
import javax.inject.Inject

/**
 * Mapper class for convert [BlogDataModel] to [BlogDomainModel] and vice versa
 */
class BlogDataDomainMapper @Inject constructor() : Mapper<BlogDataModel, BlogDomainModel> {

    override fun from(i: BlogDataModel): BlogDomainModel {
        return BlogDomainModel(
            pk = i.pk,
            title = i.title,
            description = i.description,
            created = i.created,
            updated = i.updated,
            username = i.username
        )
    }

    override fun to(o: BlogDomainModel): BlogDataModel {
        return BlogDataModel(
            pk = o.pk,
            title = o.title,
            description = o.description,
            created = o.created,
            updated = o.updated,
            username = o.username
        )
    }
}
