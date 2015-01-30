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
import com.mygdx.game.GameObjects.Ships.PlayerShip;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.GameObjects.Weapons.Gun;
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

    private Texture gameObjectTextureSheet = new Texture(Gdx.files.internal("Images/GameObjectSpriteSheet.png"));
    private PlayerShip player;
    private EnemyCapitalShip testCap;

    private final ArrayList<Bullet> activeBullets = new ArrayList<Bullet>();
    private final Pool<Bullet> bulletPool = new Pool<Bullet>(){

        @Override
        protected Bullet newObject(){
            return new Bullet();
        }
    };

    private Gun testGun = null;
    private Rectangle testBulletBounds = new Rectangle(0,750,18,50);

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

        player = new PlayerShip(gameObjectTextureSheet, bulletPool, activeBullets);
        player.setPosition(200,-400);

        testCap = new EnemyCapitalShip(gameObjectTextureSheet);
        testCap.setPosition(800,800);

        player.SetTargetAndFire(PlayerShip.ShipGuns.AUTOCANNON, testCap);

        return true;
    }
    //Abstract method that runs on state destruction, for cleaning up memory
    public void Dispose()
    {
        gameObjectTextureSheet.dispose();
        fpsFont.dispose();

        camera = null;
    }
    // Abstract method intended to act as the main loop of the state.
    public void Update(HashMap<Integer,TouchInfo> touches)
    {
        float elapsed = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyJustPressed(Input.Keys.B))
        {
            testGun.Shoot(new Vector2(player.getX() + player.getRegionWidth()/2, player.getY() + player.getRegionHeight()/2));
        }
        for(Bullet bullet : activeBullets)
        {
            bullet.Update(elapsed, camera);
        }
        KillOffscreenBullets();

        player.Update(elapsed);

        player.translateY(45 * elapsed);
        testCap.translateY(-20 * elapsed);

    }
    // Abstract method intended to render all objects of the state.
    public void Draw(SpriteBatch spriteBatch)
    {
        //Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fpsFont.draw(spriteBatch, "FPS : " + Gdx.graphics.getFramesPerSecond(), 50, 50);
        
        testCap.Render(spriteBatch);
        for(Bullet bullet : activeBullets)
        {
            bullet.Render(spriteBatch);
        }
        player.Render(spriteBatch);
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
