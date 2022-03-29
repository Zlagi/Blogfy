package com.zlagi.presentation.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.domain.model.BlogDomainModel
import com.zlagi.presentation.model.BlogPresentationModel
import javax.inject.Inject

/**
 * Mapper class for convert [BlogDomainModel] to [BlogPresentationModel] and vice versa
 */
class BlogDomainPresentationMapper @Inject constructor() : Mapper<BlogDomainModel, BlogPresentationModel> {

    override fun from(i: BlogDomainModel): BlogPresentationModel {
        return BlogPresentationModel(
            pk = i.pk,
            title = i.title,
            description = i.description,
            created = i.created,
            updated = i.updated,
            username = i.username
        )
    }

    override fun to(o: BlogPresentationModel): BlogDomainModel {
        return BlogDomainModel(
            pk = o.pk,
            title = o.title,
            description = o.description,
            created = o.created,
            updated = o.updated,
            username = o.username
        )
    }
}