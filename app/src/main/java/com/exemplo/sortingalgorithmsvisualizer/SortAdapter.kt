package com.exemplo.sortingalgorithmsvisualizer

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.exemplo.sortingalgorithmsvisualizer.data.SortItem
import com.exemplo.sortingalgorithmsvisualizer.databinding.ItemSortBinding

class SortAdapter : ListAdapter<SortItem, SortAdapter.SortViewHolder>(SortDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortViewHolder {
        val binding = ItemSortBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SortViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SortViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class SortViewHolder(private val binding: ItemSortBinding) : RecyclerView.ViewHolder(binding.root) {

        // Store the current background color to animate from it
        //private var currentBackgroundColor: Int = 0 // Will be initialized by the first bind call

        // It is necessary to keep the reference to the TextView's GradientDrawable
        private val itemBackgroundDrawable: GradientDrawable? =
            binding.colorBackground.background as? GradientDrawable

        // Stores the current stroke color for the animation
        private var currentStrokeColor: Int = 0
        private val STROKE_WIDTH = 18 // Outline width in dp (adjustment as defined in XML)

        init {
            // Initializes currentStrokeColor to the default background color
            currentStrokeColor = ContextCompat.getColor(binding.root.context, R.color.default_item_background)

            //Sets the initial color of the stroke and fill
            itemBackgroundDrawable?.setStroke(STROKE_WIDTH, currentStrokeColor)
            itemBackgroundDrawable?.setColor(ContextCompat.getColor(binding.root.context, R.color.default_item_background))
            //binding.numberTv.setTextColor(Color.BLACK)
        }

        fun bind(item: SortItem) {
            binding.numberTv.text = item.itemValue.toString()
            //binding.colorBackground.setBackgroundColor(item.color)

            val context = binding.root.context
            //currentStrokeColor = item.color
            itemBackgroundDrawable?.setColor(item.color)

            // Determine the target background color based on item state
            val targetStrokeColor = when {
                item.sorted -> ContextCompat.getColor(context, R.color.orange_swapping)
                item.swapped -> ContextCompat.getColor(context, R.color.green_sorted)
                item.comparing -> ContextCompat.getColor(context, R.color.white)
                else -> item.color
            }

            // Animate background color change
            if (currentStrokeColor != targetStrokeColor) {
                val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), currentStrokeColor, targetStrokeColor)
                colorAnimation.duration = 750 // Short duration for quick color changes
                colorAnimation.addUpdateListener { animator ->
                    val animatedColor = animator.animatedValue as Int
                    // Uses the GradientDrawable's setStroke() function to animate the stroke color
                    itemBackgroundDrawable?.setStroke(STROKE_WIDTH, animatedColor)
                }
                colorAnimation.start()
                currentStrokeColor = targetStrokeColor // Update current color
            } else {
                // If the color hasn't changed, just make sure it's set (important when recycling views)
                itemBackgroundDrawable?.setStroke(STROKE_WIDTH, targetStrokeColor)
            }

            // Scale animation for highlighting (optional, can be added for Swap/Compare)
            val targetScale = if (item.comparing || item.swapped) 1.1f else 1.0f
            if (binding.numberTv.scaleX != targetScale) { // Apply scale animation if moved
                binding.colorBackground.animate()
                    .scaleX(targetScale)
                    .scaleY(targetScale)
                    .setDuration(100)
                    .start()
            }
        }

    }

    class SortDiffCallback : DiffUtil.ItemCallback<SortItem>() {
        override fun areItemsTheSame(oldItem: SortItem, newItem: SortItem): Boolean {
            // Items are the same if their values are the same
            return oldItem.itemValue == newItem.itemValue
        }

        override fun areContentsTheSame(oldItem: SortItem, newItem: SortItem): Boolean {
            // Content is the same if all attributes are the same
            return oldItem == newItem
        }
    }
}
