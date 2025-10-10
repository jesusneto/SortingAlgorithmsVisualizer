package com.exemplo.sortingalgorithmsvisualizer.data

import androidx.annotation.ColorInt

data class SortItem(
    val itemValue: Int,
    var comparing: Boolean,
    var swapped: Boolean,
    var sorted: Boolean,
    @ColorInt val color: Int
)