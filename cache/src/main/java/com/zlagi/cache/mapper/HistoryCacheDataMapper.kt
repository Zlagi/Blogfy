package com.zlagi.cache.mapper

import com.zlagi.cache.model.HistoryCacheModel
import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.HistoryDataModel
import javax.inject.Inject

/**
 * Mapper class for convert [HistoryCacheModel] to [HistoryDataModel] and vice versa
 */
class HistoryCacheDataMapper @Inject constructor() :
    Mapper<HistoryCacheModel, HistoryDataModel> {

    override fun from(i: HistoryCacheModel): HistoryDataModel {
        return HistoryDataModel(
            query = i.query
        )
    }

    override fun to(o: HistoryDataModel): HistoryCacheModel {
        return HistoryCacheModel(
            query = o.query
        )
    }
}
