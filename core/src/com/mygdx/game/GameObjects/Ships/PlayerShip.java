package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.GameObject;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.GameObjects.Weapons.Gun;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 28/01/2015.
 */
public class PlayerShip extends Ship{

    public enum ShipGuns
    {
        AUTOCANNON,
        LASER,
        MISSILE
    };

    private Texture gameObjectTexSheet; //Reference to game object texture sheet

    private Gun autoCannon;
    private Vector2 autoCannonMuzzleOffset = new Vector2(0, -20);

    public PlayerShip(Texture gameObjectTexSheet, Pool<Bullet> bulletPool, ArrayList<Bullet> bulletList)
    {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet,0,0,100,76), 100);
        this.gameObjectTexSheet = gameObjectTexSheet;

        autoCannon = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1000, 1, new Rectangle(0,750,18,50), GetCenterPosition(), 12, 0.15f);
    }

    public void Update(float elapsed)
    {
        autoCannon.Update(elapsed);
        autoCannon.SetPosition(GetCenterPosition().x + autoCannonMuzzleOffset.x, GetCenterPosition().y + autoCannonMuzzleOffset.y);
    }

    public void SetTargetAndFire(ShipGuns gun, GameObject target)
    {
        if(gun == ShipGuns.AUTOCANNON) {
            if(autoCannon != null) {
                autoCannon.SetTarget(target);
                autoCannon.SetIsAutoFiring(true);
            }
        }
        else if(gun == ShipGuns.LASER){

        }
        else if(gun == ShipGuns.MISSILE){

        }
    }


}
