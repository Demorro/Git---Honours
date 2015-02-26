package com.mygdx.game.LogicBlocks.SpecificBlocks.PlayerShip;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 26/02/2015.
 */
public class ShipHP extends LogicBlock
{
    public ShipHP(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "     Ship HP     ", LogicGroups.LogicGroup.PLAYERSHIP, LogicGroups.LogicBlockType.SHIPHP);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        nextLogicGroups.add(LogicGroups.LogicGroup.MORELESSTHAN);
        return nextLogicGroups;
    }

    @Override
    public String GetBlockDescription(){
        return "How many hit points your ship has. \n If this hits zero the ship explodes.";
    }
}