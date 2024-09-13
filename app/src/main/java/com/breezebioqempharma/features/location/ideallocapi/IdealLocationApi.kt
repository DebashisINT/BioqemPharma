package com.breezebioqempharma.features.location.ideallocapi

import com.breezebioqempharma.app.NetworkConstant
import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.location.model.IdealLocationInputParams
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Saikat on 05-02-2019.
 */
interface IdealLocationApi {
    @POST("IdealLocation/Submit")
    fun submitIdealLocation(@Body idealLoc: IdealLocationInputParams?): Observable<BaseResponse>

    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): IdealLocationApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(IdealLocationApi::class.java)
        }
    }
}