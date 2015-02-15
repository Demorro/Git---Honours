package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.GameObjects.Weapons.Bullet;
import com.mygdx.game.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyFighterShip extends Ship {

    private static float shipRadius = 10;
    private static float maxLinearVelocity = 1100;
    private static float maxLinearVelocityAccel = 1100;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    private PlayerShip player;
    private float fleeRadius = 200; //If the ship is in this radius, it flees.
    private float pursueRadius = 500; //if fleeing, and we get to this point, we start pursuing

    private static Vector2 collisionBoxNegativeOffset = new Vector2(20,0);

    public EnemyFighterShip(Texture gameObjectTexSheet, PlayerShip player, TextureAtlas destructionExplosionAtlas) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 350, 545, 74, 50) , 30, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel, collisionBoxNegativeOffset, destructionExplosionAtlas);
        SetPursueTarget(player, Utility.Speed.QUICK);

        this.player = player;

        SetBehaviorActive(cohesionBehavior, true);
        SetBehaviorActive(alignmentBehavior, true);
    }

    public void Update(float elapsed, OrthographicCamera camera, ArrayList<Bullet> bullets)
    {
        super.Update(elapsed,camera, bullets);
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
    }
}
