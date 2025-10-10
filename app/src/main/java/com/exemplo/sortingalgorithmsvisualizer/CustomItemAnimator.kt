package com.exemplo.sortingalgorithmsvisualizer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class CustomItemAnimator : DefaultItemAnimator() {

    private val moveAnimationDuration = 750L

    private val pendingMoveAnimations: MutableMap<RecyclerView.ViewHolder, Animator> = mutableMapOf()

    override fun animateMove(
        holder: RecyclerView.ViewHolder,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        // Clear any existing move animations for this holder
        pendingMoveAnimations[holder]?.cancel()

        val view = holder.itemView
        // Calculate current translation from previous animation (if any)
        val prevTranslationX = view.translationX
        val prevTranslationY = view.translationY

        // Reset view's translations to 0 before calculating new deltas,
        // as the RecyclerView handles the final position
        view.translationX = 0f
        view.translationY = 0f

        val deltaX = toX - fromX // Difference in X position
        val deltaY = toY - fromY // Difference in Y position

        // If there's no actual movement, just dispatch and return
        if (deltaX == 0 && deltaY == 0) {
            dispatchAnimationFinished(holder)
            return false
        }

        // Create PropertyValuesHolder objects for TRANSLATION_X and TRANSLATION_Y
        val pvhX = PropertyValuesHolder.ofFloat(View.TRANSLATION_X, prevTranslationX, prevTranslationX + deltaX.toFloat())
        val pvhY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, prevTranslationY, prevTranslationY + deltaY.toFloat())

        val moveAnimator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY)
        moveAnimator.duration = moveAnimationDuration

        moveAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationCancel(animation: Animator) {
                // Garante que o RecyclerView saiba que a animação foi cancelada
                // e pode finalizar o estado do item.
                dispatchAnimationFinished(holder)
                // Ensure translation is reset if animation is cancelled
                view.translationX = 0f
                view.translationY = 0f
            }

            override fun onAnimationEnd(animation: Animator) {
                // Ensure translation is reset at the end of the animation
                view.translationX = 0f
                view.translationY = 0f
                dispatchAnimationFinished(holder)
                pendingMoveAnimations.remove(holder)
            }
        })

        // Store the animator for this holder
        pendingMoveAnimations[holder] = moveAnimator
        moveAnimator.start()
        return true
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        // You can add custom add animations here, or let DefaultItemAnimator handle it.
        // For simplicity, we can just call super if no specific add animation is desired.
        // If you want to make it explicit, you could do:
        // holder?.itemView?.alpha = 0f
        // holder?.itemView?.animate()?.alpha(1f)?.setDuration(addDuration)?.setListener(
        //     object : AnimatorListenerAdapter() {
        //         override fun onAnimationEnd(animation: Animator) {
        //             dispatchAddFinished(holder)
        //         }
        //     }
        // )?.start()
        // return true
        return super.animateAdd(holder)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        // You can add custom remove animations here, or let DefaultItemAnimator handle it.
        return super.animateRemove(holder)
    }

    override fun runPendingAnimations() {
        super.runPendingAnimations()
    }

    override fun isRunning(): Boolean {
        return super.isRunning() || pendingMoveAnimations.isNotEmpty()
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        pendingMoveAnimations[item]?.cancel()
        pendingMoveAnimations.remove(item)
    }

    override fun endAnimations() {
        super.endAnimations()
        for (animator in pendingMoveAnimations.values) {
            animator.cancel()
        }
        pendingMoveAnimations.clear()
    }
}