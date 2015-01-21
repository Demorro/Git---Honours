package com.mygdx.game.LogicBlocks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.UI.BlockSelectionList;
import com.mygdx.game.UI.CancelBlockButton;
import com.mygdx.game.UI.NextBlockButton;
import com.mygdx.game.Utility.SpriteAccessor;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Elliot Morris on 14/01/2015.
 * Chunk of blocks, one "Line" of logic block code
 */
public class BlockChain {

    private FullBlockScript fullScript; //Reference to the full script.
    private CancelBlockButton cancelButton; //The button that cancels a block, not the button that cancels a list, THEYRE DIFFERENT THINGS DAMMIT
    private NextBlockButton nextButton; //The button that either summons a new list or alternatively lets you select the block in the list

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


    private boolean isOnEndOfChain = false; //True if the chain is finished, with no other blocks to be placed.

    public BlockChain(float xPos, float yPos, Texture blockSpriteSheet, FullBlockScript fullScript)
    {
        this.fullScript = fullScript;
        blockTextureSheet = blockSpriteSheet;

        ResetChain(xPos, yPos);
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

        blockChainBounds = new Rectangle();
        blockChainBounds.setPosition(position.x, position.y);

        startingGroups.clear();
        startingGroups.add(LogicGroups.LogicGroup.COMMAND);
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
                    // \/ removes both Timer1 and Timer2
                    position.x += nextBlock.GetFullBlockWidth();
                    selectionList.ResetList(nextGroups, new Vector2(position.x + spacingBetweenBlocks, position.y), false, false);
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
            position.x += nextBlock.GetFullBlockWidth();
            FadeInCancelButtonAtLastBlock();
            nextButton.SetEnabled(false);
            nextButton.SetVisible(false);
            isOnEndOfChain = true;
        }
        previousBlock = nextBlock;
    }

    public void ListClosed()
    {
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
        Tween.to((cancelButton), SpriteAccessor.OPACITY, timeToTweenFromListToChain)
                .target(0.0f)
                .start(chainTweenManager);

        //Tween the next button back to the previous block
        Tween.to((nextButton), SpriteAccessor.POSITION_X, timeToTweenFromListToChain)
                .target(blockChainBounds.getX() + blockChainBounds.getWidth() + spacingBetweenNextButton)
                .start(chainTweenManager);

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
                    previousBlock = null;
                }
                if(blocks.size() > 1)
                {
                    FadeInCancelButtonAtLastBlock();
                    previousBlock = blocks.get(blocks.size() - 2);
                    selectionList.ResetList(blocks.get(blocks.size() - 1).GetNextLogicGroup(previousBlock), new Vector2(position.x  + spacingBetweenBlocks, position.y), false, false);
                }
                else if(blocks.size() > 0){
                    FadeInCancelButtonAtLastBlock();
                    selectionList.ResetList(blocks.get(blocks.size() - 1).GetNextLogicGroup(previousBlock), new Vector2(position.x  + spacingBetweenBlocks, position.y), false, false);
                }
                else
                {
                    FadeOutCancelButton();
                    selectionList.ResetList(startingGroups, new Vector2(position.x + spacingBetweenBlocks, position.y), false, false);
                }

                //Re-enable the next button after tween
                nextButton.SetEnabled(true);
                cancelButton.SetEnabled(true);

            }
        }, timeToTweenFromListToChain);

        isOnEndOfChain = false; //Its important that this is set at the end of this function
    }
    //Called by nextButton when it is triggered
    public void NextButtonPushed()
    {
        if(selectionList == null)
        {
            fullScript.AnyListOpened(); //This needs to happen before you open the list, trust me my son, (For reals, its because Anylistopens closes all the list, and we want to open dis one)
            selectionList = new BlockSelectionList(startingGroups, blockTextureSheet, new Vector2(position.x + nextButton.getWidth() + spacingBetweenNextButton , position.y), false, this);
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
                fullScript.AnyListOpened();  //This needs to happen before you open the list, trust me my son, (For reals, its because Anylistopens closes all the list, and we want to open dis one)
                selectionList.SetPosition(position.x + nextButton.getWidth() + spacingBetweenNextButton, position.y);
                selectionList.OpenList();
            }
        }
    }
    //Called by cancel button when it is triggered
    public void CancelButtonPushed()
    {
        if(blocks.size() > 0) {
            RemoveBlockFromEndOfChain();
        }
    }

    public void Update()
    {
        if(nextButton.GetEnabled()) {
            nextButton.Update();
        }
        if(blocks.size() > 0) {
            cancelButton.Update();
        }

        if(selectionList != null)
        {
            selectionList.Update();
        }

        chainTweenManager.update(Gdx.graphics.getDeltaTime());
    }

    public void Render(SpriteBatch batch)
    {
        nextButton.Render(batch);

        for(LogicBlock block : blocks)
        {
            block.Render(batch);
        }

        if(selectionList != null)
        {
            selectionList.Render(batch);
        }

        cancelButton.Render(batch);
    }

    public NextBlockButton GetNextBlockButton()
    {
        return  nextButton;
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

    public boolean GetIsOnEndOfChain()
    {
        return isOnEndOfChain;
    }

    private void Move(float xMov, float yMov) //Move, DO NOT MOVE WHILE THE LIST IS DOING ANYTHING FFS
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

}
