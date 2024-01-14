package com.example.moneywise.Expenses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularProgressBar extends View {
    private float progress;

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Initialization code goes here
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        // Calculate available dimensions
        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        // Calculate center coordinates
        int centerX = (width + paddingLeft) / 2;
        int centerY = (height + paddingTop) / 2;

        // Calculate radius as half of the minimum dimension
        int radius = Math.min(width, height) / 2;

        // Paint for the background circle
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#d9d9d9")); // Set the background color
        bgPaint.setStrokeWidth(50);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);

        // Paint for the progress arc
        Paint progressPaint = new Paint();
        progressPaint.setColor(Color.parseColor("#3498db")); // Set the progress color to blue
        progressPaint.setStrokeWidth(50);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        // Define the bounding rectangle for the oval
        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // Draw the background circle
        canvas.drawArc(rectF, 0, 360, false, bgPaint);

        // Draw the progress arc
        canvas.drawArc(rectF, -90, progress * 360, false, progressPaint);
    }

    // Set the progress value (percentage)
    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }
}
