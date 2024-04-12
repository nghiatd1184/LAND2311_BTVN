package com.example.equation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.equation.databinding.ActivityMainBinding
import kotlin.math.abs
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding : ActivityMainBinding by lazy { requireNotNull(_binding) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnCal.setOnClickListener{
            calculate()
        }

    }
    private fun checkValue(value:String):Boolean {
        for (item in 0..<value.length) {
            if (item == 0) {
               if (!value[item].isDigit() &&value[item]!='-') return false
            } else {
                if (!value[item].isDigit()) return false
            }
        }
        return true
    }
    private fun calculate() {
        val aValue = binding.editA.text.toString()
        val a:Int
        val b:Int
        val c:Int
        if (checkValue(aValue)) {
            a = aValue.toInt()
        } else {
            Toast.makeText(this,"Vui lòng nhập đúng định dạng", Toast.LENGTH_SHORT).show()
            return
        }

        val bValue = binding.editB.text.toString()
        if (checkValue(bValue)) {
            b = bValue.toInt()
        } else {
            Toast.makeText(this,"Vui lòng nhập đúng định dạng", Toast.LENGTH_SHORT).show()
            return
        }

        val cValue = binding.editC.text.toString()
        if (checkValue(cValue)) {
            c = cValue.toInt()
        } else {
            Toast.makeText(this,"Vui lòng nhập đúng định dạng", Toast.LENGTH_SHORT).show()
            return
        }

        if (a+b+c == 0) {
            binding.tvResult.text = "Phương trình có nghiệm: x1 = 1, x2 = ${(c.toDouble()/a.toDouble())}"
            return
        } else if (a-b+c == 0) {
            binding.tvResult.text = "Phương trình có nghiệm: x1 = 1, x2 = ${(-c.toDouble()/a.toDouble())}"
            return
        } else {
            val delta = b*b - 4*a*c
            if (delta<0){
                binding.tvResult.text = "Phương trình vô nghiệm"
                return
            } else if (delta == 0) {
                binding.tvResult.text = "Phương trình có nghiệm kép: x1 = x2 = ${-b.toDouble()/(2.0*a.toDouble())}"
                return
            } else {
                binding.tvResult.text = "Phương trình có 2 nghiệm: x1 = ${(-b.toDouble()+sqrt(delta.toDouble()))/(2.0*a.toDouble())}, x2 = ${(-b.toDouble()-sqrt(delta.toDouble()))/(2.0*a.toDouble())}"
            }
        }
    }
}