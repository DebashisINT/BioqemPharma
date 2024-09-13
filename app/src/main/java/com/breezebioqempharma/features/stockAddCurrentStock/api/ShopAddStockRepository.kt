package com.breezebioqempharma.features.stockAddCurrentStock.api

import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.location.model.ShopRevisitStatusRequest
import com.breezebioqempharma.features.location.shopRevisitStatus.ShopRevisitStatusApi
import com.breezebioqempharma.features.stockAddCurrentStock.ShopAddCurrentStockRequest
import com.breezebioqempharma.features.stockAddCurrentStock.model.CurrentStockGetData
import com.breezebioqempharma.features.stockCompetetorStock.model.CompetetorStockGetData
import io.reactivex.Observable

class ShopAddStockRepository (val apiService : ShopAddStockApi){
    fun shopAddStock(shopAddCurrentStockRequest: ShopAddCurrentStockRequest?): Observable<BaseResponse> {
        return apiService.submShopAddStock(shopAddCurrentStockRequest)
    }

    fun getCurrStockList(sessiontoken: String, user_id: String, date: String): Observable<CurrentStockGetData> {
        return apiService.getCurrStockListApi(sessiontoken, user_id, date)
    }

}