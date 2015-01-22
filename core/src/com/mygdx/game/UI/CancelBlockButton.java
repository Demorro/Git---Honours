package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.LogicBlocks.BlockChain;

/**
 * Created by Elliot Morris on 14/01/2015.
 */
public class CancelBlockButton extends Button
{
    public final static Vector2 cancelButtonSheetPos = new Vector2(64,488);
    public final static Vector2 cancelButtonSheetDimensions = new Vector2(26,26);

    private BlockChain blockChain; //Reference to the blockchain so it can know when this has been triggered

    public CancelBlockButton(Texture blockSpriteSheet, BlockChain blockChain)
    {
        super(blockSpriteSheet, (int)cancelButtonSheetPos.x, (int)cancelButtonSheetPos.y, (int)cancelButtonSheetDimensions.x, (int)cancelButtonSheetDimensions.y);
        this.blockChain = blockChain;
    }

    @Override
    protected void Trigger()
    {
        blockChain.CancelButtonPushed();
    }
}
