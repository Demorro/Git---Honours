package com.mygdx.game.GameObjects.Ships;

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
public class EnemyFighterShip extends Ship {

    private static float shipRadius = 10;
    private static float maxLinearVelocity = 660;
    private static float maxLinearVelocityAccel = 660;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    private PlayerShip player;
    private float fleeRadius = 200; //If the ship is in this radius, it flees.
    private float pursueRadius = 500; //if fleeing, and we get to this point, we start pursuing
    private float fireMaxRadius = 600; //If the ship is in this radius, and it can, it FIRES!
    private float fireMinRadius = 350; //If the ship is in this radius, and it can, it FIRES!

    private static Vector2 collisionBoxNegativeOffset = new Vector2(20,0);

    public static int destroyScore = 5;

    public EnemyFighterShip(Texture gameObjectTexSheet, PlayerShip player, TextureAtlas destructionExplosionAtlas, Pool<Bullet> bulletPool, ArrayList<Bullet> bulletList, Camera cam) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 350, 545, 74, 50) , 1.5f, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel, collisionBoxNegativeOffset, destructionExplosionAtlas, cam);
        SetPursueTarget(player, Utility.Speed.QUICK);

        this.player = player;

        SetBehaviorActive(cohesionBehavior, true);
        SetBehaviorActive(alignmentBehavior, true);

        torpedo = new Gun(bulletPool, bulletList, gameObjectTexSheet, 750, 2, new Rectangle(275,770,30,30), GetCenterPosition(), 6, Utility.Weapon.MISSILE, cam);
        torpedo.SetFastMedSlowFireRate(3.0f, 5.0f, 8.0f);
        torpedo.SetFireRate(Utility.Speed.QUICK, false);
        torpedo.SetTarget(player);

        noOfDeathExplosions = 5;

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
        if(player.getPosition().dst(getPosition()) < fleeRadius){
            SetEvadeTarget(player, Utility.Speed.MODERATE);
            SetBehaviorActive(pursueBehavior, false);
        }
        else if(player.getPosition().dst(getPosition()) > pursueRadius){
            SetPursueTarget(player, Utility.Speed.QUICK);
            SetBehaviorActive(evadeBehavior, false);
        }

        if((player.getPosition().dst(getPosition()) < fireMaxRadius) && (player.getPosition().dst(getPosition()) > fireMinRadius)){
            if((torpedo.CanFireRightNow()) && (GetBehaviorActive(pursueBehavior))){
                torpedo.ShootAtTarget();
            }
        }
    }
}
