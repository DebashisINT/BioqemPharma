package com.breezebioqempharma.features.photoReg.present

import com.breezebioqempharma.app.domain.ProspectEntity
import com.breezebioqempharma.features.photoReg.model.UserListResponseModel

interface DsStatusListner {
    fun getDSInfoOnLick(obj: ProspectEntity)
}