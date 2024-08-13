package com.nghiatd.quanlycuahangroom

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nghiatd.quanlycuahangroom.databinding.ActivityMainBinding
import com.nghiatd.quanlycuahangroom.handler.DatabaseHandler

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding : ActivityMainBinding by lazy { requireNotNull(_binding) }
    private val databaseHandler by lazy { DatabaseHandler.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}