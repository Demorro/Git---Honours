package com.mygdx.game.GameObjects.Ships;

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
public class EnemyFrigateShip extends Ship {

    private static float shipRadius = 50;
    private static float maxLinearVelocity = 120;
    private static float maxLinearVelocityAccel = 200;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    private PlayerShip player;
    private float fleeRadius = 200; //If the ship is in this radius, it flees.
    private float pursueRadius = 250; //if fleeing, and we get to this point, we start pursuing

    private static Vector2 collisionBoxNegativeOffset = new Vector2(20,20);

    public static int destroyScore = 20;

    public EnemyFrigateShip(Texture gameObjectTexSheet, PlayerShip player, TextureAtlas destructionExplosionAtlas, Pool<Bullet> bulletPool, ArrayList<Bullet> bulletList) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 220, 545, 110, 114), 30, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel, collisionBoxNegativeOffset, destructionExplosionAtlas);

        SetPursueTarget(player, Utility.Speed.MODERATE);
        this.player = player;

        noOfDeathExplosions = 15;

        autoCannon = new Gun(bulletPool, bulletList, gameObjectTexSheet, 1600, 2.5f, new Rectangle(316,728,16,64), GetCenterPosition(),2, Utility.Weapon.AUTOCANNON);
        autoCannon.SetFastMedSlowFireRate(0.5f, 0.22f, 0.32f);
        autoCannon.SetFireRate(Utility.Speed.QUICK, false);
        autoCannon.SetTarget(player);

        blendedSteering.add(avoidObjectBehavior, 0.4f);
        blendedSteering.add(sepationBehavior, 500.0f);
        blendedSteering.add(noiseAddWanderBehavior, 0.1f);

    }

    public void Update(float elapsed, OrthographicCamera camera, ArrayList<Bullet> bullets)
    {
        super.Update(elapsed, camera, bullets);
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

        if((player.getPosition().dst(getPosition()) < pursueRadius)){
            if(autoCannon.CanFireRightNow()) {
                autoCannon.ShootAtTarget();
            }
        }
    }
}
