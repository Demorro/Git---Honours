package com.mygdx.game.LogicBlocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import javax.xml.soap.Text;
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
    private int chainYSeperation = 145; //The amount of seperation active

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
    }
    public void Render(SpriteBatch batch)
    {
        for(BlockChain chain : blockChains)
        {
            chain.Render(batch);
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

            if(chainToAdd.GetAboveBlockChain() != null) {
                if (chainToAdd.GetAboveBlockChain().GetIsOnEndOfChain() == true) {
                    if (chainToAdd.GetAboveBlockChain().GetAboveBlockChain() != null) {
                        TweenChainToAboveChain(chainToAdd.GetAboveBlockChain());
                        //Move the new chain so it is the same distance away from the newly tweened chain
                        chainToAdd.SetPosition(chainToAdd.GetX(), chainToAdd.GetAboveBlockChain().GetY() - chainYSeperation);
                    }
                }
            }
        }

    }

    //Tweens the chain to the above
    private void TweenChainToAboveChain(BlockChain chain)
    {
        chain.SetPosition(chain.GetX(), chain.GetAboveBlockChain().GetBlockBounds().getY() - LogicBlock.GetBlockHeight());
    }

    //Called by the blockchains when one of their lists opens, so that only one list may be open at a time.
    public void AnyListOpened()
    {
        //Since this is called just before the list opens, then we can just close all the lists and the one that calls it will still open
        for(BlockChain blockChain : blockChains)
        {
            if(blockChain.IsListOpen()) {
                blockChain.CloseList();
            }
        }
    }



}
