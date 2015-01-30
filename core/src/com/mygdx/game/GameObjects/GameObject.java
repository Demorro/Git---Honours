package com.mygdx.game.GameObjects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.UUID;

/**
 * Created by Elliot Morris on 27/01/2015.
 * Base gameobject for the gamestate
 */
public abstract class GameObject extends Sprite {

    private UUID id;

    private boolean isActive = true; //Whether on not this bullet is active, also means that is wont render

    public GameObject() //No arguments for poolable
    {
        super();
        id = UUID.randomUUID();
    }

    public GameObject(Texture gameObjectTexSheet)
    {
        super(gameObjectTexSheet);
        id = UUID.randomUUID();
    }

    public UUID GetID()
    {
        return id;
    }

    public void SetActive(boolean _active)
    {
        isActive = _active;
    }
    public boolean GetActive()
    {
        return isActive;
    }

    //Needs to be called from the derived class
    protected void Render(TextureRegion region, SpriteBatch batch)
    {
        batch.draw(region, getX(), getY(), region.getRegionWidth()/2, region.getRegionHeight()/2, region.getRegionWidth(), region.getRegionHeight(), getScaleX(),getScaleY(),getRotation());
    }

    public Vector2 GetCenterPosition()
    {
        return new Vector2(getX() + getRegionWidth()/2, getY() + getRegionHeight()/2);

    }
}
