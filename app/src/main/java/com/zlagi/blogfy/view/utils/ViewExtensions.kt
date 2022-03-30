package com.zlagi.blogfy.view.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.dynamicanimation.animation.DynamicAnimation.ViewProperty
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.zlagi.blogfy.R
import com.zlagi.blogfy.view.utils.ViewExt.PERMISSIONS_REQUEST_READ_STORAGE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun Activity.hideKeyboard() {
    val insetsControllerCompat = ViewCompat.getWindowInsetsController(window.decorView)
    insetsControllerCompat?.hide(WindowInsetsCompat.Type.ime())
}

fun Fragment.showToast(text: Int) {
    val toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
    toast.show()
}

fun Activity.showKeyboard() {
    val insetsControllerCompat = ViewCompat.getWindowInsetsController(window.decorView)
    insetsControllerCompat?.show(WindowInsetsCompat.Type.ime())
}

fun Fragment.showSnackBar(failure: Int, timeLength: Int) {
    Snackbar.make(requireView(), failure, timeLength).show()
}

fun <T> LifecycleOwner.collectWhenStarted(
    flow: Flow<T>,
    firstTimeDelay: Long = 0L,
    action: suspend (value: T) -> Unit
) {
    lifecycleScope.launch {
        delay(firstTimeDelay)
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(action)
        }
    }
}

fun Fragment.isStoragePermissionGranted(): Boolean {
    if (
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        != PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSIONS_REQUEST_READ_STORAGE
        )
        return false
    } else {
        // Permission has already been granted
        return true
    }
}

/**
 * An extension function which creates/retrieves a [SpringAnimation] and stores it in the [View]s
 * tag.
 */
fun View.spring(
    property: ViewProperty,
    stiffness: Float = 200f,
    damping: Float = 0.3f,
    startVelocity: Float? = null
): SpringAnimation {
    val key = getKey(property)
    var springAnim = getTag(key) as? SpringAnimation?
    if (springAnim == null) {
        springAnim = SpringAnimation(this, property).apply {
            spring = SpringForce().apply {
                this.dampingRatio = damping
                this.stiffness = stiffness
                startVelocity?.let { setStartVelocity(it) }
            }
        }
        setTag(key, springAnim)
    }
    return springAnim
}

/**
 * Map from a [ViewProperty] to an `id` suitable to use as a [View] tag.
 */
@IdRes
private fun getKey(property: ViewProperty): Int {
    return when (property) {
        SpringAnimation.TRANSLATION_X -> R.id.translation_x
        SpringAnimation.TRANSLATION_Y -> R.id.translation_y
        SpringAnimation.TRANSLATION_Z -> R.id.translation_z
        SpringAnimation.SCALE_X -> R.id.scale_x
        SpringAnimation.SCALE_Y -> R.id.scale_y
        SpringAnimation.ROTATION -> R.id.rotation
        SpringAnimation.ROTATION_X -> R.id.rotation_x
        SpringAnimation.ROTATION_Y -> R.id.rotation_y
        SpringAnimation.X -> R.id.x
        SpringAnimation.Y -> R.id.y
        SpringAnimation.Z -> R.id.z
        SpringAnimation.ALPHA -> R.id.alpha
        SpringAnimation.SCROLL_X -> R.id.scroll_x
        SpringAnimation.SCROLL_Y -> R.id.scroll_y
        else -> throw IllegalAccessException("Unknown ViewProperty: $property")
    }
}

object ViewExt {
    const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
}
