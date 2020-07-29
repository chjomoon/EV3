# LEGO® Mindstorms® EV3 Robotics

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

## Functionalities

### A* Algorithm

* Pathfinding technique to reach the target grid in a shortest pathway

* The heuristic used in A * is a user defined function which estimates the distance from a given point to the destination

* prioritizes paths that seem to be leading closer to a goal

* Used heuristics calculation to find the solution
  
  * Math.abs(current.x - endX) + Math.abs(current.y - endY);

### Coordinates

* Robot can detect the location by recognizing the coordination on the arena. Once it moves from one to another grid, it keeps to record the coordinates to reach the point.


### Server

* Robot can communicate with AgentSpeak through the server by sending or receiving the details that robot collected. AgentSpeak can decide the action based on the information provided by EV3.

### Color Sensor

* Color sensor is being used to check the details of the grid. By collecting color ranges, it saves samples to check the accurate color sample. The color plays the role for AgentSpeak to
decide sending the direction EV3.

### [JASON] (AgentSpeak)

* AgentSpeak models an agent situated in an environment

* The agent reasons about how to act to achieve its goals, permanently running responding to events by executing plans • The architecture of AgentSpeak : Belief, Plan, Events, Intentions


[LeJos] : http://www.lejos.org/ev3.php
  
[JASON] : http://jason.sourceforge.net/wp/
