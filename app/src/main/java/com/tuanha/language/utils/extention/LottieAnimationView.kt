package com.tuanha.language.utils.extention

import android.animation.Animator
import com.airbnb.lottie.LottieAnimationView

fun LottieAnimationView.doEnd(onAction: () -> Unit): LottieAnimationView = apply {

    addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {
            removeAnimatorListener(this)
            onAction.invoke()
        }

        override fun onAnimationCancel(animation: Animator?) {
            removeAnimatorListener(this)
            onAction.invoke()
        }

        override fun onAnimationRepeat(animation: Animator?) {
        }

    })
}