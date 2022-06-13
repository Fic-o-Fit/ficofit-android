package com.c22ps072.ficofit.utils

import android.animation.ObjectAnimator
import android.view.View

object Helpers {
    fun View.isVisible(isVisible : Boolean, duration: Long = 400) {
        ObjectAnimator
            .ofFloat(this, View.ALPHA,if (isVisible) 1f else 0f)
            .setDuration(duration)
            .start()
    }

    fun Int.setOrdinal() : String {
        val j = this % 10
        val k = this % 100
        if (j == 1 && k != 11) {
            return this.toString() + "st"
        }
        if (j == 2 && k != 12) {
            return this.toString() + "nd"
        }
        if (j == 3 && k != 13) {
            return this.toString() + "rd"
        }
        return this.toString() + "th"
    }
}