package com.tuanha.language.ui.fragment.subtitle.question.option.word.choose

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.tuanha.core.utils.extentions.normalize
import com.tuanha.core.utils.extentions.toArrayList
import com.tuanha.core.utils.extentions.validate
import com.tuanha.coreapp.data.usecase.ResultState
import com.tuanha.coreapp.data.usecase.doSuccess
import com.tuanha.coreapp.utils.extentions.*
import com.tuanha.language.entities.Word
import com.tuanha.language.entities.WordStatus
import com.tuanha.language.ui.fragment.subtitle.question.option.OptionViewModel
import com.tuanha.language.ui.fragment.subtitle.question.option.word.choose.adapter.ChooseWordViewItem
import com.tuanha.language.utils.clone.randomLevel

class ChooseWordViewModel : OptionViewModel() {

    @VisibleForTesting
    val words: LiveData<List<Word>> = MediatorLiveData()

    @VisibleForTesting
    val optionState: LiveData<ResultState<List<Word>>> = combineSources<ResultState<List<Word>>>(word, words) {

        words.getOrEmpty().filter {

            it.isWord && !it.content.equals(word.get().content, true)
        }.associateBy { it.content.normalize() }.values.toList().randomLevel(3).toArrayList().apply {

            add(word.get())
        }.map {

            it.copy().apply {
                status = WordStatus.Normal
            }
        }.shuffled().let {

            postValue(ResultState.Success(it))
        }
    }.apply {

        postDifferentValue(ResultState.Start)
    }

    @VisibleForTesting
    val optionList: LiveData<List<Word>> = combineSources(optionState) {

        optionState.get().doSuccess {
            postValue(it)
        }
    }

    @VisibleForTesting
    val optionViewItemList: LiveData<List<ChooseWordViewItem>> = combineSources(optionList) {

        optionList.getOrEmpty().map {

            ChooseWordViewItem(it).refresh()
        }.let {

            postValue(it)
        }
    }

    @VisibleForTesting
    val optionViewItemListDisplayRefresh: LiveData<Long> = MediatorLiveData<Long>().apply {

        value = System.currentTimeMillis()
    }

    val optionViewItemListDisplay: LiveData<List<ChooseWordViewItem>> = combineSources(optionViewItemList, optionViewItemListDisplayRefresh) {

        optionViewItemList.getOrEmpty().map {

            it.clone()
        }.let {

            postValue(it)
        }
    }

    fun updateWord(word: Word) {

        this.word.postValue(word)
    }

    fun updateWords(words: List<Word>) {

        this.words.postValue(words)
    }

    fun clearOption() {

        optionViewItemList.getOrEmpty().validate {
            data.status = WordStatus.Normal
            refresh()
        }

        optionViewItemListDisplayRefresh.postValue(System.currentTimeMillis())
    }

    fun updateOptionSelect(chooseWordViewItem: ChooseWordViewItem) {

        if (optionViewItemList.getOrEmpty().any { it.data.status != WordStatus.Normal }) {
            return
        }

        val item = optionViewItemList.getOrEmpty().find { it.data.id == chooseWordViewItem.data.id } ?: return

        if (item.data.id == word.get().id) {
            item.data.status = WordStatus.True
        } else {
            item.data.status = WordStatus.False
        }

        item.refresh()

        optionViewItemListDisplayRefresh.postValue(System.currentTimeMillis())
    }

}