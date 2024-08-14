package com.nghiatd.quanlycuahangroom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghiatd.quanlycuahangroom.adapter.ProductAdapter
import com.nghiatd.quanlycuahangroom.controller.Controller
import com.nghiatd.quanlycuahangroom.databinding.ActivityMainBinding
import com.nghiatd.quanlycuahangroom.entity.Product
import com.nghiatd.quanlycuahangroom.listener.OnProductClickListner

class MainActivity : AppCompatActivity(), OnProductClickListner {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding by lazy { requireNotNull(_binding) }
    private val controller by lazy { Controller.getInstance(this) }
    private val adapter: ProductAdapter by lazy {
        ProductAdapter(
            controller.getAllProducts(),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnAdd.setOnClickListener {
                controller.addProduct(
                    this@MainActivity,
                    etProductId,
                    etProductName,
                    etProductInventoryQuantity,
                    etProductPrice,
                    adapter
                )
            }
            rvProduct.apply {
                adapter = this@MainActivity.adapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onProductLongClick(product: Product, position : Int) {
        controller.showOnLongClickDialog(
            this,
            product,
            adapter,
            binding.etProductId,
            binding.etProductName,
            binding.etProductInventoryQuantity,
            binding.etProductPrice,
            binding.btnAdd,
            binding.btnUpdate
        )
    }
}