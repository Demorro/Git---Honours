package com.mygdx.game.LogicBlocks.SpecificBlocks.Command;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class SearchForBlock extends LogicBlock
{
    public SearchForBlock(Texture blockSheet) {
        super(blockSheet, "Search For", LogicGroups.LogicGroup.COMMAND, LogicGroups.LogicBlockType.SEARCHFOR);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        nextLogicGroups.add(LogicGroups.LogicGroup.POWERUPS);
        return nextLogicGroups;
    }
}
