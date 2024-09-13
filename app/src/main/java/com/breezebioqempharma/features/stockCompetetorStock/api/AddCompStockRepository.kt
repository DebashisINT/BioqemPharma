package com.breezebioqempharma.features.stockCompetetorStock.api

import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.orderList.model.NewOrderListResponseModel
import com.breezebioqempharma.features.stockCompetetorStock.ShopAddCompetetorStockRequest
import com.breezebioqempharma.features.stockCompetetorStock.model.CompetetorStockGetData
import io.reactivex.Observable

class AddCompStockRepository(val apiService:AddCompStockApi){

    fun addCompStock(shopAddCompetetorStockRequest: ShopAddCompetetorStockRequest): Observable<BaseResponse> {
        return apiService.submShopCompStock(shopAddCompetetorStockRequest)
    }

    fun getCompStockList(sessiontoken: String, user_id: String, date: String): Observable<CompetetorStockGetData> {
        return apiService.getCompStockList(sessiontoken, user_id, date)
    }
}