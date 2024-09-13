package com.breezebioqempharma.features.viewAllOrder.interf

import com.breezebioqempharma.app.domain.NewOrderColorEntity
import com.breezebioqempharma.app.domain.NewOrderProductEntity

interface ColorListNewOrderOnClick {
    fun productListOnClick(color: NewOrderColorEntity)
}