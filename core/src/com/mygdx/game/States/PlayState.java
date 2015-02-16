package com.mygdx.game.States;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.Ships.EnemyCapitalShip;
import com.mygdx.game.GameObjects.Ships.EnemyFighterShip;
import com.mygdx.game.GameObjects.Ships.EnemyFrigateShip;
import com.mygdx.game.GameObjects.Ships.PlayerShip;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.GameObjects.Weapons.Explosion;
import com.mygdx.game.GameObjects.Weapons.Gun;
import com.mygdx.game.GameSystems.ShipHandler;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies.CapitalShipBlock;
import com.mygdx.game.UI.Button;
import com.mygdx.game.Utility.ParralaxBackground;
import com.mygdx.game.Utility.TouchInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Elliot on 19/01/14.
 */
public class PlayState extends State implements InputProcessor
{
    private SpriteBatch backgroundBatch = new SpriteBatch();
    private SpriteBatch foreGroundBatch = new SpriteBatch();
    private SpriteBatch hudBatch = new SpriteBatch();
    private OrthographicCamera camera;
    private Vector2 cameraVelocity = new Vector2(0,0);
    private Vector2 lastFrameCamPos = new Vector2(0,0);

    private BitmapFont fpsFont;

    private ParralaxBackground backGround = new ParralaxBackground(1.0f);

    private Button returnToEditorButton = null;
    private Texture greyButtonsSheet = new Texture(Gdx.files.internal("Images/GreyButton.png"));

    private Texture gameObjectTextureSheet = new Texture(Gdx.files.internal("Images/GameObjectSpriteSheet.png"));

    private final ArrayList<Bullet> playerShotBullets = new ArrayList<Bullet>();
    private final ArrayList<Bullet> enemyShotBullets = new ArrayList<Bullet>();
    private final Pool<Bullet> bulletPool = new Pool<Bullet>(){

        @Override
        protected Bullet newObject(){
            return new Bullet();
        }
    };

    private ShipHandler shipHandler = null;

    float gameTime = 0;

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

        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        shipHandler = new ShipHandler(gameObjectTextureSheet, bulletPool, playerShotBullets, enemyShotBullets);

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
        backGround.Dispose();

        camera = null;
    }
    // Abstract method intended to act as the main loop of the state.
    public void Update(HashMap<Integer,TouchInfo> touches)
    {
        float elapsed = Gdx.graphics.getDeltaTime();

        shipHandler.Update(elapsed, camera);

        for(Bullet bullet : playerShotBullets)
        {
            bullet.Update(elapsed, camera);
        }
        for(Bullet bullet : enemyShotBullets)
        {
            bullet.Update(elapsed, camera);
        }
        KillOffscreenBullets();

        returnToEditorButton.Update();
        gameTime += elapsed;

    }
    // Abstract method intended to render all objects of the state.
    public void Draw(SpriteBatch spriteBatch)
    {
        spriteBatch.end();

        camera.position.x = shipHandler.GetPlayer().GetCenterPosition().x;
        camera.position.y = shipHandler.GetPlayer().GetCenterPosition().y;
        camera.update();

        cameraVelocity.x = camera.position.x - lastFrameCamPos.x;
        cameraVelocity.y = camera.position.y - lastFrameCamPos.y;

        backgroundBatch.begin();
        backGround.Render(backgroundBatch, cameraVelocity);
        backgroundBatch.end();


        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        for(Bullet bullet : enemyShotBullets)
        {
            bullet.Render(spriteBatch);
        }

        shipHandler.RenderEnemyShips(spriteBatch);

        for(Bullet bullet : playerShotBullets)
        {
            bullet.Render(spriteBatch);
        }

        shipHandler.RenderPlayer(spriteBatch);
        spriteBatch.end();

        foreGroundBatch.begin();
        backGround.RenderForeground(foreGroundBatch, cameraVelocity);
        foreGroundBatch.end();

        hudBatch.begin();
        fpsFont.draw(hudBatch, "FPS : " + Gdx.graphics.getFramesPerSecond(), 30, Gdx.graphics.getHeight() - 30);
        fpsFont.draw(hudBatch, "Score : " + shipHandler.GetScore(), 50, 50);

        int minutes = ((int)gameTime) / 60;
        int seconds = ((int)gameTime) % 60;

        fpsFont.draw(hudBatch, minutes + "m  " + seconds + "s", Gdx.graphics.getWidth() - 120, 50);
        returnToEditorButton.Render(hudBatch);
        hudBatch.end();

        lastFrameCamPos.x = camera.position.x;
        lastFrameCamPos.y = camera.position.y;
    }

    private void KillOffscreenBullets()
    {
        // if you want to free dead bullets, returning them to the pool:
        Bullet item;
        int len = playerShotBullets.size();
        for (int i = len; --i >= 0;) {
            item = playerShotBullets.get(i);
            if (item.alive == false) {
                playerShotBullets.remove(i);
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
