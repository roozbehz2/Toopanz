package com.roozbeh.toopan.dialog;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;


public abstract class Dialog extends android.app.Dialog implements View.OnClickListener, DialogInterface {

    protected boolean checked = false;
    protected OnSubmitClickListener onSubmitClickListener;
    protected OnClickListener onClickListener;
    protected OnProgressListener onProgressListener;

    Dialog(@NonNull Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setDimAmount(0.85f);

        initDialog();

    }


    abstract void initDialog();


    public void setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        this.onSubmitClickListener = onSubmitClickListener;
    }

    public interface OnSubmitClickListener {

        void onSubmitClick();
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {

//        void onPositiveClick();

        void submitSetTime(String start, String end);

       /* void onNegativeClick();

        void onCloseClick();*/

    }


    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public interface OnProgressListener {
        void onProgress(int progress);

        void onComplete();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void setTimes(@NonNull String start, @NonNull String end) {

    }
}
