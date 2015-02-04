package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyCapitalShip extends Ship {

    private static float shipRadius = 40;
    private static float maxLinearVelocity = 30;
    private static float maxLinearVelocityAccel = 20;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;


    public EnemyCapitalShip(Texture gameObjectTexSheet, PlayerShip player) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 0, 545, 200, 178) , 200, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel);

        SetPursueTarget(player, Utility.Speed.MODERATE);
    }

}
