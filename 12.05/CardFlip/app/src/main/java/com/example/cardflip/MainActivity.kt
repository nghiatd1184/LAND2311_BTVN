package com.example.cardflip

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.cardflip.controller.MusicController
import com.example.cardflip.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding : ActivityMainBinding by lazy { requireNotNull(_binding) }
    private val musicController = MusicController.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        supportFragmentManager
            .beginTransaction()
            .replace(binding.container.id, HomeFragment())
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicController.release()
        _binding = null
    }
}