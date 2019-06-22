package com.example.ex4;

// Listen to joystick movement.
public interface JoystickListener {
    void OnMoved(double aileron, double elevator);
}