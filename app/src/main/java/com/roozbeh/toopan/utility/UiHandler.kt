package com.roozbeh.toopan.utility

import android.widget.EditText
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView

object UiHandler {
    fun keyboardDown(editText: EditText, activity: Activity) {
        val mrg = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mrg.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun keyboardUp(editText: EditText?, activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun enableMode(isEnable: Boolean, vararg views: View?) {
        Handler(Looper.getMainLooper()).post(Runnable {
            for (view in views) {
                if (view != null) {
                    view.isEnabled = isEnable
                    view.animate().setDuration(80).alpha(1f)
                }
            }
        })
    }

    fun disableMode(isEnable: Boolean, vararg views: View?) {

        Handler(Looper.getMainLooper()).post(Runnable {
            for (view in views) {
                if (view != null) {
                    view.isEnabled = isEnable
                    view.animate().setDuration(80).alpha(.5f)
                }
            }
        })
    }

    fun pressedMode(background: Int, setAlpha: Boolean, vararg views: View?) {
        Handler(Looper.getMainLooper()).post(Runnable {
            for (view in views) {
                if (view != null) {
                    if (setAlpha) view.animate().setDuration(100).alpha(1f)
                    view.setBackgroundResource(background)
                }
            }
        })
    }

    fun unPressedMode(background: Int, setAlpha: Boolean, vararg views: View?) {
        Handler(Looper.getMainLooper()).post(Runnable {
            for (view in views) {
                if (view != null) {
                    if (setAlpha) view.animate().setDuration(100).alpha(0.5f)
                    view.setBackgroundResource(background)
                }
            }
        })
    }

    fun goneViews(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.visibility = View.GONE
            }
        }
    }

    fun visibleViews(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.visibility = View.VISIBLE
            }
        }
    }

    fun invisibleViews(vararg views: View?) {
        for (view in views) {
            if (view != null) {
                view.visibility = View.INVISIBLE
            }
        }
    }

    fun increaseWidthOrLength(widthOrLength: EditText, addOrMinus: ImageView?) {
        if (widthOrLength.text != null) {
            if (widthOrLength.text.toString() == "" || widthOrLength.text.toString()
                    .startsWith("0")
            ) widthOrLength.setText("1") else if (!widthOrLength.text.toString().startsWith("0")) {
                val t = widthOrLength.text.toString().toInt()
                AnimationManager.blueRedButtonClick(addOrMinus, .3f, 1f)
                if (t < 999) widthOrLength.setText((t + 1).toString())
            }
        }
    }

    fun decreaseWidthOrLength(widthOrLength: EditText, addOrMinus: ImageView?) {
        if (widthOrLength.text != null) {
            if (widthOrLength.text.toString() == "" || widthOrLength.text.toString()
                    .startsWith("0")
            ) widthOrLength.setText("1") else if (!widthOrLength.text.toString().startsWith("0")) {
                val t = widthOrLength.text.toString().toInt()
                AnimationManager.blueRedButtonClick(addOrMinus, .3f, 1f)
                if (t > 1) widthOrLength.setText((t - 1).toString())
            }
        }
    } /*    public static void openImageViewer(Context context, ImageView targetImageView, String imageUrl) {
        ArrayList<String> images = new ArrayList<>();
        images.add(imageUrl); //your img url here
        new StfalconImageViewer.Builder<String>(context, images, new ImageLoader<String>() {
            @Override
            public void loadImage(ImageView imageView, String image) {
                Glide.with(context).load(BASE_IMAGE + image).into(imageView);

            }
        }).withTransitionFrom(targetImageView)
                .show();
    }*/
}