package com.example.cardflip.controller

import android.util.Log
import com.example.cardflip.R

class Controller private constructor() {
    companion object {
        private var instance: Controller? = null

        fun getInstance(): Controller {
            if (instance == null) {
                instance = Controller()
            }
            return instance!!
        }
    }

    private val cardList = arrayListOf(
        R.drawable.golden_1,
        R.drawable.golden_2,
        R.drawable.golden_3,
        R.drawable.golden_1,
        R.drawable.golden_2,
        R.drawable.golden_3
    )

    fun shuffleList() {
        cardList.shuffle()
    }


    private var count = 0
    private var firstCardId: Int? = null
    private var secondCardId: Int? = null

    fun getCount() : Int {
        return count
    }

    fun cardClick(int: Int) : Int {
        Log.d("dmm",count.toString())
        when (count) {
            0 -> {
                if (firstCardId == null) {
                    firstCardId = int
                    count ++
                    return cardList[int]
                }
            }
            1 -> {
                if (secondCardId == null) {
                    secondCardId = int
                    count ++
                    return cardList[int]
                }
            }
        }
        return 0
    }

    fun compareCard(): Boolean {
        if (firstCardId != null && secondCardId != null) {
            if (cardList[firstCardId!!] == cardList[secondCardId!!]) {
                return true
            }
        }
        return false
    }
}