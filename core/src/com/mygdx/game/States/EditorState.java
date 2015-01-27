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
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.LogicBlocks.*;

import com.mygdx.game.UI.BlockSelectionList;
import com.mygdx.game.UI.Button;
import com.mygdx.game.Utility.ScriptSaver;
import com.mygdx.game.Utility.SpriteAccessor;
import com.mygdx.game.Utility.TouchInfo;
import jdk.internal.util.xml.impl.Input;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

/**
 * Created by Elliot on 19/01/14.
 */
public class EditorState extends State implements InputProcessor
{
    private OrthographicCamera camera;

    private Texture blockSpriteSheet = new Texture(Gdx.files.internal("Images/BlockSpriteSheet.png"));
    private Texture greyButtonsSheet = new Texture(Gdx.files.internal("Images/GreyButton.png"));

    private FullBlockScript scriptContainer;

    private BitmapFont fpsFont;

    private Button testButt;



    public EditorState()
    {
        super(State.StateID.EDITOR_STATE);
    }
    //Abstract method that runs on state initialisation, for loading resources
    public boolean Load()
    {
        Gdx.input.setInputProcessor(this);

        Tween.registerAccessor(LogicBlock.class, new LogicBlockAccessor());
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        Tween.registerAccessor(BlockChain.class, new BlockChainAccessor());

        fpsFont =  new BitmapFont(Gdx.files.internal("Fonts/8Bitfont.fnt"));

        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        blockSpriteSheet.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        greyButtonsSheet.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        scriptContainer = new FullBlockScript(blockSpriteSheet);

        testButt = new Button(greyButtonsSheet,0,0,128,46,0,49,128,44,false){
            @Override
            protected void Trigger() {

            }
        };
        testButt.setPosition(300,300);
        return true;
    }
    //Abstract method that runs on state destruction, for cleaning up memory
    public void Dispose()
    {

    }
    // Abstract method intended to act as the main loop of the state.
    public void Update(HashMap<Integer,TouchInfo> touches)
    {
        scriptContainer.Update();
        testButt.Update();


    }
    // Abstract method intended to render all objects of the state.
    public void Draw(SpriteBatch spriteBatch)
    {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        scriptContainer.Render(spriteBatch);
        testButt.Render(spriteBatch);
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
