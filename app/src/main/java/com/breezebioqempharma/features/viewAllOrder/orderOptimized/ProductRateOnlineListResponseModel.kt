package com.breezebioqempharma.features.viewAllOrder.orderOptimized

import com.breezebioqempharma.app.domain.ProductOnlineRateTempEntity
import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.login.model.productlistmodel.ProductRateDataModel
import java.io.Serializable

class ProductRateOnlineListResponseModel: BaseResponse(), Serializable {
    var product_rate_list: ArrayList<ProductOnlineRateTempEntity>? = null
}