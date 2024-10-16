package com.nghiatd.mixic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.data.model.Feature
import com.nghiatd.mixic.databinding.ItemFeatureBinding

class FeatureAdapter(private val features: List<Feature>, private val onItemClick: (Feature) -> Unit) : RecyclerView.Adapter<FeatureAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemFeatureBinding, private val onItemClick: (Feature) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: Feature) {
            Glide.with(binding.imgFeature)
                .load(feature.image)
                .apply(RequestOptions().transform(RoundedCorners(15)))
                .into(binding.imgFeature)
            binding.root.setOnClickListener {
                onItemClick(feature)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(features[position])
    }

    override fun getItemCount(): Int = features.size
}