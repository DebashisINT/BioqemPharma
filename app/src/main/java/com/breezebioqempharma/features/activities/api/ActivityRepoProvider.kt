package com.breezebioqempharma.features.activities.api

import com.breezebioqempharma.features.member.api.TeamApi
import com.breezebioqempharma.features.member.api.TeamRepo

object ActivityRepoProvider {
    fun activityRepoProvider(): ActivityRepo {
        return ActivityRepo(ActivityApi.create())
    }

    fun activityImageRepoProvider(): ActivityRepo {
        return ActivityRepo(ActivityApi.createImage())
    }
}