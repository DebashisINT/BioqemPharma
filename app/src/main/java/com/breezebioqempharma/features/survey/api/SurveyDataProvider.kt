package com.breezebioqempharma.features.survey.api

import com.breezebioqempharma.features.photoReg.api.GetUserListPhotoRegApi
import com.breezebioqempharma.features.photoReg.api.GetUserListPhotoRegRepository

object SurveyDataProvider{

    fun provideSurveyQ(): SurveyDataRepository {
        return SurveyDataRepository(SurveyDataApi.create())
    }

    fun provideSurveyQMultiP(): SurveyDataRepository {
        return SurveyDataRepository(SurveyDataApi.createImage())
    }
}