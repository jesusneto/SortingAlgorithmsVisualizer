package com.exemplo.sortingalgorithmsvisualizer.algorithms

import android.util.Log
import com.exemplo.sortingalgorithmsvisualizer.SortState
import com.exemplo.sortingalgorithmsvisualizer.data.SortItem
import com.exemplo.sortingalgorithmsvisualizer.utils.swap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class BubbleSort {

    private val animationDelay = 800L

    suspend operator fun invoke(
        list: MutableList<SortItem>,
        sortStateFlow: MutableStateFlow<SortState>
    ) = withContext(Dispatchers.Default) {
        val size = list.size
        var swapped: Boolean

        for (i in 0 until size - 1) {
            swapped = false
            for (j in 0 until size - i - 1) {
                // Reset visual states (comparing/swapped) for all unordered items.
                // It is crucial to create COPIES of the items so that DiffUtil can detect the changes.
                list.forEachIndexed { index, item ->
                    if (!item.sorted) {
                        // Apenas crie uma cópia se o estado for mudar para evitar emissões desnecessárias
                        if (item.comparing || item.swapped) {
                            list[index] = item.copy(comparing = false, swapped = false)
                        }
                    }
                }
                // indicates that is comparing
                list[j] = list[j].copy(comparing = true)
                list[j + 1] = list[j + 1].copy(comparing = true)

                sortStateFlow.emit(
                    SortState.Sorting(list.toList())
                )
                delay(animationDelay) // to be able to see the animation

                if (list[j].itemValue > list[j + 1].itemValue) {
                    // Desmarcar comparação e marcar troca
                    list[j] = list[j].copy(comparing = false, swapped = true)
                    list[j + 1] = list[j + 1].copy(comparing = false, swapped = true)

                    sortStateFlow.emit(
                        SortState.Sorting(list.toList())
                    )
                    delay(animationDelay)

                    list.swap(j, j + 1)
                    swapped = true

                    // swap was done
                    list[j] = list[j].copy(swapped = false)
                    list[j + 1] = list[j + 1].copy(swapped = false)

                    sortStateFlow.emit(
                        SortState.Sorting(list.toList())
                    )
                    delay(animationDelay / 2)
                }

                else {
                    // If there was no exchange, just unmark the comparison status
                    list[j] = list[j].copy(comparing = false)
                    list[j + 1] = list[j + 1].copy(comparing = false)
                    sortStateFlow.emit(SortState.Sorting(list.toList()))
                    delay(animationDelay / 2) // Small animation delay to see items return to normal
                }

            }

            // The last element of the iteration is sorted (its final position)
            list[size - 1 - i] = list[size - 1 - i].copy(sorted = true)
            sortStateFlow.emit(
                SortState.Sorting(list.toList())
            )
            delay(animationDelay)

            if (!swapped) {
                // If no elements were swapped, the array is sorted.
                for (k in 0 until size - i - 1) { // Mark elements that have not yet been marked as sorted
                    if (!list[k].sorted) { // Check if it is not already sorted
                        list[k] = list[k].copy(sorted = true)
                    }
                }
                break
            }
        }

        // Make sure all elements are marked as sorted at the end
        list.forEachIndexed { index, item ->
            if (!item.sorted) { // Check if it is not already sorted
                list[index] = item.copy(sorted = true)
            }
        }

        sortStateFlow.emit(
            SortState.Sorted(list.toList())
        )

    }
}