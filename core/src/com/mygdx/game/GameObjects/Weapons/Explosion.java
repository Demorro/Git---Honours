package com.mygdx.game.GameObjects.Weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Elliot Morris on 14/02/2015.
 * when something is destroyed, this is a short animation that is played. normally used on bullets.
 */
public class Explosion extends Sprite
{

    float damageValue = 0;
    boolean doesDamage;

    private boolean isExploding = false;
    private TextureAtlas explosionAnimAtlas;
    private Animation explosionAnimation;
    private float animElapsedTime = 0.0f;
    TextureRegion currentAnimRegion = new TextureRegion();

    public Explosion(TextureAtlas explosionAtlas, float explosionFPS, boolean doesDamage, float damageValue)
    {
        super(explosionAtlas.getTextures().first());

        this.explosionAnimAtlas = explosionAtlas;
        explosionAnimation = new Animation(1/explosionFPS, explosionAtlas.getRegions());

        this.doesDamage = doesDamage;
        this.damageValue = damageValue;

        isExploding = true;
        currentAnimRegion = explosionAnimation.getKeyFrame(animElapsedTime, false);
    }

    public void Update(float elapsed)
    {

        if(explosionAnimation.isAnimationFinished(animElapsedTime))
        {
            isExploding = false;
        }
    }

    public void Render(SpriteBatch batch)
    {
        animElapsedTime += Gdx.graphics.getDeltaTime();
        currentAnimRegion = explosionAnimation.getKeyFrame(animElapsedTime, false);
        Vector2 animOrigin = new Vector2(currentAnimRegion.getRegionWidth()/2, currentAnimRegion.getRegionWidth()/2);
        batch.draw(currentAnimRegion ,getX(), getY(), animOrigin.x, animOrigin.y , currentAnimRegion.getRegionWidth(), currentAnimRegion.getRegionHeight() , getScaleX() , getScaleY(), getRotation());
    }

    public boolean IsCurrentlyExploding()
    {
        return isExploding;
    }

    public float GetFrameWidth()
    {
        return currentAnimRegion.getRegionWidth();
    }
    public float GetFrameHeight()
    {
        return currentAnimRegion.getRegionHeight();
    }
}
