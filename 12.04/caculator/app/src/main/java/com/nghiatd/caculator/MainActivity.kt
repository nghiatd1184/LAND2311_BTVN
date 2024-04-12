package com.nghiatd.caculator

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nghiatd.caculator.databinding.ActivityMainBinding
import kotlin.Boolean
import kotlin.Char
import kotlin.Double
import kotlin.Int
import kotlin.getValue
import kotlin.lazy
import kotlin.requireNotNull


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding by lazy { requireNotNull(_binding) }

    private var mathText = StringBuilder("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn0.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x0")
                } else if (addZeroCheck()) {
                    mathText.append("0")
                }
            }
            updateUI()
        }
        binding.btn1.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x1")
                } else if (addZeroCheck()) {
                    mathText.append("1")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("1")
                }
            }
            updateUI()
        }
        binding.btn2.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x2")
                } else if (addZeroCheck()) {
                    mathText.append("2")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("2")
                }
            }
            updateUI()
        }
        binding.btn3.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x3")
                } else if (addZeroCheck()) {
                    mathText.append("3")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("3")
                }
            }
            updateUI()
        }
        binding.btn4.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x4")
                } else if (addZeroCheck()) {
                    mathText.append("4")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("4")
                }
            }
            updateUI()
        }
        binding.btn5.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x5")
                } else if (addZeroCheck()) {
                    mathText.append("5")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("5")
                }
            }
            updateUI()
        }
        binding.btn6.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x6")
                } else if (addZeroCheck()) {
                    mathText.append("6")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("6")
                }
            }
            updateUI()
        }
        binding.btn7.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x7")
                } else if (addZeroCheck()) {
                    mathText.append("7")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("7")
                }
            }
            updateUI()
        }
        binding.btn8.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x8")
                } else if (addZeroCheck()) {
                    mathText.append("8")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("8")
                }
            }
            updateUI()
        }
        binding.btn9.setOnClickListener {
            if (checkLength()) {
                if (mathText.isNotEmpty() && !mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] == '%') {
                    mathText.append("x9")
                } else if (addZeroCheck()) {
                    mathText.append("9")
                } else {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("9")
                }
            }
            updateUI()
        }
        binding.btnAC.setOnClickListener {
            binding.tvTextLine1.text = ""
            binding.tvTextLine2.text = ""
            mathText.clear()
            binding.tvSymbolEqual.visibility = View.GONE
        }
        binding.btnPoint.setOnClickListener {
            if (checkLength()) {
                if (mathText.isEmpty() || (!mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] != '.')) {
                    mathText.append("0.")
                } else if (addPointCheck()) {
                    mathText.append(".")
                }
            }
            updateUI()
        }
        binding.btnPercent.setOnClickListener {
            if (checkLength()) {
                if (mathText.isEmpty() || !mathText[mathText.length - 1].isDigit()) {
                    Toast.makeText(this, "Định dạng không hợp lệ", Toast.LENGTH_SHORT).show()
                } else {
                    mathText.append("%")
                }
            }
            updateUI()
        }
        binding.btnDivide.setOnClickListener {
            if (checkLength()) {
                if (mathText.isEmpty()) {
                    Toast.makeText(this, "Định dạng không hợp lệ", Toast.LENGTH_SHORT).show()
                } else if (!mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] != '%' && mathText[mathText.length - 1] != '.') {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("/")
                } else {
                    mathText.append("/")
                }
            }
            updateUI()
        }
        binding.btnMulti.setOnClickListener {
            if (checkLength()) {
                if (mathText.isEmpty()) {
                    Toast.makeText(this, "Định dạng không hợp lệ", Toast.LENGTH_SHORT).show()
                } else if (!mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] != '%' && mathText[mathText.length - 1] != '.') {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("x")
                } else {
                    mathText.append("x")
                }
            }
            updateUI()
        }
        binding.btnMinus.setOnClickListener {
            if (checkLength()) {
                if (mathText.isEmpty()) {
                    Toast.makeText(this, "Định dạng không hợp lệ", Toast.LENGTH_SHORT).show()
                } else if (!mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] != '%' && mathText[mathText.length - 1] != '.') {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("-")
                } else {
                    mathText.append("-")
                }
            }
            updateUI()
        }
        binding.btnPlus.setOnClickListener {
            if (checkLength()) {
                if (mathText.isEmpty()) {
                    Toast.makeText(this, "Định dạng không hợp lệ", Toast.LENGTH_SHORT).show()
                } else if (!mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] != '%' && mathText[mathText.length - 1] != '.') {
                    mathText.deleteCharAt(mathText.length - 1)
                    mathText.append("+")
                } else {
                    mathText.append("+")
                }
            }
            updateUI()
        }
        binding.btnCalculate.setOnClickListener {
            calculate()
        }
    }

    private fun updateUI() {
        binding.tvSymbolEqual.visibility = View.INVISIBLE
        binding.tvTextLine2.text = mathText.toString()
    }

    private fun checkLength(): Boolean {
        if (mathText.length >= 40) {
            Toast.makeText(this, "Tối đa 40 ký tự", Toast.LENGTH_SHORT).show()
            while (mathText.length > 40) {
                mathText.deleteCharAt(40)
            }
            return false
        }
        return true
    }

    private fun addPointCheck(): Boolean {
        for (i in mathText.length - 1 downTo 0) {
            if (mathText[i] == '.') {
                return false
            } else if (mathText[i] == 'x' || mathText[i] == '-' || mathText[i] == 'x' || mathText[i] == '/') {
                return true
            }
        }
        return true
    }

    private fun addZeroCheck(): Boolean {
        if (mathText.isEmpty()) return true
        return if (mathText.length == 1 && mathText[0] == '0') {
            false
        } else if (mathText[mathText.length - 1] == '0' && !mathText[mathText.length - 2].isDigit() && mathText[mathText.length - 2] != '.') {
            false
        } else {
            true
        }
    }

    private fun calculate() {
        if (mathText.isEmpty() || (!mathText[mathText.length - 1].isDigit() && mathText[mathText.length - 1] != '%')) {
            Toast.makeText(this, "Định dạng không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }
        var result = 0.0
        var percentCheck = false
        val numberList = arrayListOf<Double>()
        val symbolList = arrayListOf<Char>()
        val number = StringBuilder("")
        var count = 0
        mathText.forEach {
            count++
            if (it.isDigit() || it == '.') {
                number.append(it)
            } else if (it == '%') {
                percentCheck = true
            } else {
                if (percentCheck) {
                    numberList.add(number.toString().toDouble() * 0.01)
                    percentCheck = false
                } else {
                    numberList.add(number.toString().toDouble())
                }
                number.clear()
                symbolList.add(it)
            }

            if (count == mathText.length) {
                if (percentCheck) {
                    numberList.add(number.toString().toDouble() * 0.01)
                } else {
                    numberList.add(number.toString().toDouble())
                }
            }
        }
        var multiIndex: Int
        var divideIndex: Int
        var temp: Double
        while (symbolList.contains('x') || symbolList.contains('/')) {
            multiIndex = symbolList.indexOf('x')
            divideIndex = symbolList.indexOf('/')
            if (divideIndex == -1 || (multiIndex < divideIndex && multiIndex != -1)) {
                temp = numberList[multiIndex] * numberList[multiIndex + 1]
                numberList.removeAt(multiIndex)
                numberList.removeAt(multiIndex)
                numberList.add(multiIndex, temp)
                symbolList.removeAt(multiIndex)
            } else {
                temp = numberList[divideIndex] / numberList[divideIndex + 1]
                numberList.removeAt(divideIndex)
                numberList.removeAt(divideIndex)
                numberList.add(divideIndex, temp)
                symbolList.removeAt(divideIndex)
            }
        }
        result += numberList[0]
        count = 0
        symbolList.forEach {
            count++
            if (it == '+') {
                result += numberList[count]
            } else {
                result -= numberList[count]
            }
        }
        binding.tvTextLine1.text = binding.tvTextLine2.text
        mathText.clear()
        mathText.append(result)
        if (mathText[mathText.length - 1] == '0' && mathText[mathText.length - 2] == '.') {
            mathText.deleteCharAt(mathText.length - 1)
            mathText.deleteCharAt(mathText.length - 1)
        }
        updateUI()
        binding.tvSymbolEqual.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}