package com.example.ex4;

// Listen to joystick movement.
public interface JoystickListener {
    void OnMoved(int aileron, int elevator);
}