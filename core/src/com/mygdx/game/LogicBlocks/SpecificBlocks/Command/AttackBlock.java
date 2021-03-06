package com.mygdx.game.LogicBlocks.SpecificBlocks.Command;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class AttackBlock extends LogicBlock
{
    public AttackBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "  Attack  ", LogicGroups.LogicGroup.COMMAND, LogicGroups.LogicBlockType.ATTACK);
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
        return "Fire at a target.";
    }
}
