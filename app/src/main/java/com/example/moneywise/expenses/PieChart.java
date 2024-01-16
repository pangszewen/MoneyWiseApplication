package com.example.moneywise.expenses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneywise.R;

import java.util.ArrayList;

public class PieChart extends View {
    private ArrayList<Float> dataValues;
    private ArrayList<Integer> colors;
    private ArrayList<String> categoryTexts;
    private float holeRadiusPercentage = 0.3f;
    private int touchedSliceIndex = -1; // Index of the touched slice

    public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        dataValues = new ArrayList<>();
        colors = new ArrayList<>();
        categoryTexts = new ArrayList<>();
        setOnTouchListener(new OnTouchListener());
    }

    public void setData(ArrayList<Float> values, ArrayList<Integer> sliceColors, ArrayList<String> texts) {
        dataValues = values;
        colors = sliceColors;
        categoryTexts = texts;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (dataValues.isEmpty() || colors.isEmpty()) {
            return;
        }

        float total = 0;

        for (float value : dataValues) {
            total += value;
        }

        float startAngle = 0;

        RectF rectF = new RectF(0, 0, getWidth(), getHeight());

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        float holeRadius = Math.min(getWidth(), getHeight()) * holeRadiusPercentage;

        for (int i = 0; i < categoryTexts.size(); i++) {
            float sweepAngle = 360 * (dataValues.get(i) / total);

            // Draw the outer arc
            paint.setColor(colors.get(i));
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);

            // Draw the inner arc to create the hole (donut)
            paint.setColor(Color.WHITE);
            canvas.drawCircle(rectF.centerX(), rectF.centerY(), holeRadius, paint);

            startAngle += sweepAngle;
        }
    }


    private void drawCategoryText(Canvas canvas, float startAngle, float sweepAngle, String categoryText, float centerX, float centerY) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setAntiAlias(true);

        float halfAngle = (startAngle + sweepAngle / 2);
        float radius = Math.min(centerX, centerY) - holeRadiusPercentage * Math.min(centerX, centerY);
        float textX = (float) (centerX + radius * Math.cos(Math.toRadians(halfAngle)));
        float textY = (float) (centerY + radius * Math.sin(Math.toRadians(halfAngle)));

        canvas.drawText(categoryText, textX, textY, textPaint);
    }

    private class OnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchedSliceIndex = getTouchedSliceIndex(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    touchedSliceIndex = -1;
                    invalidate();
                    break;
            }

            return true;
        }
    }

    private void showCustomToast(String message, float percentage) {
        // Create a custom Toast
        Toast customToast = new Toast(getContext());

        // Inflate the custom layout
        View toastView = LayoutInflater.from(getContext()).inflate(R.layout.custom_toast_layout, null);

        // Find the TextViews in the custom layout
        TextView messageTextView = toastView.findViewById(R.id.custom_toast_message);
        TextView percentageTextView = toastView.findViewById(R.id.custom_toast_percentage);

        // Set the message and percentage
        messageTextView.setText(message);
        percentageTextView.setText(Integer.toString((int)percentage)+"%");

        // Set the custom layout as the view for the Toast
        customToast.setView(toastView);

        // Set the custom layout parameters to display at the top-center of the screen
        customToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 300);

        // Set a duration for the custom Toast
        customToast.setDuration(Toast.LENGTH_SHORT);

        // Show the custom Toast
        customToast.show();
    }



    private int getTouchedSliceIndex(float x, float y) {
        if (dataValues.isEmpty() || colors.isEmpty()) {
            return -1;
        }

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;

        float touchX = x - centerX;
        float touchY = y - centerY;

        double touchAngle = Math.toDegrees(Math.atan2(touchY, touchX));
        touchAngle = (touchAngle + 360) % 360;

        float total = 0;
        for (float value : dataValues) {
            total += value;
        }

        float startAngle = 0;
        for (int i = 0; i < categoryTexts.size(); i++) {
            float sweepAngle = 360 * (dataValues.get(i) / total);

            if (touchAngle >= startAngle && touchAngle < startAngle + sweepAngle) {
                // Show a custom Toast at the upper part of the touched area
                showCustomToast("Category: " + categoryTexts.get(i), dataValues.get(i));

                return i;
            }

            startAngle += sweepAngle;
        }

        return -1; // No slice is touched
    }



}
