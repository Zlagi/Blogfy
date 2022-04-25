package com.zlagi.presentation.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.domain.model.SearchSuggestionDomainModel
import com.zlagi.presentation.model.SearchSuggestionPresentationModel
import javax.inject.Inject

/**
 * Mapper class for convert [SearchSuggestionDomainModel] to [SearchSuggestionPresentationModel] and vice versa
 */
class SearchSuggestionDomainPresentationMapper @Inject constructor() :
    Mapper<SearchSuggestionDomainModel, SearchSuggestionPresentationModel> {

    override fun from(i: SearchSuggestionDomainModel): SearchSuggestionPresentationModel {
        return SearchSuggestionPresentationModel(
            pk = i.pk,
            query = i.query
        )
    }

    override fun to(o: SearchSuggestionPresentationModel): SearchSuggestionDomainModel {
        return SearchSuggestionDomainModel(
            pk = o.pk,
            query = o.query
        )
    }
}
