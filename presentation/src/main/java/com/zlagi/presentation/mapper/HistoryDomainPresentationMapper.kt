package com.zlagi.presentation.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.domain.model.HistoryDomainModel
import com.zlagi.presentation.model.HistoryPresentationModel
import javax.inject.Inject

/**
 * Mapper class for convert [HistoryDomainModel] to [HistoryPresentationModel] and vice versa
 */
class HistoryDomainPresentationMapper @Inject constructor() :
    Mapper<HistoryDomainModel, HistoryPresentationModel> {

    override fun from(i: HistoryDomainModel): HistoryPresentationModel {
        return HistoryPresentationModel(
            query = i.query
        )
    }

    override fun to(o: HistoryPresentationModel): HistoryDomainModel {
        return HistoryDomainModel(
            query = o.query
        )
    }
}
