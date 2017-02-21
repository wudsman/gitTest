/**
 * @author Group B
 * Date: 3/23/2016
 *
 * This is the actual "game". May/will have to make some major changes.
 * This is just a "hollow" shell.
 *
 * When you get done, I should see the buttons at the top in the "play" area
 * (not a pull-down menu). The only one that should do anything is Quit.
 *
 * Should also see something that shows where the 4x4 board and the "spare"
 * tiles will be when we get them stuffed in.
 */
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

//import GameTiles.Line;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.Random;


public class GameWindow extends JFrame implements ActionListener, Observer
  {
    /**
     * because it is a serializable object, need this or javac
     * complains a lot
     */
    public static final long serialVersionUID=1;
    
    public String messenger;
    public static boolean won = false;
    
    /**********************************************************************************
    File Chooser and filter
    **********************************************************************************/
    public JFileChooser loadGame= new JFileChooser();
    public FileNameExtensionFilter mazeFilter =new FileNameExtensionFilter("maze game files", "mze");
    public File workingDir =new File(System.getProperty("user.dir"));
    /*********************************************************************************
    **********************************************************************************/
    
    // create an array for the GameTiles
    private  GameTiles[] tiles = new GameTiles[16]; 
    
    //public Boolean gameWon = false;
    
    // create GridFlags variable
    GridFlags gridFlags;
    
    final int[] startingSpotsY = new int[]{34,152,270,388,506,624,742,860};
    final int[] startingSpotsX = new int[]{15,779};
    final int[] gridSpotsY = new int[]{286,386,486,586};
    final int[] gridSpotsX = new int[]{247,347,447,547};
    final Point[] positions = new Point[32]; 
    
    JPopupMenu menu = new JPopupMenu();
    
    Time time = new Time();
    /**
     * Constructor sets the window name using super(), changes the layout,
     * which you really need to read up on, and maybe you can see why I chose
     * this one.
     *
     * @param s
     */
    
    public void setPositions()
    {
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
    public GameWindow(String s)
    {
      super(s);
      GridBagLayout gbl=new GridBagLayout();
      setLayout(gbl);
      gridFlags = GridFlags.instance();
      setPositions();
  
    }
    
    //Create X & Y arrays for
    //starting positions
    final int[] spotsY = new int[]{34,152,270,388,506,624,742,860};
    final int[] spotsX = new int[]{15,779};
    
    final int gameX = 247;
    final int gameY = 286;
    final int[] rotations = new int[]{0,0,0,0,1,1,1,1,2,
    		2,2,2,3,3,3,3};
    
    //Create a list of strings and path names for
    //the tiles
    final String[] tileID = new String[]{"p0","p1","p2","p3","p4","p5",
  		  "p6","p7","p8","p9","p10","p11","p12","p13","p14","p15"};
    JPanel panelGlass;
    
    
    
    //This class is used as the background
    //canvas for us to draw our game board
    //and starting tile locations on
    class backGround extends JPanel {
    	
    /**
	* because it is a serializable object, need this or javac
    * complains a lot
	*/
		private static final long serialVersionUID = 1L;

		
		
		//This method is used to paint background tiles
		//for the starting locations and game board
		@Override
		public void paint(Graphics g) {
 
			//Draw the background tiles for the 
			//starting locations
			for (int i = 0; i < spotsX.length;i++ )
			{
				for(int j = 0; j < spotsY.length; j++ )
				{
					g.setColor(Color.lightGray);
					g.drawRect(spotsX[i], spotsY[j], 99, 99);
					g.setColor(Color.gray);
					g.fillRect(spotsX[i]+1, spotsY[j]+1, 98, 98);
				}
			}
          
			//Draw the game board
			for (int i = 0; i < 4;i++ )
			{  
				for(int j = 0; j < 4; j++ )
				{
					g.setColor(Color.lightGray);
					g.drawRect(gameX+(i*100), gameY+(j*100), 99, 99);
					g.setColor(Color.gray);
					g.fillRect(gameX+(i*100)+1, gameY+(j*100)+1, 98, 98);
				}

			}
		}
	}

    /**
     *  Establishes the initial board
     * @throws IOException 
     */
    
    // creates gameTile, takes FileData object and lpanel object
    public void createTiles(FileData fd,JPanel p)
    {
    	
    	//Add the game pieces to the board and 
        //then store them in an array.
        for (int i = 0; i < spotsX.length;i++)
        {
      	  for(int j = 0; j < spotsY.length; j++)
      	  {
      	      float[] arr = fd.TwoDArray[j+i*8];    
      	      GameTiles tile = new GameTiles(new JLabel(tileID[j+(i*8)]),
      	    		  fd.locArray[j+i*8],
      	    		  arr,
      	    		  gridFlags, 
      	    		  fd.rotateArray[j + (i*8)]);

      	      p.add(tile);
      	      tiles[j+(i*8)] = tile;
      	      
      	    tiles[j+(i*8)].addObserver(this);
      	    
      	  }
        } 
        newGame(fd);
    }
    
    public void updateTiles(FileData fd)
    {
    	int i =0;
    	for(GameTiles g: tiles)
    	{
    		g.updateLines(fd.TwoDArray[i]);
    		g.updateLoc(fd.locArray[i]);
    		g.updateRot(fd.rotateArray[i]);
    		i++;
    	}
    }
    
    public void setUp() throws IOException
    {
      // Need to play around with the dimensions and the grid x/y values
      // These constraints are going to be added to the pieces/parts I 
      // stuff into the "GridBag".
      
    	
      GridBagConstraints c = new GridBagConstraints();
      
      //Add 
      JPanel toolBar = new JPanel();
      toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
      toolBar.setAlignmentX(Component.LEFT_ALIGNMENT);
      toolBar.setBackground(Color.lightGray);
      c.fill = GridBagConstraints.HORIZONTAL;
      c.weightx = .5;
      c.weighty = 10;
      c.anchor = GridBagConstraints.NORTH;
      c.gridheight = 2;
      c.gridwidth = 3;
      c.gridx = 0;
      c.gridy = 0;
      this.add(toolBar, c);

      //New Game button
      JButton button1 = new JButton("File");
      button1.setActionCommand("file");
      button1.setEnabled(true);
      button1.addActionListener(this);
      
      //Reset Button
      JButton button2 = new JButton("Reset");
      button2.setActionCommand("reset");
      button2.setEnabled(true);
      button2.addActionListener(this);
      
      //Quit Button
      JButton button3 = new JButton("Quit");
      button3.setActionCommand("exit");
      button3.setEnabled(true);
      button3.addActionListener(this);
      
      JLabel timer = new JLabel("   " + " Time :  ");
      timer.setEnabled(true);
      
      //Add the buttons
      toolBar.add(button1);
      toolBar.add(button2);
      toolBar.add(button3);
      toolBar.add(timer);
      
      //Timer

     
      if(time.isRunning() == false)
      {
    	  toolBar.add(time.getTimerLabel());
      }
      
      
      JMenuItem load = new JMenuItem("Load");
      load.setHorizontalTextPosition(JMenuItem.CENTER);
      load.setActionCommand("load");
      load.addActionListener(this);
      menu.addSeparator();
      
      
      JMenuItem save = new JMenuItem("Save");
      save.setHorizontalTextPosition(JMenuItem.CENTER);
      save.setActionCommand("save");
      save.addActionListener(this);
      
      
      menu.add(load);
      menu.add(save);
      
      menu.setBorder(new BevelBorder(BevelBorder.RAISED));
      menu.setPopupSize(200, 80);
      
      
      //Create the background canvas
      //for the gameboard and starting 
      //locations, and apply the appropriate 
      //gridbag constraints
      backGround panel2 = new backGround();
      panel2.setBackground(Color.red);
      c.fill = GridBagConstraints.BOTH;
      c.anchor = GridBagConstraints.CENTER;
      c.gridx = 1;
      c.gridy = 1;
      c.ipadx = 0;
      c.ipady = 0;
      this.add(panel2, c);
      panel2.setOpaque(false);
      
      //Set the panel for our game pieces
      panelGlass = new JPanel();      
      panelGlass.setLayout(null);
      this.setGlassPane(panelGlass);
      
      
      // read data from file
      FileReader reader = new FileReader("default.mze");
      
      FileData fd = new FileData();
      
      // read data
      reader.readMagicNumber(fd);
      reader.readFile(fd);
      
      //close the file
      reader.close();
      
      
      // create game tiles
      createTiles(fd,panelGlass);
      
   
      
      //Set the glassPanel to be invisible
      //otherwise it overwrites the
      //background panel
      panelGlass.setOpaque(false);
      panelGlass.setVisible(true);

      return;      
      
    }
    
    
    // Create a new game with new starting 
    // positions
    public void newRandGame()
    {
    	
    	shuffleArray(tiles);
    	shuffleRotation(rotations);
    
    	for (int i = 0; i < spotsX.length;i++ )
        {
      	  for(int j = 0; j < spotsY.length; j++ )
      	  {
      		tiles[j+(i*8)].newStart(spotsX[i], spotsY[j], rotations[j+(i*8)]);
      	  }
      	}
    	
    }
    
    public void newGame(FileData fd)
    {
    	
    	for (int i = 0; i < 16;i++ )
        {
      	 
    		tiles[i].newPlayed(fd.locArray[i], fd.rotateArray[i]);
      	  
      	}
    	
    }
    
    // Shuffle the array storing the GameTiles
    private void shuffleArray(GameTiles[] list)
    {
        int index; 
        GameTiles temp;
        Random random = new Random();
        for (int i = list.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = list[index];
            list[index] = list[i];
            list[i] = temp;
        }
    }

    public void blankTiles(){
    	for(GameTiles g : tiles){
    		g.erase();
    	}
    }
   
    //Shuffle the starting rotations
    private void shuffleRotation(int[] list)
    {
        int index; 
        int temp;
        Random random = new Random();
        for (int i = list.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = list[index];
            list[index] = list[i];
            list[i] = temp;
        }
    }
    
    public void writeFileData(FileData fd)
    {
    	
    	fd.hex = 0xCAFEDEED;
    	for(int i = 0; i< tiles.length;i++)
    	{
    		int s = tiles[i].lineDataSize();
    		
    		float arr[] = new float [s];
    		tiles[i].lineData(arr);
    		
    		fd.TwoDArray[i] = arr;
    		fd.locArray[i] = tiles[i].getLoc();
    		fd.rotateArray[i]=tiles[i].getRotation();
    	}
    }
/**
     * For the buttons
     * @param e is the ActionEvent
     * 
     * BTW can ask the event for the name of the object generating event.
     * The odd syntax for non-java people is that "exit" for instance is
     * converted to a String object, then that object's equals() method is
     * called.
     */
    public void actionPerformed(ActionEvent e) {
    	loadGame.setFileFilter(mazeFilter);
    	loadGame.setCurrentDirectory(workingDir);
    	
    	//Exit button
        //closes the game and halts the 
    	//program
        if("exit".equals(e.getActionCommand()))
        {
        	for(GameTiles i: tiles)
	      	  {
        		if(i.getCurrent() != i.getInitial())
        		{
      			
  	        	int reply = JOptionPane.showConfirmDialog(null,"Would you like to save?", "Save", JOptionPane.YES_NO_OPTION);
  	        	if(reply == JOptionPane.YES_OPTION)
  	        	{
  	      	        	fileSaveReader();
  	      	    }
  	        	/*
  	        	 
  	        	 */
  	        	System.exit(0);
      		}
	      		  
	      	  }
      	System.exit(0);
        }
        
        //Reset button
        //resets the flags for the board
        //sets the current back to initial position
        // and set the location to initial 
        if("reset".equals(e.getActionCommand())){                	  
        	gridFlags.gridSetAllFalse();
        	time.reset();
      	  for(GameTiles i: tiles)
      	  {
      		  i.setLocation(i.getInitial());
      		  i.setCurrent(i.getInitial());
      		  i.setPrevious(i.getInitial());
      		  i.resetRotate();
      		  gridFlags.resetFlags(i.getInitial());
      		  i.checkTile();
      		  repaint();
      		  
      		  
      	  }
        }
        
        //New game button
        //currently does nothing
        //New game button
        //currently does nothing
        if("file".equals(e.getActionCommand())){
        	menu.show(GameWindow.this, 0,64);
        }
        
        if("load".equals(e.getActionCommand()))
        {
	        	fileLoadReader();
	        	//GameTiles.checkTile();
	        	  for(GameTiles i: tiles)
	          	  {
	          		i.checkTile();
	          		  
	          	  }
        }
        if("save".equals(e.getActionCommand()))
        {
        	Time.isRunning = false;
        	fileSaveReader();
        
        }
    }
    
    
    
    public void fileSaveReader() {
    	System.out.println("save");
    	String gameName=null;
    	int saveVal=loadGame.showSaveDialog(GameWindow.this);
        if(saveVal==JFileChooser.APPROVE_OPTION)
        {
        	gameName=(loadGame.getSelectedFile().getName());
        	workingDir=loadGame.getCurrentDirectory();
        	loadGame.setCurrentDirectory(workingDir);        	        
        if(gameName.endsWith(".mze")==true)
        {
        }
        else
        {
        	gameName += ".mze";
        }
    	File f = new File(gameName);        	
    	if(f.exists()){
    	    if(f.delete() == true){
    	    }
    	}
    	try {
    		//f.createNewFile();
    		FileData fData = new FileData();
    		writeFileData(fData);
    		FileWriter writer = new FileWriter(workingDir+"\\"+gameName);
    		writer.writeFile(fData);
    		writer.close();
    		//f1.createNewFile();

    	} catch ( IOException e1) {
    		// TODO Auto-generated catch block
    		//e1.printStackTrace();
    	}
    	System.out.println("GameName is: " + gameName);
        }
    }
    
    
    
    public void fileLoadReader() {
    	String gameName= "default.mze";
    	int loadVal=loadGame.showOpenDialog(GameWindow.this);
    	
    	if(loadVal==JFileChooser.APPROVE_OPTION)
    	{
    		gameName=(loadGame.getSelectedFile().getName());
    		workingDir=loadGame.getCurrentDirectory();
    	
        if(gameName.endsWith(".mze")==true)
        {
        }
        else
        {
        	gameName += ".mze";
        }
    	//File f = new File("saveData/" + gameName);
    	 
    	//if(f.exists()){
    		System.out.println("File loaded");
    		
    		// read data from file
    	      FileReader reader = null;
    		try {
    			reader = new FileReader(workingDir+"\\"+gameName);
    		} catch (FileNotFoundException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    			JOptionPane.showMessageDialog(null, "no file found, cannot load it");
    			fileLoadReader();
    			return;
    		}
    	      
    	      FileData fd = new FileData();
    	      
    	      // read data
    	      try {
    			reader.readMagicNumber(fd);
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    	      
    	     	
    	   // test hex to find out if this is a played game
    	      if(fd.hex == 0xCAFEBEEF)// new unplayed game
    	      {
    	    	  try {
    	    			reader.readFile(fd);
    	    		} catch (IOException e1) {
    	    			// TODO Auto-generated catch block
    	    			e1.printStackTrace();
    	    		}
    	    	  newRandGame();
    	    	  gridFlags.gridSetAllFalse();
    	    	  for(GameTiles i: tiles)
    	      	  {
    	    		  i.setLocation(i.getInitial());
    	    		  gridFlags.resetFlags(i.getInitial());
    	      	  }
    	    	  
    	    	  time.setInitialTime(fd.seconds);
    	    	  
    	      }
    	      else if(fd.hex == 0xCAFEDEED) // load played game
    	      {
    	    	  try {
    	    			reader.readFile(fd);
    	    		} catch (IOException e1) {
    	    			// TODO Auto-generated catch block
    	    			e1.printStackTrace();
    	    		}
    	    	  newGame(fd);
    	    	  gridFlags.gridSetAllFalse();
    	    	  for(GameTiles i: tiles)
    	      	  {
    	    		  gridFlags.resetFlags(i.getCurrent());
    	      	  }
    	    	  
    	    	  time.setInitialTime(fd.seconds);
    	    	 
    	      }

    	else{
    		blankTiles();
    	    final JPanel panel = new JPanel();
    	    JOptionPane.showMessageDialog(panel, "Invalid file format", "Error", JOptionPane.ERROR_MESSAGE);
    	    
    	}
    	      try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} 
    	
    	
    }
	@Override
	public void winTest() {
		// TODO Auto-generated method stub
		   int reply = JOptionPane.showConfirmDialog(null,"YOU BEAT THE MAZE IN " + time.timeFormat() + "\n Would you like to play again?", "Congradulations!!!" , JOptionPane.YES_NO_OPTION);
        	if(reply == JOptionPane.YES_OPTION){
        		GameTiles.resetSpots();
        		newRandGame();
  	    	  gridFlags.gridSetAllFalse();
  	    	  for(GameTiles i: tiles)
            	  {
  	    		  i.setLocation(i.getInitial());
  	    		  gridFlags.resetFlags(i.getInitial());
            	  }
        		//GameTiles.resetSpots();
        		//System.exit(0);
        	}
        	else{
        		System.exit(0);
        	}
       
	}
	@Override
	public void didTheyWin() {
		// TODO Auto-generated method stub
		
	}
    
    /*
    public void winningOptions(){
    	if(GameTiles.didTheyWin()){
			   System.out.println("YOU WIN THE GAME!!");
			   int reply = JOptionPane.showConfirmDialog(null,"Would you like to play again?", "YOU WON!", JOptionPane.YES_NO_OPTION);
	        	if(reply == JOptionPane.YES_OPTION){
	        		//GameWindow.newRandGame();
	        		System.exit(0);
	        	}
	        	else{
	        		System.exit(0);
	        	}
    	}
    }
   */
    
    
  };
