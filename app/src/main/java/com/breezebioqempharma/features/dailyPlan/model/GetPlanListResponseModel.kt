package com.breezebioqempharma.features.dailyPlan.model

import com.breezebioqempharma.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 24-12-2019.
 */
class GetPlanListResponseModel : BaseResponse(), Serializable {
    var update_plan_list: ArrayList<GetPlanListDataModel>? = null
}