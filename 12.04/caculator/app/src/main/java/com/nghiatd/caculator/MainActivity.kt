package com.nghiatd.caculator

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nghiatd.caculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding : ActivityMainBinding? = null
    private val binding : ActivityMainBinding by lazy { requireNotNull(_binding) }

    private var mathText = StringBuilder("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn0.setOnClickListener {
            mathText.append("0")
            updateUI()
        }
        binding.btn1.setOnClickListener {
            mathText.append("1")
            updateUI()
        }
        binding.btn2.setOnClickListener {
            mathText.append("2")
            updateUI()
        }
        binding.btn3.setOnClickListener {
            mathText.append("3")
            updateUI()
        }
        binding.btn4.setOnClickListener {
            mathText.append("4")
            updateUI()
        }
        binding.btn5.setOnClickListener {
            mathText.append("5")
            updateUI()
        }
        binding.btn6.setOnClickListener {
            mathText.append("6")
            updateUI()
        }
        binding.btn7.setOnClickListener {
            mathText.append("7")
            updateUI()
        }
        binding.btn8.setOnClickListener {
            mathText.append("8")
            updateUI()
        }
        binding.btn9.setOnClickListener {
            mathText.append("9")
            updateUI()
        }
        binding.btnAC.setOnClickListener {
            binding.tvTextLine1.text = ""
            binding.tvTextLine2.text = ""
            mathText.clear()
            binding.tvSymbolEqual.visibility = View.GONE
        }
        binding.btnPoint.setOnClickListener {
            if (mathText.isEmpty() || (!mathText[mathText.length-1].isDigit() && mathText[mathText.length-1] != '.')) {
                mathText.append("0.")
            }else if (addPointCheck()) {
                mathText.append(".")
            }
            updateUI()
        }
        binding.btnPercent.setOnClickListener {
            if (!mathText[mathText.length-1].isDigit() && mathText[mathText.length-1] != '.') {
                Toast.makeText(this,"Định dạng không hợp lệ",Toast.LENGTH_SHORT).show()
            } else {
                mathText.append("%")
            }
            updateUI()
        }
        binding.btnDivide.setOnClickListener {
            if (!mathText[mathText.length-1].isDigit() && mathText[mathText.length-1] != '%' && mathText[mathText.length-1] != '.') {
                mathText.deleteCharAt(mathText.length-1)
                mathText.append("/")
            } else {
                mathText.append("/")
            }
            updateUI()
        }
        binding.btnMulti.setOnClickListener {
            if (!mathText[mathText.length-1].isDigit() && mathText[mathText.length-1] != '%' && mathText[mathText.length-1] != '.') {
                mathText.deleteCharAt(mathText.length-1)
                mathText.append("x")
            } else {
                mathText.append("x")
            }
            updateUI()
        }
        binding.btnMinus.setOnClickListener {
            if (!mathText[mathText.length-1].isDigit() && mathText[mathText.length-1] != '%' && mathText[mathText.length-1] != '.') {
                mathText.deleteCharAt(mathText.length-1)
                mathText.append("-")
            } else {
                mathText.append("-")
            }
            updateUI()
        }
        binding.btnPlus.setOnClickListener {
            if (!mathText[mathText.length-1].isDigit() && mathText[mathText.length-1] != '%' && mathText[mathText.length-1] != '.') {
                mathText.deleteCharAt(mathText.length-1)
                mathText.append("+")
            } else {
                mathText.append("+")
            }
            updateUI()
        }
    }

    private fun updateUI() {
        binding.tvTextLine2.text = mathText.toString()
    }

    private fun addPointCheck() : Boolean {
        for (i in mathText.length-1 downTo  0){
            if (mathText[i] == '.') {
                return false
            } else if (mathText[i] == 'x' || mathText[i] == '-' || mathText[i] == 'x' || mathText[i] == '/') {
                break
            }
        }
        return true
    }
}