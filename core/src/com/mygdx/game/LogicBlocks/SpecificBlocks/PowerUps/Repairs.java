package com.mygdx.game.LogicBlocks.SpecificBlocks.PowerUps;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class Repairs extends LogicBlock
{
    public Repairs(Texture blockSheet) {
        super(blockSheet, "Repairs", LogicGroups.LogicGroup.POWERUPS, LogicGroups.LogicBlockType.REPAIRS);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        return null;
    }
}
