package com.zlagi.network.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.PaginationDataModel
import com.zlagi.network.model.response.PaginationNetworkModel
import javax.inject.Inject

/**
 * Mapper class for convert [PaginationNetworkModel] to [PaginationDataModel] and vice versa
 */
class PaginationNetworkDataMapper @Inject constructor() : Mapper<PaginationNetworkModel?, PaginationDataModel> {

    override fun from(i: PaginationNetworkModel?): PaginationDataModel {
        return PaginationDataModel(
            currentPage = i?.current_page ?: 0,
            totalPages = i?.total_pages  ?: 0,
        )
    }

    override fun to(o: PaginationDataModel): PaginationNetworkModel {
        return PaginationNetworkModel(
            current_page = o.currentPage,
            total_pages = o.totalPages
        )
    }
}
