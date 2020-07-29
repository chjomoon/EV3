# EV3 Robotics

![alt example](/img/img1.jpg "mindstorms")

## 프로젝트 설명

AgentSpeak에 의해 다양한 상황을 판단하여 움직이는 모바일 IoT 로봇(EV3) 개발 프로젝트입니다. EV3는 A 알고리즘을 통해 최적의 경로를 탐사하여 목적지까지 도달합니다. EV3가 목표에 도달했을때, 에이전트는 로봇이 보내온 정보에 따라 다음지점으로 이동할지 원지점으로 돌아올지 자율적으로 결정하게 됩니다.
EV3는 컬러센서, 적외선센서, 균형센서등 다양한 센서들을 활용하여 장애물을 피하거나 주변의 정보를 서버를 통해 전달하게됩니다.
AgentSpeak은 BDI(Beliefs-Desires-Intentions) 아키텍쳐 기반의 추상언어로, 설정된 계획을 기반으로 로봇이 어떤행동을 할지 결정하게 됩니다. 

프로젝트는 아이작 아시모프가 제시한 로봇 3원칙에 의해 로봇의 기능을 구현하였습니다. 

	제1원칙: 로봇은 인간에게 해를 입혀서는 안 된다. 그리고 위험에 처한 인간을 모른 척해서도 안 된다.

	제2원칙: 제1원칙에 위배되지 않는 한, 로봇은 인간의 명령에 복종해야 한다.

	제3원칙: 제1원칙과 제2원칙에 위배되지 않는 한, 로봇은 로봇 자신을 지켜야 한다

프로그래밍 언어 LeJos를 통해 EV3의 움직임, 위치측정, 색감지 등 기능을 구현하였습니다.

* A mobile IoT robot(EV3) that handles multiple forms of uncertainty under the autonomous agents called AgentSpeak. EV3 can navigate the path based on A* algorithm by finding a shortest pathway to reach a destination. Once EV3 reaches the point, the robot can send information to the agents, deciding whether returning to the base or finding a next point.

* EV3 uses various sensors such as color, ultrasonic, and gyroscope to avoid an obstacle or to send details of the surroundings.

* AgentSpeak to determine what Intentions to execute, based on a set of plans

* Programing language LeJOS on the EV3 robot to determine movement, localization, and cell color detection

## 사용 언어 및 기술

* Java Frameworks

  * [LeJos] 
  
  * [JASON]  

* Mindstorms EV3

## 개발 환경

* MacOS Catalina 10.15

* Eclipse IDE

## 주요기능

### A* Algorithm

그리드상의 최단경로를 찾기위해 구현된 알고리즘으로, 시작 노드와 목적지 노드를 분명하게 지정해 이 두 노드 간의 최단 경로를 파악할 수 있습니다. 휴리스틱스값을 이용해 최적의 경로 값을 계산하는데 휴리스틱 추정값을 어떤 방식으로 제공하느냐에 따라 얼마나 빨리 최단 경로를 파악할 수 있느냐가 결정됩니다. 이번 프로젝트에서는 휴리스틱값 <code> Math.abs(current.x - endX) + Math.abs(current.y - endY); </code> 를 사용하여 거리값을 계산하였습니다. EV3가 그리드를 이동할때마다 휴리스틱스값을 계산하여 이동경로를 단축하였습니다.   

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

한정된 아레나에서 프로젝트가 진행됨에 따라 로봇이 이동할때마다 위치측정하는 기능을 구현하였습니다. EV3가 한번 이동할때 위치값을 저장해서 경로를 탐색하는데 활용하였습니다. 

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

AgentSpeak과 EV3 통신을 위해 서버를 만들어 실시간으로 EV3가 수집한 정보를 주고 받는 기능을 구현하였습니다. AgentSpeak은 EV3가 수집한 정보를 기반으로 움직임을 결정하게됩니다.

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

컬러를 사용하여 그리드의 정보를 구분하게 됩니다. ( 예를 들어 출발지는 노란색, 목표지점은 자주색, 또는 파란색, 그리드의 경계는 검정색) 

컬러값을 수집하여, 수집된 샘플을 이용하여 EV3는 정확한 색을 감지해낼 수 있습니다. 이러한 정보는 AgentSpeak의 결정에 중요한 역할을 하게됩니다.

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

### JASON AgentSpeak

AgentSpeak은 주어진 가상환경을 구현하는 추상언어입니다. AgentSpeak은 어떻게 목표를 이루고, 지속적으로 작동하여 발생하는 이벤트에 반응하고 계획을 처리합니다.
이번 프로젝트는 인명구조라는 가상상황에서 EV3가 각 지점에 도달하여 수집한 정보를 바탕으로 구조를 할지 다음 목적지를 결정하게 됩니다. AgentSpeak은 목표지점에서 수집된 정보를 미리설정된 기준에 따라 자율적으로 결정하는 기능 구현하였습니다.

* AgentSpeak models an agent situated in an environment

* The agent reasons about how to act to achieve its goals, permanently running responding to events by executing plans 

* The architecture of AgentSpeak : Belief, Plan, Events, Intentions

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
