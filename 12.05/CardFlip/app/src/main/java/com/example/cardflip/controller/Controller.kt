package com.example.cardflip.controller

import android.os.Handler
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.example.cardflip.R
import com.example.cardflip.adapter.CardAdapter
import kotlin.concurrent.thread


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

    private var count = 0
    private var cards = arrayListOf<Int>()
    private var cardData = arrayListOf<Int>()
    private val list6 = arrayListOf(
        R.drawable.golden_1,
        R.drawable.golden_2,
        R.drawable.golden_3,
        R.drawable.golden_1,
        R.drawable.golden_2,
        R.drawable.golden_3
    )
    private val list16 = arrayListOf(
        R.drawable.golden_1,
        R.drawable.golden_2,
        R.drawable.golden_3,
        R.drawable.golden_4,
        R.drawable.golden_5,
        R.drawable.golden_6,
        R.drawable.purple_1,
        R.drawable.purple_2,
        R.drawable.golden_1,
        R.drawable.golden_2,
        R.drawable.golden_3,
        R.drawable.golden_4,
        R.drawable.golden_5,
        R.drawable.golden_6,
        R.drawable.purple_1,
        R.drawable.purple_2
    )
    private val list36 = arrayListOf(
        R.drawable.golden_1,
        R.drawable.golden_2,
        R.drawable.golden_3,
        R.drawable.golden_4,
        R.drawable.golden_5,
        R.drawable.golden_6,
        R.drawable.purple_1,
        R.drawable.purple_2,
        R.drawable.purple_3,
        R.drawable.purple_4,
        R.drawable.purple_5,
        R.drawable.purple_6,
        R.drawable.purple_7,
        R.drawable.purple_8,
        R.drawable.green_1,
        R.drawable.green_2,
        R.drawable.green_3,
        R.drawable.green_4,
        R.drawable.golden_1,
        R.drawable.golden_2,
        R.drawable.golden_3,
        R.drawable.golden_4,
        R.drawable.golden_5,
        R.drawable.golden_6,
        R.drawable.purple_1,
        R.drawable.purple_2,
        R.drawable.purple_3,
        R.drawable.purple_4,
        R.drawable.purple_5,
        R.drawable.purple_6,
        R.drawable.purple_7,
        R.drawable.purple_8,
        R.drawable.green_1,
        R.drawable.green_2,
        R.drawable.green_3,
        R.drawable.green_4
    )
    private var firstPosition: Int? = null
    private var secondPosition: Int? = null
    private var isCompare = false
    private var lifeCount = 3
    private var gameMode = 0
    private var column = 0

    fun getLifeCount(): Int {
        return lifeCount
    }

    fun setGameMote(int: Int) {
        gameMode = int
    }

    fun getLayoutColumn(): Int {
        return column
    }

    fun getCards(): ArrayList<Int> {
        return cards
    }

    fun isCompare(): Boolean {
        return isCompare
    }

    fun cardClick(adapter: CardAdapter,position: Int): Int {
        when (count) {
            0 -> {
                if (firstPosition == null) {
                    firstPosition = position
                    count++
                }
            }

            1 -> {
                if (secondPosition == null) {
                    secondPosition = position
                    count++
                }
            }
        }
        cards.removeAt(position)
        cards.add(position, cardData[position])
        adapter.notifyDataSetChanged()
        return count
    }

    fun prepareData() {
        when (gameMode) {
            6 -> {
                cardData.addAll(list6)
                column = 3
            }

            16 -> {
                cardData.addAll(list16)
                column = 4
            }

            36 -> {
                cardData.addAll(list36)
                column = 6
            }
        }
        cardData.shuffle()
        cards.addAll(cardData)
    }

    fun flipBackSide() {
        cards.clear()
        for (i in 1..gameMode) {
            cards.add(R.drawable.backside)
        }
    }

    fun compareCard(adapter: CardAdapter, img: View) : String {
        isCompare = true
        if (firstPosition != null && secondPosition != null) {
            if (cardData[firstPosition!!] != cardData[secondPosition!!]) {
                lifeCount--
                img.isVisible = false
                if (lifeCount > 0) {
                    @Suppress("DEPRECATION")
                    Handler().postDelayed({
                        cards.removeAt(firstPosition!!)
                        cards.add(firstPosition!!, R.drawable.backside)
                        cards.removeAt(secondPosition!!)
                        cards.add(secondPosition!!, R.drawable.backside)
                        isCompare = false
                        count = 0
                        firstPosition = null
                        secondPosition = null
                        adapter.notifyDataSetChanged()
                    }, 1000L)
                }
            } else {
                isCompare = false
                count = 0
                firstPosition = null
                secondPosition = null
            }
        }
        if (!cards.contains(R.drawable.backside)) {
            return "You Win!"
        } else if (lifeCount == 0) {
            cards.clear()
            cards.addAll(cardData)
            adapter.notifyDataSetChanged()
            return "Game Over!"
        }
        return ""
    }

    fun clearControllerData() {
        cards.clear()
        cardData.clear()
        firstPosition = null
        secondPosition = null
        count = 0
        column = 0
        gameMode = 0
        lifeCount = 3
        isCompare = false
    }
    fun restart() {
        firstPosition = null
        secondPosition = null
        count = 0
        isCompare = false
        lifeCount = 3
        cardData.shuffle()
        cards.clear()
        cards.addAll(cardData)
    }
}
