package com.exemplo.sortingalgorithmsvisualizer.utils

fun <T> MutableList<T>.swap(indexOne: Int, indexTwo: Int) {
    val temp = this[indexOne]
    this[indexOne] = this[indexTwo]
    this[indexTwo] = temp
}