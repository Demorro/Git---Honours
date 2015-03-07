package com.mygdx.game.Utility;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.LogicBlocks.BlockChain;
import com.mygdx.game.LogicBlocks.FullBlockScript;
import com.mygdx.game.LogicBlocks.LogicGroups;
import com.mygdx.game.States.EditorState;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.runtime.Debug;

import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Elliot Morris on 26/01/2015.
 * Saves the logic block scripts
 */
public class ScriptSaver {

    public static String scriptFolderPath = "/Scripts/";
    public static String workingScriptPath = "Data/WorkingScript.xml"; //The path that the editor state will save to so the playstate can read from it

    private static String internalXMLName = "PlayerLogicScript";

    public static void SaveScript(FullBlockScript script, String savePath)
    {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(savePath);
            XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = new IndentingXMLStreamWriter(xmlOutFact.createXMLStreamWriter(fos));
            writer.writeStartDocument();
            writer.writeStartElement(internalXMLName);
            // write stuff
            ArrayList<BlockChain> scriptChains = script.GetAllBlockChainsFromFirst();
            BlockChain nextChain = scriptChains.get(0);
            while(nextChain != null) {
                writer.writeStartElement("Line");
                WriteSingleChain(nextChain, writer);

                if(nextChain.GetAllChildChainsRecursively() != null)
                {
                    if(nextChain.GetAllChildChainsRecursively().size() != 0) {
                        nextChain = nextChain.GetAllChildChainsRecursively().get(0);
                        continue;
                    }
                }

                writer.writeEndElement();

                if(nextChain.GetBelowBlockChain() != null)
                {
                    //if the chain below isnt on the same indentation level, means we're going down a chain, which means we need to close the element
                    if(BlockChain.CheckIfChainsAreInSameIndentationLevel(nextChain, nextChain.GetBelowBlockChain()) == false)
                    {
                        writer.writeEndElement();
                    }

                    nextChain = nextChain.GetBelowBlockChain();
                }
                else
                {
                    nextChain = null;
                }
            }
            writer.writeEndElement();
            writer.flush();
        }
        catch(IOException exc) {
        }
        catch(XMLStreamException exc) {
        }
        finally {
        }
    }

    private static void WriteSingleChain(BlockChain chain, XMLStreamWriter writer) throws XMLStreamException {
        for(int j = 0; j < chain.GetBlockList().size(); j++)
        {
            writer.writeStartElement("Block");
            writer.writeCharacters(chain.GetBlockList().get(j).GetBlockType().name());
            writer.writeEndElement();
        }
    }


    public static void LoadScript(FullBlockScript script, String scriptXMLDocPath) {
        script.GetBlockChains().clear();
        script.DeleteActiveChain();
        FileInputStream fis = null;

        ArrayList<ArrayList<LogicGroups.LogicBlockType>> scriptLines = new ArrayList<ArrayList<LogicGroups.LogicBlockType>>();
        ArrayList<Integer> lineIndentationLevels = new ArrayList<Integer>();
        int currentLineIndentationLevel = 0;

        try {
            fis = new FileInputStream(scriptXMLDocPath);
            XMLInputFactory xmlInFact = XMLInputFactory.newInstance();
            XMLStreamReader reader = xmlInFact.createXMLStreamReader(fis);


            while (reader.hasNext()) {
                reader.next(); // do something here
                if (reader.isStartElement()) {
                    if (reader.getName().getLocalPart().equals("Line")) {
                        scriptLines.add(new ArrayList<LogicGroups.LogicBlockType>());
                        lineIndentationLevels.add(currentLineIndentationLevel);
                        currentLineIndentationLevel++;
                    }

                    if(reader.getName().getLocalPart().equals("Block")){
                        reader.next();
                        if(reader.isCharacters()) {
                            scriptLines.get(scriptLines.size()-1).add(LogicGroups.LogicBlockType.valueOf((reader.getText())));
                            System.out.println(reader.getText());
                        }
                    }
                }
                if(reader.isEndElement()){
                    if(reader.getName().getLocalPart().equals("/Line"))
                    {
                        currentLineIndentationLevel--;
                    }
                }

            }
        } catch (IOException exc) {
            Gdx.app.log("Error", "IOException, ScriptSaver LoadScript()");
        } catch (XMLStreamException exc) {
            Gdx.app.log("Error", "XMLStreamException, ScriptSaver LoadScript()");
        }

        if(scriptLines.size() != lineIndentationLevels.size()){
            Gdx.app.log("Error", "InLoadScript, indentation array and scriptline array must be equal");
        }


        Stack<ArrayList<BlockChain>> currentParentContainers = new Stack<ArrayList<BlockChain>>();
        currentParentContainers.push(script.GetBlockChains());
        currentLineIndentationLevel = 0;
        BlockChain chainJustAdded = null;
        //Now we have an arraylist full of lines n' blocks, we can populate the script
        for(int i = 0; i < scriptLines.size(); i++)
        {
            if(chainJustAdded != null)
            {
                //There is a blockslot already available below
                if(chainJustAdded.GetBelowBlockChain() != null)
                {
                    for(int j = 0; j < scriptLines.get(i).size(); j++) {
                        chainJustAdded.GetBelowBlockChain().DirectlyAddBlock(scriptLines.get(i).get(j));
                    }

                    //If this is a when block, we better do the indentation
                    if(chainJustAdded.GetBelowBlockChain().GetBlockList().size() > 0) {
                        if (chainJustAdded.GetBelowBlockChain().GetBlockList().get(0).GetBlockType() == LogicGroups.LogicBlockType.WHEN) {
                            script.AddNewIfBlock(chainJustAdded.GetBelowBlockChain());
                            currentLineIndentationLevel++;
                        }
                    }


                    chainJustAdded = chainJustAdded.GetBelowBlockChain();
                    if(chainJustAdded.parentContainer == chainJustAdded.GetAboveBlockChain().parentContainer){
                        chainJustAdded.needsNewLine = true;
                    }
                    script.CheckForWhetherWeNeedNewChain();
                    script.AssertAllLinePositions();
                    continue;
                }

            }

            //No blockslot, need to add one
            BlockChain chainToAdd;
            chainToAdd = script.LoaderAddNewChain(FullBlockScript.blockChainStartPos.x, FullBlockScript.blockChainStartPos.y, currentParentContainers.peek(), chainJustAdded);

            for(int j = 0; j < scriptLines.get(i).size(); j++) {
                chainToAdd.DirectlyAddBlock(scriptLines.get(i).get(j));
            }

            //If this is a when block, we better do the indentation
            if(chainToAdd.GetBlockList().size() > 0) {
                if (chainToAdd.GetBlockList().get(0).GetBlockType() == LogicGroups.LogicBlockType.WHEN) {
                    script.AddNewIfBlock(chainToAdd);
                    currentLineIndentationLevel++;
                }
            }

            chainJustAdded = chainToAdd;
            script.AssertAllLinePositions();
        }


        if (script.GetBlockChains().size() <= 0) {
            script.AddNewChain(FullBlockScript.blockChainStartPos.x, FullBlockScript.blockChainStartPos.y);
        }
        script.AssertAllLinePositions();

    }


    //Used in load scrips, depends on reader state, so you cant really use it anywhere else
    private static void LoadBlocksIntoBlockChain(BlockChain chainToLoadInto, XMLStreamReader reader) throws XMLStreamException {
        if(reader.isStartElement()) {
            if(reader.getName().getLocalPart().equals("Block")){
                reader.next();
                if(reader.isCharacters()) {
                    chainToLoadInto.DirectlyAddBlock(LogicGroups.LogicBlockType.valueOf(reader.getText()));
                    System.out.println(reader.getText());
                }
            }
        }
    }


    /*
    public static void LoadScript(FullBlockScript script, String scriptXMLDocPath)
    {
        script.GetBlockChains().clear();
        script.DeleteActiveChain();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(scriptXMLDocPath);
            XMLInputFactory xmlInFact = XMLInputFactory.newInstance();
            XMLStreamReader reader = xmlInFact.createXMLStreamReader(fis);
            while(reader.hasNext()) {
                reader.next(); // do something here
                if (reader.isStartElement()) {
                    if (reader.getName().getLocalPart().equals("Line")) {
                        script.AddNewChain(FullBlockScript.blockChainStartPos.x, FullBlockScript.blockChainStartPos.y - (script.GetBlockChains().size() * FullBlockScript.chainYSeperation));
                    }
                }
                if(reader.isStartElement()) {
                    if(reader.getName().getLocalPart().equals("Block")){
                        reader.next();
                        if(reader.isCharacters()) {
                            script.GetBlockChains().get(script.GetBlockChains().size() - 1).DirectlyAddBlock(LogicGroups.LogicBlockType.valueOf(reader.getText()));
                            System.out.println(reader.getText());
                        }
                    }
                }
            }
        }
        catch(IOException exc) {
            Gdx.app.log("Error", "IOException, ScriptSaver LoadScript()");
        }
        catch(XMLStreamException exc) {
            Gdx.app.log("Error", "XMLStreamException, ScriptSaver LoadScript()");
        }

        if(script.GetBlockChains().size() <= 0)
        {
            script.AddNewChain(FullBlockScript.blockChainStartPos.x, FullBlockScript.blockChainStartPos.y);
        }
    }
    */

    public static ArrayList<ArrayList<LogicGroups.LogicBlockType>> LoadScriptIntoArray(String scriptXMLDocPath)
    {

        ArrayList<ArrayList<LogicGroups.LogicBlockType>> scriptStorage = new ArrayList<ArrayList<LogicGroups.LogicBlockType>>();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(scriptXMLDocPath);
            XMLInputFactory xmlInFact = XMLInputFactory.newInstance();
            XMLStreamReader reader = xmlInFact.createXMLStreamReader(fis);
            while(reader.hasNext()) {
                reader.next(); // do something here
                if (reader.isStartElement()) {
                    if (reader.getName().getLocalPart().equals("Line")) {
                        scriptStorage.add(new ArrayList<LogicGroups.LogicBlockType>());
                    }
                }
                if(reader.isStartElement()) {
                    if(reader.getName().getLocalPart().equals("Block")){
                        reader.next();
                        if(reader.isCharacters()) {
                            scriptStorage.get(scriptStorage.size() - 1).add(LogicGroups.LogicBlockType.valueOf(reader.getText()));
                            System.out.println(reader.getText());
                        }
                    }
                }
            }
        }
        catch(IOException exc) {
            Gdx.app.log("Error", "IOException, ScriptSaver LoadScriptIntoArray()");
        }
        catch(XMLStreamException exc) {
            Gdx.app.log("Error", "XMLStreamException, ScriptSaver LoadScriptIntoArray()");
        }

        if(scriptStorage.size() <= 0)
        {
            scriptStorage.add(new ArrayList<LogicGroups.LogicBlockType>());
        }

        return scriptStorage;
    }
}
