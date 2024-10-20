package com.nghiatd.mixic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.databinding.ItemCategoryBinding

class CategoryAdapter(private val onItemClick: (Category) -> Unit) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(object :
    DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding, private val onItemClick: (Category) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.tvName.text = category.name
            if (category.songs.isNotEmpty()) {
                loadImage(binding.img1, category.songs[0].image)
                loadImage(binding.img2, category.songs[1].image)
                loadImage(binding.img3, category.songs[2].image)
                loadImage(binding.img4, category.songs[3].image)
            }
            binding.root.setOnClickListener {
                onItemClick(category)
            }
        }
    }

    private fun loadImage(image: ImageView, url: String) {
        Glide.with(image)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade(500))
            .into(image)
    }

}