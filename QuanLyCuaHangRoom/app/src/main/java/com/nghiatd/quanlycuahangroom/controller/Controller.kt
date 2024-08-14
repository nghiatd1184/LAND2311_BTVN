package com.nghiatd.quanlycuahangroom.controller

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.nghiatd.quanlycuahangroom.adapter.ProductAdapter
import com.nghiatd.quanlycuahangroom.entity.Product
import com.nghiatd.quanlycuahangroom.handler.DatabaseHandler
import kotlin.concurrent.thread

class Controller private constructor(context: Context) {
    companion object {
        private var instance: Controller? = null
        fun getInstance(context: Context): Controller {
            if (instance == null) {
                instance = Controller(context)
            }
            return instance!!
        }
    }

    private val databaseHandler: DatabaseHandler by lazy { DatabaseHandler.getInstance(context) }

    fun getAllProducts(): List<Product> {
        val products = arrayListOf<Product>()
        thread(start = true) {
            products.addAll(databaseHandler.ProductDao().getAll())
        }
        return products
    }

    private fun getInfo(
        context: Context,
        idEdt: EditText,
        nameEdt: EditText,
        stockEdt: EditText,
        priceEdt: EditText
    ): Product? {
        val id: String
        val name: String
        val stock: Int
        val price: Double

        if (idEdt.text.isEmpty()) {
            Toast.makeText(context, "Please enter ID", Toast.LENGTH_SHORT).show()
            idEdt.requestFocus()
            return null
        } else {
            id = idEdt.text.toString()
            if (nameEdt.text.isEmpty()) {
                Toast.makeText(context, "Please enter name", Toast.LENGTH_SHORT).show()
                nameEdt.requestFocus()
                return null
            } else {
                name = nameEdt.text.toString()
                if (stockEdt.text.isEmpty()) {
                    Toast.makeText(context, "Please enter stock", Toast.LENGTH_SHORT).show()
                    stockEdt.requestFocus()
                    return null
                } else if (stockEdt.text.toString().toInt() < 0) {
                    Toast.makeText(context, "Please enter valid stock", Toast.LENGTH_SHORT).show()
                    stockEdt.setText("")
                    stockEdt.requestFocus()
                    return null
                } else {
                    stock = stockEdt.text.toString().toInt()
                    if (priceEdt.text.isEmpty()) {
                        Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show()
                        priceEdt.requestFocus()
                        return null
                    } else if (priceEdt.text.toString().toDouble() < 0) {
                        Toast.makeText(context, "Please enter valid price", Toast.LENGTH_SHORT)
                            .show()
                        priceEdt.setText("")
                        priceEdt.requestFocus()
                        return null
                    } else {
                        price = priceEdt.text.toString().toDouble()
                    }
                }
            }
        }
        idEdt.setText("")
        nameEdt.setText("")
        stockEdt.setText("")
        priceEdt.setText("")
        return Product(id, name, stock, price)
    }

    fun addProduct(
        context: Context,
        idEdt: EditText,
        nameEdt: EditText,
        stockEdt: EditText,
        priceEdt: EditText,
        adapter: ProductAdapter
    ) {
        val product: Product? = getInfo(context, idEdt, nameEdt, stockEdt, priceEdt)
        if (product != null) {
            val isExist = getAllProducts().find { it.id == product.id }
            if (isExist != null) {
                Toast.makeText(context, "ID already exists!", Toast.LENGTH_SHORT).show()
                return
            } else {
                thread(start = true) {
                    databaseHandler.ProductDao().insert(product)
                }
                adapter.notifyDataSetChanged()
                Toast.makeText(context, "Add successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editProduct(
        context: Context,
        product: Product,
        adapter: ProductAdapter,
        idEdt: EditText,
        nameEdt: EditText,
        stockEdt: EditText,
        priceEdt: EditText,
        btnAdd: Button,
        btnUpdate: Button
    ) {
        idEdt.setText(product.id)
        nameEdt.setText(product.name)
        stockEdt.setText(product.inventoryQuantity.toString())
        priceEdt.setText(product.price.toString())
        btnAdd.visibility = View.GONE
        btnUpdate.visibility = View.VISIBLE
        btnUpdate.setOnClickListener {
            val newProduct: Product? = getInfo(context, idEdt, nameEdt, stockEdt, priceEdt)
            if (newProduct != null) {
                if (newProduct.id == product.id) {
                    thread(start = true) {
                        databaseHandler.ProductDao().update(newProduct)
                    }
                } else {
                    thread(start = true) {
                        databaseHandler.ProductDao().delete(product)
                        databaseHandler.ProductDao().insert(newProduct)
                    }

                }
                adapter.notifyDataSetChanged()
                Toast.makeText(context, "Update successfully!", Toast.LENGTH_SHORT).show()
                btnAdd.visibility = View.VISIBLE
                btnUpdate.visibility = View.GONE
            }
        }
    }

    fun showOnLongClickDialog(
        context: Context,
        product: Product,
        adapter: ProductAdapter,
        idEdt: EditText,
        nameEdt: EditText,
        stockEdt: EditText,
        priceEdt: EditText,
        btnAdd: Button,
        btnUpdate: Button
    ) {
        var selectedItem = 0
        val items = arrayOf("Edit Product Info", "Delete Product")
        AlertDialog.Builder(context).apply {
            setTitle("Product Info: ${product.id} - ${product.name}")
            setCancelable(false)
            setSingleChoiceItems(items, 0) { _, which ->
                selectedItem = which
            }
            setPositiveButton("OK") { _, _ ->
                when (selectedItem) {
                    0 -> editProduct(
                        context,
                        product,
                        adapter,
                        idEdt,
                        nameEdt,
                        stockEdt,
                        priceEdt,
                        btnAdd,
                        btnUpdate
                    )

                    1 -> showDeleteDialog(context, product, adapter)
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }

    private fun showDeleteDialog(
        context: Context,
        product: Product,
        adapter: ProductAdapter
    ) {
        AlertDialog.Builder(context).apply {
            setTitle("Alert!")
            setCancelable(false)
            setMessage("Do you want to delete this product?")
            setPositiveButton("Delete") { _, _ ->
                thread(start = true) {
                    databaseHandler.ProductDao().delete(product)
                }
                adapter.notifyDataSetChanged()
                Toast.makeText(context, "Delete successfully!", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }


}