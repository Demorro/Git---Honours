package com.mygdx.game.LogicBlocks.SpecificBlocks.Speeds;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class SlowlyBlock extends LogicBlock
{
    public SlowlyBlock(Texture blockSheet) {
        super(blockSheet, "Slowly", LogicGroups.LogicGroup.SPEED, LogicGroups.LogicBlockType.SLOWLY);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        return null;
    }
}
