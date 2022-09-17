package com.tuanha.language

import com.tuanha.language.utils.clone.LatinCrawl
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() = runBlocking{
        assert(LatinCrawl().isWord("n"))
    }
}