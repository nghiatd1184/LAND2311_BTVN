package com.nghiatd.bieudo

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Dimension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nghiatd.bieudo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding : ActivityMainBinding by lazy { requireNotNull(_binding) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var textColor =  0x101010   // Obtained from style attributes.

        @Dimension
        var textHeight = 10f  // Obtained from style attributes.

        val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = textHeight
        }

        val piePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textSize = textHeight
        }

        val shadowPaint = Paint(0).apply {
            color = 0x101010
            maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}