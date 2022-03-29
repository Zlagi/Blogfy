package com.zlagi.cache.mapper

import com.zlagi.cache.model.SearchSuggestionCacheModel
import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.SearchSuggestionDataModel
import javax.inject.Inject

/**
 * Mapper class for convert [SearchSuggestionCacheModel] to [SearchSuggestionDataModel] and vice versa
 */
class SearchSuggestionCacheDataMapper @Inject constructor() :
    Mapper<SearchSuggestionCacheModel, SearchSuggestionDataModel> {

    override fun from(i: SearchSuggestionCacheModel): SearchSuggestionDataModel {
        return SearchSuggestionDataModel(
            pk = i.pk,
            query = i.query
        )
    }

    override fun to(o: SearchSuggestionDataModel): SearchSuggestionCacheModel {
        return SearchSuggestionCacheModel(
            pk = o.pk,
            query = o.query
        )
    }
}
