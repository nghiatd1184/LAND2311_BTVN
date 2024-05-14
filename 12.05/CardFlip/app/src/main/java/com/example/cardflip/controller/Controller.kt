package com.example.cardflip.controller

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import com.example.cardflip.R
import com.example.cardflip.adapter.CardAdapter
import com.example.cardflip.databinding.FragmentPlayBinding
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

    fun prepareData() {
        for (i in 1..gameMode) {
            cards.add(R.drawable.backside)
        }
        when (gameMode) {
            6 -> {
                cardData = list6
                column = 3
            }

            16 -> {
                cardData = list16
                column = 4
            }

            36 -> {
                cardData = list36
                column = 6
            }
        }
        cardData.shuffle()
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

    fun cardClick(position: Int): Int {

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
        cards.add(position,cardData[position])
        return count
    }

    fun isCompare(): Boolean {
        return isCompare
    }

    fun compareCard(adapter: CardAdapter, img : ImageView) {
        isCompare = true
        val isDifferent = true
        val handler = Handler(Looper.getMainLooper()) {
            val isDelete = it.data.getBoolean("is_dDifferent")
            img.isVisible = isDelete
            adapter.notifyDataSetChanged()
            return@Handler true
        }
        thread(start = true) {
            var isDifferent = true
            if (firstPosition != null && secondPosition != null) {
                if (cardData[firstPosition!!] != cardData[secondPosition!!]) {
                    try {
                        Thread.sleep(2500L)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                    isDifferent = false
                    lifeCount--
                    cards.removeAt(firstPosition!!)
                    cards.add(firstPosition!!,R.drawable.backside)
                    cards.removeAt(secondPosition!!)
                    cards.add(secondPosition!!,R.drawable.backside)
                }
                isCompare = false
                clear()
                val msg = handler.obtainMessage()
                msg.data = Bundle().apply {
                    putBoolean("is_dDifferent",isDifferent)
                }
                handler.sendMessage(msg)
            }
        }
    }

    fun clear() {
        count = 0
        firstPosition = null
        secondPosition = null
    }
}
