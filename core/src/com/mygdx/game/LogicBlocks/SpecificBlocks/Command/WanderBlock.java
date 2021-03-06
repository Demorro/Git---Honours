package com.mygdx.game.LogicBlocks.SpecificBlocks.Command;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class WanderBlock extends LogicBlock
{
    public WanderBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "  Wander  ", LogicGroups.LogicGroup.COMMAND, LogicGroups.LogicBlockType.WANDER);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        //ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        return null;
    }

    @Override
    public String GetBlockDescription(){
        return "Wander around space aimlessly.";
    }
}
