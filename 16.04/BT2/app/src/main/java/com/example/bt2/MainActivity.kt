package com.example.bt2

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bt2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding : ActivityMainBinding by lazy { requireNotNull(_binding) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            val input = assets.open("account.txt")
            val size = input.available()
            val buffer = ByteArray(size)
            input.read(buffer)
            input.close()
            val data = String(buffer)
            val editable : Editable = SpannableStringBuilder(data)
            binding.edt.text = editable
        }
    }
}