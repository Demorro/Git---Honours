package com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class LaserBlock extends LogicBlock
{
    public LaserBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "With Lasers", LogicGroups.LogicGroup.WEAPONS, LogicGroups.LogicBlockType.LASER);
        if(previousBlock != null) {
            if (previousBlock.GetBlockType() == LogicGroups.LogicBlockType.WHEN) {
                ResetBlockText("     Lasers     ");
            }
        }
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        if(previousBlock != null) {
            if (previousBlock.GetBlockType() == LogicGroups.LogicBlockType.WHEN) {
                nextLogicGroups.add(LogicGroups.LogicGroup.IFWEAPON);
            } else {
                return null;
            }
        }
        return nextLogicGroups;
    }

    @Override
    public String GetBlockDescription(){
        return "Your quick moving, moderate damage laser beams with high accuracy.";
    }
}
