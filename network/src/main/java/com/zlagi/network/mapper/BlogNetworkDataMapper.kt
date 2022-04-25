package com.zlagi.network.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.BlogDataModel
import com.zlagi.network.model.response.BlogNetworkModel
import javax.inject.Inject

/**
 * Mapper class for convert [BlogNetworkModel] to [BlogDataModel] and vice versa
 */
class BlogNetworkDataMapper @Inject constructor() : Mapper<BlogNetworkModel, BlogDataModel> {

    override fun from(i: BlogNetworkModel): BlogDataModel {
        return BlogDataModel(
            pk = i.pk ?: 0,
            title = i.title.orEmpty(),
            description = i.description.orEmpty(),
            created = i.created.orEmpty(),
            updated = i.updated.orEmpty(),
            username = i.username.orEmpty()
        )
    }

    override fun to(o: BlogDataModel): BlogNetworkModel {
        return BlogNetworkModel(
            pk = o.pk,
            title = o.title,
            description = o.description,
            updated = o.updated,
            created = o.created,
            username = o.username
        )
    }
}
