package com.tuanha.language.utils.clone

import com.tuanha.language.entities.Subtitle
import com.tuanha.language.entities.Word
import java.util.regex.Pattern


interface Crawl {


    suspend fun getWord(subtitle: Subtitle): List<Word>


    suspend fun isWord(text: String): Boolean {
        return !hasSpecialCharacters(text) && !hasNumber(text) && !hasSpace(text)
    }


    suspend fun getCharType(text: String) = if (hasNumber(text)) {
        CharType.NUMBER
    } else if (hasSpace(text)) {
        CharType.SPACE
    } else if (hasSpecialCharacters(text)) {
        CharType.SPECIAL
    } else {
        CharType.CHAR
    }


    suspend fun hasSpace(text: String): Boolean {
        return Pattern.compile("[ ]").matcher(text).find()
    }

    suspend fun hasNumber(text: String): Boolean {
        return Pattern.compile("[0-9]").matcher(text).find()
    }

    suspend fun hasSpecialCharacters(text: String): Boolean {
        return Pattern.compile("[\n`!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~]").matcher(text).find()
    }
}

enum class CharType {
    CHAR, NUMBER, SPECIAL, SPACE
}