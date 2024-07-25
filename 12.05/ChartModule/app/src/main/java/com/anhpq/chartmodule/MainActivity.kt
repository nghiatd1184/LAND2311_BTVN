package com.anhpq.chartmodule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anhpq.chartmodule.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    // 1. xac dinh model
    // 2. neu co data moi thi se update nhu nao?
    // 3. viec cap nhat nhieu data cung luc co gay ra van de gi ko?
    // 4. Thao tac tren cac ban ve nhu nao?

    // BTVN: Thu 5 minh chua tiep
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            chartView.addCharts(
                listOf(
                    Chart(id = 1, name = "Hà Nội", value = 20.0F),
                    Chart(id = 2, name = "Hà Nam", value = 12.0F),
                    Chart(id = 3, name = "Thanh Hoá", value = 43.0F),
                    Chart(id = 4, name = "TP.HCM", value = 50.0F)
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}