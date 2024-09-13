package com.breezebioqempharma.features.location.api

import com.breezebioqempharma.features.location.shopdurationapi.ShopDurationApi
import com.breezebioqempharma.features.location.shopdurationapi.ShopDurationRepository


object LocationRepoProvider {
    fun provideLocationRepository(): LocationRepo {
        return LocationRepo(LocationApi.create())
    }
}