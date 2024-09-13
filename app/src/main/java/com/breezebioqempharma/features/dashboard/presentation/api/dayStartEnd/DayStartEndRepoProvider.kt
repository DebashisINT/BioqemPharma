package com.breezebioqempharma.features.dashboard.presentation.api.dayStartEnd

import com.breezebioqempharma.features.stockCompetetorStock.api.AddCompStockApi
import com.breezebioqempharma.features.stockCompetetorStock.api.AddCompStockRepository

object DayStartEndRepoProvider {
    fun dayStartRepositiry(): DayStartEndRepository {
        return DayStartEndRepository(DayStartEndApi.create())
    }

}