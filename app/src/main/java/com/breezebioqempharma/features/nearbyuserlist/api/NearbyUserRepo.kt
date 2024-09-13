package com.breezebioqempharma.features.nearbyuserlist.api

import com.breezebioqempharma.app.Pref
import com.breezebioqempharma.features.nearbyuserlist.model.NearbyUserResponseModel
import com.breezebioqempharma.features.newcollection.model.NewCollectionListResponseModel
import com.breezebioqempharma.features.newcollection.newcollectionlistapi.NewCollectionListApi
import io.reactivex.Observable

class NearbyUserRepo(val apiService: NearbyUserApi) {
    fun nearbyUserList(): Observable<NearbyUserResponseModel> {
        return apiService.getNearbyUserList(Pref.session_token!!, Pref.user_id!!)
    }
}