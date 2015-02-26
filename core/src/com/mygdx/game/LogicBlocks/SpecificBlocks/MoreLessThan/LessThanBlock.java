package com.mygdx.game.LogicBlocks.SpecificBlocks.MoreLessThan;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 26/02/2015.
 */
public class LessThanBlock extends LogicBlock
{
    public LessThanBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "Is Less Than", LogicGroups.LogicGroup.MORELESSTHAN, LogicGroups.LogicBlockType.LESSTHAN);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        nextLogicGroups.add(LogicGroups.LogicGroup.NUMBERS);
        return nextLogicGroups;
    }

    @Override
    public String GetBlockDescription(){
        return "Less Than.";
    }
}