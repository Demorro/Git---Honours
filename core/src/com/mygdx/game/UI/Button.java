package com.mygdx.game.UI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utility.Utility;

/**
 * Created by Elliot Morris on 14/01/2015.
 */
public abstract class Button extends Sprite {

    protected boolean isBeingHeld = false;
    private static float pressedScalar = 0.9f;

    protected boolean enabled = true;
    protected  boolean visible = true;

    private BitmapFont buttonFont;
    protected String buttonText = new String();
    private Vector2 textOffset = new Vector2(0,0);

    private boolean shouldScaleWhenDepressed = false;
    private  boolean shouldChangeSpriteWhenDepressed = false;

    private TextureRegion nonDepressedRegion;
    private TextureRegion depressedRegion;

    public Button(Texture blockSpriteSheet, int buttonX, int buttonY, int buttonWidth, int buttonHeight, boolean font2xSize)
    {
        super(blockSpriteSheet, buttonX, buttonY, buttonWidth, buttonHeight);
        nonDepressedRegion = new TextureRegion(blockSpriteSheet, buttonX, - buttonY, buttonWidth, buttonHeight);
        depressedRegion = new TextureRegion(blockSpriteSheet,buttonX, buttonY, buttonWidth, buttonHeight);

        shouldScaleWhenDepressed = true;
        shouldChangeSpriteWhenDepressed = false;

        if(font2xSize)
        {
            buttonFont = new BitmapFont(Gdx.files.internal("Fonts/8Bitfont2x.fnt"));
        }
        else
        {
            buttonFont = new BitmapFont(Gdx.files.internal("Fonts/8Bitfont.fnt"));
        }
        buttonFont.setColor(Color.BLACK);
        buttonFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

    }
    public Button(Texture blockSpriteSheet, int buttonX, int buttonY, int buttonWidth, int buttonHeight, int depressedButtonX, int depressedButtonY, int depressedButtonWidth, int depressedButtonHeight, boolean scaleWhenDepressed,  boolean font2xSize)
    {
        super(blockSpriteSheet, buttonX, buttonY, buttonWidth, buttonHeight);
        nonDepressedRegion = new TextureRegion(blockSpriteSheet,buttonX, buttonY, buttonWidth, buttonHeight);
        depressedRegion = new TextureRegion(blockSpriteSheet,depressedButtonX, depressedButtonY, depressedButtonWidth, depressedButtonHeight);
        shouldScaleWhenDepressed = scaleWhenDepressed;
        shouldChangeSpriteWhenDepressed = true;

        if(font2xSize)
        {
            buttonFont = new BitmapFont(Gdx.files.internal("Fonts/8Bitfont2x.fnt"));
        }
        else
        {
            buttonFont = new BitmapFont(Gdx.files.internal("Fonts/8Bitfont.fnt"));
        }
        buttonFont.setColor(Color.BLACK);
        buttonFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void Update()
    {
        if(Gdx.app.getType() == Application.ApplicationType.Desktop)
        {

            if(Gdx.app.getInput().justTouched())
            {
                if(this.getBoundingRectangle().contains(Utility.GetScreenSpaceInput())) {
                    isBeingHeld = true;
                    if (shouldScaleWhenDepressed) {
                        setScale(pressedScalar);
                    }
                }
            }
        }
        else if(Gdx.app.getType() == Application.ApplicationType.Android)
        {
            if(Gdx.app.getInput().justTouched())
            {
                if(this.getBoundingRectangle().contains(Gdx.input.getX(), Gdx.input.getY())) {
                    isBeingHeld = true;
                    if (shouldScaleWhenDepressed) {
                        setScale(pressedScalar);
                    }
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
                    if(enabled) {
                        Trigger();
                    }
                }
            }
            else if(Gdx.app.getType() == Application.ApplicationType.Android)
            {
                if(!Gdx.app.getInput().isTouched())
                {
                    isBeingHeld = false;
                    setScale(1.0f);
                    if(enabled) {
                        Trigger();
                    }
                }
            }
        }
    }

    public void Render(SpriteBatch batch)
    {
        if(visible) {
            if(shouldChangeSpriteWhenDepressed)
            {
               if(isBeingHeld)
               {
                   batch.draw(depressedRegion, getX(), getY(), depressedRegion.getRegionWidth(), depressedRegion.getRegionHeight());
                   buttonFont.draw(batch, buttonText, getX() + depressedRegion.getRegionWidth()/2 - buttonFont.getBounds(buttonText).width/2 + textOffset.x, getY() + depressedRegion.getRegionHeight()/2 + buttonFont.getBounds(buttonText).height/2 + textOffset.y);
               }
                else
               {
                   batch.draw(nonDepressedRegion, getX(), getY(), nonDepressedRegion.getRegionWidth(), nonDepressedRegion.getRegionHeight());
                   buttonFont.draw(batch, buttonText, getX() + nonDepressedRegion.getRegionWidth()/2 - buttonFont.getBounds(buttonText).width/2 + textOffset.x, getY() + nonDepressedRegion.getRegionHeight()/2 + buttonFont.getBounds(buttonText).height/2 + textOffset.y);
               }
            }
            else {
                this.draw(batch);
                buttonFont.draw(batch, buttonText, getX() + getWidth()/2 - buttonFont.getBounds(buttonText).width/2 + textOffset.x, getY() + getHeight()/2 + buttonFont.getBounds(buttonText).height/2 + textOffset.y);
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

    public void SetVisible(boolean visible){ this.visible = visible;}
    public  Boolean GetVisible() {return visible;}

    public void SetText(String text) {
        buttonText = text;

    }
    public String GetText() {
        return buttonText;
    }

    public void SetFontScale(float scale){
        buttonFont.setScale(scale);
    }
    public void SetTextOffset(int xOff, int yOff)
    {
        textOffset.x = xOff;
        textOffset.y = yOff;
    }
}
