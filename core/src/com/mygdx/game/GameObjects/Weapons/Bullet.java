package com.mygdx.game.GameObjects.Weapons;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.GameObject;
import javafx.scene.Camera;
import sun.security.krb5.internal.crypto.Des;

/**
 * Created by Elliot Morris on 29/01/2015.
 * Used in gun, defines the projectile that is shot
 */
public  class Bullet extends GameObject implements Pool.Poolable{


    private float bulletSpeed = 0;
    private float bulletDamage = 0;

    private float bulletSpin = 0;
    private boolean bulletSpins = false;

    private TextureRegion bulletRegion = null;
    public boolean alive = false; //Used for pooling

    private BoundingBox boundingBox = new BoundingBox(); //Because the bullet dosent use the regular sprite rendering
    private Vector2 movementVector = new Vector2(0,0); //The vector that this bullet moves along, set in Shoot()]

    private boolean isExploding = false;
    private Explosion destructionExplosion = null;
    private TextureAtlas explosionAtlas;
    private float explosionFPS = 25;

    public Bullet()
    {
        super();
        this.alive = false;
    }
    public void init(Texture bulletSpriteSheet, TextureRegion bulletRegion,  float x, float y, float bulletSpeed, float bulletDamage, Vector2 shootDirection, TextureAtlas explosionAtlas)
    {
        setTexture(bulletSpriteSheet);
        setRegion(bulletRegion);

        this.bulletRegion = bulletRegion;
        this.bulletSpeed = bulletSpeed;
        this.bulletDamage = bulletDamage;

        setOrigin(getWidth()/2, getHeight()/2);
        setPosition(x, y);
        SetActive(true);
        movementVector = shootDirection.nor();
        this.explosionAtlas = explosionAtlas;
        this.alive = true;
    }
    @Override
    public void reset(){
        setPosition(0,0);
        bulletSpin = 0;
        bulletSpins = false;
        bulletDamage = 0;
        bulletSpeed = 0;
        destructionExplosion = null;
        alive = false;
        isExploding = false;

    }

    public void Update(float elapsed, OrthographicCamera camera)
    {
        if(GetActive())
        {
            if(isExploding == false) { //Only move if we're exploding
                Vector2 frameMovVec = movementVector.nor();
                frameMovVec.scl(bulletSpeed);
                translate(frameMovVec.x * elapsed, frameMovVec.y * elapsed);
                if (bulletSpins) {
                    rotate(bulletSpin * elapsed);
                } else {
                    setRotation(movementVector.angle() - 90);
                }
            }
            else {
                if (destructionExplosion != null) {
                    destructionExplosion.Update(elapsed);
                    if (destructionExplosion.IsCurrentlyExploding() == false) //If the explosion has finished
                    {
                        destructionExplosion = null;
                        Destroy();
                    }
                }
            }
        }

        DestroyIfOffScreen(camera);
    }
    private void DestroyIfOffScreen(OrthographicCamera camera)
    {
        Vector3 screenPos = new Vector3(getX(), getY(), 0);
        if(!camera.frustum.boundsInFrustum(screenPos.x,screenPos.y,screenPos.z,getWidth()/2, getHeight()/2,1))
        {

            alive = false;
        }
    }

    public void Destroy()
    {
        alive = false;
    }

    public void ExplodeAndDestroy()
    {
        destructionExplosion = new Explosion(explosionAtlas, explosionFPS, false, 0 );
        destructionExplosion.setPosition(GetCenterPosition().x - destructionExplosion.GetFrameWidth()/2, GetCenterPosition().y - destructionExplosion.GetFrameHeight()/2);
        isExploding = true;
    }

    public void Render(SpriteBatch batch)
    {
        if(GetActive()) {
            if(isExploding == false) {
                Render(bulletRegion, batch);
            }
            else {
                if (destructionExplosion != null) {
                    destructionExplosion.Render(batch);
                }
            }
        }
    }

    public void SetSpin(float spin){
        bulletSpin = spin;
        bulletSpins = true;
    }

    public Boolean IsExploding()
    {
        return isExploding;
    }

    public float GetBulletDamage()
    {
        return bulletDamage;
    }

}
