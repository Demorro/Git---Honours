package com.mygdx.game.LogicBlocks.SpecificBlocks.Numbers;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 26/02/2015.
 */
public class EightyBlock extends LogicBlock
{
    public EightyBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "     Eighty     ", LogicGroups.LogicGroup.NUMBERS, LogicGroups.LogicBlockType.EIGHTY);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        return null;
    }

    @Override
    public String GetBlockDescription(){
        return "Eighty.";
    }
}