package com.exemplo.sortingalgorithmsvisualizer

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.exemplo.sortingalgorithmsvisualizer.databinding.ActivityMainBinding
import com.exemplo.sortingalgorithmsvisualizer.model.SortingMethod
import com.exemplo.sortingalgorithmsvisualizer.ui.SortingViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: SortingViewModel by viewModels()
    private lateinit var sortAdapter: SortAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()

        setupDropdownMenu()
        setupButtons()
    }

    private fun setupRecyclerView() {
        sortAdapter = SortAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = sortAdapter
            itemAnimator = DefaultItemAnimator().apply {
                moveDuration = 750L
            }
        }

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sortState.collectLatest { state ->
                    when (state) {
                        is SortState.Sorting -> {
                            sortAdapter.submitList(state.items)  // Sends the list to the adapter to display

                            // During the sorting of elements, the dropdown menu and buttons are disabled
                            binding.sortingContainer.isEnabled = false
                            binding.startButton.isEnabled = false
                            binding.resetButton.isEnabled = false
                        }

                        is SortState.Sorted -> {
                            sortAdapter.submitList(state.items)

                            // When the sorting ends, only the reset button is enabled to allow a new sorting
                            binding.sortingContainer.isEnabled = true
                            binding.startButton.isEnabled = false
                            binding.resetButton.isEnabled = true

                            Toast.makeText(this@MainActivity, "Elements Sorted!", Toast.LENGTH_SHORT).show()
                        }

                        is SortState.Idle -> {
                            sortAdapter.submitList(state.items) // Display initial or reset array
                            binding.startButton.isEnabled = true // Enable button to start sorting
                        }
                    }
                }
            }
        }
    }

    private fun setupDropdownMenu() {

        val adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            arrayListOf(
                SortingMethod.BubbleSort,
                SortingMethod.SelectionSort,
                SortingMethod.InsertionSort
            )
        )

        binding.sortingDropMenu.setAdapter(adapter)

        binding.sortingDropMenu.setDropDownBackgroundDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.sorting_dropdown_menu_background,
                null
            )
        )

        binding.sortingDropMenu.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id->
                val selectedOption = parent.getItemAtPosition(position) as SortingMethod
                viewModel.setSortingMethod(selectedOption)
            }
    }

    private fun setupButtons() {
        binding.startButton.setOnClickListener{
            viewModel.startSorting()
        }

        binding.resetButton.setOnClickListener {
            viewModel.generateNewArray()
        }
    }
}