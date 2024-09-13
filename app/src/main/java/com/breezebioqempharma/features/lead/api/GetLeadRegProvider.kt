package com.breezebioqempharma.features.lead.api

import com.breezebioqempharma.features.NewQuotation.api.GetQuotListRegRepository
import com.breezebioqempharma.features.NewQuotation.api.GetQutoListApi


object GetLeadRegProvider {
    fun provideList(): GetLeadListRegRepository {
        return GetLeadListRegRepository(GetLeadListApi.create())
    }
}