package com.mygdx.game.LogicBlocks.SpecificBlocks.If;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 22/02/2015.
 */
public class When extends LogicBlock
{
    public When(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "   When   ", LogicGroups.LogicGroup.IF, LogicGroups.LogicBlockType.WHEN);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        nextLogicGroups.add(LogicGroups.LogicGroup.ENEMIES);
        nextLogicGroups.add(LogicGroups.LogicGroup.PLAYERSHIP);
        nextLogicGroups.add(LogicGroups.LogicGroup.WEAPONS);
        return nextLogicGroups;
    }

    @Override
    public String GetBlockDescription(){
        return "Actions inside a When statement are only performed if the statement condition is met.";
    }
}
