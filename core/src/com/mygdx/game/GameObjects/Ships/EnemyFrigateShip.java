package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyFrigateShip extends Ship {
    public EnemyFrigateShip(Texture gameObjectTexSheet) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 220, 545, 110, 114) , 80);
    }
}
