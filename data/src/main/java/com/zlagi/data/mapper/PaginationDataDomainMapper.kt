package com.zlagi.data.mapper

import com.zlagi.common.mapper.Mapper
import com.zlagi.data.model.PaginationDataModel
import com.zlagi.domain.model.PaginationDomainModel
import javax.inject.Inject

/**
 * Mapper class for convert [PaginationDataModel] to [PaginationDomainModel] and vice versa
 */
class PaginationDataDomainMapper @Inject constructor() :
    Mapper<PaginationDataModel, PaginationDomainModel> {

    override fun from(i: PaginationDataModel): PaginationDomainModel {
        return PaginationDomainModel(
            currentPage = i.currentPage,
            totalPages = i.totalPages,
        )
    }

    override fun to(o: PaginationDomainModel): PaginationDataModel {
        return PaginationDataModel(
            currentPage = o.currentPage,
            totalPages = o.totalPages
        )
    }
}
