package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Elliot Morris on 26/02/2015.
 */
public class BlockDescriptionPanel extends Sprite
{
    private BitmapFont descriptionFont = null;
    private static String defaultString = "Touch the green arrow to begin building your ships logic.";
    private String descriptionText = "";
    private static Vector2 offsetFromTopRight = new Vector2(30,25);
    private static Vector2 textOffset = new Vector2(20,30);

    public BlockDescriptionPanel(float xPos, float yPos, BitmapFont font, Texture panelTexture, float screenWidth, float screenHeight){
        super(panelTexture);
        setX(screenWidth - offsetFromTopRight.x - panelTexture.getWidth());
        setY(screenHeight - offsetFromTopRight.y - panelTexture.getHeight());

        descriptionFont = font;
        descriptionText = defaultString;
    }

    public void ResetDescriptionText(){
        descriptionText = defaultString;
    }

    public void SetDescriptionText(String text){
        descriptionText = text;
    }

    public void Render(SpriteBatch batch){
        super.draw(batch);
        descriptionFont.drawWrapped(batch, descriptionText, getX() + textOffset.x, getY() + getHeight() - textOffset.y, getWidth() - textOffset.x * 2, BitmapFont.HAlignment.CENTER);
    }

    public void CleanUp(){
        descriptionFont.dispose();
    }
}
