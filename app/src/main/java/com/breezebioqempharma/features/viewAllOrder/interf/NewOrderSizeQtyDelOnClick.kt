package com.breezebioqempharma.features.viewAllOrder.interf

import com.breezebioqempharma.app.domain.NewOrderGenderEntity
import com.breezebioqempharma.features.viewAllOrder.model.ProductOrder
import java.text.FieldPosition

interface NewOrderSizeQtyDelOnClick {
    fun sizeQtySelListOnClick(product_size_qty: ArrayList<ProductOrder>)
    fun sizeQtyListOnClick(product_size_qty: ProductOrder,position: Int)
}