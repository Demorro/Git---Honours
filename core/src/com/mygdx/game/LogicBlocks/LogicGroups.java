package com.mygdx.game.LogicBlocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Command.*;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Distance.CloseBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Distance.FarBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies.CapitalShipBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies.FighterShipBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies.FrigateShipBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Numbers.OneHundredBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.If.When;
import com.mygdx.game.LogicBlocks.SpecificBlocks.MoreLessThan.FullBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.MoreLessThan.LessThanBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.MoreLessThan.MoreThanBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Numbers.*;
import com.mygdx.game.LogicBlocks.SpecificBlocks.PlayerShip.ShipHP;
import com.mygdx.game.LogicBlocks.SpecificBlocks.ScaryObjects.Asteroids;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Speeds.QuicklyBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Speeds.SlowlyBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons.AutoCannonBlock;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons.IfWeapon.IsFiring;
import com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons.IfWeapon.IsntFiring;
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
        CAPITALSHIP,
        FRIGATESHIP,
        FIGHTERSHIP,
        QUICKLY,
        SLOWLY,
        AUTOCANNON,
        MISSILE,
        LASER,
        ASTEROIDS,
        ISFIRING,
        ISNTFIRING,
        CLOSE,
        FAR,
        FULL,
        MORETHAN,
        LESSTHAN,
        ZERO,
        TEN,
        TWENTY,
        THIRTY,
        FORTY,
        FIFTY,
        SIXTY,
        SEVENTY,
        EIGHTY,
        NINETY,
        ONEHUNDRED,
        SHIPHP
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
        IFWEAPON,
        DISTANCE,
        MORELESSTHAN,
        NUMBERS,
        PLAYERSHIP
    };

    //Depending on the group the block belongs to, returns the height of the colour row on the sprite sheet for colour coding.
    public static int GetBlockSheetPixelYByType(LogicGroups.LogicGroup type)
    {
        if(type == LogicGroup.COMMAND) { return 72; }
        else if(type == LogicGroup.ENEMIES) { return 273; }
        else if(type == LogicGroup.WEAPONS) { return 206; }
        else if(type == LogicGroup.SPEED) { return 5; }
        else if(type == LogicGroup.SCARYOBJECTS) {return 340;}
        else if(type == LogicGroup.IF){return 5;}
        else if(type == LogicGroup.IFWEAPON){return 139;}
        else if(type == LogicGroup.DISTANCE){return  72;}
        else if(type == LogicGroup.MORELESSTHAN){ return 273; }
        else if(type == LogicGroup.NUMBERS) {return 206;}
        else if(type == LogicGroup.PLAYERSHIP){ return 139;}
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
        else if(blockType == LogicBlockType.WHEN){
            return new When(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.ISFIRING){
            return new IsFiring(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.ISNTFIRING){
            return new IsntFiring(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.CLOSE){
            return new CloseBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.FAR){
            return new FarBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.FULL){
            return new FullBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.MORETHAN){
            return new MoreThanBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.LESSTHAN){
            return new LessThanBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.ZERO){
            return new ZeroBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.TEN){
            return new TenBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.TWENTY){
            return new TwentyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.THIRTY){
            return new ThirtyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.FORTY){
            return new FortyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.FIFTY){
            return new FiftyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.SIXTY){
            return new SixtyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.SEVENTY){
            return new SeventyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.EIGHTY){
            return new EightyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.NINETY){
            return new NinetyBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.ONEHUNDRED){
            return new OneHundredBlock(blockSpriteSheet, previousBlock);
        }
        else if(blockType == LogicBlockType.SHIPHP){
            return new ShipHP(blockSpriteSheet, previousBlock);
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
        else if(group == LogicGroup.IF)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.WHEN, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.IFWEAPON)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.ISFIRING, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.ISNTFIRING, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.DISTANCE)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.CLOSE, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.FAR, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.MORELESSTHAN)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.FULL, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.MORETHAN, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.LESSTHAN, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.NUMBERS)
        {
            blocks.add(ConstructSpecificBlock(LogicBlockType.ZERO, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.TEN, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.TWENTY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.THIRTY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.FORTY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.FIFTY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.SIXTY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.SEVENTY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.EIGHTY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.NINETY, blockSpriteSheet, previousBlock));
            blocks.add(ConstructSpecificBlock(LogicBlockType.ONEHUNDRED, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else if(group == LogicGroup.PLAYERSHIP){
            blocks.add(ConstructSpecificBlock(LogicBlockType.SHIPHP, blockSpriteSheet, previousBlock));
            return blocks;
        }
        else
        {
            Gdx.app.debug("Error", "Attempting to construct a group that isn't in the ConstructLogicGroup function, you need to update it");
            return null;
        }
    }

    public static int NumberBlockToInt(LogicBlockType numberBlock)
    {
        if(numberBlock == LogicBlockType.ZERO){ return 0;}
        else if(numberBlock == LogicBlockType.TEN){ return 10;}
        else if(numberBlock == LogicBlockType.TWENTY){ return 20;}
        else if(numberBlock == LogicBlockType.THIRTY){ return 30;}
        else if(numberBlock == LogicBlockType.FORTY){ return 40;}
        else if(numberBlock == LogicBlockType.FIFTY){ return 50;}
        else if(numberBlock == LogicBlockType.SIXTY){ return 60;}
        else if(numberBlock == LogicBlockType.SEVENTY){ return 70;}
        else if(numberBlock == LogicBlockType.EIGHTY){ return 80;}
        else if(numberBlock == LogicBlockType.NINETY){ return 90;}
        else if(numberBlock == LogicBlockType.ONEHUNDRED){ return 100;}

        return 0;
    }

}
