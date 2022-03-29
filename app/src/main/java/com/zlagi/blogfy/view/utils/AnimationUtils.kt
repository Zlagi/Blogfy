package com.zlagi.blogfy.view.utils

import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation

object AnimationUtils {

    /**
     * A class which adds [DynamicAnimation.OnAnimationEndListener]s to the given `springs` and invokes
     * `onEnd` when all have finished.
     */
    class MultiSpringEndListener(
        onEnd: (Boolean) -> Unit,
        vararg springs: SpringAnimation
    ) {
        private val listeners = ArrayList<DynamicAnimation.OnAnimationEndListener>(springs.size)

        private var wasCancelled = false

        init {
            springs.forEach {
                val listener = object : DynamicAnimation.OnAnimationEndListener {
                    override fun onAnimationEnd(
                        animation: DynamicAnimation<out DynamicAnimation<*>>?,
                        canceled: Boolean,
                        value: Float,
                        velocity: Float
                    ) {
                        animation?.removeEndListener(this)
                        wasCancelled = wasCancelled or canceled
                        listeners.remove(this)
                        if (listeners.isEmpty()) {
                            onEnd(wasCancelled)
                        }
                    }
                }
                it.addEndListener(listener)
                listeners.add(listener)
            }
        }
    }


    fun listenForAllSpringsEnd(
        onEnd: (Boolean) -> Unit,
        vararg springs: SpringAnimation
    ) = MultiSpringEndListener(onEnd, *springs)
}
