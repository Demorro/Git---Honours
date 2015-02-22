package com.mygdx.game.LogicBlocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Command.*;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies.CapitalShipBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies.FighterShipBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies.FrigateShipBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.If.When;
import com.mygdx.game.LogicBlocks.SpecificBlocks.PowerUps.Ammo;
import com.mygdx.game.LogicBlocks.SpecificBlocks.PowerUps.Fuel;
import com.mygdx.game.LogicBlocks.SpecificBlocks.PowerUps.Repairs;
import com.mygdx.game.LogicBlocks.SpecificBlocks.ScaryObjects.Asteroids;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Speeds.QuicklyBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Speeds.SlowlyBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons.AutoCannonBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons.LaserBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons.MissileBlock;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 * definition class for defining stuff relating to the logic flow of the visual programming
 */
public class LogicGroups {

    //The individual blocks, one for each specific control block
    public static enum LogicBlockType
    {
        WHEN,
        ATTACK,
        EVADE,
        PURSUE,
        WANDER,
        SEARCHFOR,
        CAPITALSHIP,
        FRIGATESHIP,
        FIGHTERSHIP,
        QUICKLY,
        SLOWLY,
        AUTOCANNON,
        MISSILE,
        LASER,
        REPAIRS,
        AMMO,
        FUEl,
        ASTEROIDS
    };

    //The general block groups, used for defining flow easily.
    public static enum LogicGroup
    {
        IF,
        COMMAND,
        ENEMIES,
        WEAPONS,
        SPEED,
        SCARYOBJECTS,
        POWERUPS
    };

    //Depending on the group the block belongs to, returns the height of the colour row on the sprite sheet for colour coding.
    public static int GetBlockSheetPixelYByType(LogicGroups.LogicGroup type)
    {
        if(type == LogicGroup.COMMAND) { return 72; }
        else if(type == LogicGroup.ENEMIES) { return 273; }
        else if(type == LogicGroup.WEAPONS) { return 206; }
        else if(type == LogicGroup.SPEED) { return 5; }
        else if(type == LogicGroup.SCARYOBJECTS) {return 340;}
        else if(type == LogicGroup.POWERUPS){return 139;}
        else if(type == LogicGroup.IF){return 5;}
        else{ return -1;} //Error code
    }
    //Depending on the length of the text, we want a longer block, which are arranged horizontally. x of vector2 is x coord, y is width
    public static Vector2 GetBlockSheetPixelXAndWidthByTextLength(BitmapFont font, String text)
    {
        final int textWidthPadding = 14; //Padding pixels on the text width, the block will default to go longer if the text is longer than it, and this padding gives the text some extra room if it would overlap too far.

        //X Co-ords and widths of the start of the blocks on the spritesheet
        final Vector2 zeroLengthBlock = new Vector2(5,40);
        final Vector2 oneLengthBlock = new Vector2(50,80);
        final Vector2 twoLengthBlock = new Vector2(135,119);
        final Vector2 threeLengthBlock = new Vector2(259,159);
        final Vector2 fourLengthBlock = new Vector2(423,199);
        final Vector2 fiveLengthBlock = new Vector2(627,239);
        final Vector2 sixLengthBlock = new Vector2(871,279);
        final Vector2 sevenLengthBlock = new Vector2(1155,319);

        float textWidth = font.getBounds(text).width;

        if(textWidth + textWidthPadding < zeroLengthBlock.y)
        {
            return zeroLengthBlock;
        }
        else if(textWidth + textWidthPadding < oneLengthBlock.y)
        {
            return oneLengthBlock;
        }
        else if(textWidth + textWidthPadding < twoLengthBlock.y)
        {
            return twoLengthBlock;
        }
        else if(textWidth + textWidthPadding < threeLengthBlock.y)
        {
            return threeLengthBlock;
        }
        else if(textWidth + textWidthPadding < fourLengthBlock.y)
        {
            return fourLengthBlock;
        }
        else if(textWidth + textWidthPadding < fiveLengthBlock.y)
        {
            return fiveLengthBlock;
        }
        else if(textWidth + textWidthPadding < sixLengthBlock.y)
        {
            return sixLengthBlock;
        }
        else
        {
            return sevenLengthBlock;
        }

    }

    public static LogicBlock ConstructSpecificBlock(LogicBlockType blockType, Texture blockSpriteSheet, LogicBlock previousBlock)
    {
        if(blockType == LogicBlockType.ATTACK) {
            return new AttackBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.EVADE) {
            return new EvadeBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.PURSUE) {
            return new PursueBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.WANDER) {
            return new WanderBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.SEARCHFOR){
            return new SearchForBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.CAPITALSHIP) {
            return new CapitalShipBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.FRIGATESHIP){
            return new FrigateShipBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.FIGHTERSHIP) {
            return new FighterShipBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.ASTEROIDS){
            return new Asteroids(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.QUICKLY) {
            return new QuicklyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.SLOWLY) {
            return new SlowlyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.AUTOCANNON) {
            return new AutoCannonBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.MISSILE) {
            return new MissileBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.LASER) {
            return new LaserBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.AMMO) {
            return new Ammo(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.REPAIRS){
            return  new Repairs(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.FUEl){
            return new Fuel(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.WHEN){
            return new When(blockSpriteSheet, previousBlock);
        }
        else {
            Gdx.app.debug("Error", "Attempting to construct a block that isn't in the ConstructSpecificBlock function, you need to update it");
            return null;
        }
    }

    public static ArrayList<LogicBlock> ConstructLogicGroup(LogicGroup group,  Texture blockSpriteSheet,  LogicBlock previousBlock)
    {
        ArrayList<LogicBlock> blocks = new ArrayList<LogicBlock>();

        if(group == LogicGroup.COMMAND)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.ATTACK, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.EVADE, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.WANDER, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.PURSUE, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.SEARCHFOR, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.ENEMIES)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.CAPITALSHIP, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.FRIGATESHIP, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.FIGHTERSHIP, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.WEAPONS)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.AUTOCANNON, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.MISSILE, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.LASER, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.SPEED)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.QUICKLY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.SLOWLY, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.SCARYOBJECTS)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.ASTEROIDS, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.POWERUPS)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.REPAIRS, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.AMMO, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.FUEl, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.IF)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.WHEN, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else
        {
            Gdx.app.debug("Error", "Attempting to construct a group that isn't in the ConstructLogicGroup function, you need to update it");
            return null;
        }
    }

}
