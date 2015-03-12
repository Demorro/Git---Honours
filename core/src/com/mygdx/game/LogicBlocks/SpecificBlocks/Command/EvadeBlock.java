package com.mygdx.game.LogicBlocks.SpecificBlocks.Command;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class EvadeBlock extends LogicBlock
{
    public EvadeBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "   Evade   ", LogicGroups.LogicGroup.COMMAND, LogicGroups.LogicBlockType.EVADE);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        nextLogicGroups.add(LogicGroups.LogicGroup.ENEMIES);
        return nextLogicGroups;
    }

    @Override
    public String GetBlockDescription(){
        return "Attempt to avoid the target.";
    }
}
