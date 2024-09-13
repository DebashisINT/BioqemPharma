package com.breezebioqempharma.features.login.model.opportunitymodel

import com.breezebioqempharma.app.domain.OpportunityStatusEntity
import com.breezebioqempharma.app.domain.ProductListEntity
import com.breezebioqempharma.base.BaseResponse

/**
 * Created by Puja on 30.05.2024
 */
class OpportunityStatusListResponseModel : BaseResponse() {
    var status_list: ArrayList<OpportunityStatusEntity>? = null
}