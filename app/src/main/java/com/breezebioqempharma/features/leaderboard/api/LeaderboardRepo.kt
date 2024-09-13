package com.breezebioqempharma.features.leaderboard.api

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.breezebioqempharma.app.FileUtils
import com.breezebioqempharma.app.Pref
import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.addshop.model.AddLogReqData
import com.breezebioqempharma.features.addshop.model.AddShopRequestData
import com.breezebioqempharma.features.addshop.model.AddShopResponse
import com.breezebioqempharma.features.addshop.model.LogFileResponse
import com.breezebioqempharma.features.addshop.model.UpdateAddrReq
import com.breezebioqempharma.features.contacts.CallHisDtls
import com.breezebioqempharma.features.contacts.CompanyReqData
import com.breezebioqempharma.features.contacts.ContactMasterRes
import com.breezebioqempharma.features.contacts.SourceMasterRes
import com.breezebioqempharma.features.contacts.StageMasterRes
import com.breezebioqempharma.features.contacts.StatusMasterRes
import com.breezebioqempharma.features.contacts.TypeMasterRes
import com.breezebioqempharma.features.dashboard.presentation.DashboardActivity
import com.breezebioqempharma.features.login.model.WhatsappApiData
import com.breezebioqempharma.features.login.model.WhatsappApiFetchData
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by Puja on 10-10-2024.
 */
class LeaderboardRepo(val apiService: LeaderboardApi) {

    fun branchlist(session_token: String): Observable<LeaderboardBranchData> {
        return apiService.branchList(session_token)
    }
    fun ownDatalist(user_id: String,activitybased: String,branchwise: String,flag: String): Observable<LeaderboardOwnData> {
        return apiService.ownDatalist(user_id,activitybased,branchwise,flag)
    }
    fun overAllAPI(user_id: String,activitybased: String,branchwise: String,flag: String): Observable<LeaderboardOverAllData> {
        return apiService.overAllDatalist(user_id,activitybased,branchwise,flag)
    }
}