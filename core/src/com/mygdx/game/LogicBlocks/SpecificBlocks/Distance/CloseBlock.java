package com.mygdx.game.LogicBlocks.SpecificBlocks.Distance;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 26/02/2015.
 */
public class CloseBlock extends LogicBlock
{
    public CloseBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "Are Close", LogicGroups.LogicGroup.DISTANCE, LogicGroups.LogicBlockType.CLOSE);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        return null;
    }

    @Override
    public String GetBlockDescription(){
        return "Whether something is close to your ship";
    }
}
