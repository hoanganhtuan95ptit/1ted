package com.tuanha.language.utils.clone

import com.tuanha.core.utils.extentions.toArrayList
import java.util.*


fun <T> List<T>.randomLevel(levelMax: Int): List<T> {

    val list = if (size > 2) {
        toArrayList().subList(1, size - 1)
    } else {
        return emptyList()
    }

    val breakIndex = arrayListOf<Int>()
    val randomIndex = arrayListOf<Int>()

    while (randomIndex.size < levelMax && breakIndex.size < list.size) {

        val numberRandom = Random().nextInt(list.size)

        if (breakIndex.contains(numberRandom)) continue

        randomIndex.add(numberRandom)

        breakIndex.add(numberRandom)
    }

    return randomIndex.map {

        list[it]
    }
}
