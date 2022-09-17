package com.tuanha.language.utils.clone

import android.text.SpannableStringBuilder


class WordSpan() : SpannableStringBuilder() {

    fun append(text: CharSequence, vararg spans: Any): WordSpan {

        append(text)

        for (span in spans) {
            setSpan(span, length - text.length, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return this
    }
}