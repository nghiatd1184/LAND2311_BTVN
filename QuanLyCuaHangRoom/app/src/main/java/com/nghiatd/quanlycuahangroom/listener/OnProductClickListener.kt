package com.nghiatd.quanlycuahangroom.listener

import com.nghiatd.quanlycuahangroom.entity.Product

interface OnProductClickListener {
    fun onProductLongClick(product: Product, position: Int)
}