
package com.mygdx.game;


import java.util.HashMap;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.States.StateManager;
import com.mygdx.game.Utility.TouchInfo;


public class GameApplication implements ApplicationListener  {
    private SpriteBatch spriteBatch;
    private StateManager stateManager;

    //For touch input
    static final int MAXFINGERS = 5;
    private int screenWidth,screenHeight;

    private HashMap<Integer,TouchInfo> touches = new HashMap<Integer,TouchInfo>();

	@Override
	public void create ()
    {
        //So we can use non-power of 2 textures
        spriteBatch = new SpriteBatch();
        stateManager = new StateManager();

        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        //
        for(int i = 0; i < MAXFINGERS; i++){
            touches.put(i, new TouchInfo());
        }
	}
    @Override
    public void dispose()
    {
        spriteBatch.dispose();
    }
    @Override
	public void render ()
    {
        //Run update logic
        stateManager.Update(touches);

        //Clear the screen to black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.begin();
        //Render current state
        stateManager.Draw(spriteBatch);
        spriteBatch.end();
	}


    @Override
    public void resize(int width, int height)
    {
    }
    @Override
    public void pause()
    {
    }
    @Override
    public void resume()
    {
    }



}
