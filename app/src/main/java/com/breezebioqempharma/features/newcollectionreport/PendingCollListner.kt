package com.breezebioqempharma.features.newcollectionreport

import com.breezebioqempharma.features.photoReg.model.UserListResponseModel

interface PendingCollListner {
    fun getUserInfoOnLick(obj: PendingCollData)
}