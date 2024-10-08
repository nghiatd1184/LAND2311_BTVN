package com.nghiatd.quanlycuahangroom.controller

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.nghiatd.quanlycuahangroom.R
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
    private val products: ArrayList<Product> = arrayListOf()


    fun getAllProducts(): List<Product> {
        thread(start = true) {
            products.addAll(databaseHandler.ProductDao().getAll())
        }.join()
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
                    stockEdt.setText(R.string.blank)
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
                        priceEdt.setText(R.string.blank)
                        priceEdt.requestFocus()
                        return null
                    } else {
                        price = priceEdt.text.toString().toDouble()
                    }
                }
            }
        }
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
            var isExist: Product? = null
            thread {
                isExist = databaseHandler.ProductDao().getProductById(product.id)
            }.join()
            if (isExist != null) {
                Toast.makeText(context, "ID already exists!", Toast.LENGTH_SHORT).show()
                return
            } else {
                thread(start = true) {
                    databaseHandler.ProductDao().insert(product)
                }
                products.add(product)
                adapter.notifyItemInserted(adapter.itemCount)
                Toast.makeText(context, "Add successfully!", Toast.LENGTH_SHORT).show()
                idEdt.setText(R.string.blank)
                nameEdt.setText(R.string.blank)
                stockEdt.setText(R.string.blank)
                priceEdt.setText(R.string.blank)
            }
        }
    }

    private fun editProduct(
        context: Context,
        product: Product,
        position: Int,
        adapter: ProductAdapter,
        idEdt: EditText,
        nameEdt: EditText,
        stockEdt: EditText,
        priceEdt: EditText,
        btnAdd: Button,
        btnUpdate: Button,
        btnCancel: Button
    ) {
        idEdt.setText(product.id)
        idEdt.isEnabled = false
        idEdt.keyListener = null
        nameEdt.setText(product.name)
        stockEdt.setText(product.inventoryQuantity.toString())
        priceEdt.setText(product.price.toString())
        btnAdd.visibility = View.GONE
        btnUpdate.visibility = View.VISIBLE
        btnCancel.visibility = View.VISIBLE
        btnCancel.setOnClickListener {
            idEdt.setText(R.string.blank)
            nameEdt.setText(R.string.blank)
            stockEdt.setText(R.string.blank)
            priceEdt.setText(R.string.blank)
            btnAdd.visibility = View.VISIBLE
            btnUpdate.visibility = View.GONE
            btnCancel.visibility = View.GONE
        }
        btnUpdate.setOnClickListener {
            val newProduct: Product? = getInfo(context, idEdt, nameEdt, stockEdt, priceEdt)
            if (newProduct != null) {
                thread(start = true) {
                    databaseHandler.ProductDao().update(newProduct)
                }.join()
                products.removeAt(position)
                products.add(position, newProduct)
                idEdt.isEnabled = true
                idEdt.keyListener = idEdt.keyListener
                adapter.notifyItemChanged(position)
                Toast.makeText(context, "Update successfully!", Toast.LENGTH_SHORT).show()
                idEdt.setText(R.string.blank)
                nameEdt.setText(R.string.blank)
                stockEdt.setText(R.string.blank)
                priceEdt.setText(R.string.blank)
                btnAdd.visibility = View.VISIBLE
                btnUpdate.visibility = View.GONE
                btnCancel.visibility = View.GONE
            }
        }
    }

    fun showOnLongClickDialog(
        context: Context,
        product: Product,
        position: Int,
        adapter: ProductAdapter,
        idEdt: EditText,
        nameEdt: EditText,
        stockEdt: EditText,
        priceEdt: EditText,
        btnAdd: Button,
        btnUpdate: Button,
        btnCancel: Button
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
                        position,
                        adapter,
                        idEdt,
                        nameEdt,
                        stockEdt,
                        priceEdt,
                        btnAdd,
                        btnUpdate,
                        btnCancel
                    )

                    1 -> showDeleteDialog(context, product, position, adapter)
                }
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }

    private fun showDeleteDialog(
        context: Context,
        product: Product,
        position: Int,
        adapter: ProductAdapter
    ) {
        AlertDialog.Builder(context).apply {
            setTitle("Alert!")
            setCancelable(false)
            setMessage("Are you sure to delete this product?")
            setPositiveButton("Delete") { _, _ ->
                thread(start = true) {
                    databaseHandler.ProductDao().delete(product)
                }.join()
                products.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(context, "Delete successfully!", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel") { _, _ -> }
            show()
        }
    }
}