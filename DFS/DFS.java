import java.rmi.*;
import java.net.*;
import java.util.*;

import javax.json.*;

import java.io.*;
import java.nio.file.*;
import java.math.BigInteger;
import java.security.*;
// import a json package


/* JSON Format

 {
    "metadata" :
    {
        file :
        {
            name  : "File1"
            numberOfPages : "3"
            pageSize : "1024"
            size : "2291"
            page :
            {
                number : "1"
                guid   : "22412"
                size   : "1024"
            }
            page :
            {
                number : "2"
                guid   : "46312"
                size   : "1024"
            }
            page :
            {
                number : "3"
                guid   : "93719"
                size   : "243"
            }
        }
    }
}
 
 
 */


public class DFS
{
    int port;
    Chord  chord;
    
    /** Function that will help generate the GUID
     * @objectName not sure yet
     * @return a GUID
     */
    private long md5(String objectName)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(objectName.getBytes());
            BigInteger bigInt = new BigInteger(1,m.digest());
            return Math.abs(bigInt.longValue());
        }
        catch(NoSuchAlgorithmException e)
        {
                e.printStackTrace();
                
        }
        return 0;
    }
    
    /** Constructor that initialize the port and create the metadata
     * @Port: the port that the user wish to connect to
     */
    public DFS(int port) throws Exception
    {
        
        this.port = port;
        long guid = md5("" + port);
        chord = new Chord(port, guid);
        Files.createDirectories(Paths.get(guid+"/repository"));
    }
    

    /** Connect to the desired destination through a certain port
     * @Ip The destination
     * @port The port which we will connect to
     */
    public  void join(String Ip, int port) throws Exception
    {
        chord.joinRing(Ip, port);
        chord.Print();
    }
    

    /** Function that will help generate the GUID
     * @return JsonReader with the Json information ready to be read
     */
    public JsonReader readMetaData() throws Exception
    {
        long guid = md5("Metadata");

        ChordMessageInterface peer = chord.locateSuccessor(guid);
        InputStream metadataraw = peer.get(guid);
        return Json.createReader(metadataraw);
    }
    
    public void writeMetaData(InputStream stream) throws Exception
    {
        long guid = md5("Metadata");
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, stream);
    }

    /** ??
     */   
    public void mv(String oldName, JsonValue newName) throws Exception
    {
        // TODO:  Change the name in Metadata
        // Write Metadata
    	// Mental Note: Just renames the file.. apparently
    }


    /** Gather all files
     * @return a string of all files
     */    
    public String ls() throws Exception
    {
        String listOfFiles = "";
       JsonArray metaReader = getMetaData();

       for (int i = 0; i < metaReader.size(); i++){
    	   JsonObject getJson = metaReader.getJsonObject(i);
    	   JsonObject getJsonFile = getJson.getJsonObject("file");
    	   listOfFiles += getJsonFile.getJsonString("name") + "\n";
       }
       
       return listOfFiles;
    }

    /** Create the filename by adding a new entry to the metadata
     *
     * @param fileName the name of the new file
     * @throws Exception
     */
    public void touch(String fileName) throws Exception
    {
    	JsonArray metaReader = getMetaData();
    	
    	JsonObjectBuilder newFile = Json.createObjectBuilder()
    			.add("file", Json.createObjectBuilder()
    					.add("name", fileName)
    					.add("numberOfPages", 0)
    					.add("pageSize", 1024)
    					.add("size", 0)
    					.add("page", Json.createObjectBuilder()));

    	JsonArrayBuilder newMetaJsonArray = Json.createArrayBuilder(metaReader)
    			.add(newFile.build());
    	JsonArray newMeta = newMetaJsonArray.build();

    	JsonObject newMetaData = Json.createObjectBuilder()
    			.add("metadata", newMeta).build();

    	writeMetaData(newMetaData.toString());
    }
    public void delete(String fileName) throws Exception
    {
        // TODO: remove all the pages in the entry fileName in the Metadata and then the entry
        // for each page in Metadata.filename
        //     peer = chord.locateSuccessor(page.guid);
        //     peer.delete(page.guid)
        // delete Metadata.filename
        // Write Metadata

    	JsonArray metaReader = getMetaData();
    	JsonArrayBuilder newMeta = Json.createArrayBuilder();
    	
    	for (int i = 0; i < metaReader.size();i++){
    		JsonObject getJson = metaReader.getJsonObject(i).getJsonObject("file");
//    		System.out.println(getJson.getJsonString("name") + " " + fileName);
    		if (getJson.getJsonString("name").toString().replaceAll("\"", "").equals(fileName)){
    			JsonArray getJsonPages = getJson.getJsonArray("page");
    			for (int j = 0; j < getJsonPages.size(); j++){
    				JsonObject tempPage = getJsonPages.getJsonObject(j);
    				long guidPage = Integer.parseInt(tempPage.getJsonString("guid").toString().replaceAll("\"", ""));
    				
    				ChordMessageInterface peer = chord.locateSuccessor(guidPage);
    				peer.delete(guidPage);
    				
    			}
    		} else {
    			JsonArrayBuilder jsonFileArray = Json.createArrayBuilder();
    			jsonFileArray.add(getJson);
    			JsonObjectBuilder jsonMainFileObject = Json.createObjectBuilder()
    					.add("file", jsonFileArray.build());
    			newMeta.add("metaData")
    				.add(jsonMainFileObject);
    		}
    	}
    	

		System.out.println(newMeta.build().toString());
        
    }
    
    public byte[] read(String fileName, int pageNumber) throws Exception
    {
        // TODO: read pageNumber from fileName
        return null;
    }
    
    
    public byte[] tail(String fileName) throws Exception
    {
        // TODO: return the last page of the fileName
        return null;
    }
    public byte[] head(String fileName) throws Exception
    {
        // TODO: return the first page of the fileName
        return null;
    }
    public void append(String filename, Byte[] data) throws Exception
    {
        // TODO: append data to fileName. If it is needed, add a new page.
        // Let guid be the last page in Metadata.filename
        //ChordMessageInterface peer = chord.locateSuccessor(guid);
        //peer.put(guid, data);
        // Write Metadata

        
    }
    
    public JsonArray getMetaData() throws Exception{
        JsonReader reader = readMetaData();
        JsonObject readerGet = reader.readObject();
        reader.close();
        JsonArray metaReader = readerGet.getJsonArray("metadata");
		return metaReader;    
    }
    
    public void writeMetaData(String getString) throws Exception{
    	InputStream is = new ByteArrayInputStream(getString.toString().getBytes());
    	writeMetaData(is);
    }
}
