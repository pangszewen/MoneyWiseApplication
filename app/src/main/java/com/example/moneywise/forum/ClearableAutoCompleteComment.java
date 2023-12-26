package com.example.moneywise.forum;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.moneywise.R;

public class ClearableAutoCompleteComment extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {
    boolean justCleared = false;
    private OnClearListener onClearListener;
    private Drawable sendButton = getResources().getDrawable(R.drawable.baseline_send_dark_blue);

    public interface OnClearListener {
        void onClear();
    }

    public ClearableAutoCompleteComment(Context context) {
        super(context);
        init();
    }

    public ClearableAutoCompleteComment(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClearableAutoCompleteComment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setCompoundDrawablesWithIntrinsicBounds(null, null, sendButton, null);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ClearableAutoCompleteComment et = ClearableAutoCompleteComment.this;

                if (et.getCompoundDrawables()[2] == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                if (event.getX() > et.getWidth() - et.getPaddingRight() - sendButton.getIntrinsicWidth()) {
                    et.clearFocus();
                    onClearListener.onClear();
                    justCleared = true;
                    return true;
                }
                return false;
            }
        });
    }

    public void setOnClearListener(OnClearListener listener) {
        onClearListener = listener;
    }

}
