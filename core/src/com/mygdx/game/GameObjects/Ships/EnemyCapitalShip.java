package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyCapitalShip extends Ship {
    public EnemyCapitalShip(Texture gameObjectTexSheet) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 0, 545, 200, 178) , 200);
    }
}
