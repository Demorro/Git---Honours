package com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class MissileBlock extends LogicBlock
{
    public MissileBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "With Missiles", LogicGroups.LogicGroup.WEAPONS, LogicGroups.LogicBlockType.MISSILE);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        nextLogicGroups.add(LogicGroups.LogicGroup.SPEED);
        return nextLogicGroups;
    }
}
