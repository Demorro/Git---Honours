package com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class AutoCannonBlock extends LogicBlock
{
    public AutoCannonBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "With Cannons", LogicGroups.LogicGroup.WEAPONS, LogicGroups.LogicBlockType.AUTOCANNON);
        if(previousBlock.GetBlockType() == LogicGroups.LogicBlockType.WHEN)
        {
            ResetBlockText("    Cannons    ");
        }
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        if(previousBlock.GetBlockType() == LogicGroups.LogicBlockType.WHEN) {
            nextLogicGroups.add(LogicGroups.LogicGroup.IFWEAPON);
        }
        else{
            nextLogicGroups.add(LogicGroups.LogicGroup.SPEED);
        }
        return nextLogicGroups;
    }

    @Override
    public String GetBlockDescription(){
        return "Quick firing, low damage cannon.";
    }
}
