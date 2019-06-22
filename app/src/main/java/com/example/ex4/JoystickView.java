package com.example.ex4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {

    private Paint surfacePaint, handlePaint;
    private double handleX, handleY;
    private Point mid = new Point();
    private int handleRadius, sensitivity, surfaceRadius;
    private JoystickListener listener;

    // Constructors for joystick view.
    public JoystickView(Context context) {
        super(context);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
        initJoystickView();
    }

    // Default init if no special attributes delivered.
    private void initJoystickView() {
        //setFocusable(true);
        // Default style for surface and handle painting.
        surfacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        surfacePaint.setColor(Color.DKGRAY);
        surfacePaint.setStrokeWidth(5);
        surfacePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(Color.RED);
        handlePaint.setStrokeWidth(5);
        handlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        sensitivity = 100; // Sensitivity of data (.2f).
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

        // Set middle points after measure.
        mid.x = measuredWidth / 2;
        mid.y = measuredHeight / 2;
        // Set radius multiply by ratio (percent of the screen).
        surfaceRadius = Math.min(mid.x, mid.y);
        surfaceRadius *= 0.7;

        // Handle radius size defined as 40% of the surface size.
        handleRadius = (int) (surfaceRadius * 0.4);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    // Default size.
    private int measure(int measureSpec) {
        return MeasureSpec.getMode(measureSpec) ==
                MeasureSpec.UNSPECIFIED ? 200 : MeasureSpec.getSize(measureSpec);
    }

    // Check if movement is inside circle.
    Boolean insideCircle(float x, float y) {
        double distance = Math.sqrt((mid.x - x) * (mid.x - x) + (mid.y - y)
                * (mid.y - y));
        return distance <= surfaceRadius;
    }

    // Find from middle to point.
    public float getAngle(float x, float y) {
        float angle = (float) Math.toDegrees(Math.atan2(y - mid.y, x - mid.x));
        return angle < 0 ? angle + 360 : angle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw the surface
        canvas.drawCircle(mid.x, mid.y, surfaceRadius, surfacePaint);

        // Draw the handle
        canvas.drawCircle((int) handleX + mid.x, (int) handleY + mid.y,
                handleRadius, handlePaint);
        canvas.save();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getAction();

        if (userAction == MotionEvent.ACTION_MOVE) {

            if (insideCircle(event.getX(), event.getY())) {
                handleX = event.getX() - mid.x;
                handleY = event.getY() - mid.y;
            } else {
                float angle = getAngle(event.getX(), event.getY());
                handleX = (float) (surfaceRadius * Math.cos(angle * Math.PI / 180F));
                handleY = (float) (surfaceRadius * Math.sin(angle * Math.PI / 180F));
            }

            if (listener != null) {
                listener.OnMoved(Math.ceil((handleX / surfaceRadius) * sensitivity) / sensitivity,
                        Math.ceil((handleY / surfaceRadius) * sensitivity) / sensitivity);
            }
            invalidate();
        } else if (userAction == MotionEvent.ACTION_UP) {
            Handler handler = new Handler();
            int frameRate = 5;
            final double intervalsX = (0 - handleX) / frameRate;
            final double intervalsY = (0 - handleY) / frameRate;

            for (int i = 0; i < frameRate; i++) {
                handler.postDelayed(() -> {
                    handleX += intervalsX;
                    handleY += intervalsY;
                    invalidate();
                }, i * 40);
            }
        }
        return true;
    }
}