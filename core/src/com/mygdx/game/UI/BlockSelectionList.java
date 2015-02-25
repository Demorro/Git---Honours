package com.mygdx.game.UI;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.LogicBlocks.BlockChain;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicBlockAccessor;
import com.mygdx.game.LogicBlocks.LogicGroups;
import com.mygdx.game.Utility.Utility;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 * Vertically scrollable list used for selecting the next logic block in a BlockChain
 */
public class BlockSelectionList {

    private ArrayList<LogicBlock> selectionBlocks = new ArrayList<LogicBlock>(); //Main storage container for the blocks that are part of this list.
    private int currentSelectionIndex = 0; //Index to the selectionBlocks array that is the current selection.

    private Texture blockSpriteSheet; //Reference to the block sprite sheet passed in the constructor

    private Vector2 position; //List position. Changing this won't actually do anything, only used as a aid for placing the LogicBlocks

    private final static float YSeperationDistance = 68; //Distance of seperation between each logic block in the list
    private final static float adjacentListBlockOpacity = 1.0f; //Opacity of the blocks above/below the main central one
    private final static float adjacentListBlockScale = 0.85f; //Scale of the blocks above/below the main central one
    private float listScrollTime = 0.18f; //Time it takes to do one scroll
    public final static float tweenInOutTime = 0.2f; //Time the tweens in the list take
    private final static float tweenInOutDistance = 30.0f; //Distance to the right the list tweens in/out from

    TweenManager listTweenManager = new TweenManager();

    private boolean isScrolling = false;
    private boolean isTweeningIn = false;
    private boolean isTweeningOut = false;
    private boolean isOpen = false;

    private CancelBlockSelectionListButton cancelButton; //Button for closing the list
    private BlockChain blockChain; //Reference to the blockChain, passed in so the blockchain knows when a block is selected

    private BitmapFont blockFont;

    public BlockSelectionList(ArrayList<LogicGroups.LogicGroup> groupsToLoad, Texture blockSpriteSheets, Vector2 pos, boolean startOpen, BlockChain blockChain, LogicBlock previousBlock)
    {
        blockSpriteSheet = blockSpriteSheets;
        this.blockChain = blockChain;
        cancelButton = new CancelBlockSelectionListButton(blockSpriteSheets, this);
        ResetList(groupsToLoad,pos,startOpen,false, previousBlock);
    }

    public void Update()
    {
        cancelButton.Update();
        ClickToScroll();
        HandleBlockSelection();
        listTweenManager.update(Gdx.graphics.getDeltaTime());
    }

    public void Render (SpriteBatch batch)
    {

        if((isOpen) || (isTweeningIn) || (isTweeningOut)) {
            int upperIndex = GetUpperIndex();
            int lowerIndex = GetLowerIndex();
            int doubleLowerIndex = GetDoubleLowerIndex();
            int doubleUpperIndex = GetDoubleUpperIndex();

            if(selectionBlocks.size() > lowerIndex){selectionBlocks.get(lowerIndex).Render(batch);}
            if(selectionBlocks.size() > currentSelectionIndex){selectionBlocks.get(currentSelectionIndex).Render(batch);}
            if(selectionBlocks.size() > upperIndex){selectionBlocks.get(upperIndex).Render(batch);}

            if (isScrolling) {
                selectionBlocks.get(doubleLowerIndex).Render(batch);
                selectionBlocks.get(doubleUpperIndex).Render(batch);
            }
            cancelButton.Render(batch);
        }
    }

    public void ResetList(ArrayList<LogicGroups.LogicGroup> groupsToLoad, Vector2 pos, boolean startOpen, boolean moveOverHalfLargestBlockWidth, LogicBlock previousBlock)
    {
        isOpen = false;
        isTweeningIn = false;
        isTweeningOut = false;
        isScrolling = false;
        selectionBlocks.clear();
        currentSelectionIndex = 0;

        //Reload the proper groups inthe list
        for(LogicGroups.LogicGroup group : groupsToLoad)
        {
            selectionBlocks.addAll(LogicGroups.ConstructLogicGroup(group, blockSpriteSheet, previousBlock));
        }

        //List needs to be at least 4 big
        while(selectionBlocks.size() < 4) {
            for (LogicGroups.LogicGroup group : groupsToLoad) {
                selectionBlocks.addAll(LogicGroups.ConstructLogicGroup(group, blockSpriteSheet, previousBlock));
            }
        }
        //Set the position
        position = pos;
        if(moveOverHalfLargestBlockWidth)
        {
            position.x = pos.x + GetLargestBlockWidth()/2;
        }

        //Set all blocks to the adjacent settings, and an opacity of 0.0f so they can fade in. The center one will fade in to full opacity by virtue of the tween
        for(LogicBlock block : selectionBlocks)
        {
            block.SetScaleXY(adjacentListBlockScale);
            block.SetOpacity(0.0f);
        }
        //Reassert the positions based on position
        AssertPositions();

        isOpen = startOpen;
    }

    private void HandleBlockSelection()
    {
        if(isOpen) {
            if ((isTweeningIn) || (isTweeningOut) || (isScrolling)) {
                return;
            }
            if ((Gdx.app.getInput().justTouched()) & (!cancelButton.getBoundingRectangle().contains(Utility.GetScreenSpaceInput()))) //Dont want to be able to cancel and select a block at the same time
            {
                if (selectionBlocks.get(currentSelectionIndex).GetBoundingRectangle().contains(Utility.GetScreenSpaceInput())) {
                    SelectBlock();
                }
            }
        }
    }

    public void SelectBlock()
    {
        blockChain.ListBlockSelected(selectionBlocks.get(currentSelectionIndex), tweenInOutTime);
        selectionBlocks.remove(currentSelectionIndex);
        CloseList();
    }

    private void ClickToScroll()
    {
        if(isOpen) {
            if ((isTweeningIn) || (isTweeningOut) || (isScrolling)) {
                return;
            }
            if(Gdx.app.getInput().isTouched())
            {
                if(selectionBlocks.get(GetUpperIndex()).GetBoundingRectangle().contains(Utility.GetScreenSpaceInput())) {
                    ScrollUp();
                    return;
                }
                else if(selectionBlocks.get(GetLowerIndex()).GetBoundingRectangle().contains(Utility.GetScreenSpaceInput())) {
                    ScrollDown();
                    return;
                }
            }
        }
    }

    private void ScrollUp()
    {
        if(isOpen) {
            //Scrolls the list up
            if ((isTweeningIn) || (isTweeningOut)) {
                return;
            }

            if (isScrolling == false) {
                //The doulbe lower block is the block that will scroll onto the list
                LogicBlock doubleLowerIndexBlock = selectionBlocks.get(GetDoubleLowerIndex());
                doubleLowerIndexBlock.SetPosition(selectionBlocks.get(GetLowerIndex()).GetX() + (selectionBlocks.get(GetLowerIndex()).GetFullBlockWidth() / 2) - selectionBlocks.get(GetDoubleLowerIndex()).GetFullBlockWidth() / 2, selectionBlocks.get(GetLowerIndex()).GetY() - YSeperationDistance);
                doubleLowerIndexBlock.SetOpacity(0.0f);
                doubleLowerIndexBlock.SetTextOpacity(1.0f);
                Tween.to(doubleLowerIndexBlock, LogicBlockAccessor.POSITION_Y, listScrollTime)
                        .target(selectionBlocks.get(GetLowerIndex()).GetY())
                        .start(listTweenManager);
                Tween.to(doubleLowerIndexBlock, LogicBlockAccessor.OPACITYNOTEXT, listScrollTime)
                        .target(adjacentListBlockOpacity)
                        .start(listTweenManager);

                //Scroll the bottom block to the center
                Tween.to(selectionBlocks.get(GetLowerIndex()), LogicBlockAccessor.POSITION_Y, listScrollTime)
                        .target(selectionBlocks.get(currentSelectionIndex).GetY())
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(GetLowerIndex()), LogicBlockAccessor.OPACITYNOTEXT, listScrollTime)
                        .target(1.0f)
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(GetLowerIndex()), LogicBlockAccessor.SCALEXY, listScrollTime)
                        .target(1.0f)
                        .start(listTweenManager);

                //Scroll the center block up
                Tween.to(selectionBlocks.get(currentSelectionIndex), LogicBlockAccessor.POSITION_Y, listScrollTime)
                        .target(selectionBlocks.get(GetUpperIndex()).GetY())
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(currentSelectionIndex), LogicBlockAccessor.OPACITYNOTEXT, listScrollTime)
                        .target(adjacentListBlockOpacity)
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(currentSelectionIndex), LogicBlockAccessor.SCALEXY, listScrollTime)
                        .target(adjacentListBlockScale)
                        .start(listTweenManager);

                //Scroll the top block off the top
                Tween.to(selectionBlocks.get(GetUpperIndex()), LogicBlockAccessor.POSITION_Y, listScrollTime)
                        .target(selectionBlocks.get(GetUpperIndex()).GetY() + YSeperationDistance)
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(GetUpperIndex()), LogicBlockAccessor.OPACITY, listScrollTime)
                        .target(0.0f)
                        .start(listTweenManager);

                isScrolling = true;

                //The timer triggers when the scroll animation is done, setting stuff right
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        // \/ removes both Timer1 and Timer2
                        currentSelectionIndex = GetLowerIndex();
                        isScrolling = false;
                    }
                }, listScrollTime + 0.025f);
            }
        }
    }

    private void ScrollDown()
    {
        if(isOpen) {
            if ((isTweeningIn) || (isTweeningOut)) {
                return;
            }
            //Scrolls the list down
            if (isScrolling == false) {
                //The double upper block is the block that will scroll onto the list
                LogicBlock doubleUpperIndexBlock = selectionBlocks.get(GetDoubleUpperIndex());
                doubleUpperIndexBlock.SetPosition(selectionBlocks.get(GetUpperIndex()).GetX() + (selectionBlocks.get(GetUpperIndex()).GetFullBlockWidth() / 2) - selectionBlocks.get(GetDoubleUpperIndex()).GetFullBlockWidth() / 2, selectionBlocks.get(GetUpperIndex()).GetY() + YSeperationDistance);
                doubleUpperIndexBlock.SetOpacity(0.0f);
                doubleUpperIndexBlock.SetTextOpacity(1.0f);
                Tween.to(doubleUpperIndexBlock, LogicBlockAccessor.POSITION_Y, listScrollTime)
                        .target(selectionBlocks.get(GetUpperIndex()).GetY())
                        .start(listTweenManager);
                Tween.to(doubleUpperIndexBlock, LogicBlockAccessor.OPACITYNOTEXT, listScrollTime)
                        .target(adjacentListBlockOpacity)
                        .start(listTweenManager);

                //Scroll the bottom block to the center
                Tween.to(selectionBlocks.get(GetUpperIndex()), LogicBlockAccessor.POSITION_Y, listScrollTime)
                        .target(selectionBlocks.get(currentSelectionIndex).GetY())
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(GetUpperIndex()), LogicBlockAccessor.OPACITYNOTEXT, listScrollTime)
                        .target(1.0f)
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(GetUpperIndex()), LogicBlockAccessor.SCALEXY, listScrollTime)
                        .target(1.0f)
                        .start(listTweenManager);

                //Scroll the center block up
                Tween.to(selectionBlocks.get(currentSelectionIndex), LogicBlockAccessor.POSITION_Y, listScrollTime)
                        .target(selectionBlocks.get(GetLowerIndex()).GetY())
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(currentSelectionIndex), LogicBlockAccessor.OPACITYNOTEXT, listScrollTime)
                        .target(adjacentListBlockOpacity)
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(currentSelectionIndex), LogicBlockAccessor.SCALEXY, listScrollTime)
                        .target(adjacentListBlockScale)
                        .start(listTweenManager);

                //Scroll the top block off the top
                Tween.to(selectionBlocks.get(GetLowerIndex()), LogicBlockAccessor.POSITION_Y, listScrollTime)
                        .target(selectionBlocks.get(GetLowerIndex()).GetY() - YSeperationDistance)
                        .start(listTweenManager);
                Tween.to(selectionBlocks.get(GetLowerIndex()), LogicBlockAccessor.OPACITY, listScrollTime)
                        .target(0.0f)
                        .start(listTweenManager);

                isScrolling = true;

                //The timer triggers when the scroll animation is done, setting stuff right
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        // \/ removes both Timer1 and Timer2
                        currentSelectionIndex = GetUpperIndex();
                        isScrolling = false;
                    }
                }, listScrollTime + 0.025f);

            }
        }
    }

    public void OpenList()
    {
        if((isOpen == false) && (isScrolling == false)) {
            if ((isTweeningIn) || (isTweeningOut)) {
                return;
            }
            LogicBlock upperBlock = null;
            LogicBlock centerBlock = null;
            LogicBlock lowerBlock = null;

            //Tweens in from left
            if(selectionBlocks.size() > GetUpperIndex()){ upperBlock = selectionBlocks.get(GetUpperIndex());}
            if(selectionBlocks.size() > currentSelectionIndex){centerBlock = selectionBlocks.get(currentSelectionIndex);}
            if(selectionBlocks.size() > GetLowerIndex()){lowerBlock = selectionBlocks.get(GetLowerIndex());}

            if(upperBlock != null){
                upperBlock.SetX(position.x + tweenInOutDistance);
                upperBlock.SetOpacity(0.0f);
                Tween.to(upperBlock, LogicBlockAccessor.POSITION_X, tweenInOutTime)
                        .target(upperBlock.GetX() - tweenInOutDistance)
                        .start(listTweenManager);
                Tween.to(upperBlock, LogicBlockAccessor.OPACITYANDTEXT, tweenInOutTime)
                        .target(adjacentListBlockOpacity, 1.0f)
                        .start(listTweenManager);
            }
            if(centerBlock != null){
                centerBlock.SetX(position.x + tweenInOutDistance);
                centerBlock.SetOpacity(0.0f);
                Tween.to(centerBlock, LogicBlockAccessor.POSITION_X, tweenInOutTime)
                        .target(centerBlock.GetX() - tweenInOutDistance)
                        .start(listTweenManager);
                Tween.to(centerBlock, LogicBlockAccessor.OPACITYANDTEXT, tweenInOutTime)
                        .target(1.0f, 1.0f)
                        .start(listTweenManager);
            }
            if(lowerBlock != null){
                lowerBlock.SetX(position.x + tweenInOutDistance);
                lowerBlock.SetOpacity(0.0f);
                Tween.to(lowerBlock, LogicBlockAccessor.POSITION_X, tweenInOutTime)
                        .target(lowerBlock.GetX() - tweenInOutDistance)
                        .start(listTweenManager);
                Tween.to(lowerBlock, LogicBlockAccessor.OPACITYANDTEXT, tweenInOutTime)
                        .target(adjacentListBlockOpacity, 1.0f)
                        .start(listTweenManager);
            }

            //Cancelbutton tween
            cancelButton.setPosition(position.x + selectionBlocks.get(currentSelectionIndex).GetFullBlockWidth() - (cancelButton.getWidth() / 2) + tweenInOutDistance + LogicBlock.cancelButtonOffset.x, selectionBlocks.get(currentSelectionIndex).GetY() + LogicBlock.GetBlockHeight() - (cancelButton.getHeight() / 2) + LogicBlock.cancelButtonOffset.y);
            cancelButton.setAlpha(0.0f);
            Tween.to(cancelButton, LogicBlockAccessor.POSITION_X, tweenInOutTime)
                    .target(selectionBlocks.get(currentSelectionIndex).GetX() + selectionBlocks.get(currentSelectionIndex).GetFullBlockWidth() - (cancelButton.getWidth() / 2) - tweenInOutDistance + LogicBlock.cancelButtonOffset.x)
                    .start(listTweenManager);
            Tween.to(cancelButton, LogicBlockAccessor.OPACITY, tweenInOutTime)
                    .target(1.0f)
                    .start(listTweenManager);

            isTweeningIn = true;

            //The timer triggers when the scroll animation is done, setting stuff right
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    isTweeningIn = false;
                    isOpen = true;
                }
            }, tweenInOutTime);
        }
    }

    public void CloseList()
    {
        if((isOpen == true) && (isScrolling == false)) {
            if ((isTweeningIn) || (isTweeningOut)) {
                return;
            }

            LogicBlock upperBlock = null;
            LogicBlock centerBlock = null;
            LogicBlock lowerBlock = null;

            //Tweens in from left
            if(selectionBlocks.size() > GetUpperIndex()){ upperBlock = selectionBlocks.get(GetUpperIndex());}
            if(selectionBlocks.size() > currentSelectionIndex){centerBlock = selectionBlocks.get(currentSelectionIndex);}
            if(selectionBlocks.size() > GetLowerIndex()){lowerBlock = selectionBlocks.get(GetLowerIndex());}

            if(upperBlock != null) {
                Tween.to(upperBlock, LogicBlockAccessor.POSITION_X, tweenInOutTime)
                        .target(upperBlock.GetX() + tweenInOutDistance)
                        .start(listTweenManager);
                Tween.to(upperBlock, LogicBlockAccessor.OPACITYANDTEXT, tweenInOutTime)
                        .target(0.0f, 0.0f)
                        .start(listTweenManager);
            }
            if(centerBlock != null) {
                Tween.to(centerBlock, LogicBlockAccessor.POSITION_X, tweenInOutTime)
                        .target(centerBlock.GetX() + tweenInOutDistance)
                        .start(listTweenManager);
                Tween.to(centerBlock, LogicBlockAccessor.OPACITYANDTEXT, tweenInOutTime)
                        .target(0.0f, 0.0f)
                        .start(listTweenManager);
            }
            if(lowerBlock != null) {
                Tween.to(lowerBlock, LogicBlockAccessor.POSITION_X, tweenInOutTime)
                        .target(lowerBlock.GetX() + tweenInOutDistance)
                        .start(listTweenManager);
                Tween.to(lowerBlock, LogicBlockAccessor.OPACITYANDTEXT, tweenInOutTime)
                        .target(0.0f, 0.0f)
                        .start(listTweenManager);
            }

            Tween.to(cancelButton, LogicBlockAccessor.POSITION_X, tweenInOutTime)
                    .target(cancelButton.getX() + tweenInOutDistance)
                    .start(listTweenManager);
            Tween.to(cancelButton, LogicBlockAccessor.OPACITY, tweenInOutTime)
                    .target(0.0f)
                    .start(listTweenManager);


            isTweeningOut = true;
            isOpen = false;

            //The timer triggers when the scroll animation is done, setting stuff right
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    isTweeningOut = false;
                }
            }, tweenInOutTime);
        }
    }

    private void AssertPositions()
    {

        float blockCenterY = position.y - (LogicBlock.GetBlockHeight() - blockChain.GetNextBlockButton().getHeight())/2;

        if((selectionBlocks.size() > 0) && (currentSelectionIndex < selectionBlocks.size())) {
            selectionBlocks.get(currentSelectionIndex).SetPosition(position.x, blockCenterY);
            selectionBlocks.get(currentSelectionIndex).SetOpacity(1.0f);
            selectionBlocks.get(currentSelectionIndex).SetScaleXY(1.0f);
            cancelButton.setPosition(selectionBlocks.get(currentSelectionIndex).GetX() + selectionBlocks.get(currentSelectionIndex).GetFullBlockWidth() - cancelButton.getWidth()/2, selectionBlocks.get(currentSelectionIndex).GetY() + LogicBlock.GetBlockHeight() - cancelButton.getHeight()/2);

        }
        int upperIndex = GetUpperIndex();
        if((selectionBlocks.size() > 1) && (upperIndex < selectionBlocks.size()) && (currentSelectionIndex < selectionBlocks.size())) {
            //The big complicated Xvalue is just to center the block on the center of the central selection block
            selectionBlocks.get(upperIndex).SetPosition(selectionBlocks.get(currentSelectionIndex).GetX() + (selectionBlocks.get(currentSelectionIndex).GetFullBlockWidth()/2) - selectionBlocks.get(upperIndex).GetFullBlockWidth()/2, blockCenterY + YSeperationDistance);
            selectionBlocks.get(upperIndex).SetOpacityWithoutChangingText(adjacentListBlockOpacity);
            selectionBlocks.get(upperIndex).SetTextOpacity(1.0f);
            selectionBlocks.get(upperIndex).SetScaleXY(adjacentListBlockScale);
        }
        int lowerIndex = GetLowerIndex();
        if((selectionBlocks.size() > 2) && (lowerIndex < selectionBlocks.size()) && (currentSelectionIndex < selectionBlocks.size()))
        {
            //The big complicated Xvalue is just to center the block on the center of the central selection block
            selectionBlocks.get(lowerIndex).SetPosition(selectionBlocks.get(currentSelectionIndex).GetX() + (selectionBlocks.get(currentSelectionIndex).GetFullBlockWidth()/2) - selectionBlocks.get(lowerIndex).GetFullBlockWidth()/2, blockCenterY - YSeperationDistance);
            selectionBlocks.get(lowerIndex).SetOpacityWithoutChangingText(adjacentListBlockOpacity);
            selectionBlocks.get(lowerIndex).SetTextOpacity(1.0f);
            selectionBlocks.get(lowerIndex).SetScaleXY(adjacentListBlockScale);
        }
    }

    public void CancelButtonPushed()
    {
        if(GetIsOpen())
        {
            CloseList();
            blockChain.ListClosed();
        }
    }

    //2 spaces above the center block in the list
    private int GetDoubleUpperIndex()
    {
        int douleUpperIndex = currentSelectionIndex;
        if(currentSelectionIndex == 1)
        {
            douleUpperIndex = selectionBlocks.size() - 1;
        }
        else if(currentSelectionIndex == 0)
        {
            douleUpperIndex = selectionBlocks.size() - 2;
        }
        else
        {
            douleUpperIndex = currentSelectionIndex - 2;
        }
        return douleUpperIndex;
    }
    // 1 spaces above the center block in the list
    private int GetUpperIndex()
    {
        int upperIndex = currentSelectionIndex;
        if(currentSelectionIndex == 0)
        {
            upperIndex = selectionBlocks.size() - 1;
        }
        else
        {
            upperIndex = currentSelectionIndex - 1;
        }
        return upperIndex;
    }
    //1 spaces below the center block in the list
    private int GetLowerIndex()
    {
        int lowerIndex = currentSelectionIndex;
        if(currentSelectionIndex == selectionBlocks.size() - 1)
        {
            lowerIndex = 0;
        }
        else
        {
            lowerIndex = currentSelectionIndex + 1;
        }
        return lowerIndex;
    }
    //2 spaces below the center block in the list
    private int GetDoubleLowerIndex()
    {
        int doubleLowerIndex = currentSelectionIndex;
        if(currentSelectionIndex == selectionBlocks.size() - 2)
        {
            doubleLowerIndex = 0;
        }
        else if(currentSelectionIndex == selectionBlocks.size() - 1)
        {
            doubleLowerIndex = 1;
        }
        else
        {
            doubleLowerIndex = currentSelectionIndex + 2;
        }
        return doubleLowerIndex;
    }

    public boolean GetIsOpen()
    {
        return isOpen;
    }

    public boolean GetIsTweeningIn()
    {
       return isTweeningIn;
    }
    public boolean GetIsTweeningOut()
    {
        return isTweeningOut;
    }


    public static float GetTweenInOutDistance()
    {
        return tweenInOutDistance;
    }
    public float GetLargestBlockWidth()
    {
        float largestWidth = 0;
        for(LogicBlock block : selectionBlocks)
        {
            if(block.GetFullBlockWidth() > largestWidth)
            {
                largestWidth = block.GetFullBlockWidth();
            }
        }
        return largestWidth;
    }

    public void SetPosition(float xPos, float yPos)
    {
        position.x = xPos;
        position.y = yPos;

        AssertPositions();
    }

    public Vector2 GetPosition()
    {
        return  position;
    }

    public void Move(float xMov, float yMov)
    {
        position.x += xMov;
        position.y += yMov;

        for(LogicBlock block : selectionBlocks)
        {
            block.Move(xMov, yMov);
        }

        cancelButton.translate(xMov, yMov);
    }

    public void CleanUp(){
        for(LogicBlock block : selectionBlocks)
        {
            block.CleanUp();
        }
        selectionBlocks.clear();
        selectionBlocks = null;

        listTweenManager = null;

        cancelButton.CleanUp();
        cancelButton = null;
    }


}
