package com.nghiatd.mixic.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar

class VerticalSeekBar(context: Context, attrs: AttributeSet) : AppCompatSeekBar(context, attrs) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw) // Swap width and height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec) // Swap width and height
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                val progress = max - (max * event.y / height).toInt()
                setProgress(progress)
                onSizeChanged(width, height, 0, 0)
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return true
    }
}