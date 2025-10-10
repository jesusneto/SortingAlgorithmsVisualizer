package com.exemplo.sortingalgorithmsvisualizer.algorithms

import com.exemplo.sortingalgorithmsvisualizer.SortState
import com.exemplo.sortingalgorithmsvisualizer.data.SortItem
import com.exemplo.sortingalgorithmsvisualizer.utils.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class InsertionSort {

    private val animationDelay = 750L

    suspend operator fun invoke(
        list: MutableList<SortItem>,
        sortStateFlow: MutableStateFlow<SortState>
    ) = withContext(Dispatchers.Default) {
        val size = list.size
        var j: Int

        // Initialize the first element as sorted
        list[0] = list[0].copy(sorted = true)
        sortStateFlow.emit(SortState.Sorting(list.toList()))
        delay(animationDelay)

        for (i in 1 until size) {
            var aux = list[i].itemValue
            j = i

            // Marks the current element ('keyItem') as the one being inserted
            list[i] = list[i].copy(comparing = true)
            sortStateFlow.emit(SortState.Sorting(list.toList()))
            delay(animationDelay)

            while (j > 0 && aux < list[j - 1].itemValue) {

                list[j] = list[j].copy(comparing = true, swapped = true)
                list[j - 1] = list[j - 1].copy(sorted = false, comparing = true, swapped = true)
                sortStateFlow.emit(SortState.Sorting(list.toList()))
                delay(animationDelay)

                list.swap(j, j - 1)

                list[j] = list[j].copy(sorted = true, comparing = false, swapped = false)
                list[j - 1] = list[j - 1].copy(comparing = false, swapped = false)
                sortStateFlow.emit(SortState.Sorting(list.toList()))
                delay(animationDelay)

                j--
            }

            list[j] = list[j].copy(sorted = true)
            sortStateFlow.emit(SortState.Sorting(list.toList()))
            delay(animationDelay)
        }

        // Mark all remaining elements as sorted at the end
        list.forEachIndexed { index, item ->
            if (!item.sorted) {
                list[index] = item.copy(sorted = true)
            }
        }
        sortStateFlow.emit(SortState.Sorted(list.toList()))
    }
}