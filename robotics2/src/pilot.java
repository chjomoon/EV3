import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class pilot {


	
	public static void main(String[] args) throws Exception {

		PilotRobot me = new PilotRobot();
		RobotMonitor monitor = new RobotMonitor(me, 400);
		EV3Server ev3 = new EV3Server(me);

		monitor.start();
		
	    ev3.start();
//	    while(!Button.ESCAPE.isDown()) {
//	    	me.moveForward();
//	    	Button.waitForAnyEvent();
//	    }
//	    me.startBeeps();
        // Start the Pilot Monitor
	    
		Button.waitForAnyPress();



	}
	


}
