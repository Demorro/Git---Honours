package com.mygdx.game.LogicBlocks.SpecificBlocks.ScaryObjects;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class IncomingProjectiles extends LogicBlock
{
    public IncomingProjectiles(Texture blockSheet) {
        super(blockSheet, "Incoming Fire", LogicGroups.LogicGroup.SCARYOBJECTS, LogicGroups.LogicBlockType.INCOMINGPROJECTILES);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();

        if(previousBlock.GetBlockType() == LogicGroups.LogicBlockType.ATTACK)
        {
            nextLogicGroups.add(LogicGroups.LogicGroup.WEAPONS);
        }
        else
        {
            nextLogicGroups.add(LogicGroups.LogicGroup.SPEED);
        }

        return nextLogicGroups;
    }
}
