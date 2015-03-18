package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.GameObjects.Weapons.Gun;
import com.mygdx.game.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyCapitalShip extends Ship {

    private static float shipRadius = 150;
    private static float maxLinearVelocity = 100;
    private static float maxLinearVelocityAccel = 60;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    private PlayerShip player;
    private float fleeRadius = 350; //If the ship is in this radius, it flees.
    private float pursueRadius = 450; //if fleeing, and we get to this point, we start pursuing

    private static Vector2 collisionBoxNegativeOffset = new Vector2(46,40);

    public static int destroyScore = 50;

    public EnemyCapitalShip(Texture gameObjectTexSheet, PlayerShip player, TextureAtlas destructionExplosionAtlas, Pool<Bullet> bulletPool, ArrayList<Bullet> bulletList, Camera cam) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 0, 545, 200, 178) , 60, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel,collisionBoxNegativeOffset,destructionExplosionAtlas, cam);

        SetPursueTarget(player, Utility.Speed.MODERATE);
        this.player = player;

        noOfDeathExplosions = 40;
        timeBetweenExplosionSpawns = 0.02f;

        torpedo = new Gun(bulletPool, bulletList, gameObjectTexSheet, 220, 12, new Rectangle(78,768,30,30), GetCenterPosition(), 0, Utility.Weapon.MISSILE, cam);
        torpedo.SetFastMedSlowFireRate(2.4f, 5.0f, 7.5f);
        torpedo.SetFireRate(Utility.Speed.QUICK, false);
        torpedo.SetTarget(player);

    }

    public void Update(float elapsed, OrthographicCamera camera, ArrayList<Bullet> bullets)
    {
        super.Update(elapsed,camera, bullets);
        DoShipAI();

    }

    private void DoShipAI()
    {
        if(player.getPosition().dst(getPosition()) < pursueRadius){
            if(GetBehaviorActive(evadeBehavior) == false) {
                SetBehaviorActive(pursueBehavior, false);
            }
        }
        else {
            SetPursueTarget(player, Utility.Speed.MODERATE);
        }

        if(player.getPosition().dst(getPosition()) < fleeRadius){
            SetEvadeTarget(player, Utility.Speed.QUICK);
        }
        else
        {
            SetBehaviorActive(evadeBehavior, false);
        }

        if((player.getPosition().dst(getPosition()) > fleeRadius)){
            if(torpedo.CanFireRightNow()) {
                if(gameCam.frustum.pointInFrustum(GetCenterPosition().x, GetCenterPosition().y, 0)){
                    torpedo.ShootAtTarget();
                }
            }
        }
    }

}
