
import java.util.List;
import java.util.Random;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;
import lejos.hardware.motor.EV3MediumRegulatedMotor;


public class PilotRobot {
//	private EV3IRSensor usSensor;
	private EV3GyroSensor gSensor;
	private SampleProvider distSP, gyroSP, leftColourSP, rightColourSP;	
	private float[] leftSample, rightSample, distSample, angleSample, ultrasonicSensorSample; 
	private MovePilot pilot;
	private EV3ColorSensor leftColourSensor, rightColourSensor;
	private float distanceBetweenCentre = (float) 5.0;
	private boolean obstacleFront;
	private float distanceFront, DistanceLeft, DistanceRight;
	public static final int ROBOT_LENGTH = 25;
	private static int xGridCount, yGridCount;
	private static OdometryPoseProvider opp;
	private int currentHeading;
	private int heading = 0;
	private int[] coordinates = {0, 0};
	public int sensorX, sensorY, sensorXf, sensorYf;
	private int searchCounter = 0;
	private AStar astar;
	private List<Node> path;
	private boolean running;
	private String s = "0";
	public boolean soundFlag = false;

	public static final int HEADING_NORTH = 0;
	public static final int HEADING_WEST = -90;
	public static final int HEADING_EAST = 90;
	public static final int HEADING_SOUTH = -180;
    
	public static final String BLACK = "BLACK";
	public static final String CYAN = "CYAN";
	public static final String BURGANDY = "BURGANDY";
	public static final String YELLOW = "YELLOW";
	public static final String WHITE = "WHITE";
	public static final String LEFT = "L";
	public static final String RIGHT = "R";
	

	
	public PilotRobot() throws Exception{
		Brick myEV3 = BrickFinder.getDefault();

	//	usSensor = new EV3IRSensor(myEV3.getPort("S3"));
		gSensor = new EV3GyroSensor(myEV3.getPort("S2"));
		leftColourSensor = new EV3ColorSensor(myEV3.getPort("S1"));
		rightColourSensor = new EV3ColorSensor(myEV3.getPort("S4"));
		
//		distSP = usSensor.getDistanceMode();
		gyroSP = gSensor.getAngleMode();
		leftColourSP = leftColourSensor.getRGBMode();
		rightColourSP = rightColourSensor.getRGBMode();
		
		
		leftSample = new float[leftColourSP.sampleSize()];		
		rightSample = new float[rightColourSP.sampleSize()];		
	//	distSample = new float[distSP.sampleSize()];		
		angleSample = new float[gyroSP.sampleSize()];
		
		running = true;

		
		
		setPilot();

	    //Initial speed of robot
	    setLinearSpeed(20);
	    


		// Reset the value of the gyroscope to zero
		gSensor.reset();
	}

//	public static void main(String[] args) {
//		knn = new KNNTest();
//		knn.setTest(0.029411765, 0.05980392, 0.023529412);
//		System.out.println(knn.getNearest());
//	}
	/*
	 * get the current heading 
	 * 
	 * */


	public boolean moveForward() {
		
		int counter = 0;
		setLinearSpeed(10);
		boolean flag = false;
		boolean lineFlag = false;
		String curColor = getColor(RIGHT);
		pilot.setLinearAcceleration(18);
		pilot.forward();
		while(!flag) {
			
			if(counter > 3) {
				break;
			}
			if ((getColor(LEFT) != BLACK) && getColor(RIGHT) == BLACK) {
			//if (getColor(LEFT) == curColor && getColor(RIGHT) != curColor) {
				pilot.stop();
				rotate(2);
				counter ++;
				lineFlag = true;
				//flag = true;
		
			} else if ((getColor(LEFT) == BLACK) && getColor(RIGHT) != BLACK) {
				pilot.stop();
				rotate(-2);
				counter ++;
				lineFlag = true;
				//flag = true;
			} else if((getColor(LEFT) == BLACK) && getColor(RIGHT) == BLACK){
				pilot.stop();
				flag = true;
				}
			 else if(getColor(LEFT) != BLACK && getColor(RIGHT) != BLACK && lineFlag) {
				 flag = true;
			 }
		}

		pilot.setLinearAcceleration(25);
		pilot.setLinearSpeed(15);
		pilot.travel(17);
		return true;
//		pilot.setLinearAcceleration(25);
//		pilot.travel(24);
//		return true;
	}
	
	public void startBeeps() {
		LCD.clear();
		LCD.drawString("Victim rescued", 0, 6);
		Sound.playTone(440, 100);
		Button.LEDPattern(5);
	}
	
	public void closeLED() {
		Button.LEDPattern(0);
	}
	public void act(String actionS) {
		int action = Integer.parseInt(actionS);
		if(action == 0) {
			moveForward();
		}
	}
	
	public void setString(String s) {
		this.s = s;
	}
	
	public String getString() {
		return s;
	}


	/*
	 * Getter and setter to get obstacle
	 * 
	 * */
	public void rotate(float angle) {
		setAngularVelocity(50);
		resetGSensor();
		pilot.rotate(angle);
		
		//if (!(getLeftColour() == WHITE && getRightColour() == WHITE)&&!(getLeftColour() == BLACK && getRightColour() == BLACK)) {
			if(getAngle() != angle) {
				rotate(angle - getAngle());
			}
	//	}
		
	}
	



	public boolean moveTo(int endX, int endY) {
		int startX = coordinates[0];
		int startY = coordinates[1];
	//	System.out.println(heading);
		String direction = null;
		if(endY - startY == 1) {
			direction = "UP";
		}else if(endY - startY == -1) {
			direction = "DOWN";
		}else if(endX - startX == -1) {
			direction = "LEFT";
		}else if(endX - startX == 1) {
			direction = "RIGHT";
		}
		if(direction == "UP") {
			switch(heading) {
				case 0:
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 1:
					rotate(-90);
					updateHeading("left");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 2:
					rotate(180);
					updateHeading("back");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 3:
					rotate(90);
					updateHeading("right");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
			}
		}else if(direction == "DOWN") {
			switch(heading) {
				case 0:
					rotate(180);
					updateHeading("back");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 1:
					rotate(90);
					updateHeading("right");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 2:
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 3:
					rotate(-90);
					updateHeading("left");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
			}
		}else if(direction == "RIGHT") {
			switch(heading) {
				case 0:
					rotate(90);
					updateHeading("right");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 1:
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 2:
					rotate(-90);
					updateHeading("left");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 3:
					rotate(180);
					updateHeading("back");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
			}
		}else if(direction == "LEFT") {
			switch(heading) {
				case 0:
					rotate(-90);
					updateHeading("left");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 1:
					rotate(180);
					updateHeading("back");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 2:
					rotate(90);
					updateHeading("right");
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
				case 3:
					if(!moveForward()) {
						return false;
					};
					updateCoordinates();
					break;
			}
		}
		return true;
	}
	
	public String getColor(String side) {
		
		if(side == LEFT) {
		
			if (getLeftColourR() > 0.011764706 && getLeftColourR() < 0.046078432) {
				if (getLeftColourG() > 0.01372549 && getLeftColourG() < 0.05490196) {
					if (getLeftColourB() > 0.007843138 && getLeftColourB() < 0.023529412) {
						return BLACK;
					}
				}
			} else if (getLeftColourR() > 0.05 && getLeftColourR() < 0.07254902) {
				if (getLeftColourG() > 0.021568628 && getLeftColourG() < 0.08039216) {
					if (getLeftColourB() > 0.006862745 && getLeftColourB() < 0.023529412) {
						return BURGANDY;
					}
				}
			} else if (getLeftColourR() > 0.08529412 && getLeftColourR() < 0.25882354) {
				if (getLeftColourG() > 0.08627451 && getLeftColourG() < 0.28529412) {
					if (getLeftColourB() > 0.04901961 && getLeftColourB() < 0.15) {
						return YELLOW;
					}
				}
			} 
		}
		else {
		
			if (getRightColourR() > 0.025490196 && getRightColourR() < 0.07254902) {
				if (getRightColourG() > 0.030392157 && getRightColourG() < 0.083333336) {
					if (getRightColourB() > 0.021568628 && getRightColourB() < 0.055882353) {
						return BLACK;
					}
				}
			} else if (getRightColourR() > 0.08235294 && getRightColourR() < 0.1254902) {
				if (getRightColourG() > 0.04901961 && getRightColourG() < 0.09803922) {
					if (getRightColourB() > 0.028431373 && getRightColourB() < 0.061764706) {
						return BURGANDY;
					}
				}
			} else if (getRightColourR() > 0.24901961 && getRightColourR() < 0.422549) {
				if (getRightColourG() > 0.055882353  && getRightColourG() < 0.3882353) {
					if (getRightColourB() > 0.03137255  && getRightColourB() < 0.32843137) {
						return YELLOW;
					}
				}
			} 
		}
		return CYAN;
	}

//	if(side == LEFT) {
//		
//		if (getLeftColourR() > 0.011764706 && getLeftColourR() < 0.046078432) {
//			if (getLeftColourG() > 0.01372549 && getLeftColourG() < 0.05490196) {
//				if (getLeftColourB() > 0.007843138 && getLeftColourB() < 0.023529412) {
//					return BLACK;
//				}
//			}
//		} else if (getLeftColourR() > 0.05 && getLeftColourR() < 0.07254902) {
//			if (getLeftColourG() > 0.021568628 && getLeftColourG() < 0.08039216) {
//				if (getLeftColourB() > 0.006862745 && getLeftColourB() < 0.023529412) {
//					return BURGANDY;
//				}
//			}
//		} else if (getLeftColourR() > 0.006862745 && getLeftColourR() < 0.078431375) {
//			if (getLeftColourG() > 0.063725494 && getLeftColourG() < 0.22647059) {
//				if (getLeftColourB() > 0.024509804 && getLeftColourB() < 0.11960784) {
//					return CYAN;
//				}
//			}
//		} 
//	}
//	else {
//	
//		if (getRightColourR() > 0.025490196 && getRightColourR() < 0.07254902) {
//			if (getRightColourG() > 0.030392157 && getRightColourG() < 0.083333336) {
//				if (getRightColourB() > 0.021568628 && getRightColourB() < 0.055882353) {
//					return BLACK;
//				}
//			}
//		} else if (getRightColourR() > 0.08235294 && getRightColourR() < 0.1254902) {
//			if (getRightColourG() > 0.04901961 && getRightColourG() < 0.09803922) {
//				if (getRightColourB() > 0.028431373 && getRightColourB() < 0.061764706) {
//					return BURGANDY;
//				}
//			}
//		} else if (getRightColourR() > 0.004901961 && getRightColourR() < 0.12745099) {
//			if (getRightColourG() > 0.030392157  && getRightColourG() < 0.39215687) {
//				if (getRightColourB() > 0.030392157  && getRightColourB() < 0.29215688) {
//					return CYAN;
//				}
//			}
//		} 
//	}
//	return YELLOW;
//}

	public void updateHeading(String direction) {

		switch(direction) {
		case "right":
			if(heading + 1 == 4) {
				heading = 0;
			}else {
				heading += 1;
			}
			break;
		case "left":
			if(heading - 1 == -1) {
				heading = 3;
			}else {
				heading -= 1;
			}
			break;
		case "back":
			if(heading + 2 > 3) {
				heading -= 2;
			}else {
				heading += 2;
			}
			break;
		}
	}


	
	public void updateCoordinates() {
		if(heading == 0) {
			coordinates[1] ++;
		}else if(heading == 1) {
			coordinates[0] ++;
		}else if(heading == 2) {
			coordinates[1] --;
		}else if(heading == 3) {
			coordinates[0] --;
		}
	}
	
	public void calibrateRotation() {
		float rotationAngle = 0;
		do {
			setPilot();
			pilot.rotate(360);
			rotationAngle = getAngle();
			if(rotationAngle < 359) {
				distanceBetweenCentre += 0.05;
			}
			else if(rotationAngle > 361) {
				distanceBetweenCentre -= 0.05;
			}
			resetGSensor();
		}while(rotationAngle > 362 || rotationAngle < 358);
	}

	

	public String showCoordinates() {
		return "x: " + coordinates[0] + " y: " + coordinates[1];
	}
	
	public int getX() {
		return coordinates[0];
	}
	
	public int getY() {
		return coordinates[1];
	}
	
	//set the calibration for piloting the robot
	public void setPilot() {
		// Set up the wheels by specifying the diameter of the
		// left (and right) wheels in centimeters, i.e. 4.05 cm.
		// The offset number is the distance between the centre
		// of wheel to the centre of robot (4.9 cm)
		// NOTE: this may require some trial and error to get right!!!
		Wheel leftWheel = WheeledChassis.modelWheel(Motor.B, 4.05).offset(-1 * distanceBetweenCentre);
		Wheel rightWheel = WheeledChassis.modelWheel(Motor.D, 4.05).offset(distanceBetweenCentre);
		
		Chassis myChassis = new WheeledChassis( new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);

	    pilot = new MovePilot(myChassis);
		this.obstacleFront = false;

	}
	public void closeRobot() {
		leftColourSensor.close();
		rightColourSensor.close();
//		usSensor.close();
		gSensor.close();
	}
	
	public float getLeftColourR() {
		leftColourSP.fetchSample(leftSample, 0);
		return leftSample[0];
	}
	public float getLeftColourG() {
		leftColourSP.fetchSample(leftSample, 0);
		return leftSample[1];
	}
	public float getLeftColourB() {
		leftColourSP.fetchSample(leftSample, 0);
		return leftSample[2];
	}

	public float getRightColourR() {
		rightColourSP.fetchSample(rightSample, 0);
		return rightSample[0];
	}
	
	public float getRightColourG() {
		rightColourSP.fetchSample(rightSample, 0);
		return rightSample[1];
	}
	
	public float getRightColourB() {
		rightColourSP.fetchSample(rightSample, 0);
		return rightSample[2];
	}

	//get distance from ultrasound/infrared sensor
	//public float getDistance() {
  //  	distSP.fetchSample(distSample, 0);
   // 	return distSample[0];
//	}

	//get gyroscope angle
	public float getAngle() {
    	gyroSP.fetchSample(angleSample, 0);
    	return angleSample[0];
	}
	
	public MovePilot getPilot() {
		return pilot;
	}
	

	//check if escape is pressed

	//method to set speed of robot
	public void setLinearSpeed(int speed) {
		pilot.setLinearSpeed(speed);
	}
	
	//reset gyroscope to 0
	public void resetGSensor() {
		gSensor.reset();
	}
	
	public int getHeadingNow() {
		return currentHeading;
	}

	public void setAngularVelocity(int speed) {
		pilot.setAngularSpeed(speed);
	}
	
	
}






















