package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.LogicBlocks.BlockChain;
import com.sun.org.apache.xpath.internal.operations.Bool;
import jdk.nashorn.internal.ir.Block;

/**
 * Created by Elliot Morris on 14/01/2015.
 */
public class NextBlockButton extends Button{
    public final static Vector2 nextBlockButtonSheetPos = new Vector2(12,495);
    public final static Vector2 nextBlockButtonSheetDimensions = new Vector2(42,48);

    private BlockChain blockChain; //Reference to the blockchain so it can know when this has been triggered


    public NextBlockButton(Texture blockSpriteSheet, BlockChain blockChain)
    {
        super(blockSpriteSheet, (int)nextBlockButtonSheetPos.x, (int)nextBlockButtonSheetPos.y, (int)nextBlockButtonSheetDimensions.x, (int)nextBlockButtonSheetDimensions.y, false);
        this.blockChain = blockChain;
    }

    @Override
    protected void Trigger()
    {
        blockChain.NextButtonPushed();
    }


}
