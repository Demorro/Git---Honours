package com.mygdx.game.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Vector;

/**
 * Created by Elliot Morris on 14/01/2015.
 */
public class Utility {

    public enum Speed
    {
        QUICK,
        MODERATE,
        SLOW
    };

    public enum  Distance{
        FAR,
        MODERATE,
        NEAR
    };

    public enum Weapon {
        AUTOCANNON,
        LASER,
        MISSILE,
        ALL
    }

    public enum Target {
        CAPITAL,
        FRIGATE,
        FIGHTER,
        ALL
    };

    public static Vector2 GetScreenSpaceInput()
    {
        return new Vector2(Gdx.input.getX(), Math.abs(Gdx.input.getY() - Gdx.graphics.getHeight()));

    }
}
