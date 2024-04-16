package com.nghiatd.caculator

import java.util.Arrays
import java.util.Stack

class Controller private constructor() {
    companion object {
        private var instance: Controller? = null
        fun getInstance(): Controller {
            if (instance == null) {
                instance = Controller()
            }
            return requireNotNull(instance)
        }

        val phepTinh = StringBuilder()
        fun addValue(value: String) {
            phepTinh.append(value)
        }

        private fun chuyenDoiTrungToThanhHauTo(chuoiBanDau : String) : String {
            val s =Stack<String>()
            for (i in chuoiBanDau.indices) {
                val c:Char=chuoiBanDau[i]
                if (!kiemTraToanHang(c)) {

                } else {

                }
            }

            val result = ""
            return result
        }

        private fun kiemTraToanHang(c:Char):Boolean {
            val operator= charArrayOf('+','-','*','/')
            return Arrays.binarySearch(operator,c) != -1
        }

        fun tinhKetQua(exp :String) {
            val stack = Stack<Double>()

            for (i in exp.indices) {
                val c:Char = exp[i]
                if (Character.isDigit(c)) {
                    stack.push((c.code - '0'.code).toDouble())
                } else {
                    val number1 = stack.pop()
                    val number2 = stack.pop()
                    when (c) {
                        '+' -> stack.push(number2 + number1)
                        '-' -> stack.push(number2 - number1)
                        '*' -> stack.push(number2 * number1)
                        '/' -> stack.push(number2 / number1)
                    }
                }
            }

        }
    }
}