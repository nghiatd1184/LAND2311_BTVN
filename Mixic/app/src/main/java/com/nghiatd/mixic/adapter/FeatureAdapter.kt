package com.nghiatd.mixic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nghiatd.mixic.data.model.Feature
import com.nghiatd.mixic.databinding.ItemFeatureBinding

class FeatureAdapter(private val features: List<Feature>) : RecyclerView.Adapter<FeatureAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemFeatureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: Feature) {
            Glide.with(binding.imgFeature)
                .load(feature.image)
                .into(binding.imgFeature)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(features[position])
    }

    override fun getItemCount(): Int = features.size
}