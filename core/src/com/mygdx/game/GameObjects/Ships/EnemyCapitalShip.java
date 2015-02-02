package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.GameObjects.SteerableObject;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyCapitalShip extends Ship {

    private static float shipRadius = 200;
    private static float maxLinearVelocity = 50;
    private static float maxLinearVelocityAccel = 20;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    public EnemyCapitalShip(Texture gameObjectTexSheet) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 0, 545, 200, 178) , 200, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel);
    }
}
