package com.mygdx.game.Utility;


import com.badlogic.gdx.math.Vector2;

/**
 * Created by Elliot on 20/01/14.
 */
public class TouchInfo
{
    public float tapDownX = 0;
    public float tapDownY = 0;
    public float currentTouchX = 0;
    public float currentTouchY = 0;
    public boolean isTapping = false;
    public boolean isDragging = false;

    public float lastTouchX = 0;
    public float lastTouchY = 0;

    public Vector2 GetDragVelocity()
    {
        Vector2 dragVelocity = new Vector2();

        dragVelocity.x = currentTouchX - lastTouchX;
        dragVelocity.y = currentTouchY - lastTouchY;

        return dragVelocity;
    }

    public void Reset()
    {
        tapDownX = 0;
        tapDownY = 0;
        currentTouchX = 0;
        currentTouchY = 0;
        isTapping = false;
        isDragging = false;
    }
}
