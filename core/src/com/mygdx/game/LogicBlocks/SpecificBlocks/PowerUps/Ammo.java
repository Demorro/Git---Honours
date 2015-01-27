package com.mygdx.game.LogicBlocks.SpecificBlocks.PowerUps;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class Ammo extends LogicBlock
{
    public Ammo(Texture blockSheet) {
        super(blockSheet, "Ammo", LogicGroups.LogicGroup.POWERUPS, LogicGroups.LogicBlockType.AMMO);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        return null;
    }
}
