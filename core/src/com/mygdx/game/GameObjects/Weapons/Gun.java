package com.mygdx.game.GameObjects.Weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.GameObject;
import com.mygdx.game.GameObjects.Ships.Ship;
import com.mygdx.game.Utility.Utility;

import javax.rmi.CORBA.Util;
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


    private float SLOW_FIRE_TIME_BETWEEN_SHOTS = 0.8f;
    private float MODERATE_FIRE_TIME_BETWEEN_SHOTS = 0.4f;
    private float FAST_FIRE_TIME_BETWEEN_SHOTS = 0.15f;
    private Utility.Speed currentFiringSpeed = Utility.Speed.SLOW;
    private float autoFireTimeBetweenShots = 1.0f;
    private boolean isFiring = false;
    private float autoFireTimer = 0.0f;
    private GameObject target = null;

    private Utility.Weapon weaponType ;

    public Gun(Pool<Bullet> bulletPool, ArrayList<Bullet> activeBullets, Texture gameObjectTextureSheet, float bulletSpeed, float bulletDamage, Rectangle bulletTextureBounds, Vector2 firingPos, float accuracyConeVariance, Utility.Weapon weaponType)
    {
        this.bulletPool = bulletPool;
        this.activeBullets = activeBullets;
        this.gameObjectTextureSheet = gameObjectTextureSheet;
        this.shotBulletSpeed = bulletSpeed;
        this.shotBulletDamage = bulletDamage;
        this.bulletSrcRegion = new TextureRegion(gameObjectTextureSheet, (int)bulletTextureBounds.x, (int)bulletTextureBounds.y, (int)bulletTextureBounds.width, (int)bulletTextureBounds.height);
        this.position = firingPos;
        this.accuracyConeVariance = accuracyConeVariance;
        this.autoFireTimeBetweenShots = MODERATE_FIRE_TIME_BETWEEN_SHOTS;

        this.weaponType = weaponType;
    }

    public void SetFastMedSlowFireRate(float fastTime, float medTime, float slowTime){
        FAST_FIRE_TIME_BETWEEN_SHOTS = fastTime;
        MODERATE_FIRE_TIME_BETWEEN_SHOTS = medTime;
        SLOW_FIRE_TIME_BETWEEN_SHOTS = slowTime;
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
    public void SetIsAutoFiring(boolean _firing, Utility.Speed fireRate)
    {
        isFiring = _firing;

        SetFireRate(fireRate, false);
    }

    public boolean GetIsAutoFiring()
    {
        return  isFiring;
    }

    public void SetFireRate(Utility.Speed fireRate, boolean dontSetDown){
        if(fireRate == Utility.Speed.QUICK){
            autoFireTimeBetweenShots = FAST_FIRE_TIME_BETWEEN_SHOTS; currentFiringSpeed = fireRate;
        }
        else if(fireRate == Utility.Speed.MODERATE){
            if((dontSetDown) && (currentFiringSpeed == Utility.Speed.QUICK)) {
                return;
            }
            autoFireTimeBetweenShots = MODERATE_FIRE_TIME_BETWEEN_SHOTS; currentFiringSpeed = fireRate;
        }
        else if(fireRate == Utility.Speed.SLOW){
            if((dontSetDown) && ((currentFiringSpeed == Utility.Speed.QUICK) || (currentFiringSpeed == Utility.Speed.MODERATE))) {
                return;
            }
            autoFireTimeBetweenShots = SLOW_FIRE_TIME_BETWEEN_SHOTS; currentFiringSpeed = fireRate;
        }
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

    public Utility.Weapon GetWeaponType()
    {
        return weaponType;
    }
}
