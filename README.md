# LEGO® Mindstorms® EV3 Robotics

![alt example](/img/img1.jpg "mindstorms")

## Project Description

* A mobile IoT robot(EV3) that handles multiple forms of uncertainty under the autonomous agents called AgentSpeak. EV3 can navigate the path based on A* algorithm by finding a shortest pathway to reach a destination. Once EV3 reaches the point, the robot can send information to the agents, deciding whether returning to the base or finding a next point.

* EV3 uses various sensors such as color, ultrasonic, and gyroscope to avoid an obstacle or to send details of the surroundings.

* AgentSpeak to determine what Intentions to execute, based on a set of plans

* Programing language LeJOS on the EV3 robot to determine movement, localization, and cell color detection

## Using Technology

* Java Frameworks

  * [LeJos] 
  
  * [JASON]  

* Mindstorms EV3

## Working Environment

* MacOS Catalina 10.15

* Eclipse IDE

## Functionalities

### A* Algorithm

* Pathfinding technique to reach the target grid in a shortest pathway

* The heuristic used in A * is a user defined function which estimates the distance from a given point to the destination

* prioritizes paths that seem to be leading closer to a goal

* Used heuristics calculation to find the solution
  
  * <code> Math.abs(current.x - endX) + Math.abs(current.y - endY); </code>
  
 
![alt example](/img/astar.png "A* Algorithm")

<pre><code>
public static boolean AStarAlgorithm() {
	open = new ArrayList<Node>();
	closed = new ArrayList<Node>();
	Node startNode = new Node(null, startX, startY);
	startNode.g = 0;
	startNode.h = heuristic(startNode);
	startNode.setF();
	open.add(new Node(null, startX, startY));
	while(!open.isEmpty()) {
		Collections.sort(open);
		current = open.get(0);
		neighbours(current);
		if(current.x == endX && current.y == endY) {
			constructPath(current);
			return true;
		}
		open.remove(0);
		closed.add(current);
		for(Node neighbour : neighbours) {
			if(!checkNeighbourInList(neighbour, closed)) {
				neighbour.f = neighbour.g + heuristic(neighbour);
				if(!checkNeighbourInList(neighbour, open)) {
					open.add(neighbour);
				}
				else {
					Node openNeighbour = open.get(neighbourInListIndex);
					if(neighbour.g < openNeighbour.g) {
						open.get(neighbourInListIndex).g = neighbour.g;
						open.get(neighbourInListIndex).parent = neighbour.parent;
						open.get(neighbourInListIndex).setF();
					}
				}
			}
		}
	}
	return false;
	}

</pre></code>

### Coordinates

![alt example](/img/gyro1.jpg "gyro sensor")

* Robot can detect the location by recognizing the coordination on the arena. Once it moves from one to another grid, it keeps to record the coordinates to reach the point.

<pre><code>
private int[] coordinates = {0, 0};
...
public boolean moveTo(int endX, int endY) {
	int startX = coordinates[0];
	int startY = coordinates[1];
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
....
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
</pre></code>


### Server

* Robot can communicate with AgentSpeak through the server by sending or receiving the details that robot collected. AgentSpeak can decide the action based on the information provided by EV3.

<pre><code>
public String sendingPath(List<Node> shortestPath) throws IOException {
	String send = "sendPath";
	size = shortestPath.size() - 1;
	dOut.writeUTF(send);
	dOut.write(size);		
	boolean flag = true;
	String color = "";
	for(int i = 1; i < shortestPath.size(); i ++){
		int x = shortestPath.get(i).x;
		int y = shortestPath.get(i).y;
		dOut.write(x);
		dOut.write(y);
		}
	dOut.flush();
	while(color.equals("")) {
		color = dIn.readUTF();
	}
	x = dIn.read();
	y = dIn.read();
	return color;
}
</pre></code>

### Color Sensor

![alt example](/img/cs1.jpg "color sensor")

* Color sensor is being used to check the details of the grid. By collecting color ranges, it saves samples to check the accurate color sample. The color plays the role for AgentSpeak to
decide sending the direction EV3.

<pre><code>
private EV3ColorSensor leftColourSensor, rightColourSensor;
...
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
   		pilot.stop();
		rotate(2);
		counter ++;
		lineFlag = true;
  	}else if ((getColor(LEFT) == BLACK) && getColor(RIGHT) != BLACK) {
	   pilot.stop();
	   rotate(-2);
	   counter ++;
	   lineFlag = true;
	  ....
</pre></code>

* Collecting a sample range as well while EV3 is running
<pre><code>
public String getColor(String side) {
	if(side == LEFT) {
		if (getLeftColourR() > 0.011764706 && getLeftColourR() < 0.046078432) {
			if (getLeftColourG() > 0.01372549 && getLeftColourG() < 0.05490196) {
				if (getLeftColourB() > 0.007843138 && getLeftColourB() < 0.023529412) {
					return BLACK;
				}
			}
    .....
</pre></code>

### [JASON] (AgentSpeak)

* AgentSpeak models an agent situated in an environment

* The agent reasons about how to act to achieve its goals, permanently running responding to events by executing plans • The architecture of AgentSpeak : Belief, Plan, Events, Intentions

<pre><code>
/ ========================================================================
// Plan Library
// ========================================================================
// Plans for the CNP
// send a message to the initiator introducing the agent as a participant 
+plays(initiator,In)
   :  .my_name(Me)
   <- .send(In,tell,introduction(participant,Me)).

// answer to Call For Proposal
@c1 +cfp(CNPId,Task,C,NC)[source(A)]
   :  plays(initiator,A) & price(Task,Offer)
   <- +proposal(CNPId,Task,C,NC,Offer);		// remember my proposal
      .send(A,tell,propose(CNPId,Offer)).

// Handling an Accept message
@r1 +accept_proposal(CNPId)[source(A)]
		: proposal(CNPId,Task,C,NC,Offer)
		<- !getScenario(A);
		    +startRescueMission(A,C,NC).
 
// Handling a Reject message
@r2 +reject_proposal(CNPId)
		<- .print("I lost CNP ",CNPId, ".");
		// clear memory
		-proposal(CNPId,_,_).
</pre></code>

[LeJos] : http://www.lejos.org/ev3.php
  
[JASON] : http://jason.sourceforge.net/wp/
