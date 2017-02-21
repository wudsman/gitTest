import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;


public class Time {

	static Timer time = new Timer();
	public static long initialTime = 0;
	public static boolean isRunning = false;
    private static JLabel timeLabel = new JLabel("0", JLabel.LEFT);
    public static long mSeconds = 0;

    public Time() {
    	//start();
    }
    
    
    
    public static UpdateUITask updateUITask()
    {
    	UpdateUITask task = new UpdateUITask();
    	return task;
    }

    public static class UpdateUITask extends TimerTask {

        

       @Override
       public void run() {
    	   timeLabel.setText(String.valueOf(mSeconds++));
        		
        	}
    }
    
    public static void start()
    {
    	time.schedule(updateUITask(), 0, 1000);
    	isRunning = true;
    }
    
    public void reset()
    {
    	mSeconds = initialTime;
    	time.cancel();
    	updateUITask().cancel();
    	isRunning = false;
    	time =  new Timer();
    	timeLabel.setText(String.valueOf(mSeconds++));
    	
    }
  
    
    public void quit()
    {
    	time.cancel();
    	time.purge();
    	updateUITask().cancel();
    	isRunning = false;
    }
    
   
    public long getSeconds()
    {
    	return mSeconds;
    }
    
    public void setTime(long t)
    {
    	mSeconds = t;
    	timeLabel.setText(String.valueOf(mSeconds++));
    	isRunning = false;
    }
    
    public void setInitialTime(long t)
    {
    	initialTime = t;
    	mSeconds = initialTime;
    	timeLabel.setText(String.valueOf(mSeconds++));
    	isRunning = false;
    }
    
    public Timer getTimer()
    {
    	return time;
    }
    
    public JLabel getTimerLabel()
    {
    	return timeLabel;
    }
    
    public boolean isRunning()
    {
    	return isRunning;
    }
    
    public String timeFormat(){
    	long hours;
    	long minutes;
    	
    	
    	minutes = mSeconds / 60;
    	hours = minutes / 60;
    	
    	return (hours + " : " + minutes + " : " + mSeconds);
    	
    }
}
