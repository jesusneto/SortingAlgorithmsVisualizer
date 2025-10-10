package com.exemplo.sortingalgorithmsvisualizer.algorithms

import android.util.Log
import com.exemplo.sortingalgorithmsvisualizer.SortState
import com.exemplo.sortingalgorithmsvisualizer.data.SortItem
import com.exemplo.sortingalgorithmsvisualizer.utils.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class SelectionSort {

    private val animationDelay = 750L

    suspend operator fun invoke(
        list: MutableList<SortItem>,
        sortStateFlow: MutableStateFlow<SortState>
    ) = withContext(Dispatchers.Default) {
        val size = list.size
        var min: Int

        for (i in 0 until size - 1) {
            min = i

            list.forEachIndexed { index, item ->
                if (!item.sorted) {
                    if (item.comparing || item.swapped) {
                        list[index] = item.copy(comparing = false, swapped = false)
                    }
                }
            }

            sortStateFlow.emit(
                SortState.Sorting(list.toList())
            )
            delay(animationDelay / 2) // to be able to see the animation

            list[min] = list[min].copy(comparing = true)

            sortStateFlow.emit(
                SortState.Sorting(list.toList())
            )
            delay(animationDelay / 2)

            for (j in i + 1 until size) {
                // indicates that is comparing
                list[j] = list[j].copy(comparing = true)

                sortStateFlow.emit(
                    SortState.Sorting(list.toList())
                )
                delay(animationDelay)

                if (list[j].itemValue < list[min].itemValue) {
                    // Unmark the old minIndex and mark the new minIndex
                    list[min] = list[min].copy(comparing = false) // Unmark the old minimum
                    min = j // Updates the index of the minimum element
                    list[min] = list[min].copy(comparing = true) // Marks the new minimum
                    sortStateFlow.emit(SortState.Sorting(list.toList()))
                    delay(animationDelay / 2)
                } else {
                    // If it is not the minimum, then unmark the comparison and return to normal
                    list[j] = list[j].copy(comparing = false)
                    sortStateFlow.emit(SortState.Sorting(list.toList()))
                    delay(animationDelay / 2)
                }
            }

            if (min != i) {
                // Uncheck all compared items and mark exchange
                list.forEachIndexed { index, item ->
                    if (index == i || index == min) {
                        list[index] = item.copy(comparing = false, swapped = true)
                    } else if (!item.sorted && item.comparing) {
                        list[index] = item.copy(comparing = false)
                    }
                }
                sortStateFlow.emit(SortState.Sorting(list.toList()))
                delay(animationDelay)

                // Performs the swapping of elements
                list.swap(i, min)

                //After swapping, unmark the 'swapped' status
                list[i] = list[i].copy(swapped = false) // New item at position i
                list[min] = list[min].copy(swapped = false) // New item at position min
                sortStateFlow.emit(SortState.Sorting(list.toList()))
                delay(animationDelay / 2)
            } else {
                // If min == i, there was no swapping, it just unmarks the item 'i' that was as 'comparing'
                list[i] = list[i].copy(comparing = false)
                sortStateFlow.emit(SortState.Sorting(list.toList()))
                delay(animationDelay / 2)
            }


            list[i] = list[i].copy(sorted = true)
            sortStateFlow.emit(SortState.Sorting(list.toList()))
            delay(animationDelay)
        }

        // Mark all remaining elements as sorted at the end
        list.forEachIndexed { index, item ->
            if (!item.sorted) {
                list[index] = item.copy(sorted = true)
            }
        }

        sortStateFlow.emit(
            SortState.Sorted(list.toList())
        )
    }
}