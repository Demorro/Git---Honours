package com.mygdx.game.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Elliot Morris on 02/02/2015.
 */
public class ParralaxBackground {
    private Texture tl = new Texture(Gdx.files.internal("Images/Backgrounds/TopLeft.png"));
    private Texture tr = new Texture(Gdx.files.internal("Images/Backgrounds/TopRight.png"));
    private Texture bl = new Texture(Gdx.files.internal("Images/Backgrounds/BottomLeft.png"));
    private Texture br = new Texture(Gdx.files.internal("Images/Backgrounds/BottomRight.png"));
    private Texture stars = new Texture(Gdx.files.internal("Images/Backgrounds/Stars.png"));

    private Vector2 centralPoint = new Vector2(0,0);

    private Vector2 tlPos = new Vector2(0,0);
    private Vector2 trPos = new Vector2(0,0);
    private Vector2 blPos = new Vector2(0,0);
    private Vector2 brPos = new Vector2(0,0);

    private Vector2 tlStarPos = new Vector2(0,0);
    private Vector2 trStarPos = new Vector2(0,0);
    private Vector2 blStarPos = new Vector2(0,0);
    private Vector2 brStarPos = new Vector2(0,0);
    private float scale = 1.0f;
    private float backgroudParralaxSpeed = 0.2f;
    private float backStarParralaxSpeed = 0.35f;

    public ParralaxBackground(float scale)
    {
        this.scale = scale;

        trPos.set(0,0);
        tlPos.set(-tl.getWidth() * scale,0);
        blPos.set(-bl.getWidth() * scale, -bl.getHeight() * scale);
        brPos.set(0, -br.getHeight() * scale);

        trStarPos.set(0,0);
        tlStarPos.set(-tl.getWidth() * scale,0);
        brStarPos.set(-bl.getWidth() * scale, -bl.getHeight() * scale);
        blStarPos.set(0, -br.getHeight() * scale);
    }

    public void Render(SpriteBatch batch, Vector2 camVelocity){

        tlPos.add(-camVelocity.x * backgroudParralaxSpeed, -camVelocity.y * backgroudParralaxSpeed);
        trPos.add(-camVelocity.x * backgroudParralaxSpeed, -camVelocity.y * backgroudParralaxSpeed);
        blPos.add(-camVelocity.x * backgroudParralaxSpeed, -camVelocity.y * backgroudParralaxSpeed);
        brPos.add(-camVelocity.x * backgroudParralaxSpeed, -camVelocity.y * backgroudParralaxSpeed);

        tlStarPos.add(-camVelocity.x * backStarParralaxSpeed, -camVelocity.y * backStarParralaxSpeed);
        trStarPos.add(-camVelocity.x * backStarParralaxSpeed, -camVelocity.y * backStarParralaxSpeed);
        blStarPos.add(-camVelocity.x * backStarParralaxSpeed, -camVelocity.y * backStarParralaxSpeed);
        brStarPos.add(-camVelocity.x * backStarParralaxSpeed, -camVelocity.y * backStarParralaxSpeed);

        batch.draw(tl, tlPos.x, tlPos.y, tl.getWidth() * scale, tl.getHeight() * scale);
        batch.draw(bl, blPos.x, blPos.y, bl.getWidth() * scale, bl.getHeight() * scale);
        batch.draw(tr, trPos.x, trPos.y, tr.getWidth() * scale, tr.getHeight() * scale);
        batch.draw(br, brPos.x, brPos.y, br.getWidth() * scale, br.getHeight() * scale);
        batch.draw(stars, tlStarPos.x, tlStarPos.y, tl.getWidth() * scale, tl.getHeight() * scale);
        batch.draw(stars, blStarPos.x, blStarPos.y, bl.getWidth() * scale, bl.getHeight() * scale);
        batch.draw(stars, trStarPos.x, trStarPos.y, tr.getWidth() * scale, tr.getHeight() * scale);
        batch.draw(stars, brStarPos.x, brStarPos.y, br.getWidth() * scale, br.getHeight() * scale);
    }
}
