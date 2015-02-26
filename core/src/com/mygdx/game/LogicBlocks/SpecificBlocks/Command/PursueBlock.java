package com.mygdx.game.LogicBlocks.SpecificBlocks.Command;

        import com.badlogic.gdx.graphics.Texture;
        import com.mygdx.game.LogicBlocks.LogicBlock;
        import com.mygdx.game.LogicBlocks.LogicGroups;

        import java.util.ArrayList;

/**
 * Created by Elliot Morris on 15/01/2015.
 */
public class PursueBlock extends LogicBlock
{
    public PursueBlock(Texture blockSheet, LogicBlock previousBlock) {
        super(blockSheet, "  Pursue  ", LogicGroups.LogicGroup.COMMAND, LogicGroups.LogicBlockType.PURSUE);
    }

    @Override
    public ArrayList<LogicGroups.LogicGroup> GetNextLogicGroup(LogicBlock previousBlock)
    {
        ArrayList<LogicGroups.LogicGroup> nextLogicGroups = new ArrayList<LogicGroups.LogicGroup>();
        nextLogicGroups.add(LogicGroups.LogicGroup.ENEMIES);
        nextLogicGroups.add(LogicGroups.LogicGroup.SCARYOBJECTS);
        return nextLogicGroups;
    }

    @Override
    public String GetBlockDescription(){
        return "Chase the target down.";
    }
}
