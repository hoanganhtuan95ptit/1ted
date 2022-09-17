package com.tuanha.language.ui.view

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.tuanha.language.entities.Word
import com.tuanha.language.utils.clone.ClickableMovementMethod

class ClickPreventableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), View.OnClickListener {

    private var word: Word? = null

    private var clickListener: OnClickListener? = null

    init {
        if (movementMethod == null) movementMethod = ClickableMovementMethod.instance
    }

    fun preventNextClick(w: Word?) {
        word = w
        onClick(this)
    }

    fun getWordSelected(): Word? {
        return word
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        clickListener = listener
    }

    override fun onClick(v: View?) {
        clickListener?.onClick(v)
    }
}