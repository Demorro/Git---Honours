package com.mygdx.game.LogicBlocks.SpecificBlocks.MoreLessThan;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 26/02/2015.
 */
public class FullBlock extends LogicBlock
{
    public FullBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "     Is Full     ", LogicGroups.LogicGroup.MORELESSTHAN, LogicGroups.LogicBlockType.FULL);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        return null;
    }

    @Override
    public String GetBlockDescription(){
        return "Completely Full.";
    }
}