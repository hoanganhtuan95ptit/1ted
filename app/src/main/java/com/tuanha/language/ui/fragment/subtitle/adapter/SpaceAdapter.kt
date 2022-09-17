package com.tuanha.language.ui.fragment.subtitle.adapter

import com.tuanha.coreapp.ui.base.adapters.ViewItemAdapter
import com.tuanha.coreapp.ui.base.adapters.ViewItemCloneable
import com.tuanha.language.databinding.ItemSpaceBinding
import java.util.*

class SpaceAdapter : ViewItemAdapter<SpaceViewItem, ItemSpaceBinding>()

data class SpaceViewItem(
    val id: String = UUID.randomUUID().toString()
) : ViewItemCloneable {

    override fun clone() = copy()

    override fun areItemsTheSame(): List<Any> = listOf(

        id
    )
}