package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyFrigateShip extends Ship {

    private static float shipRadius = 120;
    private static float maxLinearVelocity = 50;
    private static float maxLinearVelocityAccel = 20;
    private static float maxAngularVelocity = 30;
    private static float maxAngularVelocityAccel = 10;

    public EnemyFrigateShip(Texture gameObjectTexSheet) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 220, 545, 110, 114) , 80, shipRadius, maxLinearVelocity, maxLinearVelocityAccel, maxAngularVelocity, maxAngularVelocityAccel);
    }
}
