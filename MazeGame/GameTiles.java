
/**
 * @author Group B
 * Date: 3/23/2016
 * 
 * This class creates the game tiles and places them in the window.
 * Also movement of the game tiles is in this class.
 */

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.util.LinkedList;
//import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

//import GameWindow.GridFlags;

public class GameTiles extends JComponent implements Subject
{
	  /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // temp position for things
    final int[] startingSpotsY = new int[]{34,152,270,388,506,624,742,860};
    final int[] startingSpotsX = new int[]{15,779};
    final int[] gridSpotsY = new int[]{286,386,486,586};
    final int[] gridSpotsX = new int[]{247,347,447,547};
    final Point[] positions = new Point[32]; 
    
    public static boolean[] rightSpot = new boolean[16];
    
    public static Boolean gameWon = false;
    public int magicNumber = 0;
    
    Observer obs;
    long seconds = 0;
    long seconds2 = 0;
  
    
    

    // line class for storing floats
    // will be used to draw lines
    public class Line{
		     float x1; 
		     float y1;
		     float x2;
		     float y2;   

		    public Line(float x1, float y1, float x2, float y2) {
		        this.x1 = x1;
		        this.y1 = y1;
		        this.x2 = x2;
		        this.y2 = y2;

		    }               
		}
	  
        // add a line to the linkedList lines
	  public void addLine(float x1, float x2, float x3, float x4) {
	      Line line = new Line(x1,x2,x3,x4);
	      lines.add(line);
	  }
	  
	  // Perform the math to rotate
	  // the line when drawn on the tile
	  public void rotateLine(Line line) {
		  float tempX = line.x1;
	      line.x1 = (100 - line.y1);
	      line.y1 = tempX;
	      tempX = line.x2;
	      line.x2 = (100 - line.y2);
	      line.y2 = tempX;
	      	      
	  }
	  
	  // take array of floats and read them into line objects
	  public void makeLines(float[] array){
	      for(int i = 0; i < array.length; i += 4){
	          addLine(array[i],array[i+1],array[i+2], array[i+3]);
	      }
	  }
	  
	  // linkedList for storing lines
	  private final LinkedList<Line> lines = new LinkedList<Line>();
	  
	  // Class for storing occupied/empty values for the grid locations
	  GridFlags flags;
	  private Boolean flagCheck;
	  
	  private Point initial= new Point(0,0);
	  private Point position = new Point(0,0); 
	  private Point startPoint = initial;
	  private Point currentPos = initial;
	  private Point previousSpot = initial;
	  private Point currentSpot = initial;
	  private int rotation, initialRotation;
	  private int intLoc;
	  private String name;
	  
	  //Makes the game tiles
	  public GameTiles(final JLabel comp, final int loc, 
			  final float[] array, GridFlags gf, final int r)
	  {
		  name = comp.getText();
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
		  // change cursor to hand cursor
		  setCursor(new Cursor(Cursor.HAND_CURSOR)); 
		  // set location, size, and layout
	      setSize(100,100);
	      setLayout(new BorderLayout());
	      // add JComponent to this object
	      add(comp); 
	      // declare MouseInputAdapter
		  MouseInputAdapter mAdapt = new MouseTracker(); 
		  addMouseMotionListener(mAdapt); 
	      addMouseListener(mAdapt);
	      makeLines(array);
	      // assign GridFlags reference to a local variable
	      flags = gf;
	      
	      // assign intLoc
	      intLoc = loc;
	      
	      // set the starting location of the tiles
	      initial.x = positions[loc].x;
	      initial.y = positions[loc].y;
	      //set current point
	      currentPos = initial;
	      setLocation(initial); 
	     
	      // this will save where the tile was last
	      previousSpot = initial;
		  // Keeps track of the current spot
	      // this is a transition variable
	      currentSpot = initial;
	      // Set the initial rotation
		  initialRotation = r;
	      //Set current rotation
	      rotation=0;
	      // Set FlagCheck to false
	      flagCheck = false;
	      
	      while(rotation != initialRotation){
	    	  rotate();
	      }
		// }
		// else{
		//	 JOptionPane.showInputDialog("GAME WON!!!", "CONGRADULATIONS you won the game.");
		// }
     }
	  
	  

	  // graphics for painting lines
	  public void paint(Graphics g) {
		  super.paint(g);
	      Graphics2D g2 = (Graphics2D) g;
	      
	      g.drawRect (0,0, 100, 100);
	      
	      if(flagCheck){
	    	  g.setColor(Color.RED);
	          flagCheck = false;
	      }
	      else{
		    g.setColor(new Color(255,255,153));		    
	      }
	      // fill rectangle with tan color
		  g.fillRect(0,0, 100, 100);
		  // set color to black for lines
		  g.setColor(Color.BLACK);
		  // draw all the lines
		  paintLines(g2);

		  }
	  
	  public void paintLines(Graphics2D g2)
	  {
		for (Line line : lines) {
			g2.setStroke(new BasicStroke(3));
		        Line2D l = new Line2D.Float(line.x1, line.y1, line.x2, line.y2);
		        g2.draw(l);
		    }  
	  }
	  
	  // takes two integers
      // passes them to GridFlags request 
	  // returns boolean,
	  // if returns true, request granted, place tile
	  // else return tile to initial position
	  public boolean request(int i){
	      return flags.gridRequest(i);
	  }
	  
	  // takes two integers
	  // passes them to GridFlags setFalse()
	  public void setFalse(int i){
	      flags.gridSetFalse(i);
	  }

	  public void erase(){
		  setVisible(false);
	  }
	  
	 // set the rotation 
	  public void rotate(){
		  if(this.rotation != 3){
			  this.rotation += 1;
		  }
		  else
		  {
			  this.rotation = 0;
		  }
		  for (Line line : lines) {
			  rotateLine(line);    
		    }
		  
	  }
	  
	  //Get method for the 
	  //initial position
	  public int getRotation(){
		    return rotation;
	  }
	  
	  //Get method for the 
	  //initial position
	  public int getInitialRotation(){
		    return initialRotation;
	  }
	  
	  //Reset the rotation to initial rotation
	  public void resetRotate(){
		  while(rotation != initialRotation){
	    	  rotate();
	      }
		  repaint();
	  }
	  
	  public Point getCurrent()
	  {
		  return currentPos;
	  }
	  public int lineDataSize(){
		  return (lines.size()*4);
	  }
	  
	  public void lineData(float[] arr)
	  {
		  int offset = 0;
		  for(Line l : lines){
			  arr[offset] = l.x1;
			  offset++;
			  arr[offset] = l.y1;
			  offset++;
			  arr[offset] = l.x2;
			  offset++;
			  arr[offset] = l.y2;
			  offset++;
		  } 
	  }
	  public int getInitLoc()
	  {
		  int i=0;
		  while(!(positions[i].equals(initial)))
		  {
			  i++;
		  }
		  return i;
	  }
	  
	  
	  
	  public int getLoc()
	  {
		  int i=0;
		  while(!(positions[i].equals(currentPos)))
		  {
			  i++;
		  }
		  return i;
	  }
	  
	  public void updateLines(float[] arr)
	  {
		  lines.clear();
		  
		  makeLines(arr);
	  }
	  
	  public void updateLoc(int l)
	  {
		  intLoc = l;
		  setLocation(positions[l]);
	  }
	  
	  public void updateRot(int r)
	  {
		  while(rotation != r)
		  {
			  rotate();
		  }
	  }
	  
	  // some methods for writing data to a file
	  public int rotateData()
	  {
		  return rotation / 90;
	  }
	  
	  public void newStart(final int x, final int y, final int r){
		  setLocation(x, y); 
		  
		  initialRotation = r;
		  resetRotate();
		// set the starting location of the tiles
	      initial.x = x;
	      initial.y = y;
	      // set the new start where the tile was last
	      previousSpot = initial;
		  // Keeps track of the current spot
	      // this is a transition variable
	      currentSpot = initial;
	      //set current point
	      setCurrent(initial);
	      setVisible(true);
	      repaint();
	  }
	  
	  public void newPlayed(final int loc, final int r){
		  setLocation(positions[loc]); 
		  
		  initialRotation = r;
		  resetRotate();
		// set the starting location of the tiles
	      initial.x = positions[loc].x;
	      initial.y = positions[loc].y;
	      // set the new start where the tile was last
	      previousSpot = initial;
		  // Keeps track of the current spot
	      // this is a transition variable
	      currentSpot = initial;
	      //set current point
	      setCurrent(initial);
	      setVisible(true);
	      repaint();
	  }
	  
	  
		  
	 //Get method for the 
	 //initial position
	  public Point getInitial()
	  {
		  return initial;
	  }
	  
	  //Set method for current
	  public void setCurrent(Point x)
	  {
	      currentPos = x;
	      currentSpot = x;
	  }
	  //Get method for the 
	  //initial position
	  public void setPrevious(Point x)
	  {
		  previousSpot = x;
	  }
	  
	  // set the position variable
	  public void setPosition(int x, int y)
	  {
		  position.x = x;
		  position.y = y;
	  }
	  public void pritGrid(){
		  flags.printGrid();
	  }
	  
	  public void paintThis()
	  {
		  repaint();
	  }
	  
	  // test
	  boolean dragging = false;
	   //Keeps track of mouse when clicking and dragging 
	   private class MouseTracker extends MouseInputAdapter
	   {
		   
		   //Mouse click tracker
		   @Override 
		   public void mousePressed(final MouseEvent e)
		   {
			   if( SwingUtilities.isRightMouseButton(e))
			   {}
			   else if( SwingUtilities.isMiddleMouseButton(e))
			   {}
			   else if( SwingUtilities.isLeftMouseButton(e)) 
			   {
				  ((JComponent) getParent()).setComponentZOrder(GameTiles.this, 0); 
				  startPoint = e.getPoint();
			   }// end if( SwingUtilities.isLeftMouseButton(e))
			   
		   }
		   
		   // Click tracker for rotation
		    @Override
		   public void mouseClicked(final MouseEvent e)
		   {
		    	if( SwingUtilities.isRightMouseButton(e)){}
				   else if( SwingUtilities.isMiddleMouseButton(e)){}
				   else if( SwingUtilities.isLeftMouseButton(e))
			 	{
				   flagCheck = false;
				   	// rotate the tile
					   rotate();
					   System.out.println("New Rotation:) " + getRotation() + "");
					   System.out.println("Tile Name: " + name);
					   checkTile();
					   paintThis();
					   //System.out.print(getLocation());
			 	}
		   }
		
		
		   //Dragging tracker
		   @Override 
		   public void mouseDragged(final MouseEvent e) 
		   {
			   dragging = true;
			   if( SwingUtilities.isRightMouseButton(e)){}
			   else if( SwingUtilities.isMiddleMouseButton(e)){}
			   else if( SwingUtilities.isLeftMouseButton(e)) 
			   {
				   int startX = startPoint.x;
				   int startY = startPoint.y;
				   
				   Point pointStart = getParent().getLocationOnScreen();
				   Point pointDragged = e.getLocationOnScreen();
				   position.x = (pointDragged.x - pointStart.x -startX); 
				   position.y = (pointDragged.y - pointStart.y -startY);
				   setLocation(position);
			   }
			   
		   }
		   
		   
		   @Override
		   public void mouseReleased(final MouseEvent e)
		   {
			   if (dragging){
			  
			   // get current location
			   // Point object used for new location   
			   Point newloc = new Point(0,0);
			   // temp var
			   int index = 0;
			   for(int i =0;i<2;i++){
		        	  for(int j =0;j<8;j++){
		        		if((Math.abs(position.x - positions[j+i*8].x) <= 50) && (Math.abs(position.y - positions[j+i*8].y) <=50)){
		        			newloc = positions[j+i*8];
		        			index = j+i*8;
		        		}
		        	  }
		            }
		        	for(int i =0;i<4;i++){
		          	  for(int j =0;j<4;j++){
		          		if((Math.abs(position.x - positions[16+(j+i*4)].x) <= 50) && (Math.abs(position.y - positions[16+(j+i*4)].y) <=50)){
		        			newloc = positions[16+(j+i*4)];
		        			index = 16+(j+i*4);
		        		}
		          	  }
		            }
				   
			   if( SwingUtilities.isLeftMouseButton(e)) 
			   {
				   // if newloc coordinates were updated, call request to GridFlags

				   if(newloc.x != 0 && newloc.y != 0){
					   if (request(index)){
						   // request granted, set new location
						   setLocation(newloc);
						   // Set the new spot as the current
						   // spot
						   currentPos = newloc;  
						   currentSpot = currentPos;
						   for( int i = 0; i<32;i++){
							   if(previousSpot.x == positions[i].x && previousSpot.y == positions[i].y){
								   setFalse(i);
								   System.out.println("Did it!");
							   }
						   }
						   previousSpot = currentSpot;
						   
						   
						   System.out.println("newlock");
						   System.out.println("Initial Position: " + getInitLoc());
						   System.out.println("Position: " + getLoc() + "");
						   System.out.println("Rotation:) " + getRotation() + "");
						   System.out.println("Tile Name: " + name);
						   
						   checkTile();
						   	   
					   }
		           else{
		               // request denied, return to previous location location
		        	   currentSpot = previousSpot;		               
		               currentPos = previousSpot;
		               setLocation(previousSpot);
		               flagCheck = true;
		        
		               int delay = 1000; //milliseconds
		               ActionListener taskPerformer = new ActionListener() {
		                   public void actionPerformed(ActionEvent evt) {
		                       repaint();
		                   }
		                   
		               };
		               System.out.println("red");
		               new Timer(delay, taskPerformer).start();
		           }
		       } // end if(newloc.x != 0 && newloc.y != 0)
               else
               {
                   // tile not in snap-to area, return to previous location
                   setLocation(previousSpot);
                   currentPos = previousSpot;
                   currentSpot = previousSpot;
                  // System.out.println("noSnap");
               }
				   dragging = false;
			  }// end if(SwingUtilities.isLeftMouseButton(e))
			 }// end dragging
				if(Time.isRunning == false)
				{
					Time.start();
				}
		   }// end mouseReleased
		   
	   }// end class mouseTracker

	   private static boolean allTrue( boolean[] rightSpot)
	    {
		    for (boolean value : rightSpot) {
		        if (!value)
		            return false;
		    }
		    return true;
		}

	   public  void checkTile(){
		   if(allTrue(rightSpot) == true){
			   System.out.println("YOU WIN THE GAME!!");
		   }
		   else{
			   
		   if(name.equals("p0") ){
			   if(name.equals("p0") && getLoc() == 16 && getRotation() == 0){
				
				   System.out.println(" Tile is in the right spot");
				   rightSpot[0] = true;
			   }
			   else if((getLoc() != 16)){
				   System.out.println("Not in the right spot!");
				   rightSpot[0] = false;
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[0] = false;						   
			   }
		   }
		   else if(name.equals("p1")){ 
			   if(name.equals("p1") && getLoc() == 20  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[1] = true;
			   }
			   else if((getLoc() != 20)){
				   System.out.println("Not in the right spot!");
				   rightSpot[1] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[1] = false;							   
			   }
		   }
		   else if( name.equals("p2")){ 
			   if(name.equals("p2") && getLoc() == 24  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[2] = true;
			   }
			   else if((getLoc() != 24)){
				   System.out.println("Not in the right spot!");
				   rightSpot[2] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[2] = false;							   
			   }
		   }
		   else if (name.equals("p3")){ 
			   if(name.equals("p3") && getLoc() == 28  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[3] = true;
			   }
			   else if((getLoc() != 28)){
				   System.out.println("Not in the right spot!");
				   rightSpot[3] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[3] = false;							   
			   }
		   }
		   else if ( name.equals("p4")){ 
			   if(name.equals("p4") && getLoc() == 17  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[4] = true;
			   }
			   else if((getLoc() != 17)){
				   System.out.println("Not in the right spot!");
				   rightSpot[4] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[4] = false;							   
			   }
		   }
		   else if ( name.equals("p5")){ 
			   if(name.equals("p5") && getLoc() == 21  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[5] = true;
			   }
			   else if((getLoc() != 21)){
				   System.out.println("Not in the right spot!");
				   rightSpot[5] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[5] = false;							   
			   }
		   }
		   else if ( name.equals("p6")){ 
			   if(name.equals("p6") && getLoc() == 25  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[6] = true;
			   }
			   else if((getLoc() != 25)){
				   System.out.println("Not in the right spot!");
				   rightSpot[6] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[6] = false;							   
			   }
		   }
		   else if (name.equals("p7")) {
			   if(name.equals("p7") && getLoc() == 29  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[7] = true;
			   }
			   else if((getLoc() != 29)){
				   System.out.println("Not in the right spot!");
				   rightSpot[7] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[7] = false;							   
			   }
		   }
		   else if ( name.equals("p8")){ 
			   if(name.equals("p8") && getLoc() == 18  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[8] = true;
			   }
			   else if((getLoc() != 18)){
				   System.out.println("Not in the right spot!");
				   rightSpot[8] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[8] = false;
			   }
		   }
		   else if( name.equals("p9")){ 
			   if(name.equals("p9") && getLoc() == 22  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[9] = true;
			   }
			   else if((getLoc() != 22)){
				   System.out.println("Not in the right spot!");
				   rightSpot[9] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[9] = false;
			   }
		   }
		   else if ( name.equals("p10")){ 
			   if(name.equals("p10") && getLoc() == 26  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[10] = true;
			   }
			   else if((getLoc() != 26)){
				   System.out.println("Not in the right spot!");
				   rightSpot[10] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[10] = false;
			   }
		   }
		   else if(name.equals("p11")){ 
			   if(name.equals("p11") && getLoc() == 30  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[11] = true;
			   }
			   else if((getLoc() != 30)){
				   System.out.println("Not in the right spot!");
				   rightSpot[11] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[11] = false;
			   }
		   }
		   else if( name.equals("p12")){ 
			   if(name.equals("p12") && getLoc() == 19  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[12] = true;
			   }
			   else if((getLoc() != 19)){
				   System.out.println("Not in the right spot!");
				   rightSpot[12] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[12] = false;
			   }
		   }
		   else if( name.equals("p13")){ 
			   if(name.equals("p13") && getLoc() == 23  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[13] = true;
			   }
			   else if((getLoc() != 23)){
				   System.out.println("Not in the right spot!");
				   rightSpot[13] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[13] = false;
			   }
		   }
		   else if (name.equals("p14")){ 
			   if(name.equals("p14") && getLoc() == 27  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[14] = true;
			   }
			   else if((getLoc() != 27)){
				   System.out.println("Not in the right spot!");
				   rightSpot[14] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[14] = false;
			   }
		   }
		   else if(name.equals("p15")){ 
			   if(name.equals("p15") && getLoc() == 31  && getRotation() == 0){
				   System.out.println(" Tile is in the right spot");
				   rightSpot[15] = true;
			   }
			   else if((getLoc() != 31)){
				   System.out.println("Not in the right spot!");
				   rightSpot[15] = false;						   
			   }
			   else if((getRotation() != 0)){
				   System.out.println("Not the correct rotation!");
				   rightSpot[15] = false;
		   }
		   }
		   }
		   
		   for(int i=0; i < rightSpot.length; i++ ){
			   System.out.println("Tile[ " + i + "] is set to " + rightSpot[i]);
		   }

		   if(allTrue(rightSpot) == true){

			   obs.winTest();
 	        	}
 	        	
 	        	
			   gameWon = true;
			   GameWindow.won = true;
		   }

		   
	   
	 
	   public static void resetSpots(){
		   //reset all values to false
		    rightSpot = new boolean[16];
		    gameWon = false;
	   }
	  /*
	   public static boolean didTheyWin(){
		   return gameWon;
	   }
	   */

	@Override
	public void addObserver(Observer o) {
		// TODO Auto-generated method stub
		obs = o;
	}

	@Override
	public void removeObserver(Observer o) {
		// TODO Auto-generated method stub
		obs = null;
	}
	   
	};


