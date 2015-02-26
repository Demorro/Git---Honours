package com.mygdx.game.LogicBlocks.SpecificBlocks.Weapons.IfWeapon;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 22/02/2015.
 */
public class IsFiring extends LogicBlock
{
    public IsFiring(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "  Are Firing  ", LogicGroups.LogicGroup.IFWEAPON, LogicGroups.LogicBlockType.ISFIRING);

    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        return null;
    }

    @Override
    public String GetBlockDescription(){
        return "If the weapon is currently firing.";
    }
}
