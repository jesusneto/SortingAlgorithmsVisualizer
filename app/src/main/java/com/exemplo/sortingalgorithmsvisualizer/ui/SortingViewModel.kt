package com.exemplo.sortingalgorithmsvisualizer.ui

import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemplo.sortingalgorithmsvisualizer.algorithms.BubbleSort
import com.exemplo.sortingalgorithmsvisualizer.algorithms.SelectionSort
import com.exemplo.sortingalgorithmsvisualizer.SortState
import com.exemplo.sortingalgorithmsvisualizer.algorithms.InsertionSort
import com.exemplo.sortingalgorithmsvisualizer.data.SortItem
import com.exemplo.sortingalgorithmsvisualizer.model.SortingMethod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Random

class SortingViewModel(
    private val bubbleSort: BubbleSort = BubbleSort(),
    private val selectionSort: SelectionSort = SelectionSort(),
    private val insertionSort: InsertionSort = InsertionSort()

) : ViewModel() {

    private val _sortState = MutableStateFlow<SortState>(SortState.Idle(emptyList()))
    val sortState: StateFlow<SortState> = _sortState

    private var newList = mutableListOf<SortItem>()

    private var selectedSortingMethod = SortingMethod.BubbleSort

    init {
        generateNewArray()
        //startSorting()
    }

    fun setSortingMethod(sortingMethod: SortingMethod) {
        selectedSortingMethod = sortingMethod
    }

    fun generateNewArray() {
        newList.clear()

        for (i in 0 until 7) {
            val rnd = Random()
            val value = rnd.nextInt(100) // generates a random value between 0 and 99

            val maxBrightness = 255f
            // Normalize 'i' from 1-100 to 0-1. If i=1 then 0.1; if i=10 then 1.0
            val normalizedValue = value / 100f
            val redComponent = 255 - (normalizedValue * maxBrightness).toInt()
            newList.add(
                SortItem(
                    itemValue = value,
                    comparing = false,
                    swapped = false,
                    sorted = false,
                    color = Color.argb(
                        255,
                        redComponent,
                        0,
                        0
                    )
                )
            )
        }

        _sortState.value = SortState.Idle(newList.toList())
    }

    fun startSorting() {
        when(selectedSortingMethod) {
            SortingMethod.BubbleSort -> {
                viewModelScope.launch {
                    bubbleSort(newList, _sortState)
                }
            }

            SortingMethod.SelectionSort -> {
                viewModelScope.launch {
                    selectionSort(newList, _sortState)
                }
            }

            SortingMethod.InsertionSort -> {
                viewModelScope.launch {
                    insertionSort(newList, _sortState)
                }
            }
        }
    }
}

// HOW THE COLOR WAS CALCULATED
// The color intensity is calculated based in the value of (i)
// To go from 0 to 150, the color value can be normalized to a scale of 0 to 1
// and then multiply it by the maximum intensity.