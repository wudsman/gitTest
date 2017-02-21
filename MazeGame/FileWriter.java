
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FileWriter extends FileOutputStream {

    // constructor for FileReader
    public FileWriter(String name) throws FileNotFoundException {
        super(name);
    }
    
    // reads four bytes from the filestream, 
    // catches end of file exception and closes file
    // converts bytes to int using DataBuffer
    public void writeInt(int in) throws IOException{
    	// convert int to bytes
        byte[] bytes = new byte[4];
        bytes = DataBuffer.intToByte(in);
        try{
            this.write(bytes);
        }catch(IOException e){
            this.close();
            System.out.println("blew it");
        }  
    } 
    
 // reads four bytes from the filestream, 
    // catches end of file exception and closes file
    // converts bytes to float using DataBuffer
    public void writeFloat(float f) throws IOException{
    	// convert float to bytes
        byte[] bytes = new byte[4]; 
        bytes = DataBuffer.floatToByte(f);
        try{
            this.write(bytes);
        }catch(IOException e){
            this.close();
            System.out.println("blew it");
        }  
    }
    
    // reads eight bytes from the filestream, 
    // catches end of file exception and closes file
    // converts bytes to float using DataBuffer
    public void writeLong(long f) throws IOException{
    	// convert float to bytes
        byte[] bytes = new byte[8]; 
        bytes = DataBuffer.longToByte(f);
        try{
            this.write(bytes);
        }catch(IOException e){
            this.close();
            System.out.println("blew it");
        }  
    }
    
    // read file using prescribed format
    // place data in a 2D array of floats
    //  return float[][]
    public void writeFile(FileData fd) throws IOException
    {
        int  numEntries; // numEntries is the number of lines * 4 for a tile
        
        try{
        	writeInt(fd.hex);
        	System.out.println(fd.hex);
            writeInt(16); 
            System.out.println(16);
            writeLong(fd.seconds);
            // loop for writing each tile
            for(int i = 0;i<16;i++){
            	System.out.println("this is run " + i);
            	
                writeInt(fd.locArray[i]);
                
                writeInt(fd.rotateArray[i]); 
                
                numEntries= fd.TwoDArray[i].length / 4;
                
                writeInt(numEntries);
                
                System.out.println(numEntries);
                // fill the ith array of 2D array
                for(int j = 0;j<numEntries * 4;j++){
                    writeFloat(fd.TwoDArray[i][j]);
                    System.out.println(j);
                }
            }
        }finally{
            this.close();
        }
    }

}