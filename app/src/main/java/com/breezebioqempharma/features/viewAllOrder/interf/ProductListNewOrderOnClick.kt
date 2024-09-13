package com.breezebioqempharma.features.viewAllOrder.interf

import com.breezebioqempharma.app.domain.NewOrderGenderEntity
import com.breezebioqempharma.app.domain.NewOrderProductEntity

interface ProductListNewOrderOnClick {
    fun productListOnClick(product: NewOrderProductEntity)
}