package com.zlagi.data.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.SearchSuggestionDataModel
import com.zlagi.domain.model.SearchSuggestionDomainModel
import javax.inject.Inject

/**
 * Mapper class for convert [SearchSuggestionDataModel] to [SearchSuggestionDomainModel] and vice versa
 */
class SearchSuggestionDataDomainMapper @Inject constructor() :
    Mapper<SearchSuggestionDataModel, SearchSuggestionDomainModel> {

    override fun from(i: SearchSuggestionDataModel): SearchSuggestionDomainModel {
        return SearchSuggestionDomainModel(
            pk = i.pk,
            query = i.query
        )
    }

    override fun to(o: SearchSuggestionDomainModel): SearchSuggestionDataModel {
        return SearchSuggestionDataModel(
            pk = o.pk,
            query = o.query
        )
    }
}
