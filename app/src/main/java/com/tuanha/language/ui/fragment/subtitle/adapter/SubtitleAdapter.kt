@file:Suppress("ConstantConditionIf")

package com.tuanha.language.ui.fragment.subtitle.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tuanha.coreapp.ui.base.adapters.ViewItemAdapter
import com.tuanha.coreapp.ui.base.adapters.ViewItemCloneable
import com.tuanha.coreapp.utils.extentions.load
import com.tuanha.language.PAYLOAD_CONTENT
import com.tuanha.language.PAYLOAD_SELECTED
import com.tuanha.language.R
import com.tuanha.language.databinding.ItemSubtitleBinding
import com.tuanha.language.entities.Subtitle
import com.tuanha.language.entities.Word
import com.tuanha.language.entities.WordStatus
import com.tuanha.language.ui.view.WordReplacementSpan
import com.tuanha.language.ui.view.WordRoundedBackgroundSpan
import com.tuanha.language.utils.clone.WordSpan

class SubtitleAdapter(
    private val onItemClick: (View, SubtitleViewItem) -> Unit = { _, _ -> },
    private val onItemWordClickListener: (View, SubtitleViewItem, Word?) -> Unit = { _, _, _ -> }
) : ViewItemAdapter<SubtitleViewItem, ItemSubtitleBinding>(onItemClick) {

    override fun createViewItem(parent: ViewGroup): ItemSubtitleBinding {
        val binding = super.createViewItem(parent)

        binding.tvSubtitle.setOnClickListener { v ->

            val viewItem = getViewItem<SubtitleViewItem>(binding) ?: return@setOnClickListener

            binding.tvSubtitle.getWordSelected().takeIf { viewItem.selected }?.let { word ->

                onItemWordClickListener.invoke(binding.root, viewItem, word)
            } ?: let {

                onItemClick.invoke(binding.root, viewItem)
            }
        }

        return binding
    }

    override fun bind(binding: ItemSubtitleBinding, viewType: Int, position: Int, item: SubtitleViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_CONTENT)) {
            refreshContent(binding, item)
        }

        if (payloads.contains(PAYLOAD_SELECTED)) {
            refreshSelected(binding, item)
        }
    }

    override fun bind(binding: ItemSubtitleBinding, viewType: Int, position: Int, item: SubtitleViewItem) {
        super.bind(binding, viewType, position, item)

        binding.ivAvatar.load(R.drawable.img_logo, CircleCrop())

        refreshContent(binding, item)
        refreshSelected(binding, item)
    }

    private fun refreshContent(binding: ItemSubtitleBinding, item: SubtitleViewItem) {

        binding.tvSubtitle.setText(item.content, TextView.BufferType.SPANNABLE)
    }

    private fun refreshSelected(binding: ItemSubtitleBinding, item: SubtitleViewItem) {

        binding.root.isSelected = item.selected
        binding.tvSubtitle.isSelected = item.selected
    }
}

data class SubtitleViewItem(

    val data: Subtitle,

    var selected: Boolean = false,

    var content: WordSpan = WordSpan(),
) : ViewItemCloneable {

    override fun clone() = copy()

    fun refresh() = apply {

        val words = data.words.flatMap { listOf(it, Word(content = " ")) }

        content = WordSpan()
        words.forEach {

            content.append(it.getContent(), if (it.isQuestion) WordRoundedBackgroundSpan(color = it.getColor(), word = it) else WordReplacementSpan(word = it, marginH = 0, paddingH = 0))
        }
    }

    override fun areItemsTheSame(): List<Any> = listOf(

        data.content
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(

        selected to PAYLOAD_SELECTED,

        content.toString() to PAYLOAD_CONTENT
    )

    private fun Word.getColor() = when (status) {

        in listOf(WordStatus.True) -> {
            Color.GREEN
        }
        in listOf(WordStatus.Focus) -> {
            Color.BLUE
        }
        else -> {
            null
        }
    }

    private fun Word.getContent() = when (status) {

        in listOf(WordStatus.Normal) -> {
            "......."
        }
        in listOf(WordStatus.Break) -> {
            "........"
        }
        in listOf(WordStatus.Focus) -> {
            ".........."
        }
        else -> {
            content
        }
    }
}