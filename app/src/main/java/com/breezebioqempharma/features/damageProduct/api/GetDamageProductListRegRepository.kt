package com.breezebioqempharma.features.damageProduct.api

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.breezebioqempharma.app.FileUtils
import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.NewQuotation.model.*
import com.breezebioqempharma.features.addshop.model.AddShopRequestData
import com.breezebioqempharma.features.addshop.model.AddShopResponse
import com.breezebioqempharma.features.damageProduct.model.DamageProductResponseModel
import com.breezebioqempharma.features.damageProduct.model.delBreakageReq
import com.breezebioqempharma.features.damageProduct.model.viewAllBreakageReq
import com.breezebioqempharma.features.login.model.userconfig.UserConfigResponseModel
import com.breezebioqempharma.features.myjobs.model.WIPImageSubmit
import com.breezebioqempharma.features.photoReg.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class GetDamageProductListRegRepository(val apiService : GetDamageProductListApi) {

    fun viewBreakage(req: viewAllBreakageReq): Observable<DamageProductResponseModel> {
        return apiService.viewBreakage(req)
    }

    fun delBreakage(req: delBreakageReq): Observable<BaseResponse>{
        return apiService.BreakageDel(req.user_id!!,req.breakage_number!!,req.session_token!!)
    }

}