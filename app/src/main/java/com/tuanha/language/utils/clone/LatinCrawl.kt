package com.tuanha.language.utils.clone

import com.tuanha.core.utils.extentions.validate
import com.tuanha.language.entities.Subtitle
import com.tuanha.language.entities.Word

class LatinCrawl : Crawl {

    override suspend fun getWord(subtitle: Subtitle): List<Word> {

        val charAndType = subtitle.content.mapIndexed { index, c ->

            val text = c.toString()
            val type = if (hasSpace(text)) {
                CharType.SPACE
            } else {
                CharType.CHAR
            }

            Pair(text, type)
        }

        val wordList = arrayListOf<Word>()


        var wordCurrent: Word? = null

        charAndType.forEachIndexed { index, pair ->

            if (pair.second != charAndType.getOrNull(index - 1)?.second) {

                wordCurrent = Word()

                if (pair.first != " ") wordList.add(wordCurrent!!)
            }

            wordCurrent!!.content = wordCurrent!!.content + pair.first
        }

        return wordList.validate {

            isWord = isWord(content)
        }
    }

    private data class Pair(
        var first: String,
        var second: CharType
    )
}