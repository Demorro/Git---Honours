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

/**
 * Created by Elliot Morris on 14/01/2015.
 * Chunk of blocks, one "Line" of logic block code
 */
public class BlockChain {

    private Vector2 position = new Vector2();
    private ArrayList<LogicBlock> blocks = new ArrayList<LogicBlock>();
    private Rectangle blockChainBounds = new Rectangle(0,0,0,LogicBlock.blockHeight);

    private CancelBlockButton cancelButton;
    private boolean cancelButtonActive = false;
    private NextBlockButton nextButton;
    private boolean drawNextButton = true;

    private BlockSelectionList selectionList;
    private final ArrayList<LogicGroups.LogicGroup> startingGroups; //The groups that are first offered when the user starts a new chain.
    private ArrayList<LogicGroups.LogicGroup> nextGroups; //The next groups that should be opened

    private LogicBlock previousBlock = null; //Store the previous block for correct logic routing

    private int spacingBetweenBlocks = 60;
    private int spacingBetweenNextButton = 10;

    private Texture blockTextureSheet;
    TweenManager chainTweenManager = new TweenManager();
    private static float timeToTweenFromListToChain = 0.2f;

    public BlockChain(float xPos, float yPos, Texture blockSpriteSheet)
    {
        position.x = xPos;
        position.y = yPos;

        blockTextureSheet = blockSpriteSheet;
        cancelButton = new CancelBlockButton(blockTextureSheet, this);
        nextButton = new NextBlockButton(blockTextureSheet, this);

        cancelButton.setPosition(position.x, position.y);
        nextButton.setPosition(position.x, position.y);

        blockChainBounds.setPosition(position.x, position.y);

        startingGroups = new ArrayList<LogicGroups.LogicGroup>();
        startingGroups.add(LogicGroups.LogicGroup.COMMAND);
        nextGroups = new ArrayList<LogicGroups.LogicGroup>(startingGroups);
    }

    //Called by the blockList when the next block is selected
    public void ListBlockSelected(final LogicBlock nextBlock, float timeToWaitTillDestroy)
    {

        nextGroups.clear();
        if(nextBlock.GetNextLogicGroup(previousBlock) != null) {
            nextGroups.addAll(nextBlock.GetNextLogicGroup(previousBlock));
            previousBlock = nextBlock;

            //The timer triggers when the scroll animation is done, setting stuff right
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // \/ removes both Timer1 and Timer2
                    position.x += nextBlock.GetFullBlockWidth();
                    selectionList.ResetList(nextGroups, new Vector2(position.x + spacingBetweenBlocks, position.y), false, false);
                    selectionList.TweenIn();
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
        cancelButtonActive = false;

        //If there is no logic group to go too, the chain is ended
        if(nextBlock.GetNextLogicGroup(previousBlock) == null)
        {
            position.x += nextBlock.GetFullBlockWidth();
            FadeInCancelButtonAtLastBlock();
            nextButton.SetEnabled(false);

        }


    }

    public void ListClosed()
    {
        FadeInCancelButtonAtLastBlock();

    }

    private void RemoveBlockFromEndOfChain()
    {
        blockChainBounds.setWidth(blockChainBounds.getWidth() - blocks.get(blocks.size() - 1).GetFullBlockWidth());
        position.x -= blocks.get(blocks.size() - 1).GetFullBlockWidth();

        //Fade the block otu
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



        //The timer triggers when the scroll animation is done, setting stuff right
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                blocks.remove(blocks.size() - 1);
                FadeInCancelButtonAtLastBlock();
                //Reset the selection list

                if(blocks.size() > 1)
                {
                    previousBlock = blocks.get(blocks.size() - 2);
                    selectionList.ResetList(blocks.get(blocks.size() - 1).GetNextLogicGroup(previousBlock), new Vector2(position.x  + spacingBetweenBlocks, position.y), false, false);
                }
                else if(blocks.size() > 0){
                    selectionList.ResetList(blocks.get(blocks.size() - 1).GetNextLogicGroup(previousBlock), new Vector2(position.x  + spacingBetweenBlocks, position.y), false, false);
                }
                else
                {
                    selectionList.ResetList(startingGroups, new Vector2(position.x  + spacingBetweenBlocks, position.y), false, false);
                }

            }
        }, timeToTweenFromListToChain);

    }
    //Called by nextButton when it is triggered
    public void NextButtonPushed()
    {
        if(selectionList == null)
        {
            selectionList = new BlockSelectionList(startingGroups, blockTextureSheet, new Vector2(position.x + nextButton.getWidth() + spacingBetweenNextButton , position.y), false, this);
            selectionList.TweenIn();
        }
        else
        {
            //So that the next button selects a block as well
            if(selectionList.GetIsOpen())
            {
                selectionList.SelectBlock();
            }
            else {
                selectionList.TweenIn();
            }
        }
    }
    //Called by cancel button when it is triggered
    public void CancelButtonPushed()
    {
        if(blocks.size() > 0) {
            RemoveBlockFromEndOfChain();
        }
        nextButton.SetEnabled(true);
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

        if(nextButton.GetEnabled()) {
            nextButton.draw(batch);
        }

        for(LogicBlock block : blocks)
        {
            block.Render(batch);
        }

        if(selectionList != null)
        {
            selectionList.Render(batch);
        }

        //Display the cancel button
        if(blocks.size() > 0) {
            if(cancelButtonActive) {
                if (selectionList == null) {
                    cancelButton.draw(batch);
                } else {
                    if ((!selectionList.GetIsOpen()) && (!selectionList.GetIsTweeningIn())) {
                        cancelButton.draw(batch);
                    }

                }
            }
        }
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
            cancelButton.setPosition(blockChainBounds.getX() + blockChainBounds.getWidth() - cancelButton.getWidth()/2, blocks.get(blocks.size() - 1).GetY() + LogicBlock.GetBlockHeight() - cancelButton.getHeight()/2);
        }
        //Fade in the cancel button
        cancelButton.setAlpha(0.0f);
        Tween.to(cancelButton, SpriteAccessor.OPACITY, timeToTweenFromListToChain)
                .target(1.0f)
                .start(chainTweenManager);

        cancelButtonActive = true;
    }
}
