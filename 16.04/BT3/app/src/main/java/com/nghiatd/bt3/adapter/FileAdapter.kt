package com.nghiatd.bt3.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nghiatd.bt3.databinding.ItemFileBinding
import com.nghiatd.bt3.listener.OnFileItemClickListener
import com.nghiatd.bt3.model.File

class FileAdapter(
    private val files: List<File>,
    private val listener: OnFileItemClickListener
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun getItemCount(): Int = files.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(files[position])
    }

    inner class FileViewHolder(private val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.tvTitle.text = file.title
            binding.tvBody.text = file.body
            val fileName = file.title.plus(".txt")
            binding.root.setOnClickListener {
                listener.onClick(fileName)
            }
        }
    }
}

