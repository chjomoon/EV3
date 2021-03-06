

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {
	
	private static int[][] map;
	private static List<Node> path;
	private static List<Node> open;
	private static List<Node> closed;
	private static Node current;
	private static final int OBSTACLE = 4;
	//length and width of the map
	public static int i = 6;
	public static int j = 6;
	public static int startX;
	public static int startY;
	public static int endX;
	public static int endY;
	public static List<Node> neighbours;
	public static int neighbourInListIndex;
	public static int size;

	
	public AStar() {
		map = new int [i][j];
		for(int i = 0; i < 6; i ++){
			for(int j = 0; j < 6; j ++){
				map[i][j] = 0;
			}
		}
		startX = 0;
		startY = 0;
	}
	
	public void setObstacles(int x, int y){
		map[x][y] = OBSTACLE;
	}
	
	public static void setStart(int x, int y) {
		startX = x;
		startY = y;
	}

	public static int heuristic(Node current) {
		return Math.abs(current.x - endX) + Math.abs(current.y - endY);
	}
	
	public static int getSize(){
		return size;
	}
	
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
	
	private static void neighbours(Node node) {
		neighbours = new ArrayList<Node>();
		Node neighbour;
		int neighbourX, neighbourY;
		for(int i = -1; i < 2; i ++) {
			for(int j = -1; j < 2; j ++) {
				if((i == 0 && j !=0) || (i != 0 && j ==0)) {
					neighbourX = node.x + i;
					neighbourY = node.y + j;
					if(valid(neighbourX, neighbourY)) {
						neighbour = new Node(node, neighbourX, neighbourY);
						neighbour.g = node.g ++;
						neighbours.add(neighbour);
					}
				}
			}
		}
	}
	
	private static boolean valid(int x, int y) {
		if(x >= 0 && x < i && y >= 0 && y < j) {
			if(map[x][y] == 0) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean checkNeighbourInList(Node node, List<Node> list) {
		neighbourInListIndex = 0;
		for(Node n: list) {
			if(node.x == n.x && node.y == n.y) {
				return true;
			}
			neighbourInListIndex ++;
		}
		return false;
	}

	public static void constructPath(Node endNode){
		path = new ArrayList<Node>();
		Node node = endNode;
		path.add(node);
		size = 0;
		while(node.parent != null) {
			node = node.parent;
			path.add(0, node);
			size ++;
		}
	}

	public static List<Node> getPath(int victimX, int victimY){
		endX = victimX; endY = victimY;
		if(AStarAlgorithm()) {
			return path;
		}
		return null;
	}
	

}
