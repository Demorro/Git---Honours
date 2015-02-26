package com.mygdx.game.LogicBlocks.SpecificBlocks.ScaryObjects;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class Asteroids extends LogicBlock
{
    public Asteroids(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "  Asteroids  ", LogicGroups.LogicGroup.SCARYOBJECTS, LogicGroups.LogicBlockType.ASTEROIDS);
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

    @Override
    public String GetBlockDescription(){
        return "Asteroids that float about in space.";
    }
}
