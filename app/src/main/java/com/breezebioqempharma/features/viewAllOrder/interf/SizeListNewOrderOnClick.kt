package com.breezebioqempharma.features.viewAllOrder.interf

import com.breezebioqempharma.app.domain.NewOrderProductEntity
import com.breezebioqempharma.app.domain.NewOrderSizeEntity

interface SizeListNewOrderOnClick {
    fun sizeListOnClick(size: NewOrderSizeEntity)
}