package com.tuanha.language.utils.extention

import android.animation.Animator
import com.daimajia.androidanimations.library.YoYo

fun YoYo.AnimationComposer.doEnd(onAnimationEnd: (Animator?) -> Unit): YoYo.AnimationComposer = withListener(object : DefaultAnimatorListener {
    override fun onAnimationEnd(animation: Animator?) {
        onAnimationEnd.invoke(animation)
    }
})

interface DefaultAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationRepeat(animation: Animator?) {
    }
}