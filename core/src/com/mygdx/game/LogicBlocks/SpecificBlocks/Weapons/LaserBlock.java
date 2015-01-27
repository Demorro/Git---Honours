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
    public LaserBlock(Texture blockSheet) {
        super(blockSheet, "With Lasers", LogicGroups.LogicGroup.WEAPONS, LogicGroups.LogicBlockType.LASER);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        nextLogicGroups.add(LogicGroups.LogicGroup.SPEED);
        return nextLogicGroups;
    }
}
