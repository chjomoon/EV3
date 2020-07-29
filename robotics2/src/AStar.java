

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {
	
	private static double[][]map;
	private static List<Node> path;
	private static List<Node> open;
	private static List<Node> closed;
	private static Node current;
	//length and width of the map
	public static int i;
	public static int j;
	public static int startX;
	public static int startY;
	public static int endX;
	public static int endY;
	public static List<Node> neighbours;
	public static int neighbourInListIndex;
	
	/*private static double[][]map;
	private static List<Node> path;
	private List<Node> open;
	private List<Node> closed;
	private Node current;
	//length and width of the map
	public static int i;
	public static int j;
	public int startX;
	public int startY;
	public int endX;
	public int endY;
	public List<Node> neighbours;
	public int neighbourInListIndex;*/
	
	//CLASS FOR NODE

	//CLASS FOR NODE ENDS
	
	public AStar() {}
	
	public void setMap(double[][] map) {
		this.map = map;
		i = map.length;
		j = map[0].length;
	}
	
	public static void setNav(int sX, int sY, int eY, int eX) {
		startX = sX;
		startY = sY;
		endX = eX;
		endY = eY;
	}
	
	public static int heuristic(Node current) {
		return Math.abs(current.x - endX) + Math.abs(current.y - endY);
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
		while(node.parent != null) {
			node = node.parent;
			path.add(0, node);

		}
	}

	public static List<Node> getPath(){
		if(AStarAlgorithm()) {
			return path;
		}
		return null;
	}
	

}
