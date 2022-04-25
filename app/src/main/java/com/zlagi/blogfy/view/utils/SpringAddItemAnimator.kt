package com.zlagi.blogfy.view.utils

import android.view.View
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.zlagi.blogfy.view.utils.AnimationUtils.listenForAllSpringsEnd

class SpringAddItemAnimator : DefaultItemAnimator() {

    private val pendingAdds = mutableListOf<RecyclerView.ViewHolder>()

    companion object {
        private const val alpha = 0f
        private const val translationY = 3f
    }

    /**
     * Setup initial values to animate. Derive initial translationY from the view's bottom to
     * produce a stagger effect, as lower items arrive from father displaced.
     */
    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = alpha
        holder.itemView.translationY = holder.itemView.bottom / translationY
        pendingAdds.add(holder)
        return true
    }

    /**
     * Animate back to full alpha and no translation.
     */
    override fun runPendingAnimations() {
        super.runPendingAnimations()
        if (pendingAdds.isNotEmpty()) {
            for (i in pendingAdds.indices.reversed()) {
                val holder = pendingAdds[i]

                val tySpring = holder.itemView.spring(
                    SpringAnimation.TRANSLATION_Y,
                    stiffness = 350f,
                    damping = 0.6f
                )
                val aSpring = holder.itemView.spring(
                    SpringAnimation.ALPHA,
                    stiffness = 100f,
                    damping = SpringForce.DAMPING_RATIO_NO_BOUNCY
                )

                listenForAllSpringsEnd(
                    { cancelled ->
                        if (!cancelled) {
                            dispatchAddFinished(holder)
                            dispatchFinishedWhenDone()
                        } else {
                            clearAnimatedValues(holder.itemView)
                        }
                    },
                    tySpring, aSpring
                )
                dispatchAddStarting(holder)
                aSpring.animateToFinalPosition(1f)
                tySpring.animateToFinalPosition(0f)
                pendingAdds.removeAt(i)
            }
        }
    }

    override fun endAnimation(holder: RecyclerView.ViewHolder) {
        holder.itemView.spring(SpringAnimation.TRANSLATION_Y).cancel()
        holder.itemView.spring(SpringAnimation.ALPHA).cancel()
        if (pendingAdds.remove(holder)) {
            dispatchAddFinished(holder)
            clearAnimatedValues(holder.itemView)
        }
        super.endAnimation(holder)
    }

    override fun endAnimations() {
        for (i in pendingAdds.indices.reversed()) {
            val holder = pendingAdds[i]
            clearAnimatedValues(holder.itemView)
            dispatchAddFinished(holder)
            pendingAdds.removeAt(i)
        }
        super.endAnimations()
    }

    override fun isRunning(): Boolean {
        return pendingAdds.isNotEmpty() || super.isRunning()
    }

    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    private fun clearAnimatedValues(view: View) {
        view.alpha = 1f
        view.translationY = 0f
    }

}
