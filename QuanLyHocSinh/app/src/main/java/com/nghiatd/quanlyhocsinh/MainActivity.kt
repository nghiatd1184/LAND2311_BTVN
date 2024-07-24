package com.nghiatd.quanlyhocsinh

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.nghiatd.quanlyhocsinh.databinding.ActivityMainBinding
import com.nghiatd.quanlyhocsinh.fragment.Screen1Fragment
import com.nghiatd.quanlyhocsinh.fragment.Screen2Fragment

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding : ActivityMainBinding by lazy { requireNotNull(_binding) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Screen1Fragment())
        binding.apply {
            navigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.navigation_add -> {
                        replaceFragment(Screen1Fragment())
                        true
                    }
                    R.id.navigation_list -> {
                        replaceFragment(Screen2Fragment())
                        true
                    } else -> false
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}