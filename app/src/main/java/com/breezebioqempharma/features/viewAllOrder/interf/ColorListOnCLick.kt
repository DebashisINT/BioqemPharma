package com.breezebioqempharma.features.viewAllOrder.interf

import com.breezebioqempharma.app.domain.NewOrderGenderEntity
import com.breezebioqempharma.features.viewAllOrder.model.ProductOrder

interface ColorListOnCLick {
    fun colorListOnCLick(size_qty_list: ArrayList<ProductOrder>, adpPosition:Int)
}