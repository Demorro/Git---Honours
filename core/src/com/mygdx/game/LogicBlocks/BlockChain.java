package com.mygdx.game.LogicBlocks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.UI.BlockDescriptionPanel;
import com.mygdx.game.UI.BlockSelectionList;
import com.mygdx.game.UI.CancelBlockButton;
import com.mygdx.game.UI.NextBlockButton;
import com.mygdx.game.Utility.SpriteAccessor;
import jdk.nashorn.internal.ir.Block;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Observable;

/**
 * Created by Elliot Morris on 14/01/2015.
 * Chunk of blocks, one "Line" of logic block code
 */
public class BlockChain {

    private FullBlockScript fullScript; //Reference to the full script.
    private CancelBlockButton cancelButton; //The button that cancels a block, not the button that cancels a list, THEYRE DIFFERENT THINGS DAMMIT
    private NextBlockButton nextButton; //The button that either summons a new list or alternatively lets you select the block in the list

    private boolean enabled = true; //Simply disables update logic.
    private boolean visible = true;
    private boolean shouldBeDeleted = false;

    //References to the above and below blockchain. Only accessed by the fullBlockScript.
    private BlockChain aboveBlockChain = null;
    private BlockChain belowBlockChain = null;

    private Vector2 position = new Vector2(); //Position of the chain, changing this wont actually change anything, gotta do dat yoself son
    private ArrayList<LogicBlock> blocks = new ArrayList<LogicBlock>(); //Main storage for the blocks in the chain, stored from left to right
    private Rectangle blockChainBounds = new Rectangle(0,0,0,LogicBlock.blockHeight); //The physical bounds of the chain, don't trust this too much

    private BlockSelectionList selectionList = null; //The chain has one list that just moves and is reloaded ... which is this
    private final ArrayList<LogicGroups.LogicGroup> startingGroups  = new ArrayList<LogicGroups.LogicGroup>(); //The groups that are first offered when the user starts a new chain.
    private ArrayList<LogicGroups.LogicGroup> nextGroups = null; //The next groups that should be opened

    private LogicBlock previousBlock = null; //Store the previous block for correct logic routing

    private static final int spacingBetweenBlocks = 60; //Space that the next block (normally the list) has from the previous entry
    private static final int spacingBetweenNextButton = 10; //Space that the next button has from the previous block

    private Texture blockTextureSheet; //Reference to the texture sheet

    TweenManager chainTweenManager = new TweenManager(); //Tween manager
    private static float timeToTweenFromListToChain = 0.2f; //Time that the selected block takes to tween from the selection list into the main chain, also used for most other tweens (I know its bad shut up)

    private boolean isTopChain = false;; //if this chain is the very top one
    private boolean isOnEndOfChain = false; //True if the chain is finished, with no other blocks to be placed.
    public boolean needsNewLine = false;

    //Used for opacity tweening
    private float currentOpacity = 1.0f;

    public ArrayList<BlockChain> childChains = new ArrayList<BlockChain>(); //If this blockchain is an if statement, it can have a child chain
    public ArrayList<BlockChain> parentContainer = null;
    private BlockChain nextBlockAfterIf = null; //The next block that is in the script after if if indentation, if this block is an if block.
    private Sprite ifIndentationGraphic;
    private Vector2 ifIndentationGraphicOffset = new Vector2();

    private BitmapFont debugFont;
    public int lineNo = 0;

    private ListIterator<BlockChain> it = null;

    public BlockChain(float xPos, float yPos, Texture blockSpriteSheet, FullBlockScript fullScript, ArrayList<BlockChain> parentContainer)
    {
        this.fullScript = fullScript;
        blockTextureSheet = blockSpriteSheet;

        debugFont =  new BitmapFont(Gdx.files.internal("Fonts/8Bitfont.fnt"));
        debugFont.setOwnsTexture(true);
        debugFont.setColor(0,1,1,1);
        debugFont.setScale(1.0f);

        ResetChain(xPos, yPos);

        selectionList = new BlockSelectionList(startingGroups, blockTextureSheet, new Vector2(position.x + spacingBetweenBlocks, position.y), false, this, null);
        this.parentContainer = parentContainer;
    }

    //Resets errythang
    private void ResetChain(float xPos, float yPos)
    {
        position.x = xPos;
        position.y = yPos;

        cancelButton = new CancelBlockButton(blockTextureSheet, this);
        nextButton = new NextBlockButton(blockTextureSheet, this);

        cancelButton.setPosition(position.x, position.y);
        cancelButton.SetEnabled(false);
        nextButton.setPosition(position.x, position.y);

        blocks.clear();
        selectionList = null;
        previousBlock = null;

        ifIndentationGraphic = null;

        blockChainBounds.setPosition(position.x, position.y - (LogicBlock.blockHeight - nextButton.getHeight())/2);
        blockChainBounds.setHeight(LogicBlock.blockHeight);

        startingGroups.clear();
        startingGroups.add(LogicGroups.LogicGroup.COMMAND);
        startingGroups.add(LogicGroups.LogicGroup.IF);
        nextGroups = new ArrayList<LogicGroups.LogicGroup>(startingGroups);

        cancelButton.SetVisible(false);
        cancelButton.SetEnabled(false);

    }

    //Called by the blockList when the next block is selected
    public void ListBlockSelected(final LogicBlock nextBlock, float timeToWaitTillDestroy)
    {
        nextGroups.clear();


        if(previousBlock != null) {
            if (previousBlock.GetBlockType() == null) {
                System.out.println("PreviousGroup : NULL");
            } else {
                System.out.println("PreviousGroup :" + previousBlock.GetBlockType());
            }
        }
        else
        {
            System.out.println("Previous Block is NULL");
        }

        if(nextBlock.GetNextLogicGroup(previousBlock) != null) {
            nextGroups.addAll(nextBlock.GetNextLogicGroup(previousBlock));

            //The timer triggers when the scroll animation is done, setting stuff right
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    selectionList.ResetList(nextGroups, new Vector2(blockChainBounds.getX() + blockChainBounds.getWidth() + spacingBetweenBlocks, position.y), false, false, previousBlock);
                    selectionList.OpenList();
                }
            }, timeToWaitTillDestroy);
        }


        //Tween the selected block to its rightful place in the block chain
        blocks.add(nextBlock);


        Tween.to(nextBlock, LogicBlockAccessor.POSITION_X, timeToTweenFromListToChain)
                .target(blockChainBounds.getX() + blockChainBounds.getWidth())
                .start(chainTweenManager);

        blockChainBounds.setWidth(blockChainBounds.getWidth() + nextBlock.GetFullBlockWidth());

        nextButton.setX(blockChainBounds.getX() + blockChainBounds.getWidth() + spacingBetweenNextButton);
        cancelButton.SetEnabled(false);
        cancelButton.SetVisible(false);



        //If there is no logic group to go too, the chain is ended
        if(nextBlock.GetNextLogicGroup(previousBlock) == null)
        {
            FadeInCancelButtonAtLastBlock();
            nextButton.SetEnabled(false);
            nextButton.SetVisible(false);
            isOnEndOfChain = true;
            needsNewLine = true;
            fullScript.AnyListClosed(this);
            fullScript.needToRunNewChainCheck = true;
        }
        previousBlock = nextBlock;
    }

    public void ListClosed()
    {
        fullScript.AnyListClosed(this);
        if(blocks.size() > 0) {
            FadeInCancelButtonAtLastBlock();
        }
        else {
            FadeOutCancelButton();
        }
    }

    private void RemoveBlockFromEndOfChain()
    {
        blockChainBounds.setWidth(blockChainBounds.getWidth() - blocks.get(blocks.size() - 1).GetFullBlockWidth());
        position.x -= blocks.get(blocks.size() - 1).GetFullBlockWidth();

        //Fade the block out
        Tween.to(blocks.get(blocks.size() - 1), LogicBlockAccessor.OPACITY, timeToTweenFromListToChain)
                .target(0.0f)
                .start(chainTweenManager);

        //Fade out cancel button
        FadeOutCancelButton();

        //Tween the next button back to the previous block
        //The if/else thing is because we dont want to do the spacing seperation if we are tweening the next button back to the very start, without a block to space from
        if(blocks.size() > 1) {
            Tween.to((nextButton), SpriteAccessor.POSITION_X, timeToTweenFromListToChain)
                    .target(blockChainBounds.getX() + blockChainBounds.getWidth() + spacingBetweenNextButton)
                    .start(chainTweenManager);
        }
        else{
            //Tween to very start
            Tween.to((nextButton), SpriteAccessor.POSITION_X, timeToTweenFromListToChain)
                    .target(blockChainBounds.getX() + blockChainBounds.getWidth())
                    .start(chainTweenManager);

        }

        //Disable buttons, to re-enable after tween
        cancelButton.SetEnabled(false);
        nextButton.SetEnabled(false);

        //If the next button is invisible (ie at the end of chain,) make it visible and fade it back in
        if(isOnEndOfChain) {
            nextButton.setAlpha(0.0f);
            nextButton.SetVisible(true);
            //Tween the next button back to the previous block
            Tween.to((nextButton), SpriteAccessor.OPACITY, timeToTweenFromListToChain * 2)
                    .target(1.0f)
                    .start(chainTweenManager);
        }


        //The timer triggers when the scroll animation is done, setting stuff right
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                blocks.remove(blocks.size() - 1);
                //set the previous block to the block before the very last. Null if there isnt one.
                if(blocks.size() > 0){
                    previousBlock = blocks.get(blocks.size() - 1);
                }
                else{
                    DoChainDestruction();
                    //Check above and below for destruction
                    if(GetBelowBlockChain() != null){
                        if(isTopChain) {
                            GetBelowBlockChain().DoChainDestruction();
                        }
                    }
                }
                if(blocks.size() > 1)
                {
                    FadeInCancelButtonAtLastBlock();
                    previousBlock = blocks.get(blocks.size() - 2);
                    selectionList.ResetList(blocks.get(blocks.size() - 1).GetNextLogicGroup(previousBlock), new Vector2(position.x  + spacingBetweenBlocks, position.y), false, false, previousBlock);
                }
                else if(blocks.size() > 0){
                    FadeInCancelButtonAtLastBlock();
                    selectionList.ResetList(blocks.get(blocks.size() - 1).GetNextLogicGroup(previousBlock), new Vector2(position.x  + spacingBetweenBlocks, position.y), false, false, previousBlock);
                }
                else
                {
                    FadeOutCancelButton();
                    if(selectionList != null) {
                        selectionList.ResetList(startingGroups, new Vector2(position.x + spacingBetweenBlocks, position.y), false, false, previousBlock);
                    }
                }

                //Re-enable the next button after tween
                nextButton.SetEnabled(true);
                cancelButton.SetEnabled(true);

            }
        }, timeToTweenFromListToChain);

        isOnEndOfChain = false; //Its important that this is set at the end of this function
    }
    private void ClearEntireChain(){
        ResetChain(GetX(), GetY());
        FadeOutCancelButton();
        DoChainDestruction();
        if(GetBelowBlockChain() != null){
            GetBelowBlockChain().DoChainDestruction();
        }
    }
    public void DeleteChain()
    {
        cancelButton.CleanUp(); //The button that cancels a block, not the button that cancels a list, THEYRE DIFFERENT THINGS DAMMIT
        nextButton.CleanUp(); //The button that either summons a new list or alternatively lets you select the block in the list


        if(previousBlock != null) {
            previousBlock.CleanUp();
            previousBlock = null;
        }

        if(debugFont != null) {
            debugFont.dispose();
            debugFont = null;
        }

        if((belowBlockChain != null) && (aboveBlockChain != null)) {
            belowBlockChain.aboveBlockChain = aboveBlockChain; //This chain is being deleted, so set the correct up/downs
            aboveBlockChain.belowBlockChain = belowBlockChain;
        }
        shouldBeDeleted = true;

        ClearChildChains();
        childChains = null;
        SetVisible(false);
        DestroyChildChains();
        if(debugFont != null) {
            debugFont.dispose();
        }

    }
    private void ClearChildChains()
    {
        if(childChains != null) {
            for (BlockChain chain : childChains) {
                chain.DeleteChain();
                chain.SetVisible(false);
            }
            childChains.clear();
        }
    }

    public void MoveChainToPosition(Vector2 position){
        //Fade out the cancel button
        Tween.to(this, BlockChainAccessor.POSITION_XY, timeToTweenFromListToChain)
                .target(position.x, position.y)
                .start(chainTweenManager);

    }
    //Called by nextButton when it is triggered
    public void NextButtonPushed()
    {
        if(selectionList == null)
        {
            fullScript.AnyListOpened(this); //This needs to happen before you open the list, trust me my son, (For reals, its because Anylistopens closes all the list, and we want to open dis one)
            selectionList = new BlockSelectionList(startingGroups, blockTextureSheet, new Vector2(blockChainBounds.getX() + blockChainBounds.getWidth() + nextButton.getWidth() + spacingBetweenNextButton , position.y), false, this, null);
            selectionList.OpenList();
        }
        else
        {
            //Fade out cancel
            FadeOutCancelButton();
            //So that the next button selects a block as well
            if(selectionList.GetIsOpen())
            {
                selectionList.SelectBlock();
            }
            else {
                fullScript.AnyListOpened(this);  //This needs to happen before you open the list, trust me my son, (For reals, its because Anylistopens closes all the list, and we want to open dis one)
                selectionList.SetPosition(blockChainBounds.getX() + blockChainBounds.getWidth() + nextButton.getWidth() + spacingBetweenNextButton, position.y);
                selectionList.OpenList();
            }
        }
    }
    //Called by cancel button when it is triggered
    public void CancelButtonPushed()
    {
        if(GetIsIfStatement()){
            ClearEntireChain();
        }
        else if(blocks.size() > 0) {
            RemoveBlockFromEndOfChain();
        }
    }

    public void Update(BlockDescriptionPanel panel)
    {
        if(enabled) {

            if (nextButton.GetEnabled()) {
                //nextButton.SetVisible(true);
                nextButton.Update();
            }
            if (blocks.size() > 0) {
                cancelButton.Update();
            }

            if (selectionList != null) {
                selectionList.Update();
            }

            chainTweenManager.update(Gdx.graphics.getDeltaTime());

        }

        DestroyChildChains();

        if(GetSelectionList() != null){
            if(GetSelectionList().GetIsOpen()){
                panel.SetDescriptionText(GetSelectionList().GetCurrentBlock().GetBlockDescription());
            }
        }
        if(childChains != null) {
            for (BlockChain chain : childChains) {
                chain.Update(panel);
            }
        }
    }

    public void Render(SpriteBatch batch)
    {
        if(visible) {

            if(ifIndentationGraphic != null){
                ifIndentationGraphic.draw(batch);
            }

            if (childChains != null) {
                for (BlockChain chain : childChains) {
                    chain.Render(batch);
                }
            }

            nextButton.Render(batch);

            for (LogicBlock block : blocks) {
                block.Render(batch);
            }
            if (selectionList != null) {
                selectionList.Render(batch);
            }
            cancelButton.Render(batch);



            /*
            debugFont.draw(batch, Integer.toString(lineNo),  GetX(), position.y);
            if(GetAboveBlockChain() != null) {
                debugFont.draw(batch, "Above : " + Integer.toString(aboveBlockChain.lineNo), GetX() + 160, position.y);
            }
            if(GetBelowBlockChain() != null) {
                debugFont.draw(batch, "Below : " + Integer.toString(belowBlockChain.lineNo), GetX() + 320, position.y);
            }
            if(GetNextBlockAfterIf() != null){
                debugFont.draw(batch, "NextAfterIF : " + Integer.toString(GetNextBlockAfterIf().lineNo),  GetX() + 480, position.y);
            }
            if(parentContainer != null){
                debugFont.draw(batch, "First in parent container : " + Integer.toString(parentContainer.get(0).lineNo),  GetX() + 680, position.y);
            }
            */

        }
    }

    public NextBlockButton GetNextBlockButton()
    {
        return  nextButton;
    }

    private void SetCancelButtonPosToLastBlock()
    {
        if(blocks.size() > 0) {
            cancelButton.setPosition(blockChainBounds.getX() + blockChainBounds.getWidth() - (cancelButton.getWidth() / 2) + LogicBlock.cancelButtonOffset.x, blocks.get(blocks.size() - 1).GetY() + LogicBlock.GetBlockHeight() - (cancelButton.getHeight() / 2) + LogicBlock.cancelButtonOffset.y);
        }
        else{
            cancelButton.setPosition(position.x, position.y);
        }
    }

    private void FadeInCancelButtonAtLastBlock()
    {
        //Set the cancel button to the position of the last block, and fade it in
        if(blocks.size() > 0)
        {
            cancelButton.setPosition(blockChainBounds.getX() + blockChainBounds.getWidth() - (cancelButton.getWidth()/2) + LogicBlock.cancelButtonOffset.x, blocks.get(blocks.size() - 1).GetY() + LogicBlock.GetBlockHeight() - (cancelButton.getHeight()/2) + LogicBlock.cancelButtonOffset.y);
        }
        else
        {
            cancelButton.SetEnabled(false);
            cancelButton.SetVisible(false);
        }
        //Fade in the cancel button
        cancelButton.setAlpha(0.0f);
        Tween.to(cancelButton, SpriteAccessor.OPACITY, timeToTweenFromListToChain)
                .target(1.0f)
                .start(chainTweenManager);

        cancelButton.SetEnabled(true);
        cancelButton.SetVisible(true);
    }

    private void FadeOutCancelButton()
    {
        //Fade out the cancel button
        Tween.to(cancelButton, SpriteAccessor.OPACITY, timeToTweenFromListToChain)
                .target(0.0f)
                .start(chainTweenManager);

        cancelButton.SetEnabled(false);
        cancelButton.SetVisible(false);
    }

    private void FadeOutNextButton()
    {
        //Fade out the cancel button
        Tween.to(nextButton, SpriteAccessor.OPACITY, timeToTweenFromListToChain)
                .target(0.0f)
                .start(chainTweenManager);

        nextButton.SetEnabled(false);
    }

    public boolean GetIsOnEndOfChain()
    {
        return isOnEndOfChain;
    }

    public void Move(float xMov, float yMov) //Move, DO NOT MOVE WHILE THE LIST IS DOING ANYTHING FFS
    {
        position.x += xMov;
        position.y += yMov;

        for(LogicBlock block : blocks)
        {
            block.Move(xMov, yMov);
        }

        blockChainBounds.setPosition(blockChainBounds.getX() + xMov, blockChainBounds.getY() + yMov);
        nextButton.translate(xMov, yMov);
        cancelButton.translate(xMov, yMov);

        if(selectionList != null) {
            selectionList.Move(xMov, yMov);
        }
    }

    public boolean IsListOpen()
    {
        if(selectionList == null)
        {
            return false;
        }

        if((selectionList.GetIsOpen()) || (selectionList.GetIsTweeningIn()) || (selectionList.GetIsTweeningOut()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void CloseList()
    {
        if(selectionList != null) {
            selectionList.CloseList();
            ListClosed();
        }
    }

    public ArrayList<LogicBlock> GetBlockList()
    {
        return blocks;
    }

    public BlockSelectionList GetSelectionList(){
        if(selectionList != null){
            return selectionList;
        }
        else{
            return null;
        }
    }
    public boolean GetEnabled(){return enabled;}
    public void SetEnabled(boolean enabled){this.enabled = enabled;}
    public boolean GetVisible(){return visible;}
    public void SetVisible(boolean visible){this.visible = visible;}
    public void SetAboveBlockChain(BlockChain chain)
    {
        aboveBlockChain = chain;
    }
    public BlockChain GetAboveBlockChain()
    {
        return aboveBlockChain;
    }
    public void SetBelowBlockChain(BlockChain chain)
    {
        belowBlockChain = chain;
    }
    public BlockChain GetBelowBlockChain()
    {
        return belowBlockChain;
    }
    public Rectangle GetBlockBounds()
    {
        return blockChainBounds;
    }
    public float GetX()
    {
        return blockChainBounds.getX();
    }
    public float GetY()
    {
            return blockChainBounds.getY() + (LogicBlock.blockHeight - nextButton.getHeight()) / 2;
    }
    public void SetPosition(float x, float y) {

        int blockWidth = 0; //The width of the entire chain
        for(int i = 0; i < blocks.size(); i++)
        {
            blocks.get(i).SetPosition(x + blockWidth, y  - (LogicBlock.blockHeight - nextButton.getHeight())/2);
            blockWidth += blocks.get(i).GetFullBlockWidth();
        }

        blockChainBounds.setPosition(x, y - (LogicBlock.blockHeight - nextButton.getHeight())/2);
        blockChainBounds.setWidth(blockWidth);

        position.x = blockChainBounds.getX() + blockChainBounds.getWidth() + spacingBetweenNextButton;
        position.y = y;

        //If the chain is empty, we dont want the added spacing, so it lines up right
        if(IsEmpty()) {
            nextButton.setPosition(blockChainBounds.getX() + blockChainBounds.getWidth(), blockChainBounds.getY() + (LogicBlock.GetBlockHeight() - nextButton.getRegionHeight()) / 2);
        }
        else {
            nextButton.setPosition(blockChainBounds.getX() + blockChainBounds.getWidth() + spacingBetweenNextButton, blockChainBounds.getY() + (LogicBlock.GetBlockHeight() - nextButton.getRegionHeight()) / 2);
        }

        cancelButton.setPosition(x + blockWidth, y);

        SetCancelButtonPosToLastBlock();

        if(ifIndentationGraphic != null) {
            ifIndentationGraphic.setPosition(GetX() + ifIndentationGraphicOffset.x, GetY() - ifIndentationGraphicOffset.y);
        }

        /*
        if(selectionList != null) {
            selectionList.SetPosition(x, y);
        }
        */
    }
    public float GetOpacity(){
        return currentOpacity;
    }
    public void SetOpacity(float opacity) {
        currentOpacity = opacity;
        for(LogicBlock block : blocks){
            block.SetOpacity(opacity);
        }
        if(ifIndentationGraphic != null){
            ifIndentationGraphic.setAlpha(opacity);
        }
        nextButton.setAlpha(opacity);
        cancelButton.setAlpha(opacity);
    }
    public boolean ShouldDelete()
    {
        return shouldBeDeleted;
    }
    public void SetIsTopChain(){
        isTopChain = true;
    }
    //Used to directly add/remove blocks, used mainly in loading
    public void DirectlyAddBlock(LogicGroups.LogicBlockType type)
    {
        if(blocks.size() >= 1)
        {
            previousBlock = blocks.get(blocks.size() - 1);
        }
        else
        {
            previousBlock = null;
        }

        LogicBlock newBlock = LogicGroups.ConstructSpecificBlock(type, blockTextureSheet, previousBlock);
        newBlock.SetPosition(blockChainBounds.getX() + blockChainBounds.getWidth(), position.y - (LogicBlock.blockHeight - nextButton.getHeight()) /2 );
        blockChainBounds.width = blockChainBounds.getWidth() + newBlock.GetFullBlockWidth();
        blocks.add(newBlock);

        if(newBlock.GetNextLogicGroup(previousBlock) == null)
        {
            isOnEndOfChain = true;
            nextButton.SetEnabled(false);
            nextButton.SetVisible(false);
        }

        nextButton.setX(blockChainBounds.getX() + blockChainBounds.getWidth() + spacingBetweenNextButton);

        FadeInCancelButtonAtLastBlock();
    }

    public boolean GetIsIfStatement() {
        if(blocks.size() > 0){
            if(blocks.get(0).GetBlockType() == LogicGroups.LogicBlockType.WHEN){
                return true;
            }
        }
        return false;
    }

    public static void SetUpperLowerRelations(BlockChain upper, BlockChain lower)
    {
        upper.SetBelowBlockChain(lower);
        lower.SetAboveBlockChain(upper);
    }

    public BlockChain AddIfChildBlock()
    {
        childChains.add(new BlockChain(GetX() + FullBlockScript.IfIndentation, GetY() , blockTextureSheet, fullScript, childChains));
        childChains.get(childChains.size() - 1).SetIsTopChain();
        childChains.get(childChains.size() - 1).LoadIterator(childChains.size() - 1);
        return childChains.get(childChains.size() - 1);
    }

    //Gets a list of all the child chains
    public ArrayList<BlockChain> GetAllChildChainsRecursively()
    {
        ArrayList<BlockChain> chainsToReturn = new ArrayList<BlockChain>();

        if(childChains!= null) {
            for (BlockChain childChain : childChains) {
                chainsToReturn.add(childChain);
                chainsToReturn.addAll(childChain.GetAllChildChainsRecursively());
            }
        }

        return chainsToReturn;
    }

    //Destroy chains that need to be destroyed
    private void DestroyChildChains()
    {
        if(childChains != null) {
            for (int i = 0; i < childChains.size(); i++) {
                if (childChains.get(i).ShouldDelete()) {
                    childChains.remove(i);
                }
            }
        }
    }

    public BlockChain GetNextBlockAfterIf()
    {
        if(GetIsIfStatement() == false){
            return null;
        }
        return nextBlockAfterIf;
    }
    public void SetNextBlockAfterIf(BlockChain nextBlock)
    {
        nextBlockAfterIf = nextBlock;
    }

    public void LoadIterator(int index){
        it = parentContainer.listIterator(index);
    }
    public BlockChain GetNextBlockInContainerFromIterator(){
        if(it == null){
            Gdx.app.log("Error", "Iterator is null, probably need to call LoadIterator");
        }

        if(it.hasNext()) {
            return it.next();
        }
        else{
            return null;
        }
    }


    public BlockChain GetFirstInParentContainer()
    {
        if(parentContainer != null) {
            if(parentContainer.size() > 0) {
                return parentContainer.get(0);
            }
            else{
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public void DoChainDestruction(){
        if(IsEmpty()) {
            previousBlock = null;
            if (ShouldBeDestroyedWhenEmpty()) {
                DeleteChain();
                fullScript.needToRunDestoryChainsCheck = true;
            } else {
                ClearChildChains();
                DestroyChildChains();
                fullScript.needToRunDestoryChainsCheck = true;
            }
        }
    }

    private boolean ShouldBeDestroyedWhenEmpty()
    {
        //Don't delete if this chain is the first in its indentation level
        if(GetFirstInParentContainer() != null) {
            if (this.lineNo == GetFirstInParentContainer().lineNo) {
                return false;
            } else {
                if(GetAboveBlockChain() != null){
                    if(CheckIfChainsAreInSameIndentationLevel(this, GetAboveBlockChain()) == false){
                        return false;
                    }
                }
                return true;
            }
        }
        else{
            return true;
        }

    }
    public boolean IsEmpty()
    {
        if(blocks.size() == 0){
            return  true;
        }
        else{
            return false;
        }
    }

    public void LoadIfIndentationGraphic(float graphicOffsetX, float graphicOffsetY){
        ifIndentationGraphic = new Sprite(blockTextureSheet, 300, 495, 50, 54);
        ifIndentationGraphic.setPosition(GetX() + graphicOffsetX , GetY() - graphicOffsetY);
        ifIndentationGraphicOffset.x = graphicOffsetX;
        ifIndentationGraphicOffset.y = graphicOffsetY;
    }

    //Checks if the chains are in the same container
    public static boolean CheckIfChainsAreInSameIndentationLevel(BlockChain chain1, BlockChain chain2){
        ArrayList<BlockChain> parentContainer = chain1.parentContainer;

        for(BlockChain chain : parentContainer){
            if(chain.lineNo == chain2.lineNo){
                return true;
            }
        }

        return false;
    }
}
