package com.tuanha.language.di

import com.tuanha.language.ui.fragment.subtitle.SubtitleViewModel
import com.tuanha.language.ui.fragment.subtitle.question.option.word.choose.ChooseWordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@JvmField
val viewModelModule = module {

    viewModel {
        SubtitleViewModel(get())
    }

    viewModel {
        ChooseWordViewModel()
    }
}
