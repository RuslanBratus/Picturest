package com.det.picturest.utils.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat

class ActivityExtensions {
    companion object {
        fun Activity.hideKeyboardAndFocus() {
            val viewFocused = this.currentFocus
            val inputManager =
                ContextCompat.getSystemService(
                    this.applicationContext,
                    InputMethodManager::class.java
                )
            viewFocused?.let {
                inputManager!!.hideSoftInputFromWindow(it.windowToken, 0)
            }
            this.loseEditViewFocus()
        }

        fun Activity.loseEditViewFocus() {
            val viewFocused = this.currentFocus
            if (viewFocused is EditText) {
                val outRect = Rect()
                viewFocused.getGlobalVisibleRect(outRect)
                viewFocused.clearFocus()
            }
        }
    }
}