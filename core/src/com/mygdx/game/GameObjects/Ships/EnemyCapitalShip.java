package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.ai.steer.behaviors.Pursue;
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
public class EnemyCapitalShip extends Ship {

    private static float shipRadius = 150;
    private static float maxLinearVelocity = 80;
    private static float maxLinearVelocityAccel = 50;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    private PlayerShip player;
    private float fleeRadius = 300; //If the ship is in this radius, it flees.
    private float pursueRadius = 400; //if fleeing, and we get to this point, we start pursuing

    private static Vector2 collisionBoxNegativeOffset = new Vector2(46,40);

    public EnemyCapitalShip(Texture gameObjectTexSheet, PlayerShip player, TextureAtlas destructionExplosionAtlas) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 0, 545, 200, 178) , 200, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel,collisionBoxNegativeOffset,destructionExplosionAtlas);

        SetPursueTarget(player, Utility.Speed.MODERATE);
        this.player = player;

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
    }

}
