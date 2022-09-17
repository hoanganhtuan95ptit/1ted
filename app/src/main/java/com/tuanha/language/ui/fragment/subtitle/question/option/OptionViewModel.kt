package com.tuanha.language.ui.fragment.subtitle.question.option

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.tuanha.coreapp.ui.viewmodels.BaseViewModel
import com.tuanha.coreapp.utils.extentions.Event
import com.tuanha.coreapp.utils.extentions.toEvent
import com.tuanha.language.entities.Word

open class OptionViewModel : BaseViewModel() {

    val word: LiveData<Word> = MediatorLiveData()


    val completed: LiveData<Boolean> = MediatorLiveData()

    val completedEvent: LiveData<Event<Boolean>> = completed.toEvent()
}