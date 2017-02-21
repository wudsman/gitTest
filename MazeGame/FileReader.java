
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.DatatypeConverter;


public class FileReader extends FileInputStream {

    // constructor for FileReader
    public FileReader(String name) throws FileNotFoundException {
        super(name);
    }
    
    // reads four bytes from the filestream, 
    // catches end of file exception and closes file
    // converts bytes to int using DataBuffer
    public int readInt() throws IOException{
        byte[] bytes = new byte[4];
        try{
            this.read(bytes);
        }catch(EOFException e){
            this.close();
        }
        // convert bytes to int
        int returnInt = DataBuffer.convertToInt(bytes);
        return returnInt;
    } 
    
 // reads four bytes from the filestream, 
    // catches end of file exception and closes file
    // converts bytes to float using DataBuffer
    public float readFloat() throws IOException{
        byte[] bytes = new byte[4];
        try{
            this.read(bytes);
        }catch(EOFException e){
            this.close();
        }
        // convert bytes to float
        float returnFloat = DataBuffer.convertToFloat(bytes);
        return returnFloat;
    }
    

      public long readLong() throws IOException{
    	byte[] bytes = new byte[8];
    	try{
    		this.read(bytes);
    	}catch(EOFException e){
    		this.close();
    	}
    	long returnLong = DataBuffer.convertToLong(bytes);
    	return returnLong;
    }
    
    // read file using prescribed format
    // place data in a 2D array of floats
    //  return float[][]
    public void readMagicNumber( FileData fd) throws IOException{
    	fd.hex = readInt();
    }
      
    public void readFile(FileData fd) throws IOException
    {
        int numTiles, numLines;
        long numSec;
        
        try{
        	
            numTiles = readInt();
            System.out.println(numTiles);
            numSec = readLong();
            fd.seconds = numSec;
            System.out.println(numSec);
            // loop for reading each tile
            for(int i = 0;i<numTiles;i++){
            	
                fd.locArray[i] = readInt();
                
                fd.rotateArray[i] = readInt();
                
                numLines = readInt();
                // initialize "rows" of 2D array at location i
                fd.TwoDArray[i] = new float[numLines*4];
                
                // fill the ith array of 2D array
                for(int j = 0;j<numLines*4;j++){
                    fd.TwoDArray[i][j] = this.readFloat();
                }
            }
        }finally{
            this.close();
        }
    }

}
