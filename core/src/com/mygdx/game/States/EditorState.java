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
    private Texture greybuttons2xSheet = new Texture(Gdx.files.internal("Images/GreyButton2x.png"));

    private FullBlockScript scriptContainer;


    private BitmapFont fpsFont;

    private Button saveButton;
    private Button loadButton;
    private Button playButton;
    private int buttonOffsetFromRight = 120;

    private boolean isSaving = false; //If the save (or load!) dialog is open



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
        greybuttons2xSheet.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        scriptContainer = new FullBlockScript(blockSpriteSheet);

        SetupButtons();

        return true;
    }
    private void SetupButtons()
    {
        saveButton = new Button(greyButtonsSheet,0,0,98,36,0,39,98,34,false, false){
            @Override
            protected void Trigger() {
                if(isSaving == false) {
                    int saveResult = scriptContainer.SaveScript();
                    if(saveResult >= -1){ isSaving = false; }
                    else { isSaving = false; Gdx.app.log("Error","Something went wrong in the Saving, ScriptSave.java, SaveScript function. Oops ;p");}
                }
            }
        };
        loadButton = new Button(greyButtonsSheet,0,0,98,36,0,39,98,34,false, false){
            @Override
            protected void Trigger() {
                if(isSaving == false) {
                    int loadResult = scriptContainer.LoadScript();
                    if (loadResult >= -1) {isSaving = false; }
                    else {isSaving = false; Gdx.app.log("Error", "Something went wrong in the Loading, LoadScript.java, LoadScript function. Oops ;p");
                    }
                }
            }
        };
        playButton = new Button(greybuttons2xSheet,0,0,196,76,0,76,196,68,false, true){
            @Override
            protected void Trigger() {
                scriptContainer.SaveScriptDirectly(ScriptSaver.workingScriptPath);
                SwitchState(StateID.PLAY_STATE);
            }
        };



        saveButton.setPosition(Gdx.graphics.getWidth() - buttonOffsetFromRight, 64);
        saveButton.SetText("Save");
        saveButton.SetTextOffset(0,2);

        loadButton.setPosition(Gdx.graphics.getWidth() - buttonOffsetFromRight,20);
        loadButton.SetText("Load");
        loadButton.SetTextOffset(0,2);

        playButton.setPosition(Gdx.graphics.getWidth() - buttonOffsetFromRight - loadButton.getWidth() - buttonOffsetFromRight, 22);
        playButton.SetText("Play");
        playButton.SetTextOffset(0, 4);
    }
    //Abstract method that runs on state destruction, for cleaning up memory
    public void Dispose()
    {
        blockSpriteSheet.dispose();
        greyButtonsSheet.dispose();
        greybuttons2xSheet.dispose();

        fpsFont.dispose();

        camera = null;
    }
    // Abstract method intended to act as the main loop of the state.
    public void Update(HashMap<Integer,TouchInfo> touches)
    {
        scriptContainer.Update();
        saveButton.Update();
        loadButton.Update();
        playButton.Update();

    }
    // Abstract method intended to render all objects of the state.
    public void Draw(SpriteBatch spriteBatch)
    {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        scriptContainer.Render(spriteBatch);
        saveButton.Render(spriteBatch);
        loadButton.Render(spriteBatch);
        playButton.Render(spriteBatch);

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
