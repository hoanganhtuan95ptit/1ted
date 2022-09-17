@file:Suppress("ConstantConditionIf")

package com.tuanha.language.ui.fragment.subtitle.question.option.word.choose.adapter

import android.view.View
import com.tuanha.coreapp.ui.base.adapters.ViewItemAdapter
import com.tuanha.coreapp.ui.base.adapters.ViewItemCloneable
import com.tuanha.language.PAYLOAD_BACKGROUND
import com.tuanha.language.PAYLOAD_CONTENT
import com.tuanha.language.R
import com.tuanha.language.databinding.ItemWordChooseBinding
import com.tuanha.language.entities.Word
import com.tuanha.language.entities.WordStatus

class ChooseWordAdapter(
    private val onItemClick: (View, ChooseWordViewItem) -> Unit = { _, _ -> }
) : ViewItemAdapter<ChooseWordViewItem, ItemWordChooseBinding>(onItemClick) {

    override fun bind(binding: ItemWordChooseBinding, viewType: Int, position: Int, item: ChooseWordViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_CONTENT)) {
            refreshContent(binding, item)
        }

        if (payloads.contains(PAYLOAD_BACKGROUND)) {
            refreshBackground(binding, item)
        }
    }

    override fun bind(binding: ItemWordChooseBinding, viewType: Int, position: Int, item: ChooseWordViewItem) {
        super.bind(binding, viewType, position, item)

        refreshContent(binding, item)
        refreshBackground(binding, item)
    }

    private fun refreshContent(binding: ItemWordChooseBinding, item: ChooseWordViewItem) {

        binding.tvText.setText(item.content)
    }

    private fun refreshBackground(binding: ItemWordChooseBinding, item: ChooseWordViewItem) {

        binding.tvText.setBackgroundResource(item.backgroundRes)
    }
}

data class ChooseWordViewItem(

    val data: Word,

    var content: String = "",

    var backgroundRes: Int = 0
) : ViewItemCloneable {

    override fun clone() = copy()

    fun refresh() = apply {

        content = data.content

        backgroundRes = when (data.status) {
            WordStatus.True -> {
                R.drawable.bg_corner_24dp_stroke_true
            }
            WordStatus.False -> {
                R.drawable.bg_corner_24dp_stroke_error
            }
            else -> {
                R.drawable.bg_corner_24dp_stroke_normal
            }
        }
    }

    override fun areItemsTheSame(): List<Any> = listOf(

        data.id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(

        content to PAYLOAD_CONTENT,

        backgroundRes to PAYLOAD_BACKGROUND,
    )
}