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

// View for joystick.
public class JoystickView extends View {

    // Paint for surface(handle is inside surface and move there) and handle.
    private Paint surfacePaint, handlePaint;

    // Handle width and height.
    private double handleX, handleY;

    // Middle screen, surface circle positioned there.
    private Point mid = new Point();

    // Handle radius, sensitivity(float .xf), surface radius.
    private int handleRadius, sensitivity, surfaceRadius;

    // Observer to joystick movement.
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

    // Screen size (after change occur).
    private int measure(int measureSpec) {
        return MeasureSpec.getMode(measureSpec) ==
                MeasureSpec.UNSPECIFIED ? 200 : MeasureSpec.getSize(measureSpec);
    }

    // Check if user position is inside circle.
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

    // User touch the screen.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getAction();

        // If action type is move.
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
            // If action type is up(release).
        } else if (userAction == MotionEvent.ACTION_UP) {

            // Move Joystick back to center, with interval movement.
            Handler handler = new Handler();
            int frameRate = 5;  // Speed of movement back to center.
            final double intervalsX = (0 - handleX) / frameRate;
            final double intervalsY = (0 - handleY) / frameRate;

            // Every time move a bit more to the center, until reached.
            for (int i = 0; i < frameRate; i++) {
                handler.postDelayed(() -> {
                    handleX += intervalsX;
                    handleY += intervalsY;
                    invalidate();
                    // Interval become bigger when approach to center(add mechanical touch).
                }, i * 40);
            }
        }
        return true;
    }
}