package com.mygdx.game.GameObjects.Ships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.GameObjects.LogicDrivenObject;

/**
 * Created by Elliot Morris on 29/01/2015.
 */
public class Ship extends LogicDrivenObject {

    private TextureRegion shipRegion;

    private float hp = 100;

    public Ship(Texture gameObjectTexSheet, TextureRegion shipRegion, float startHealth)
    {
        super(gameObjectTexSheet);
        this.shipRegion = shipRegion;
        setRegion(shipRegion);

        hp = startHealth;

    }

    public void Render(SpriteBatch batch)
    {
        Render(shipRegion, batch);
    }

    public void ChangeHealth(float healthChange){
        hp += healthChange;
    }
    public float GetHealth()
    {
        return hp;
    }
}
