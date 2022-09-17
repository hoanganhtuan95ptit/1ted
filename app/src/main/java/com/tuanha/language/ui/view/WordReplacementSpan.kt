package com.tuanha.language.ui.view

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan
import com.tuanha.language.entities.Word
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

val marginVertical by lazy { 8 }
val marginHorizontal by lazy { 8 }

val paddingVertical by lazy { 28 }
val paddingHorizontal by lazy { 32 }

val cornerRadius by lazy { 8 }

var height: Int? = null

open class WordReplacementSpan(
    open val word: Word? = null,
    open val marginH: Int = marginHorizontal,
    open val paddingH: Int = paddingHorizontal
) : ReplacementSpan(), LineHeightSpan {

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {

        val textColor = paint.color

        paint.style = Paint.Style.FILL

        canvas.drawText(text, start, end, x + marginH + paddingH, y.toFloat(), paint)

        paint.color = textColor
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {

        return paint.measureText(text, start, end).roundToInt() + marginH * 2 + paddingH * 2
    }

    override fun chooseHeight(text: CharSequence?, start: Int, end: Int, spanstartv: Int, lineHeight: Int, fm: Paint.FontMetricsInt) {

        height = height ?: (fm.descent.absoluteValue + fm.ascent.absoluteValue)

        fm.top = -height!! - paddingVertical - marginVertical
        fm.ascent = -height!! - paddingVertical - marginVertical

        fm.bottom = paddingVertical + marginVertical
        fm.descent = paddingVertical + marginVertical
    }
}
