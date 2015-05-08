package com.mygdx.game.LogicBlocks;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Elliot Morris on 11/01/2015.
 * Base class for logic blocks, deals with rendering + text
 */
public abstract class LogicBlock
{
    private LogicGroups.LogicGroup blockGroup;
    private LogicGroups.LogicBlockType blockType;

    private Vector2 position;

    private Texture blockSpriteSheet;
    private Sprite blockGraphic = new Sprite();
    public final static int blockHeight = 62;

    private BitmapFont blockFont;
    private final static float fontSize = 1.0f;
    private final static Color fontColor = Color.BLACK;
    private String blockText;
    private Vector2 textPosition;


    public static final Vector2 cancelButtonOffset = new Vector2(-8,-8); //The offset the cancel button should be from the top right corner of the block

    public LogicBlock(Texture blockSheet, String text, LogicGroups.LogicGroup group, LogicGroups.LogicBlockType type)
    {
        blockSpriteSheet = blockSheet;
        blockFont =  new BitmapFont(Gdx.files.internal("Fonts/LogicFont.fnt"));
        blockFont.setOwnsTexture(true);
        blockFont.setColor(fontColor);
        blockFont.setScale(fontSize);
        blockFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); //Set Smooth Text
        position = new Vector2(0,0);
        blockText = text;
        blockGroup = group;
        blockType = type;
        LoadBlockGraphics();
        textPosition = new Vector2(position.x + (GetFullBlockWidth() - blockFont.getBounds(blockText).width)/2 , position.y + blockHeight/2 + blockFont.getXHeight()/2);
    }
    public LogicBlock(Texture blockSheet, String text, LogicGroups.LogicGroup group, LogicGroups.LogicBlockType type, float x, float y)
    {
        blockSpriteSheet = blockSheet;
        blockFont =  new BitmapFont(Gdx.files.internal("Fonts/LogicFont.fnt"));
        blockFont.setColor(fontColor);
        blockFont.setScale(fontSize);
        blockFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); //Set Smooth Text
        position = new Vector2(x,y);
        blockText = text;
        blockGroup = group;
        blockType = type;
        LoadBlockGraphics();
        textPosition = new Vector2(position.x + (GetFullBlockWidth() - blockFont.getBounds(blockText).width)/2 , position.y + blockHeight/2 + blockFont.getXHeight()/2);
    }

    protected void ResetBlockText(String text)
    {
        blockFont.setScale(fontSize);
        blockFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); //Set Smooth Text
        blockText = text;
        LoadBlockGraphics();
        textPosition = new Vector2(position.x + (GetFullBlockWidth() - blockFont.getBounds(blockText).width)/2 , position.y + blockHeight/2 + blockFont.getXHeight()/2);
    }

    public void Render(SpriteBatch batch)
    {
        blockGraphic.draw(batch);
        blockFont.draw(batch,blockText,textPosition.x, textPosition.y);
    }

    public void SetPosition(float x, float y)
    {
        position.x = x;
        position.y = y;

        blockGraphic.setPosition(x, y);

        textPosition.x = position.x + (GetFullBlockWidth() - blockFont.getBounds(blockText).width)/2;
        textPosition.y = position.y + blockHeight/2 + blockFont.getXHeight()/2;
    }

    public void SetX(float x)
    {
        position.x = x;
        blockGraphic.setX(x);
        textPosition.x = position.x + (GetFullBlockWidth() - blockFont.getBounds(blockText).width)/2;

    }
    public  void SetY(float y)
    {
        position.y = y;
        blockGraphic.setY(y);
        textPosition.y = position.y + blockHeight/2 + blockFont.getXHeight()/2;
    }

    public void Move(float xMov, float yMov)
    {
        position.x += xMov;
        position.y += yMov;

        blockGraphic.setPosition(blockGraphic.getX() + xMov, blockGraphic.getY() + yMov);

        textPosition.x += xMov;
        textPosition.y += yMov;
    }

    public void SetOpacity (float opacity)
    {
        blockGraphic.setAlpha(opacity);
        blockFont.setColor(blockFont.getColor().r, blockFont.getColor().g, blockFont.getColor().b, opacity);
    }
    public void SetOpacityWithoutChangingText(float opacity)
    {
        blockGraphic.setAlpha(opacity);
    }
    public void SetTextOpacity(float opacity)
    {
        blockFont.setColor(blockFont.getColor().r, blockFont.getColor().g, blockFont.getColor().b, opacity);
    }

    public void SetScaleXY(float scale)
    {
        blockGraphic.setScale(scale);
        blockFont.setScale(scale);
        textPosition.x = position.x + (GetFullBlockWidth() - blockFont.getBounds(blockText).width)/2;
        textPosition.y = position.y + blockHeight/2 + blockFont.getXHeight()/2;
    }

    public void SetScaleX(float xScale)
    {
        blockGraphic.setScale(xScale, blockGraphic.getScaleY());
        blockFont.setScale(xScale, blockFont.getScaleY());
        textPosition.x = position.x + (GetFullBlockWidth() - blockFont.getBounds(blockText).width)/2;
    }
    public void SetScaleY(float yScale)
    {
        blockGraphic.setScale(blockGraphic.getScaleX(),yScale);
        blockFont.setScale(blockFont.getScaleX(), yScale);
        textPosition.y = position.y + blockHeight/2 + blockFont.getXHeight()/2;
    }

    public float GetOpacity()
    {
        return blockGraphic.getColor().a;
    }
    public float GetTextOpacity()
    {
        return blockFont.getColor().a;
    }
    public float GetScaleX()
    {
        return blockGraphic.getScaleX();
    }
    public float GetScaleY()
    {
        return blockGraphic.getScaleY();
    }

    private void LoadBlockGraphics() //Loads a loloured block graphic depending on the type, and adjusts the length tp be able to fit the text
    {
        int colourYIndex = LogicGroups.GetBlockSheetPixelYByType(blockGroup);
        Vector2 blockXIndexAndWidth = LogicGroups.GetBlockSheetPixelXAndWidthByTextLength(blockFont, blockText);

        blockGraphic = new Sprite(blockSpriteSheet, (int)blockXIndexAndWidth.x, (int)colourYIndex, (int)blockXIndexAndWidth.y, (int)blockHeight);
    }

    public float GetFullBlockWidth()
    {
        return blockGraphic.getWidth();
    }

    public float GetX()
    {
        return position.x;
    }
    public float GetY()
    {
        return position.y;
    }

    public Rectangle GetBoundingRectangle()
    {
        return blockGraphic.getBoundingRectangle();
    }

    public LogicGroups.LogicBlockType GetBlockType()
    {
        return blockType;
    }
    public abstract ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock); //Returns the next logic group that can be selected after this block
    public static int GetBlockHeight()
    {
        return blockHeight;
    }

    public void CleanUp()
    {
        blockFont.dispose();
    }

    public abstract String GetBlockDescription();
}
