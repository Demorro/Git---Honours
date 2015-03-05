package com.mygdx.game.Utility;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.LogicBlocks.BlockChain;
import com.mygdx.game.LogicBlocks.FullBlockScript;
import com.mygdx.game.LogicBlocks.LogicGroups;
import com.mygdx.game.States.EditorState;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;
import jdk.nashorn.internal.runtime.Debug;

import javax.xml.stream.*;
import javax.xml.stream.events.StartElement;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

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
