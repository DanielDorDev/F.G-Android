package com.example.ex4;

public interface JoystickListener {

    void OnMoved(int pan, int tilt);

    void OnReleased();

}