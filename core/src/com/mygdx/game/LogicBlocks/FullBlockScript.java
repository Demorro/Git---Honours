package com.mygdx.game.LogicBlocks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utility.ScriptSaver;
import com.mygdx.game.Utility.SpriteAccessor;
import jdk.nashorn.internal.ir.Block;

import javax.swing.*;
import javax.xml.soap.Text;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Elliot Morris on 20/01/2015.
 */
public class FullBlockScript
{
    private ArrayList<BlockChain> blockChains = new ArrayList<BlockChain>();
    private Texture blockTextureSheet = null; //Reference to the block texture sheet

    private Vector2 blockChainStartPos = new Vector2(25, Gdx.graphics.getHeight() - 145);
    private int chainYSeperation = 68; //The amount of seperation active

    private static final float adjacentChainOpacity = 0.5f;
    private static final float adjacentChainOpacityTweenTime = 0.2f;

    private BlockChain activeChain = null; //The active chain is the one with the currently open selectionList.

    private TweenManager fullScriptTweenManager = new TweenManager();

    private boolean isSaving = false;

    public FullBlockScript(Texture blockSheet)
    {
        blockTextureSheet = blockSheet;
        ResetScript();
    }

    private void ResetScript()
    {
        blockChains.clear();
        AddNewChain(blockChainStartPos.x, blockChainStartPos.y);
    }

    public void Update()
    {
        for(BlockChain chain : blockChains)
        {
            chain.Update();
        }
        CheckForWhetherWeNeedNewChain();

        fullScriptTweenManager.update(Gdx.graphics.getDeltaTime());

        if(Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S))
        {
            if(isSaving == false) {
                SaveScript();
            }
        }
    }
    public void Render(SpriteBatch batch)
    {

        for(BlockChain chain : blockChains)
        {
            if(chain != activeChain) {
                chain.Render(batch);
            }
        }
        if(activeChain != null) {
            activeChain.Render(batch);
        }
    }

    //Checks if we need a new chain, that is if the final open chain is finished
    public void CheckForWhetherWeNeedNewChain()
    {
        if(blockChains.get(blockChains.size() - 1).GetIsOnEndOfChain() == true)
        {
            AddNewChain(blockChainStartPos.x, blockChainStartPos.y - (blockChains.size() * chainYSeperation));
        }
    }

    private void AddNewChain(float x, float y)
    {
        BlockChain chainToAdd = new BlockChain(x, y, blockTextureSheet, this);
        blockChains.add(chainToAdd);
        if(blockChains.size() >= 2){
            chainToAdd.SetAboveBlockChain(blockChains.get(blockChains.size() - 2));
            blockChains.get(blockChains.size() - 2).SetBelowBlockChain(chainToAdd);
        }

    }


    //Called by the blockchains when one of their lists opens, so that only one list may be open at a time.
    public void AnyListOpened(BlockChain chainThatOpened)
    {
        //Set the active chain to the one thats just opened
        activeChain = chainThatOpened;
        //tween the opacity of the adjacent chains down so that the list is more pronouned
        //TweenAdjacentChainsOpacity(activeChain, adjacentChainOpacity);
        TweenAllOtherChainsOpacity(activeChain, adjacentChainOpacity);

        //Since this is called just before the list opens, then we can just close all the lists and the one that calls it will still open
        for(BlockChain blockChain : blockChains)
        {
            if(blockChain.IsListOpen()) {
                blockChain.CloseList();
            }
        }

        DisableAllOtheBlockChains(activeChain);
    }
    public void AnyListClosed(BlockChain chainThatClosed)
    {
        //tween the opacity of the adjacent chains down so that the list is more pronouned
        //TweenAdjacentChainsOpacity(activeChain, 1.0f);
        TweenAllOtherChainsOpacity(chainThatClosed, 1.0f);
        EnableAllBlockChains();
    }

    private void DisableAllOtheBlockChains(BlockChain exclusionChain)
    {
        for(BlockChain chain : blockChains)
        {
            if(chain != exclusionChain) {
                chain.SetEnabled(false);
            }
        }
    }
    private void EnableAllBlockChains()
    {
        for(BlockChain chain : blockChains)
        {
            chain.SetEnabled(true);
        }
    }

    private void TweenAdjacentChainsOpacity(BlockChain centralChain, float opacity)
    {
        if(centralChain.GetAboveBlockChain() != null)
        {
            Tween.to(centralChain.GetAboveBlockChain(), BlockChainAccessor.OPACITY, adjacentChainOpacityTweenTime)
            .target(opacity)
            .start(fullScriptTweenManager);
        }
        if(centralChain.GetBelowBlockChain() != null)
        {
            Tween.to(centralChain.GetBelowBlockChain(), BlockChainAccessor.OPACITY, adjacentChainOpacityTweenTime)
                    .target(opacity)
                    .start(fullScriptTweenManager);
        }
    }
    private void TweenAllOtherChainsOpacity(BlockChain exclusionChain, float opacity)
    {
        for(BlockChain chain : blockChains)
        {
            if(chain != exclusionChain) {
                Tween.to(chain, BlockChainAccessor.OPACITY, adjacentChainOpacityTweenTime)
                        .target(opacity)
                        .start(fullScriptTweenManager);
            }
        }
    }


    //Tweens the chain to the above
    private void TweenChainToAboveChain(BlockChain chain)
    {
        chain.SetPosition(chain.GetX(), chain.GetAboveBlockChain().GetBlockBounds().getY() - LogicBlock.GetBlockHeight());
    }

    private void MoveChainsAwayFromActiveChain(BlockChain activeChain)
    {
        //Basically, this moves all of the chains above and below the active chain up/down the chainYSeperation distance so the active chain has space for its list
        //Upper
        BlockChain upperChain = activeChain.GetAboveBlockChain();
        while(upperChain != null)
        {
            upperChain.Move(0, chainYSeperation);
            upperChain = upperChain.GetAboveBlockChain();
        }

        //Lower
        BlockChain lowerChain = activeChain.GetBelowBlockChain();
        while(lowerChain != null)
        {
            lowerChain.Move(0, -chainYSeperation);
            lowerChain = lowerChain.GetBelowBlockChain();
        }
    }

    public ArrayList<BlockChain> GetBlockChains()
    {
        return blockChains;
    }

    public void SaveScript()
    {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        //In response to a button click:
        int returnVal = fc.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            ScriptSaver.SaveScript(this, file.getAbsolutePath());
            isSaving = true;
        }
        if((returnVal == JFileChooser.CANCEL_OPTION) || (returnVal == JFileChooser.ERROR_OPTION))
        {
            isSaving = true;
        }


    }



}
