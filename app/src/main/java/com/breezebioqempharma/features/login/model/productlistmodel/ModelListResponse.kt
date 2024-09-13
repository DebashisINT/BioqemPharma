package com.breezebioqempharma.features.login.model.productlistmodel

import com.breezebioqempharma.app.domain.ModelEntity
import com.breezebioqempharma.app.domain.ProductListEntity
import com.breezebioqempharma.base.BaseResponse

class ModelListResponse: BaseResponse() {
    var model_list: ArrayList<ModelEntity>? = null
}