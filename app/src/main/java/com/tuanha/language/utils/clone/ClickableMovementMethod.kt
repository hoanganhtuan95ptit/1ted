package com.tuanha.language.utils.clone

import android.text.Layout
import android.text.Spannable
import android.text.method.BaseMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.TextView
import com.tuanha.language.ui.view.ClickPreventableTextView
import com.tuanha.language.ui.view.WordReplacementSpan


class ClickableMovementMethod : BaseMovementMethod() {

    var timeDown = System.currentTimeMillis()

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent): Boolean {

        val action = event.actionMasked

        if (action == MotionEvent.ACTION_DOWN) {
            timeDown = System.currentTimeMillis()
        }

        if (action == MotionEvent.ACTION_UP && System.currentTimeMillis() - timeDown in 0..100) {

            var x = event.x.toInt()
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY

            val layout: Layout = widget.layout

            val line: Int = layout.getLineForVertical(y)
            val off: Int = layout.getOffsetForHorizontal(line, x.toFloat())

            val link = buffer.getSpans(off, off, WordReplacementSpan::class.java).filter { it.word?.content?.isNotBlank() == true }

            if (link.isEmpty()) {
                (widget as? ClickPreventableTextView)?.preventNextClick(null)
            } else {
                (widget as? ClickPreventableTextView)?.preventNextClick(link.first().word)
            }
        }

        return false
    }

    companion object {

        private var sInstance: ClickableMovementMethod? = null

        val instance: ClickableMovementMethod?
            get() {
                if (sInstance == null) {
                    sInstance = ClickableMovementMethod()
                }
                return sInstance
            }
    }
}