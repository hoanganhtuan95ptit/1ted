package com.tuanha.language.ui.fragment.subtitle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.tuanha.core.utils.extentions.index
import com.tuanha.core.utils.extentions.toArrayList
import com.tuanha.core.utils.extentions.validateIndex
import com.tuanha.coreapp.ui.base.adapters.LoadingViewItem
import com.tuanha.coreapp.ui.base.adapters.ViewItemCloneable
import com.tuanha.coreapp.ui.viewmodels.BaseViewModel
import com.tuanha.coreapp.utils.extentions.*
import com.tuanha.language.R
import com.tuanha.language.data.api.AppApi
import com.tuanha.language.entities.Subtitle
import com.tuanha.language.entities.Word
import com.tuanha.language.entities.WordStatus
import com.tuanha.language.ui.fragment.subtitle.adapter.SubtitleViewItem
import com.tuanha.language.utils.clone.LatinCrawl
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

class SubtitleViewModel(val appApi: AppApi) : BaseViewModel() {

    private val itemSearchLoading = listOf(
        LoadingViewItem(R.layout.item_subtitle_loading),
        LoadingViewItem(R.layout.item_subtitle_loading),
        LoadingViewItem(R.layout.item_subtitle_loading),
    )

    var wordSelect: Word? = null

    val wordSelectAsync: LiveData<Word> = MediatorLiveData()


    var subtitleSelect: SubtitleViewItem? = null

    val subtitleSelectAsync: LiveData<SubtitleViewItem> = MediatorLiveData()


    var subtitleIndex: LiveData<Int> = MediatorLiveData<Int>().apply {
        value = -2
    }


    val subtitleList: LiveData<List<Subtitle>> = liveData {

        appApi.fetchSubtitles().validateIndex { i, subtitle ->

            randomBreak(subtitle.words, 1)
        }.let {

            postValue(it)
        }
    }

    val subtitleViewItemList: LiveData<List<SubtitleViewItem>> = combineSources(subtitleList) {

        subtitleList.getOrEmpty().map {

            SubtitleViewItem(it).refresh()
        }.let {

            postValue(it)
        }
    }

    val subtitleDisplayRefresh: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {

        value = true
    }

    val subtitleViewItemListDisplay: LiveData<List<ViewItemCloneable>> = combineSources<List<ViewItemCloneable>>(subtitleViewItemList, subtitleDisplayRefresh, subtitleIndex) {


        val list = arrayListOf<ViewItemCloneable>()

        min(subtitleIndex.get() + 1, subtitleViewItemList.getOrEmpty().size).takeIf { it > 0 }?.let {

            list.addAll(subtitleViewItemList.getOrEmpty().subList(0, it))
        }

        list.map {

            it.clone()
        }.let {

            postValue(it)
        }
    }.apply {

        postValue(itemSearchLoading)
    }


    fun next() = viewModelScope.launch {


    }

    fun updateSubtitleCurrent(currentPosition: Int, auto: Boolean = true) = viewModelScope.launch {

        subtitleViewItemList.getOrEmpty().find {
            currentPosition in it.data.start..it.data.end
        }.let {
            setItemSelected(it, auto)
        }
    }

    fun setItemSelected(item: SubtitleViewItem?, auto: Boolean = true) {

        val subtitleOldSelectIndex = subtitleViewItemList.getOrEmpty().index { it.selected }

        val subtitleOldSelect = subtitleViewItemList.getOrEmpty().getOrNull(subtitleOldSelectIndex)


        val subtitleNewSelectIndex = subtitleViewItemList.getOrEmpty().index { it.data.id == item?.data?.id }

        val subtitleNewSelect = subtitleViewItemList.getOrEmpty().getOrNull(subtitleNewSelectIndex)

        if (subtitleNewSelectIndex == subtitleOldSelectIndex) {
            return
        }

        if (auto && subtitleNewSelectIndex <= subtitleOldSelectIndex) {
            return
        }

        choseWord(null, false)

        subtitleOldSelect?.selected = false
        subtitleNewSelect?.selected = true

        this.subtitleSelect = subtitleNewSelect

        subtitleOldSelect?.refresh()
        subtitleNewSelect?.refresh()

        this.subtitleSelectAsync.postValue(this.subtitleSelect)

        if (subtitleNewSelectIndex > this.subtitleIndex.get()) {
            this.subtitleIndex.postValue(subtitleNewSelectIndex)
        } else {
            this.subtitleDisplayRefresh.postValue(true)
        }
    }

    fun choseWord(auto: Boolean = true) = viewModelScope.launch {

        val subtitleSelect = subtitleSelect ?: return@launch

        val questionList = subtitleSelect.data.words.filter { it.isQuestion }

        val questionListNotCompleted = questionList.filter { it.isQuestionNotCompleted }

        if (questionListNotCompleted.isEmpty()) {
            return@launch
        }

        if (questionList.contains(wordSelect)) {
            return@launch
        }

        choseWord(questionListNotCompleted.first())
    }

    fun choseWord(word: Word?, refresh: Boolean = true) {

        if (word?.isQuestionNotCompleted == false) {
            return
        }

        val wordOldSelect = wordSelect
        val wordNewSelect = word

        if (wordOldSelect == wordNewSelect) {
            return
        }

        this.wordSelect = wordNewSelect
        this.wordSelectAsync.postValue(this.wordSelect)

        wordOldSelect?.status = if (wordOldSelect?.status == WordStatus.Focus) {
            WordStatus.Break
        } else if (wordOldSelect?.status == WordStatus.True) {
            WordStatus.True
        } else {
            WordStatus.Normal
        }

        wordNewSelect?.status = WordStatus.Focus

        subtitleSelect?.refresh()

        if (refresh) {
            subtitleDisplayRefresh.postValue(true)
        }
    }

    private fun randomBreak(words: List<Word>, levelMax: Int) {

        val list = if (words.size > 2) {
            words.toArrayList().subList(1, words.size - 1).filter { it.isWord }
        } else {
            return
        }

        val breakIndex = arrayListOf<Int>()
        val randomIndex = arrayListOf<Int>()

        while (randomIndex.size < levelMax && breakIndex.size < list.size) {

            val numberRandom = Random().nextInt(list.size)

            if (breakIndex.contains(numberRandom)) continue

            val word = list[numberRandom]

            word.status = WordStatus.Normal
            randomIndex.add(numberRandom)

            breakIndex.add(numberRandom)
        }
    }


}