package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.GameObjects.SteerableObject;
import com.mygdx.game.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyFighterShip extends Ship {

    private static float shipRadius = 5;
    private static float maxLinearVelocity = 550;
    private static float maxLinearVelocityAccel = 550;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    public EnemyFighterShip(Texture gameObjectTexSheet, PlayerShip player) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 350, 545, 74, 50) , 30, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel);

        SetPursueTarget(player, Utility.Speed.MODERATE);
    }
}
