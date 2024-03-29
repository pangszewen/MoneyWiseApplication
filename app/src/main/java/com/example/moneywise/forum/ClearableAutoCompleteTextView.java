package com.example.moneywise.forum;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.moneywise.R;

public class ClearableAutoCompleteTextView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    boolean justCleared = false;
    private OnClearListener onClearListener;
    private Drawable clearButton = getResources().getDrawable(R.drawable.baseline_close_black);

    public interface OnClearListener {
        void onClear();
    }

    public ClearableAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public ClearableAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, clearButton, null);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClearableAutoCompleteTextView et = ClearableAutoCompleteTextView.this;

                if (et.getCompoundDrawables()[2] == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                if (event.getX() > et.getWidth() - et.getPaddingRight() - clearButton.getIntrinsicWidth()) {
                    onClearListener.onClear();
                    justCleared = true;
                }
                return false;
            }
        });
    }

    public void setOnClearListener(OnClearListener listener) {
        onClearListener = listener;
    }

}