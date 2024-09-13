package com.breezebioqempharma.features.mylearning.apiCall

import com.breezebioqempharma.features.login.api.opportunity.OpportunityListApi
import com.breezebioqempharma.features.login.api.opportunity.OpportunityListRepo

object LMSRepoProvider {
    fun getTopicList(): LMSRepo {
        return LMSRepo(LMSApi.create())
    }
}