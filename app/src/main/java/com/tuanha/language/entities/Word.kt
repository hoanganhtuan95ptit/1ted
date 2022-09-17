package com.tuanha.language.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import kotlinx.parcelize.Parcelize
import java.util.*

@Keep
@Parcelize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Word(

    val id: String = UUID.randomUUID().toString(),

    var content: String = "",
) : Parcelable {

    @JsonIgnore
    var status: WordStatus = WordStatus.None

    @JsonIgnore
    var isWord: Boolean = true

    val isTrue: Boolean
        @JsonIgnore get() = status in listOf(WordStatus.True)

    val isFalse: Boolean
        @JsonIgnore get() = status in listOf(WordStatus.False)

    val isQuestion: Boolean
        @JsonIgnore get() = status !in listOf(WordStatus.None)

    val isQuestionNotCompleted: Boolean
        @JsonIgnore get() = status in listOf(WordStatus.Normal, WordStatus.Focus, WordStatus.Break, WordStatus.False)
}

enum class WordStatus(val value: Int) {

    None(0), Normal(1), Focus(2), Break(3), False(4), True(5);

    companion object {

        fun Int.toWordStatus() = values().find { it.value == this } ?: error("$this is not map WordStatus")
    }
}