package com.roozbeh.toopan.utility

import android.view.animation.TranslateAnimation
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.roozbeh.toopan.utility.AnimationManager
import android.os.CountDownTimer
import android.view.animation.ScaleAnimation
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView

object AnimationManager {
    fun translateAnimation(
        view: View,
        X0: Float,
        X1: Float,
        Y0: Float,
        Y1: Float,
        duration: Int,
        repeatCount: Int
    ) {
        val translateAnimation = TranslateAnimation(X0, X1, Y0, Y1)
        translateAnimation.duration = duration.toLong()
        translateAnimation.repeatCount = repeatCount
        view.startAnimation(translateAnimation)
    }

    fun clickPressAnimation(view: View?, fadeInDuration: Int, fadeOutDuration: Int) {
        val fadeIn =
            ObjectAnimator.ofFloat(view, "alpha", 0.3f).setDuration(fadeInDuration.toLong())
        val fadeOut =
            ObjectAnimator.ofFloat(view, "alpha", 0f).setDuration(fadeOutDuration.toLong())
        val animatorSet = AnimatorSet()
        animatorSet.play(fadeIn).before(fadeOut)
        animatorSet.start()
    }

    fun fadeAnimation(
        view: View,
        fromAlpha: Float,
        toAlpha: Float,
        duration: Int,
        repeatCount: Int
    ) {
        val alphaAnimation = AlphaAnimation(fromAlpha, toAlpha)
        alphaAnimation.duration = duration.toLong()
        alphaAnimation.repeatCount = repeatCount
        alphaAnimation.repeatMode = Animation.REVERSE
        view.startAnimation(alphaAnimation)
    }

    fun fadeView(view: View?, duration: Int, fromAlpha: Float, toAlpha: Float) {
        val fadeIn =
            ObjectAnimator.ofFloat(view, "alpha", fromAlpha, toAlpha).setDuration(duration.toLong())
        val animatorSet = AnimatorSet()
        animatorSet.play(fadeIn)
        animatorSet.start()
    }

    fun blueRedButtonClick(view: View?, fromAlpha: Float, toAlpha: Float) {
        fadeView(view, 60, fromAlpha, toAlpha)
        val countDownTimer: CountDownTimer = object : CountDownTimer(50, 10) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                fadeView(view, 50, toAlpha, fromAlpha)
            }
        }
        countDownTimer.start()
    }

    fun scaleView(
        v: View,
        startScale: Float,
        endScale: Float,
        duration: Int,
        repeatCount: Int,
        repeatMode: Int
    ) {
        val anim: Animation = ScaleAnimation(
            startScale, endScale,  // Start and end values for the X axis scaling
            startScale, endScale, 0.5f, 0.5f
        ) // Start and end values for the Y axis scaling
        anim.fillAfter = true // Needed to keep the result of the animation
        anim.duration = duration.toLong()
        anim.repeatMode = repeatMode
        anim.repeatCount = repeatCount
        v.startAnimation(anim)
    }

    fun rotateScanButtonAnimation(
        view: View?,
        duration: Int,
        repeatCount: Int,
        repeatMode: Int
    ): ObjectAnimator {
        val rotation = PropertyValuesHolder.ofFloat("rotation", 0f, 360f)
        val objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, rotation)
        objectAnimator.duration = duration.toLong()
        objectAnimator.repeatCount = repeatCount
        objectAnimator.repeatMode = repeatMode
        objectAnimator.start()
        return objectAnimator
        //        view.startAnimation(alphaAnimation);
    }

    fun shakeAnimation(view: View?) {
        ObjectAnimator
            .ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
            .setDuration(500)
            .start()
    }

    fun translationYAnimator(startValue: Float, endValue: Float, view: View): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(startValue, endValue)
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            view.translationY = value
        }
        return valueAnimator
    }

    fun horizontalWeightAnimator(startValue: Float, endValue: Float, view: View): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(startValue, endValue)
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            val lp = view.layoutParams as ConstraintLayout.LayoutParams
            lp.horizontalWeight = value
            view.layoutParams = lp
        }
        return valueAnimator
    }

    fun changeHeightAnimator(startValue: Float, endValue: Float, view: View): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(startValue, endValue)
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            val lp = view.layoutParams as ConstraintLayout.LayoutParams
            lp.matchConstraintPercentHeight = value
            view.layoutParams = lp
        }
        return valueAnimator
    }

    fun VectorDrawableAnimation(context: Context?, imageView: ImageView, animation: Int) {
        val animatedVector = AnimatedVectorDrawableCompat.create(context!!, animation)
        imageView.setImageDrawable(animatedVector)
        if (animatedVector != null) {
            animatedVector.registerAnimationCallback(object :
                Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable) {
                    imageView.post { animatedVector.start() }
                }
            })
            animatedVector.start()
        }
    }
}