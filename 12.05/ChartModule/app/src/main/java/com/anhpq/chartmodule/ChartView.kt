package com.anhpq.chartmodule

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat

// step 2: custom view
class ChartView @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyle: Int = 0
) : View(context, attrs) {

    private var w: Int = 0
    private var h: Int = 0

    init {
        // set background color
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))

        // nhan su kien UI dc ve len tren man hinh
//        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                w = width
//                h = height
//                Log.d("3igZeus", "width = $w, height = $h")
//                viewTreeObserver.removeOnGlobalLayoutListener(this)
//            }
//        })

        // đầu tiên UI của bạn sẽ đc đăng ký hệ thống
        // tiếp theo, UI sẽ đc tính toán để hiển thị lên trên màn hình
        // tiếp theo, UI sẽ được cập nhật với frame tuỳ thuộc vào tần số quét của màn hình
        viewTreeObserver.addOnGlobalLayoutListener {
            Log.d("", "w = $width, h = $height")
            if (w == 0 || h == 0) {
                w = width
                h = height
            } else {
                return@addOnGlobalLayoutListener
            }
            // lấy đc width, height của 1 đối tượng View cụ thể
            // nhưng có 1 vấn đề: khi thay đổi chiều hoặc kích thước ứng dụng
            // => Observer sẽ được gọi thêm.
        }

        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                Log.d("3igZeus", "w = $width, h = $height")
                w = width
                h = height

                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
    }

    private val charts = arrayListOf<Chart>()

    // region -> Paint

    private val paintLine = Paint()
    private val paintRect = Paint()
    private val paintText = Paint()

    // endregion

    fun addChart(chart: Chart) {
        charts.add(chart)
    }

    fun addCharts(values: List<Chart>) {
        charts.addAll(values)
    }

    private val maxValue: Float
        get() {
            var max = 0F
            charts.forEach {
                if (max < it.value) max = it.value
            }
            return max
        }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.apply {
            // init color paint
            paintLine.color = ContextCompat.getColor(context, R.color.black)
            paintText.color = ContextCompat.getColor(context, R.color.black)
            paintRect.color = ContextCompat.getColor(context, R.color.rect)

            // Oy
            drawLine(0.1F * w, 0.25F * h, w * 0.1F, 0.75F * h, paintLine)
            // Ox
            drawLine(0.1F * w, h * 0.75F, w * 0.9F, 0.75F * h, paintLine)

            // viết các điểm trên trục Oy
            // các bạn có độ dài trục 0y = (w * 90 / 100F) - (w * 10 / 100F) = w*0.8
            // chừa 0.5w đằng trước và đằng sau để cho đồ thị chúng ta đẹp và trực quan
            // nghĩa là độ dài mà chúng ta có thể làm việc là w*0.7

            // lấy số đoạn cần chia = độ dài của mảng Chart truyền vào
            val countOy = charts.size //  = 4

            for (i in 0 until countOy) {
                // drawText
                paintText.textSize = 30F
                val x = (0.1F + (i + 1) * 0.16F) * w
                val y = 0.78F * h
                drawText(charts[i].name, x, y, paintText)

                // là do tôi tự ý chia
                val topY = ((0.75 - (charts[i].value / 10) * 0.09F) * h).toInt()
                // draw Rect
                drawRect(
                    Rect(
                        x.toInt(),
                        topY,
                        x.toInt() + (0.1F * w).toInt(),
                        (0.75 * h).toInt()
                    ), paintRect
                )
            }
//

            // BTVN: chia độ được: lấy min và max value từ user truyền vào để xử lý

            // độ dài trục Oy = (0.75 - 0.25) * h = 0.5*h
            // thật chất ta chỉ dùng có 0.45*h / 5 = 0.09*h
            // chia ra khoảng 5 cho đẹp  hoàn toàn là do tôi tự ý làm

            for (i in 0..12) {
                val y = (0.75F - i * (0.75F/18)) * h
                // drawLine chia độ trên trục Ox
                if (i != 0) {
                    val starX = 0.09F * w
                    val endX = 0.11F * w
                    drawLine(starX, y, endX, y, paintLine)
                }
                // drawText
                paintText.textSize = 20F
                val text = (i * 10).toString()

                // làm cách nào để lấy đc bounds của text ( w, h)
                // trừ đi để xác định vị trí x, y sao cho hiển thị đẹp nhất

                drawText(text, 0.06F * w, y + 5, paintText)
            }

//
//            drawRect(
//                Rect(

//                ), paintRect
//            )
        }

    }
}