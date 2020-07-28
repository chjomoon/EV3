
import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.io.*;
import java.net.*;
import java.util.List;
import java.lang.*;


import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Maximum LEGO EV3: Building Robots with Java Brains
 * ISBN-13: 9780986832291
 * Variant Press (C) 2014
 * Chapter 14 - Client-Server Robotics
 * Robot: EV3 Brick
 * Platform: LEGO EV3
 * @author Brian Bagnall
 * @version July 20, 2014
 */
public class EV3Connect {

	private static Label message;
	private static final String MF = "0";
	static Socket sock;
	static OutputStream out;
	static DataOutputStream dOut;
	static InputStream in;
	static DataInputStream dIn;
	boolean passed = true;
	private int x;
	private int y;
	int size;
	String start = "dont go";
	
	public EV3Connect() throws IOException {
		String ip = "192.168.70.98"; 
		boolean flag = true;
		sock = new Socket(ip, 1234);
		System.out.println("Connected");
		out = sock.getOutputStream();
		dOut = new DataOutputStream(out);
		in = sock.getInputStream();
		dIn = new DataInputStream(in);
		

	}
	

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
				
				System.out.println("SENDING LOC "+ x +", "+ y);
				dOut.write(x);
				dOut.write(y);
				}
			//System.out.println("MOVING TO " + x + ", " + y);
			dOut.flush();
			while(color.equals("")) {
				color = dIn.readUTF();
	//			System.out.println(color);
			}
			x = dIn.read();
			y = dIn.read();
	//		System.out.println(color);
			return color;
	}
	
	public void checkXandY() throws IOException {
		DataInputStream dIn = new DataInputStream(in);

		for(int i = 0; i>size; i++) {
			int x = dIn.read();
			int y = dIn.read();	
			System.out.println("PC receives x and y : "+ x + ", "+  y);
		}
	}

	public void sendingBeeps(String beep) throws IOException {
		DataOutputStream dOut = new DataOutputStream(out);
		dOut.writeUTF(beep);
		dOut.flush();
		//return beep;
	}
	
	public String receivingColor() throws IOException {
		DataInputStream dIn = new DataInputStream(in);

		String color = dIn.readUTF();
		
        System.out.print("Robot found the victim, and the color is " + color);
		
		return color;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}

	/*public boolean pathMoved() throws IOException {
		DataInputStream dIn = new DataInputStream(in);
		boolean moved = dIn.readBoolean();
		moved = passed;
		return passed;
	}
*/

	//}

	
}


