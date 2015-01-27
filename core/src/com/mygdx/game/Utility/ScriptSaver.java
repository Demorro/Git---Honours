package com.mygdx.game.Utility;

import com.mygdx.game.LogicBlocks.BlockChain;
import com.mygdx.game.LogicBlocks.FullBlockScript;
import com.mygdx.game.LogicBlocks.LogicGroups;
import com.mygdx.game.States.EditorState;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

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
            // write stuf
            ArrayList<BlockChain> scriptChains = script.GetBlockChains();
            for(int i = 0; i < scriptChains.size(); i++) {
                writer.writeStartElement("Line");
                for(int j = 0; j < scriptChains.get(i).GetBlockList().size(); j++)
                {
                    writer.writeStartElement("Block");
                    writer.writeCharacters(scriptChains.get(i).GetBlockList().get(j).GetBlockType().name());
                    writer.writeEndElement();
                }
                writer.writeEndElement();
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
        }
        catch(XMLStreamException exc) {
        }

        if(script.GetBlockChains().size() <= 0)
        {
            script.AddNewChain(FullBlockScript.blockChainStartPos.x, FullBlockScript.blockChainStartPos.y);
        }
    }
}
