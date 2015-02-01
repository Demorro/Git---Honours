package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.Ships.EnemyCapitalShip;
import com.mygdx.game.GameObjects.Ships.EnemyFighterShip;
import com.mygdx.game.GameObjects.Ships.EnemyFrigateShip;
import com.mygdx.game.GameObjects.Ships.PlayerShip;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.GameObjects.Weapons.Gun;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies.CapitalShipBlock;
import com.mygdx.game.UI.Button;
import com.mygdx.game.Utility.TouchInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Elliot on 19/01/14.
 */
public class PlayState extends State implements InputProcessor
{
    private OrthographicCamera camera;
    private BitmapFont fpsFont;

    private Button returnToEditorButton = null;
    private Texture greyButtonsSheet = new Texture(Gdx.files.internal("Images/GreyButton.png"));

    private Texture gameObjectTextureSheet = new Texture(Gdx.files.internal("Images/GameObjectSpriteSheet.png"));
    private PlayerShip player;

    private final ArrayList<Bullet> activeBullets = new ArrayList<Bullet>();
    private final Pool<Bullet> bulletPool = new Pool<Bullet>(){

        @Override
        protected Bullet newObject(){
            return new Bullet();
        }
    };

    //Enemy ship array
    private ArrayList<EnemyCapitalShip> caps = new ArrayList<EnemyCapitalShip>();
    private ArrayList<EnemyFrigateShip> frigs = new ArrayList<EnemyFrigateShip>();
    private ArrayList<EnemyFighterShip> fighters = new ArrayList<EnemyFighterShip>();

    private EnemyCapitalShip testCap2;

    public PlayState()
    {
        super(StateID.PLAY_STATE);
    }
    //Abstract method that runs on state initialisation, for loading resources
    public boolean Load()
    {
        Gdx.input.setInputProcessor(this);
        fpsFont =  new BitmapFont(Gdx.files.internal("Fonts/8Bitfont.fnt"));
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        gameObjectTextureSheet.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        greyButtonsSheet.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        SetupButtons();

        EnemyCapitalShip testCap = new EnemyCapitalShip(gameObjectTextureSheet);
        testCap.setPosition(900,100);
        testCap2 = new EnemyCapitalShip(gameObjectTextureSheet);
        testCap2.setPosition(200,1300);
        caps.add(testCap);
        caps.add(testCap2);

        EnemyFrigateShip testFrig = new EnemyFrigateShip(gameObjectTextureSheet);
        testFrig.setPosition(600,300);
        frigs.add(testFrig);

        EnemyFighterShip testFighter = new EnemyFighterShip(gameObjectTextureSheet);
        testFighter.setPosition(950,700);
        fighters.add(testFighter);

        player = new PlayerShip(gameObjectTextureSheet, bulletPool, activeBullets, caps, frigs, fighters);
        player.setPosition(500,100);

        return true;
    }
    private void SetupButtons()
    {
        int buttonOffsetFromRight = 120;

        returnToEditorButton = new Button(greyButtonsSheet,0,0,98,36,0,39,98,34,false, false){
            @Override
            protected void Trigger() {
                SwitchState(StateID.EDITOR_STATE);
            }
        };

        returnToEditorButton.setPosition(Gdx.graphics.getWidth() - buttonOffsetFromRight, Gdx.graphics.getHeight() - returnToEditorButton.getRegionHeight());
        returnToEditorButton.SetText("Editor");
        returnToEditorButton.SetTextOffset(0,2);
    }
    //Abstract method that runs on state destruction, for cleaning up memory
    public void Dispose()
    {
        gameObjectTextureSheet.dispose();
        fpsFont.dispose();
        greyButtonsSheet.dispose();

        camera = null;
    }
    // Abstract method intended to act as the main loop of the state.
    public void Update(HashMap<Integer,TouchInfo> touches)
    {
        float elapsed = Gdx.graphics.getDeltaTime();
        camera.update();

        for(Bullet bullet : activeBullets)
        {
            bullet.Update(elapsed, camera);
        }
        KillOffscreenBullets();
        testCap2.translateY(-75 * elapsed);
        player.Update(elapsed,camera);

        returnToEditorButton.Update();

    }
    // Abstract method intended to render all objects of the state.
    public void Draw(SpriteBatch spriteBatch)
    {
        //Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fpsFont.draw(spriteBatch, "FPS : " + Gdx.graphics.getFramesPerSecond(), 50, 50);

        for(EnemyCapitalShip ship : caps){
            ship.Render(spriteBatch);
        }
        for(EnemyFrigateShip ship : frigs){
            ship.Render(spriteBatch);
        }
        for(EnemyFighterShip ship : fighters){
            ship.Render(spriteBatch);
        }

        for(Bullet bullet : activeBullets)
        {
            bullet.Render(spriteBatch);
        }
        player.Render(spriteBatch);

        returnToEditorButton.Render(spriteBatch);
    }

    private void KillOffscreenBullets()
    {
        // if you want to free dead bullets, returning them to the pool:
        Bullet item;
        int len = activeBullets.size();
        for (int i = len; --i >= 0;) {
            item = activeBullets.get(i);
            if (item.alive == false) {
                activeBullets.remove(i);
                bulletPool.free(item);
            }
        }
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
