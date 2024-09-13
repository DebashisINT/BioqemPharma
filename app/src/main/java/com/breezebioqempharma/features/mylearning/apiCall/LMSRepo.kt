package com.breezebioqempharma.features.mylearning.apiCall

import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.login.api.opportunity.OpportunityListApi
import com.breezebioqempharma.features.login.model.opportunitymodel.OpportunityStatusListResponseModel
import com.breezebioqempharma.features.mylearning.LMS_CONTENT_INFO
import com.breezebioqempharma.features.mylearning.MyLarningListResponse
import com.breezebioqempharma.features.mylearning.TopicListResponse
import com.breezebioqempharma.features.mylearning.VideoPlayLMS
import com.breezebioqempharma.features.mylearning.VideoTopicWiseResponse
import io.reactivex.Observable

class LMSRepo(val apiService: LMSApi) {

    fun getTopics(user_id: String): Observable<TopicListResponse> {
        return apiService.getTopics(user_id)
    }

    fun getTopicsWiseVideo(topic_id: String): Observable<VideoTopicWiseResponse> {
        return apiService.getTopicswiseVideoApi(topic_id)
    }

    fun saveContentInfoApi(lms_content_info: LMS_CONTENT_INFO): Observable<BaseResponse> {
        return apiService.saveContentInfoApi(lms_content_info)
    }

    fun getMyLraningInfo(user_id: String): Observable<MyLarningListResponse> {
        return apiService.getMyLearningContentList(user_id)
    }
}