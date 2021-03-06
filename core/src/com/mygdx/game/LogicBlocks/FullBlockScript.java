package com.mygdx.game.LogicBlocks;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.UI.BlockDescriptionPanel;
import com.mygdx.game.UI.Button;
import com.mygdx.game.Utility.ScriptSaver;
import com.mygdx.game.Utility.SpriteAccessor;
import jdk.nashorn.internal.ir.Block;

import javax.swing.*;
import javax.xml.soap.Text;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Elliot Morris on 20/01/2015.
 */
public class FullBlockScript
{
    private ArrayList<BlockChain> blockChains = new ArrayList<BlockChain>();
    private Texture blockTextureSheet = null; //Reference to the block texture sheet

    public static Vector2 blockChainStartPos = new Vector2(25, Gdx.graphics.getHeight() - 145);
    public static int chainYSeperation = 68; //The amount of seperation active
    public static int IfIndentation = 60;

    private static final float adjacentChainOpacity = 0.5f;
    private static final float adjacentChainOpacityTweenTime = 0.2f;

    private BlockChain activeChain = null; //The active chain is the one with the currently open selectionList.

    private TweenManager fullScriptTweenManager = new TweenManager();

    public boolean needToRunNewChainCheck = false;
    public boolean needToRunDestoryChainsCheck = false;

    private BlockDescriptionPanel descriptionPanel;

    private ArrayList<Button> buttonsToDisable;


    public FullBlockScript(){ //Just for storage, no rendering if you use this constructor
        ResetScript();
    }
    public FullBlockScript(Texture blockSheet, BlockDescriptionPanel descriptionPanel, ArrayList<Button> buttonsToDisable)
    {
        this.descriptionPanel = descriptionPanel;
        blockTextureSheet = blockSheet;
        this.buttonsToDisable = buttonsToDisable;
        ResetScript();

        ScriptSaver.LoadScript(this, ScriptSaver.workingScriptPath);
    }

    public void ResetScript()
    {
        blockChains.clear();
        AddNewChain(blockChainStartPos.x, blockChainStartPos.y);
        blockChains.get(0).SetIsTopChain();
    }

    public void Update(OrthographicCamera camera)
    {

        for(BlockChain chain : blockChains)
        {
            chain.Update(descriptionPanel,camera);
        }

        boolean hasAssertedPositions = false;

        if(needToRunDestoryChainsCheck) {
            DestroyChains();
            needToRunDestoryChainsCheck = false;
            if(hasAssertedPositions == false) {
                AssertAllLinePositions();
                hasAssertedPositions = true;
            }
        }
        if(needToRunNewChainCheck) {
            CheckForWhetherWeNeedNewChain();
            needToRunNewChainCheck = false;
            if(hasAssertedPositions == false) {
                AssertAllLinePositions();
                hasAssertedPositions = true;
            }
        }

        fullScriptTweenManager.update(Gdx.graphics.getDeltaTime());

    }
    public void Render(SpriteBatch batch)
    {

        for(BlockChain chain : blockChains)
        {
            if(chain != activeChain) {
                chain.Render(batch);
            }
        }
        if(activeChain != null) {
            activeChain.Render(batch);
        }
    }

    //Checks if we need a new chain, that is if the final open chain is finished
    public void CheckForWhetherWeNeedNewChain()
    {
        ArrayList<BlockChain> allChains = GetAllChainsRecursively();
        for(BlockChain chain : allChains){
            if((chain.GetIsOnEndOfChain() == true) && (chain.GetIsIfStatement() == false) && (chain.needsNewLine == true)){
                AddNewChain(chain.GetX(), chain.GetY() - chainYSeperation, chain.parentContainer, chain);
                chain.needsNewLine = false;
            }
            else if((chain.GetIsOnEndOfChain() == true) && (chain.GetIsIfStatement() == true) && (chain.needsNewLine == true)) {
                AddNewIfBlock(chain);
                chain.needsNewLine = false;
            }
        }
        allChains.clear();
    }


    public BlockChain AddNewChain(float x, float y)
    {
        BlockChain chainToAdd = new BlockChain(x, y, blockTextureSheet, this, blockChains);
        blockChains.add(chainToAdd);
        blockChains.get(blockChains.size() - 1).LoadIterator(blockChains.size() -1 );
        if(blockChains.size() >= 2){
            chainToAdd.SetAboveBlockChain(blockChains.get(blockChains.size() - 2));
            blockChains.get(blockChains.size() - 2).SetBelowBlockChain(chainToAdd);
        }

        return chainToAdd;
    }

    public BlockChain AddNewChain(float x, float y, ArrayList<BlockChain> parentContainer, BlockChain chainJustFinished){

        if(parentContainer.size() > 0){
            if(parentContainer.get(parentContainer.size() - 1).IsEmpty() == true){
                return null;
            }
        }

        BlockChain chainToAdd = new BlockChain(x, y, blockTextureSheet, this, parentContainer);
        parentContainer.add(chainToAdd);
        parentContainer.get(parentContainer.size() -1).LoadIterator(parentContainer.size() -1);

        if(chainJustFinished != null) {
            if (chainJustFinished.GetBelowBlockChain() != null) {
                if (BlockChain.CheckIfChainsAreInSameIndentationLevel(chainJustFinished, chainJustFinished.GetBelowBlockChain())) {
                    BlockChain.SetUpperLowerRelations(chainToAdd, chainJustFinished.GetBelowBlockChain());
                }
            }
        }else{
            System.out.println("ChainJustfinished shouldnt be null, unless you're loading");
        }


        if(parentContainer.size() >= 2){
            chainToAdd.SetAboveBlockChain(parentContainer.get(parentContainer.size() - 2));
            parentContainer.get(parentContainer.size() - 2).SetBelowBlockChain(chainToAdd);
        }

        //Sets the line added after the if indentation to be related up/downwise the previous indentation below the if indentation
        if(parentContainer != null) {
            if(parentContainer.get(0) != null) {
                if (parentContainer.get(0).GetAboveBlockChain() != null) {
                    if (parentContainer.get(0).GetAboveBlockChain().GetNextBlockAfterIf() != null) {
                        BlockChain.SetUpperLowerRelations(chainToAdd, parentContainer.get(0).GetAboveBlockChain().GetNextBlockAfterIf());
                    }
                }
            }
        }

        return chainToAdd;
    }

    //Only used in the loading, with certain changes to make that work
    public BlockChain LoaderAddNewChain(float x, float y, ArrayList<BlockChain> parentContainer, BlockChain chainJustFinished){

        BlockChain chainToAdd = new BlockChain(x, y, blockTextureSheet, this, parentContainer);
        parentContainer.add(chainToAdd);
        parentContainer.get(parentContainer.size() -1).LoadIterator(parentContainer.size() -1);

        if(chainJustFinished != null) {
            if (chainJustFinished.GetBelowBlockChain() != null) {
                if (BlockChain.CheckIfChainsAreInSameIndentationLevel(chainJustFinished, chainJustFinished.GetBelowBlockChain())) {
                    BlockChain.SetUpperLowerRelations(chainToAdd, chainJustFinished.GetBelowBlockChain());
                }
            }
        }else{
            System.out.println("ChainJustfinished shouldnt be null, unless you're loading");
        }


        if(parentContainer.size() >= 2){
            chainToAdd.SetAboveBlockChain(parentContainer.get(parentContainer.size() - 2));
            parentContainer.get(parentContainer.size() - 2).SetBelowBlockChain(chainToAdd);
        }

        //Sets the line added after the if indentation to be related up/downwise the previous indentation below the if indentation
        if(parentContainer != null) {
            if(parentContainer.get(0) != null) {
                if (parentContainer.get(0).GetAboveBlockChain() != null) {
                    if (parentContainer.get(0).GetAboveBlockChain().GetNextBlockAfterIf() != null) {
                        BlockChain.SetUpperLowerRelations(chainToAdd, parentContainer.get(0).GetAboveBlockChain().GetNextBlockAfterIf());
                    }
                }
            }
        }

        return chainToAdd;
    }

    public void AddNewIfBlock(BlockChain parentIfChain)
    {
        parentIfChain.LoadIfIndentationGraphic(FullBlockScript.IfIndentation - 50, chainYSeperation - 7);

        BlockChain chainDirectlyAfterParent = parentIfChain.GetBelowBlockChain();

        BlockChain ifChildBlock = parentIfChain.AddIfChildBlock();
        ifChildBlock.parentContainer = parentIfChain.childChains;
        BlockChain.SetUpperLowerRelations(parentIfChain, ifChildBlock);
        BlockChain underIfStatementBlock = new BlockChain(parentIfChain.GetX(), ifChildBlock.GetY() , blockTextureSheet, this, parentIfChain.parentContainer);
        BlockChain.SetUpperLowerRelations(ifChildBlock,underIfStatementBlock);
        parentIfChain.parentContainer.add(underIfStatementBlock);
        parentIfChain.parentContainer.get(parentIfChain.parentContainer.size() -1).LoadIterator(parentIfChain.parentContainer.size() -1);
        if(parentIfChain.GetFirstInParentContainer().GetAboveBlockChain() != null) {
            if (parentIfChain.GetFirstInParentContainer().GetAboveBlockChain().GetNextBlockAfterIf() != null) {
                BlockChain.SetUpperLowerRelations(underIfStatementBlock, parentIfChain.GetFirstInParentContainer().GetAboveBlockChain().GetNextBlockAfterIf());
            }
        }
        else if(parentIfChain.GetFirstInParentContainer().GetAboveBlockChain() == null){
            if(chainDirectlyAfterParent != null){
                BlockChain.SetUpperLowerRelations(underIfStatementBlock, chainDirectlyAfterParent);
            }
        }

        parentIfChain.SetNextBlockAfterIf(underIfStatementBlock);
    }

    public void AssertAllLinePositions()
    {
            BlockChain nextChain = blockChains.get(0);
            int lineNo = 0;
            while (nextChain != null){
                nextChain.lineNo = lineNo;
                lineNo++;
                if(nextChain.GetBelowBlockChain() != null) {
                    nextChain = nextChain.GetBelowBlockChain();
                }
                else{
                    nextChain = null;
                }
            }

        AssertAllYs(blockChainStartPos);
    }

    public void AssertAllYs(Vector2 globalRootPos)
    {
        for(BlockChain chain : GetAllChainsRecursively())
        {
            if(chain != null) {
                chain.MoveChainToPosition(new Vector2(chain.GetX(), globalRootPos.y - (chainYSeperation * chain.lineNo)));
            }
        }
    }


    //Called by the blockchains when one of their lists opens, so that only one list may be open at a time.
    public void AnyListOpened(BlockChain chainThatOpened)
    {
        //Set the active chain to the one thats just opened
        activeChain = chainThatOpened;
        //tween the opacity of the adjacent chains down so that the list is more pronouned
        TweenAllOtherChainsOpacity(activeChain, adjacentChainOpacity);


        //Since this is called just before the list opens, then we can just close all the lists and the one that calls it will still open
        ArrayList<BlockChain> allOtherChains = GetAllBlockChainsFromOne(chainThatOpened, true);
        for(BlockChain blockChain : allOtherChains)
        {
            if (blockChain.IsListOpen()) {
                blockChain.CloseList();
            }
        }

        DisableAllOtheBlockChains(activeChain);

    }
    public void AnyListClosed(BlockChain chainThatClosed)
    {
        //tween the opacity of the adjacent chains down so that the list is more pronouned
        //TweenAdjacentChainsOpacity(activeChain, 1.0f);
        descriptionPanel.ResetDescriptionText();
        TweenAllOtherChainsOpacity(chainThatClosed, 1.0f);
        EnableAllBlockChains();
    }

    private void DisableAllOtheBlockChains(BlockChain exclusionChain)
    {
        ArrayList<BlockChain> allOtherChains = GetAllBlockChainsFromOne(exclusionChain, true);
        for(BlockChain chain : allOtherChains)
        {
            chain.SetEnabled(false);
        }
        exclusionChain.SetEnabled(true);
    }
    private void EnableAllBlockChains()
    {
        for(BlockChain chain : GetAllChainsRecursively())
        {
            chain.SetEnabled(true);
        }
    }

    private void TweenAllOtherChainsOpacity(BlockChain exclusionChain, float opacity)
    {
        ArrayList<BlockChain> otherChains = GetAllBlockChainsFromOne(exclusionChain, true);

        for(BlockChain chain : otherChains){
            Tween.to(chain, BlockChainAccessor.OPACITY, adjacentChainOpacityTweenTime)
                    .target(opacity)
                    .start(fullScriptTweenManager);
        }
    }

    //Destroy chains that need to be destroyed
    public void DestroyChains()
    {
        for(int i = 0; i < blockChains.size(); i++)
        {
            if(blockChains.get(i).ShouldDelete())
            {
                blockChains.remove(i);
            }
        }
    }

    public void DeleteActiveChain()
    {
        activeChain = null;
    }

    public ArrayList<BlockChain> GetBlockChains()
    {
        return blockChains;
    }
    private ArrayList<BlockChain> GetAllChainsRecursively()
    {
        ArrayList<BlockChain> allChains = new ArrayList<BlockChain>();
        for(BlockChain chain : blockChains)
        {
            allChains.add(chain);
            allChains.addAll(chain.GetAllChildChainsRecursively());
        }
        return allChains;
    }

    private ArrayList<BlockChain> GetAllAboveChains(BlockChain activeChain){
        ArrayList<BlockChain> aboveChains = new ArrayList<BlockChain>();

        BlockChain aboveChain = null;
        if(activeChain.GetAboveBlockChain() != null){
            aboveChain = activeChain.GetAboveBlockChain();
        }

        while(aboveChain != null){
            aboveChains.add(aboveChain);
            aboveChain = aboveChain.GetAboveBlockChain();
        }

        return aboveChains;
    }
    private ArrayList<BlockChain> GetAllBelowChains(BlockChain activeChain){
        ArrayList<BlockChain> belowChains = new ArrayList<BlockChain>();

        BlockChain belowChain = null;
        if(activeChain.GetBelowBlockChain() != null){
            belowChain = activeChain.GetBelowBlockChain();
        }

        while(belowChain != null){
            belowChains.add(belowChain);
            belowChain = belowChain.GetBelowBlockChain();
        }

        return belowChains;
    }
    private ArrayList<BlockChain> GetAllBlockChainsFromOne(BlockChain seedChain, boolean excludeCenter){
        ArrayList<BlockChain> chains = new ArrayList<BlockChain>();
        if(excludeCenter == false) {
            chains.add(seedChain);
        }
        chains.addAll(GetAllAboveChains(seedChain));
        chains.addAll(GetAllBelowChains(seedChain));

        return chains;
    }
    public ArrayList<BlockChain> GetAllBlockChainsFromFirst()
    {
        return GetAllBlockChainsFromOne(blockChains.get(0), false);
    }

    public void CloseAnyOpenLists()
    {
        for(BlockChain chain : GetAllBlockChainsFromFirst()){
            chain.CloseAnyOpenListsForSaving();
        }
    }

    //Returns 1 is succesfully saved, 0 if cancelled out, and -1 if ERROR HAPPENED
    public int SaveScript()
    {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        String workingDir = System.getProperty("user.dir");
        fc.setCurrentDirectory(new File(workingDir + ScriptSaver.scriptFolderPath));
        //In response to a button click:
        int returnVal = fc.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            ScriptSaver.SaveScript(this, file.getAbsolutePath());
            return 1;
        }
        if((returnVal == JFileChooser.CANCEL_OPTION) || (returnVal == JFileChooser.ERROR_OPTION))
        {
            return 0;
        }
        Gdx.app.log("Error","Error in SaveScript(), FullBlockScript.java");
        return -1;
    }
    public void SaveScriptDirectly(String path)
    {
        File file = new File(path);
        ScriptSaver.SaveScript(this, file.getAbsolutePath());
    }
    // //Returns 1 is succesfully saved, 0 if cancelled out, and -1 if ERROR HAPPENED
    public int LoadScript()
    {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        String workingDir = System.getProperty("user.dir");
        fc.setCurrentDirectory(new File(workingDir + ScriptSaver.scriptFolderPath));

        //In response to a button click:
        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            ScriptSaver.LoadScript(this, file.getAbsolutePath());
            return 1;
        }
        if((returnVal == JFileChooser.CANCEL_OPTION) || (returnVal == JFileChooser.ERROR_OPTION))
        {
            return 0;
        }

        Gdx.app.log("Error", "Script not succesfully loaded, the source .xml is likely wrong, FullBlockScript.java");
        return -1;
    }





}
