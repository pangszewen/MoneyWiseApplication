package com.example.moneywise.Expenses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class LineGraph extends View {
    private ArrayList<Double> dataPoints;
    private Paint linePaint;
    private Paint axisPaint;
    private ArrayList<String> monthLabels;
    private float marginStart = 100; // Adjust as needed
    private float marginTop = 60; // Adjust as needed
    private float marginEnd = 60; // Adjust as needed
    private float marginBottom = 100; // Adjust as needed

    public LineGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        dataPoints = new ArrayList<>();
        linePaint = new Paint();
        linePaint.setColor(Color.parseColor("#82C876"));
        linePaint.setStrokeWidth(8);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        axisPaint = new Paint();
        axisPaint.setColor(Color.WHITE);
        axisPaint.setStrokeWidth(3);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setAntiAlias(true);
        axisPaint.setTextSize(45);

        monthLabels = new ArrayList<>();
        // Use numbers 1 to 12 as month labels
        for (int i = 1; i <= 12; i++) {
            monthLabels.add(String.valueOf(i));
        }
    }

    public void setData(ArrayList<Double> points) {
        dataPoints = points;
        invalidate(); // Trigger redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth() - marginStart - marginEnd;
        float height = getHeight() - marginTop - marginBottom;
        float maxX = dataPoints.size() - 1;
        double maxY = getMaxValue(dataPoints);

        float xInterval = width / (monthLabels.size() - 1);  // Use the fixed range of months
        float yScale = (float) (height / (maxY + 0.1 * maxY)); // Adjust the factor (0.1) as needed

        Path path = new Path();


        int numMonthsToDraw = monthLabels.size();  // Draw labels for all months

        for (int i = 0; i < numMonthsToDraw; i++) {
            float x = marginStart + i * xInterval;

            // Draw a line if there's data for the month
            if (i < dataPoints.size()) {
                double dataPoint = dataPoints.get(i);
                float yPos = (float) (height - dataPoint*yScale + marginTop);

                // Draw circular dot
                canvas.drawCircle(x, yPos, 8, axisPaint);

                if (i == 0) {
                    path.moveTo(x, yPos);
                } else {
                    path.lineTo(x, yPos);
                }
            }
            // Draw vertical line for each month
            //canvas.drawLine(x, marginTop, x, height + marginTop, axisPaint);
        }


        // Draw X and Y axes with margins
        canvas.drawLine(marginStart, getHeight() - marginBottom, getWidth() - marginEnd, getHeight() - marginBottom, axisPaint); // X-axis
        canvas.drawLine(marginStart, marginTop, marginStart, getHeight() - marginBottom, axisPaint); // Y-axis

        // Draw X-axis labels
        for (int i = 0; i < numMonthsToDraw; i++) {
            float x = marginStart + i * xInterval;
            float y = getHeight() - marginBottom + 40; // Adjust the distance below the X-axis
            canvas.drawText(monthLabels.get(i), x, y, axisPaint);
        }

        // Draw Y-axis labels
        int numYLabels = 5; // Adjust the number of Y-axis labels as needed
        for (int i = 0; i <= numYLabels; i++) {
            double labelValue = maxY * (i / (double) numYLabels);
            float y = (float) (height - labelValue * yScale) + marginTop;
            canvas.drawText(String.format("%d", (int) labelValue), marginStart-80, y, axisPaint);
        }

        // Draw Y-axis title
        String yAxisTitle = "Expenses";
        float yAxisTitleX = 10; // Adjusted for margins
        float yAxisTitleY = marginTop - 10;
        canvas.drawText(yAxisTitle, yAxisTitleX, yAxisTitleY, axisPaint);

        // Draw X-axis title
        String xAxisTitle = "Months";
        float xAxisTitleX = marginStart + (width - marginStart - marginEnd) / 2;
        float xAxisTitleY = height + 140; // Adjusted for margins
        canvas.drawText(xAxisTitle, xAxisTitleX, xAxisTitleY, axisPaint);

        canvas.drawPath(path, linePaint);


    }


    private double getMaxValue(ArrayList<Double> values) {
        double max = Double.MIN_VALUE;
        for (double value : values) {
            max = Math.max(max, value);
        }
        return max;
    }
}
