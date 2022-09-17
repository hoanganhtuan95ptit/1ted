package com.tuanha.language.ui.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.tuanha.language.entities.Word

class WordRoundedBackgroundSpan(
    val color: Int? = null,
    override val word: Word? = null,
    override val marginH: Int = marginHorizontal,
    override val paddingH: Int = paddingHorizontal
) : WordReplacementSpan(word, marginH, paddingH) {

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {

        val textSize = paint.textSize
        val textColor = paint.color
        val textHeight = textSize - paint.fontMetrics.descent

        val rect = RectF(
            x + marginVertical,
            y.toFloat() - textHeight - paddingVertical,
            x + marginVertical + paddingHorizontal + paint.measureText(text, start, end) + paddingHorizontal,
            y.toFloat() + paddingVertical
        )

        paint.color = color ?: textColor
        paint.strokeWidth = 3f

        paint.style = Paint.Style.STROKE
        canvas.drawRoundRect(rect, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)

        super.draw(canvas, text, start, end, x, top, y, bottom, paint)
    }
}