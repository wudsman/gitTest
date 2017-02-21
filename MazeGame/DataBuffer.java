import java.math.BigInteger;
import java.nio.ByteBuffer;

public class DataBuffer {
    
    // takes a byte array (should be of size four) 
    // uses ByteBuffer to get an integer from byte array
    public static int convertToInt(byte[] b){
        int returnInt;
        ByteBuffer buffer = ByteBuffer.wrap(b);
        returnInt = buffer.getInt();
        return returnInt;
    }
    
    // takes a byte array (should be of size four) 
    // uses ByteBuffer to get an float from byte array
    public static float convertToFloat(byte[] b){
        float returnFloat;
        ByteBuffer buffer = ByteBuffer.wrap(b);
        returnFloat = buffer.getFloat();
        return returnFloat;
    }
    
    // takes a byte array (should be of size four) 
    // uses ByteBuffer to get an long int from byte array
    public static Long convertToLong(byte[] b){
        Long returnLong;
        ByteBuffer buffer = ByteBuffer.wrap(b);
        returnLong = buffer.getLong();
        return returnLong;
    }
    
    public static byte[] intToByte(int in){
    	byte b[] = ByteBuffer.allocate(4).putInt(in).array();
    	return b;
    }
    
    public static byte[] floatToByte(float f){
    	byte b[] = ByteBuffer.allocate(4).putFloat(f).array();
    	return b;
    }
    
    public static byte[] longToByte(long f){
    	byte b[] = ByteBuffer.allocate(8).putLong(f).array();
    	return b;
    }
    
/*
 *     public static byte[] hexToByte(String hex){
    	byte[] b = new BigInteger(hex,16).toByteArray();
    	return b;
    }*/
}
