package com.breezebioqempharma.features.viewAllOrder.model

import com.breezebioqempharma.app.domain.NewOrderColorEntity
import com.breezebioqempharma.app.domain.NewOrderGenderEntity
import com.breezebioqempharma.app.domain.NewOrderProductEntity
import com.breezebioqempharma.app.domain.NewOrderSizeEntity
import com.breezebioqempharma.features.stockCompetetorStock.model.CompetetorStockGetDataDtls

class NewOrderDataModel {
    var status:String ? = null
    var message:String ? = null
    var Gender_list :ArrayList<NewOrderGenderEntity>? = null
    var Product_list :ArrayList<NewOrderProductEntity>? = null
    var Color_list :ArrayList<NewOrderColorEntity>? = null
    var size_list :ArrayList<NewOrderSizeEntity>? = null
}

