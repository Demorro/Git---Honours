package com.mygdx.game.LogicBlocks.SpecificBlocks.Enemies;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class CapitalShipBlock extends LogicBlock
{
    public CapitalShipBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "Capital Ships", LogicGroups.LogicGroup.ENEMIES, LogicGroups.LogicBlockType.CAPITALSHIP);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();

        if(previousBlock != null) {
            if (previousBlock.GetBlockType() == LogicGroups.LogicBlockType.ATTACK) {
                nextLogicGroups.add(LogicGroups.LogicGroup.WEAPONS);
            } else if (previousBlock.GetBlockType() == LogicGroups.LogicBlockType.WHEN) {
                nextLogicGroups.add(LogicGroups.LogicGroup.DISTANCE);
            } else {
                nextLogicGroups.add(LogicGroups.LogicGroup.SPEED);
            }
        }
        return nextLogicGroups;
    }

    @Override
    public String GetBlockDescription(){
        return "Hulking capital ships";
    }
}
