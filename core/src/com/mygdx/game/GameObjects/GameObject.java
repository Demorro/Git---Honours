package com.mygdx.game.GameObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.UUID;

/**
 * Created by Elliot Morris on 27/01/2015.
 * Base gameobject for the gamestate
 */
public abstract class GameObject extends Sprite {

    private UUID id;

    public GameObject()
    {
        id = UUID.randomUUID();
    }

    public UUID GetID()
    {
        return id;
    }
}
