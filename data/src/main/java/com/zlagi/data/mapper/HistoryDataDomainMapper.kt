package com.zlagi.data.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.HistoryDataModel
import com.zlagi.domain.model.HistoryDomainModel
import javax.inject.Inject

/**
 * Mapper class for convert [HistoryDataModel] to [HistoryDomainModel] and vice versa
 */
class HistoryDataDomainMapper @Inject constructor() :
    Mapper<HistoryDataModel, HistoryDomainModel> {

    override fun from(i: HistoryDataModel): HistoryDomainModel {
        return HistoryDomainModel(
            query = i.query
        )
    }

    override fun to(o: HistoryDomainModel): HistoryDataModel {
        return HistoryDataModel(
            query = o.query
        )
    }
}
