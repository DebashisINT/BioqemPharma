package com.breezebioqempharma.features.location.shopRevisitStatus

import com.breezebioqempharma.features.location.shopdurationapi.ShopDurationApi
import com.breezebioqempharma.features.location.shopdurationapi.ShopDurationRepository

object ShopRevisitStatusRepositoryProvider {
    fun provideShopRevisitStatusRepository(): ShopRevisitStatusRepository {
        return ShopRevisitStatusRepository(ShopRevisitStatusApi.create())
    }
}