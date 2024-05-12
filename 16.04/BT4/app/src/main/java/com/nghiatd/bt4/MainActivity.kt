package com.nghiatd.bt4

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nghiatd.bt4.controller.Manager
import com.nghiatd.bt4.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding by lazy { requireNotNull(_binding) }
    private val manager : Manager by lazy { Manager.getIns() }
    private var number: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPref = this.getSharedPreferences("tvValue", Context.MODE_PRIVATE)

        binding.apply {
            tvMain.setBackgroundColor(
                Color.parseColor(
                    sharedPref.getString(
                        "colorValue",
                        "#A6A6A6"
                    )
                )
            )
            number = sharedPref.getInt("numberValue", 0)
            tvMain.text = number.toString()
            tvBlack.setOnClickListener {
                sharedPref.edit()
                    .putString("colorValue", "#000000")
                    .apply()

                tvMain.setBackgroundColor(Color.parseColor("#000000"))
            }

            tvRed.setOnClickListener {
                sharedPref.edit()
                    .putString("colorValue", "#BC0000")
                    .apply()
                tvMain.setBackgroundColor(Color.parseColor("#BC0000"))
            }

            tvBlue.setOnClickListener {
                sharedPref.edit()
                    .putString("colorValue", "#0D76C6")
                    .apply()
                tvMain.setBackgroundColor(Color.parseColor("#0D76C6"))
            }
            tvGreen.setOnClickListener {
                sharedPref.edit()
                    .putString("colorValue", "#10810C")
                    .apply()
                tvMain.setBackgroundColor(Color.parseColor("#10810C"))
            }

            btnCount.setOnClickListener {
                number++
                sharedPref.edit()
                    .putInt("numberValue", number)
                    .apply()
                tvMain.text = number.toString()
            }

            btnReset.setOnClickListener {
                tvMain.setBackgroundColor(Color.parseColor("#A6A6A6"))
                tvMain.text = "0"
                sharedPref.edit()
                    .putString("colorValue", "#A6A6A6")
                    .putInt("numberValue", 0)
                    .apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}