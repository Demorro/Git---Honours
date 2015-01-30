package com.mygdx.game.GameObjects.Weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.GameObject;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 * Base gun class, part of a ship that deals with firing at other things
 */
public class Gun {

    //Store references to things needed, passed in in da constructor
    private Texture gameObjectTextureSheet = null;
    private TextureRegion bulletSrcRegion = null;
    private Pool<Bullet> bulletPool = null;
    private ArrayList<Bullet> activeBullets = null;
    private float shotBulletSpeed = 0;
    private float shotBulletDamage = 0;
    private float accuracyConeVariance = 0; //The max no of units that bullets can stray from their target
    private Vector2 position = new Vector2(0,0);

    private float autoFireTimeBetweenShots = 1.0f;
    private boolean isFiring = false;
    private float autoFireTimer = 0.0f;
    private GameObject target = null;

    public Gun(Pool<Bullet> bulletPool, ArrayList<Bullet> activeBullets, Texture gameObjectTextureSheet, float bulletSpeed, float bulletDamage, Rectangle bulletTextureBounds, Vector2 firingPos, float accuracyConeVariance, float autoFireTimeBetweenShots)
    {
        this.bulletPool = bulletPool;
        this.activeBullets = activeBullets;
        this.gameObjectTextureSheet = gameObjectTextureSheet;
        this.shotBulletSpeed = bulletSpeed;
        this.shotBulletDamage = bulletDamage;
        this.bulletSrcRegion = new TextureRegion(gameObjectTextureSheet, (int)bulletTextureBounds.x, (int)bulletTextureBounds.y, (int)bulletTextureBounds.width, (int)bulletTextureBounds.height);
        this.position = firingPos;
        this.accuracyConeVariance = accuracyConeVariance;
        this.autoFireTimeBetweenShots = autoFireTimeBetweenShots;
    }

    public void Update(float elapsed)
    {
        if(GetIsAutoFiring())
        {
            autoFireTimer += elapsed;
            if(autoFireTimer > autoFireTimeBetweenShots){
                if(target != null){
                    Shoot(target.GetCenterPosition());
                    autoFireTimer = 0.0f;
                }
            }
        }
        else
        {
            autoFireTimer = 0.0f;
        }
    }

    public void Shoot(Vector2 targetPosition){
        System.out.println(activeBullets.size());

        float accuracyVariance = MathUtils.random(-accuracyConeVariance,accuracyConeVariance);

        Vector2 firingVector = new Vector2(targetPosition.x - position.x, targetPosition.y - position.y);
        firingVector.rotate(accuracyVariance);

        Bullet item = bulletPool.obtain();
        item.init(gameObjectTextureSheet,bulletSrcRegion,position.x,position.y,shotBulletSpeed,shotBulletDamage, firingVector);
        activeBullets.add(item);
    }

    public void SetIsAutoFiring(boolean _firing)
    {
        isFiring = _firing;
    }
    public boolean GetIsAutoFiring()
    {
        return  isFiring;
    }

    public void SetPosition(float x, float y)
    {
        position.x = x;
        position.y = y;
    }
    public Vector2 GetPosition()
    {
        return position;
    }

    public void SetTarget(GameObject _target)
    {
        target = _target;
    }

    public GameObject GetTarget()
    {
        return target;
    }
}
