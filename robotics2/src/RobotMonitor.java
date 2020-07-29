import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;

// PillotMonitor.java
// 
// Based on the RobotMonitor class, this displays the robot
// state on the LCD screen; however, it works with the PilotRobot
// class that exploits a MovePilot to control the Robot.
//
// Terry Payne
// 1st October 2018
//

public class RobotMonitor extends Thread {
	private int delay;
	public PilotRobot me;
	private String msg;
	private static Brick myEV3 = BrickFinder.getDefault();
	int x;
	int y;
	int cellSize = 20;
	
    GraphicsLCD lcd = LocalEV3.get().getGraphicsLCD();
	
    // Make the monitor a daemon and set
    // the robot it monitors and the delay
    public RobotMonitor(PilotRobot r, int d){
    	this.setDaemon(true);
    	delay = d;
    	me = r;
    	msg = "";
    }
    
    // Allow extra messages to be displayed
    public void resetMessage() {
    	this.setMessage("");
    }
    
    // Clear the message that is displayed
    public void setMessage(String str) {
    	msg = str;
    }

    // The monitor writes various bits of robot state to the screen, then
    // sleeps.
    public void run(){

    	while(true) {
    		lcd.clear();
			lcd.drawString("LEFT " + me.getColor("L"), 0, 20, 0);
			lcd.drawString("RIGHT " + me.getColor("R"), 0, 40, 0);
			if(me.soundFlag) {
				me.startBeeps();
			}else {
				me.closeLED();
			}
    		try{
    			sleep(delay);
    		}
    		catch(Exception e){
    			// We have no exception handling
    			;
    		}
    	}
    }
	
	/*
	 * Print method to display on lcd screen
	 * @param map 2D int array to make a grid of map
	 * 
	 * */


}