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
    private PlayerShip player;

    private final ArrayList<Bullet> playerShotBullets = new ArrayList<Bullet>();
    private final ArrayList<Bullet> enemyShotBullets = new ArrayList<Bullet>();
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

    private TextureAtlas largeExplosionAtlas = null;

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

        largeExplosionAtlas = new TextureAtlas(Gdx.files.internal("Images/LargeExplosion/LargeExplosion.txt"));

        player = new PlayerShip(gameObjectTextureSheet, bulletPool, playerShotBullets, caps, frigs, fighters, largeExplosionAtlas);
        player.setPosition(0,0);

        EnemyCapitalShip testCap = new EnemyCapitalShip(gameObjectTextureSheet,player, largeExplosionAtlas);
        testCap.setPosition(-500,300);
        caps.add(testCap);

        EnemyFrigateShip testFrig = new EnemyFrigateShip(gameObjectTextureSheet, player, largeExplosionAtlas);
        testFrig.setPosition(400,100);
        EnemyFrigateShip testFrig2 = new EnemyFrigateShip(gameObjectTextureSheet, player, largeExplosionAtlas);
        testFrig.setPosition(400,100);
        testFrig2.setPosition(-350, -200);
        frigs.add(testFrig);
       // frigs.add(testFrig2);

        EnemyFighterShip testFighter = new EnemyFighterShip(gameObjectTextureSheet, player, largeExplosionAtlas);
        testFighter.setPosition(700,200);
        EnemyFighterShip testFighter1 = new EnemyFighterShip(gameObjectTextureSheet, player, largeExplosionAtlas);
        testFighter1.setPosition(800,200);
        EnemyFighterShip testFighter2 = new EnemyFighterShip(gameObjectTextureSheet, player, largeExplosionAtlas);
        testFighter2.setPosition(600,200);
        fighters.add(testFighter);
        fighters.add(testFighter1);
        fighters.add(testFighter2);


        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();


        SetupSteerables();

        return true;
    }
    private void SetupSteerables()
    {
        ArrayList<SteerableObject> allObjects = new ArrayList<SteerableObject>();
        allObjects.addAll(caps);
        allObjects.addAll(frigs);
        allObjects.addAll(fighters);
        allObjects.add(player);
        player.SetAllSteerables(allObjects);
        for(SteerableObject obj : caps){ obj.SetAllSteerables(allObjects);}
        for(SteerableObject obj : frigs){ obj.SetAllSteerables(allObjects);}
        for(SteerableObject obj : fighters){ obj.SetAllSteerables(allObjects);}
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



        for(Bullet bullet : playerShotBullets)
        {
            bullet.Update(elapsed, camera);
        }
        KillOffscreenBullets();

        player.Update(elapsed,camera,enemyShotBullets);


        for(EnemyCapitalShip ship : caps)
        {
            ship.Update(elapsed,camera,playerShotBullets);
        }

        for(EnemyFrigateShip ship : frigs)
        {
            ship.Update(elapsed,camera,playerShotBullets);
        }

        for(EnemyFighterShip ship : fighters)
        {
            ship.Update(elapsed,camera,playerShotBullets);
        }

        returnToEditorButton.Update();

    }
    // Abstract method intended to render all objects of the state.
    public void Draw(SpriteBatch spriteBatch)
    {
        spriteBatch.end();

        camera.position.x = player.GetCenterPosition().x;
        camera.position.y = player.GetCenterPosition().y;
        camera.update();

        cameraVelocity.x = camera.position.x - lastFrameCamPos.x;
        cameraVelocity.y = camera.position.y - lastFrameCamPos.y;

        backgroundBatch.begin();
        backGround.Render(backgroundBatch, cameraVelocity);
        backgroundBatch.end();


        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        for(EnemyCapitalShip ship : caps){
            ship.Render(spriteBatch);
        }
        for(EnemyFrigateShip ship : frigs){
            ship.Render(spriteBatch);
        }
        for(EnemyFighterShip ship : fighters){
            ship.Render(spriteBatch);
        }

        for(Bullet bullet : playerShotBullets)
        {
            bullet.Render(spriteBatch);
        }
        player.Render(spriteBatch);
        spriteBatch.end();

        foreGroundBatch.begin();
        backGround.RenderForeground(foreGroundBatch, cameraVelocity);
        foreGroundBatch.end();

        hudBatch.begin();
        fpsFont.draw(hudBatch, "FPS : " + Gdx.graphics.getFramesPerSecond(), 50, 50);
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
