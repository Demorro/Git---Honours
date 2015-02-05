package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyFrigateShip extends Ship {

    private static float shipRadius = 50;
    private static float maxLinearVelocity = 260;
    private static float maxLinearVelocityAccel = 200;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    private PlayerShip player;
    private float fleeRadius = 200; //If the ship is in this radius, it flees.
    private float pursueRadius = 250; //if fleeing, and we get to this point, we start pursuing


    public EnemyFrigateShip(Texture gameObjectTexSheet, PlayerShip player) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 220, 545, 110, 114) , 80, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel);

        SetPursueTarget(player, Utility.Speed.MODERATE);
        this.player = player;
    }

    public void Update(float elapsed, OrthographicCamera camera)
    {
        super.Update(elapsed, camera);
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
