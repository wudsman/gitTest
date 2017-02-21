import java.awt.Point;
import java.util.Arrays;


// this class is used to set flags for the gameboard 
// class is Singleton pattern
// the flags represent whether or not a space is occupied
// true = occupied
// false = empty

public class GridFlags{
	final int[] startingSpotsY = new int[]{34,152,270,388,506,624,742,860};
    final int[] startingSpotsX = new int[]{15,779};
    final int[] gridSpotsY = new int[]{286,386,486,586};
    final int[] gridSpotsX = new int[]{247,347,447,547};
    final Point[] positions = new Point[32]; 
    
    
    
    

        // instance of GridFlags
        private final static GridFlags instance = new GridFlags();
        
        // 2DArray of boolean values
        // column-major representation of gameboard grid
        private boolean gridFlags[];
        
        // constructor
        // sets all values in gridFlags to false
        private GridFlags(){
        	
        	// the grid array
            gridFlags = new boolean [32];
            for(int i =0; i<32;i++){
                if (i < 16){
                	gridFlags[i] = true;
                }
                else{
                	gridFlags[i] = false;
                }
            }
            for(int i =0;i<2;i++){
      	  	  for(int j =0;j<8;j++){
      	  		positions[j+i*8] = new Point(startingSpotsX[i],startingSpotsY[j]);
      	  	   }
      	      }
      	  	for(int i =0;i<4;i++){
      	    	  for(int j =0;j<4;j++){
      	    		positions[16+(j+i*4)] = new Point(gridSpotsX[i],gridSpotsY[j]);
      	    	  }
      	      }
        }
        
        // method for creating single instance of GridFlags
        public static GridFlags instance(){
            return instance;
        }
        
        // called by GameTile
        // takes two integers to represent the location ont the grid
        // the GameTile "requests" allowance to occupy location (l,m) in the gameboard grid
        
        public boolean gridRequest(int i){
            boolean granted = false;
            // if requested location is unoccupied
            if(gridFlags[i] == false){
                // set return value to true
                granted = true;
                // set location to occupied
                gridSetTrue(i);
            }
            return granted;
        }
        // access a location in the 
        // position array
        public Point spots(int i){
        	return positions[i];
        }
       // print the grid
        public void printGrid()
        {
        	for(int i =0; i<32;i++){
        		System.out.println(gridFlags[i]);
        	}
        }
        // set flag at location (l,m) to true (occupied)
        public void gridSetTrue(int i){
            gridFlags[i] = true;
            System.out.println("Set: " +i);
          //  System.out.println("Tile: " ;
        }
        
 
        
        // set flag at location (l,m) to false (empty)
        public void gridSetFalse(int i){
            gridFlags[i] = false;
        }
        
        // set all flags to false (empty)
        public void gridSetAllFalse(){
            for(int i =0; i<32;i++){
                Arrays.fill(gridFlags, false);
            }
        }
        
        // set the flags to the starting 
        // new game position
        public void resetFlags(Point k){
        	for(int i =0; i<32;i++){
              if(k.x == positions[i].x && k.y == positions[i].y){
            	  gridFlags[i] = true;
              }
            }
        }
        
    }
