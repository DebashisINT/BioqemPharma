package com.breezebioqempharma.features.stockAddCurrentStock.api

import com.breezebioqempharma.features.location.shopRevisitStatus.ShopRevisitStatusApi
import com.breezebioqempharma.features.location.shopRevisitStatus.ShopRevisitStatusRepository

object ShopAddStockProvider {
    fun provideShopAddStockRepository(): ShopAddStockRepository {
        return ShopAddStockRepository(ShopAddStockApi.create())
    }
}