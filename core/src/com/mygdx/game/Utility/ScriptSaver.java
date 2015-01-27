package com.mygdx.game.Utility;

import com.mygdx.game.LogicBlocks.BlockChain;
import com.mygdx.game.LogicBlocks.FullBlockScript;
import com.sun.xml.internal.txw2.output.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Elliot Morris on 26/01/2015.
 * Saves the logic block scripts
 */
public class ScriptSaver {

    public static String scriptFolderPath = "Scripts/";

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
}
