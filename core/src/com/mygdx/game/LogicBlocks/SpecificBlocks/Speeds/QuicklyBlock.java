package com.mygdx.game.LogicBlocks.SpecificBlocks.Speeds;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.LogicBlocks.LogicBlock;
import com.mygdx.game.LogicBlocks.LogicGroups;

import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class QuicklyBlock extends LogicBlock
{
    public QuicklyBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "Quickly", LogicGroups.LogicGroup.SPEED, LogicGroups.LogicBlockType.QUICKLY);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        return null;
    }

    @Override
    public String GetBlockDescription(){
        return "Causes something to happen as quickly as possible";
    }
}
