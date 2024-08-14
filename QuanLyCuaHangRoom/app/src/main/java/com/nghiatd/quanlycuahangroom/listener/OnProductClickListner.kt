package com.nghiatd.quanlycuahangroom.listener

import com.nghiatd.quanlycuahangroom.entity.Product

interface OnProductClickListner {
    fun onProductLongClick(product: Product, position: Int)
}