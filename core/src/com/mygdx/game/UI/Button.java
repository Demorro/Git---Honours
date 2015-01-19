package com.mygdx.game.UI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.Utility.Utility;

/**
 * Created by Elliot Morris on 14/01/2015.
 */
public abstract class Button extends Sprite {

    protected boolean isBeingHeld = false;
    private static float pressedScalar = 0.9f;

    protected boolean enabled = true;

    public Button(Texture blockSpriteSheet, int buttonX, int buttonY, int buttonWidth, int buttonHeight)
    {
        super(blockSpriteSheet, buttonX, buttonY, buttonWidth, buttonHeight);

    }

    public void Update()
    {
        if(Gdx.app.getType() == Application.ApplicationType.Desktop)
        {

            if(Gdx.app.getInput().isTouched())
            {
                if(this.getBoundingRectangle().contains(Utility.GetScreenSpaceInput())) {
                    isBeingHeld = true;
                    setScale(pressedScalar);
                }
            }
        }
        else if(Gdx.app.getType() == Application.ApplicationType.Android)
        {
            if(Gdx.app.getInput().justTouched())
            {
                if(this.getBoundingRectangle().contains(Gdx.input.getX(), Gdx.input.getY())) {
                    isBeingHeld = true;
                    setScale(pressedScalar);
                }
            }
        }

        if(isBeingHeld == true)
        {
            if(Gdx.app.getType() == Application.ApplicationType.Desktop)
            {
                if(!Gdx.app.getInput().isTouched())
                {
                    isBeingHeld = false;
                    setScale(1.0f);
                    Trigger();
                }
            }
            else if(Gdx.app.getType() == Application.ApplicationType.Android)
            {
                if(!Gdx.app.getInput().isTouched())
                {
                    isBeingHeld = false;
                    setScale(1.0f);
                    Trigger();
                }
            }
        }
    }

    protected abstract void Trigger();

    public void SetEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    public Boolean GetEnabled()
    {
        return enabled;
    }
}
