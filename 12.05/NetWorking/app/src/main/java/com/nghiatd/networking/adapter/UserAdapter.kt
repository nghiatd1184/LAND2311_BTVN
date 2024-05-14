package com.nghiatd.networking.adapter

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nghiatd.networking.databinding.ItemUserBinding
import com.nghiatd.networking.model.User
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread

class UserAdapter(private val list: List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.id.text = user.id.toString()
            binding.login.text = user.login
            binding.nodeId.text = user.nodeId
            if (user.bitmap == null) {
                val handler = Handler(Looper.getMainLooper()) {
                    val byteArray = it.data.getByteArray("bitmap")
                    byteArray?.let {
                        val bitmap = BitmapFactory.decodeByteArray(it,0,it.size)
                        binding.avatar.setImageBitmap(bitmap)
                        user.bitmap = bitmap
                    }
                    return@Handler true
                }

                thread(start = true) {
                    val client = OkHttpClient.Builder().build()
                    val builder = Request.Builder()
                    builder.url(user.avatarUrl)
                    val byteArray = client.newCall(builder.build()).execute().body?.bytes()
                    val msg = handler.obtainMessage()
                    msg.data = Bundle().apply {
                        putByteArray("bitmap",byteArray)
                    }
                    handler.sendMessage(msg)
                }
            } else {
                binding.avatar.setImageBitmap(user.bitmap)
            }

        }
    }
}



