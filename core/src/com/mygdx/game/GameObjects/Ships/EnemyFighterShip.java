package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class EnemyFighterShip extends Ship {
    public EnemyFighterShip(Texture gameObjectTexSheet) {
        super(gameObjectTexSheet, new TextureRegion(gameObjectTexSheet, 350, 545, 74, 50) , 30);
    }
}
