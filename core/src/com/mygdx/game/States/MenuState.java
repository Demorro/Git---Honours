package com.mygdx.game.States;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Utility.TouchInfo;

import java.util.HashMap;

/**
 * Created by Elliot on 19/01/14.
 */
public class MenuState extends State
{
    private Texture texture;
    private Sprite sprite;

    public MenuState()
    {
        super(State.StateID.MENU_STATE);
    }

    @Override
    public void RunOnGameOpen() {

    }

    //Abstract method that runs on state initialisation, for loading resources
    public boolean Load()
    {
        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        sprite = new Sprite(texture);
        return true;
    }
    //Abstract method that runs on state destruction, for cleaning up memory
    public void Dispose()
    {

    }

    // Abstract method intended to act as the main loop of the state.
    public void Update(HashMap<Integer,TouchInfo> touches)
    {

    }
    // Abstract method intended to render all objects of the state.
    public void Draw(SpriteBatch spriteBatch)
    {
        sprite.draw(spriteBatch);
    }
}
