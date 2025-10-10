package com.exemplo.sortingalgorithmsvisualizer

import com.exemplo.sortingalgorithmsvisualizer.data.SortItem

sealed class SortState {
    data class Sorting(val items: List<SortItem>) : SortState()
    data class Sorted(val items: List<SortItem>) : SortState()
    data class Idle(val items: List<SortItem>) : SortState() // Initial state
}