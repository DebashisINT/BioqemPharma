package com.breezebioqempharma.features.orderhistory.activitiesapi

import com.breezebioqempharma.app.Pref
import com.breezebioqempharma.base.BaseResponse
import com.breezebioqempharma.features.orderhistory.model.FetchLocationRequest
import com.breezebioqempharma.features.orderhistory.model.FetchLocationResponse
import com.breezebioqempharma.features.orderhistory.model.SubmitLocationInputModel
import com.breezebioqempharma.features.orderhistory.model.UnknownReponseModel
import io.reactivex.Observable

/**
 * Created by Pratishruti on 30-11-2017.
 */
class LocationFetchRepository(val apiService: LocationFetchApi){
    fun fetchLocationUpdate(location: FetchLocationRequest): Observable<FetchLocationResponse> {
        return apiService.getLocationUpdates(location)
    }

    fun fetchLocationUpdate(date: String): Observable<FetchLocationResponse> {
        return apiService.getLocationUpdates(Pref.session_token!!, Pref.user_id!!, date)
    }

    fun fetchUnknownLocation(): Observable<UnknownReponseModel> {
        return apiService.getUnknownLocation(Pref.session_token!!, Pref.user_id!!)
    }

    fun submitLoc(loc: SubmitLocationInputModel): Observable<BaseResponse> {
        return apiService.submitLocation(loc)
    }
}