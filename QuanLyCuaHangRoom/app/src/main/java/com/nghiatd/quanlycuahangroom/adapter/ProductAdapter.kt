package com.nghiatd.quanlycuahangroom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nghiatd.quanlycuahangroom.databinding.ItemProductBinding
import com.nghiatd.quanlycuahangroom.entity.Product
import com.nghiatd.quanlycuahangroom.listener.OnProductClickListener

class ProductAdapter(
    private val products: List<Product>,
    private val onProductClickListener: OnProductClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, position: Int) {
            binding.apply {
                tvId.text = product.id
                tvName.text = product.name
                tvStock.text = product.inventoryQuantity.toString()
                tvPrice.text = product.price.toString()
                ivProduct.setImageResource(product.resID)
                root.setOnLongClickListener {
                    onProductClickListener.onProductLongClick(product, position)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], position)
    }
}