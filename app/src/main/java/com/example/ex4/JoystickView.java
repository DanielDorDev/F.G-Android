package com.example.ex4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    private Paint surfacePaint, handlePaint;
    private double handleX, handleY;
    private int innerPadding, handleRadius, sensitivity, handleInnerBoundaries;
    private JoystickListener listener;

    // Constructors for joystick view.
    public JoystickView(Context context) {
        super (context);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super (context, attrs);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs,
                        int defStyle) {
        super (context, attrs, defStyle);
        initJoystickView();
    }

    // Default init if no special attributes delivered.
    private void initJoystickView() {
        setFocusable(true);

        surfacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        surfacePaint.setColor(Color.GRAY);
        surfacePaint.setStrokeWidth(2);
        surfacePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(Color.DKGRAY);
        handlePaint.setStrokeWidth(2);
        handlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerPadding = 5;
        sensitivity = 100;
    }

    // Set listener to joystick, in this exercise one is enough.
    public void setJoystickListener(JoystickListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Here we make sure that we have a perfect circle
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredWidth, measuredHeight);

        handleRadius = (int) (d * 0.15);
        handleInnerBoundaries = handleRadius;

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Return a default size of 200 if no bounds are specified.
            result = 200;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int px = getMeasuredWidth() / 2;
        int py = getMeasuredHeight() / 2;
        int radius = Math.min(px, py);

        // Draw the background
        canvas.drawCircle(px, py, radius - innerPadding, surfacePaint);

        // Draw the handle
        canvas.drawCircle((int) handleX + px, (int) handleY + py,
                handleRadius, handlePaint);

        canvas.save();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getAction();
        if (userAction == MotionEvent.ACTION_MOVE) {
            int px = getMeasuredWidth() / 2;
            int py = getMeasuredHeight() / 2;
            int radius = Math.min(px, py) - handleInnerBoundaries;

            handleX = Math.max(Math.min(event.getX() - px, radius), - radius);
            handleY = Math.max(Math.min((event.getY() - py), radius), - radius);

            if (listener != null) {
                listener.OnMoved(Math.ceil((handleX / radius) * sensitivity) / sensitivity,
                        Math.ceil((handleY / radius) * sensitivity)  / sensitivity);
            }
            invalidate();
        } else if (userAction == MotionEvent.ACTION_UP) {
            returnHandleToCenter();
        }
        return true;
    }

    private void returnHandleToCenter() {

        Handler handler = new Handler();
        int numberOfFrames = 5;
        final double intervalsX = (0 - handleX) / numberOfFrames;
        final double intervalsY = (0 - handleY) / numberOfFrames;

        for (int i = 0; i < numberOfFrames; i++) {
            handler.postDelayed(() -> {
                handleX += intervalsX;
                handleY += intervalsY;
                invalidate();
            }, i * 40);
        }
    }
}