package com.mygdx.game.States;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.LogicBlocks.*;
import com.mygdx.game.UI.Button;
import com.mygdx.game.Utility.SpriteAccessor;
import com.mygdx.game.Utility.TouchInfo;

import java.util.HashMap;

/**
 * Created by Elliot on 19/01/14.
 */
public class PlayState extends State implements InputProcessor
{
    private OrthographicCamera camera;
    private BitmapFont fpsFont;




    public PlayState()
    {
        super(StateID.EDITOR_STATE);
    }
    //Abstract method that runs on state initialisation, for loading resources
    public boolean Load()
    {
        Gdx.input.setInputProcessor(this);
        fpsFont =  new BitmapFont(Gdx.files.internal("Fonts/8Bitfont.fnt"));
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
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
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fpsFont.draw(spriteBatch, "FPS : " + Gdx.graphics.getFramesPerSecond(), 50, 50);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


}
