package com.mygdx.game.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import org.omg.CORBA.BAD_TYPECODE;

/**
 * Created by Elliot Morris on 02/02/2015.
 */
public class ParralaxBackground {
    private Texture bg = new Texture(Gdx.files.internal("Images/Backgrounds/Background.png"));
    private Texture stars = new Texture(Gdx.files.internal("Images/Backgrounds/Stars.png"));
    private Texture stars1 = new Texture(Gdx.files.internal("Images/Backgrounds/Stars1.png"));
    private Texture foreGroundParticles = new Texture(Gdx.files.internal("Images/Backgrounds/ForeGroundParticles.png"));

    private Vector2 bgPos = new Vector2(0,0);

    private Vector2 bgStarPos = new Vector2(0,0);
    private Vector2 stars1Pos = new Vector2(0,0);
    private Vector2 foreGroundStarPos = new Vector2(0,0);

    private float scale = 1.0f;
    private float backgroudParralaxSpeed = 0.2f;
    private float backStarParralaxSpeed = 0.35f;
    private float stars1ParralaxSpeed = 0.75f;
    private float foreGroundParticleParralaxSpeed = 1.75f;

    public ParralaxBackground(float scale)
    {
        this.scale = scale;
        bgPos.set(0,0);
        bgStarPos.set(0,0);
        stars1Pos.set(0,0);
        foreGroundStarPos.set(0,0);
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        stars.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        stars1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        foreGroundParticles.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public void Render(SpriteBatch batch, Vector2 camVelocity){

        bgPos.add(camVelocity.x * backgroudParralaxSpeed, -camVelocity.y * backgroudParralaxSpeed);
        bgStarPos.add(camVelocity.x * backStarParralaxSpeed, -camVelocity.y * backStarParralaxSpeed);
        stars1Pos.add(camVelocity.x * stars1ParralaxSpeed, -camVelocity.y * stars1ParralaxSpeed);

        batch.draw(bg ,0 , 0 ,(int)bgPos.x,(int)bgPos.y, bg.getWidth() , bg.getHeight() );
        batch.draw(stars1, 0,0, (int)bgStarPos.x, (int)bgStarPos.y, stars1.getWidth() , stars1.getHeight()  );
        batch.draw(stars, 0,0, (int)stars1Pos.x, (int)stars1Pos.y, stars.getWidth() , stars.getHeight());
    }
    public void RenderForeground(SpriteBatch batch, Vector2 camVelocity)
    {
        foreGroundStarPos.add(camVelocity.x * foreGroundParticleParralaxSpeed, -camVelocity.y * foreGroundParticleParralaxSpeed);
        batch.draw(foreGroundParticles,0,0 ,(int)foreGroundStarPos.x,(int)foreGroundStarPos.y, foreGroundParticles.getWidth() , foreGroundParticles.getHeight());
    }

    public void Dispose()
    {
        bg.dispose();
        stars.dispose();
        stars1.dispose();
        foreGroundParticles.dispose();
    }
}
