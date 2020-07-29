import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import lejos.hardware.Battery;
import lejos.hardware.Button;
//import src.Node;

public class EV3Server extends Thread {

	private PilotRobot me;
	public static final int port = 1234;
	ServerSocket server = null;
	Socket client;
	OutputStream out;
	InputStream in;
	//static OutputStream out;

	
	public EV3Server(PilotRobot robot) throws IOException {
		me = robot;
		server = new ServerSocket(port);
		client = null;

		client = server.accept();

		out = null;
		out = client.getOutputStream();
		in = client.getInputStream();
	
	}
	
	public void receivingSound(boolean flag) {
			me.soundFlag = flag;
			if(!flag) {
				me.closeLED();
			}
	}
	
	public void receivingPath(InputStream in, OutputStream out) throws IOException {
		DataInputStream dIn = new DataInputStream(in);
		
		DataOutputStream dOut = new DataOutputStream(out);
		int size = dIn.read();
//		System.out.println(size);
		
		for(int i = 0; i<size; i++) {
			int x = dIn.read();
			int y = dIn.read();	
			me.moveTo(x,  y);
		}
		dOut.writeUTF(me.getColor("R"));
		
		//System.out.println("sending x and y:" + me.getX() + ", " + me.getY());
		dOut.write(me.getX());
		dOut.write(me.getY());
		
		dOut.flush();
		//System.out.println(me.getColor("R"));
	}
	
	

	/*public void isMoved(boolean moved) throws IOException {
		DataOutputStream dOut = new DataOutputStream(out);
		if(moved == true) {
			dOut.writeBoolean(true);
		}else {
			dOut.writeBoolean(false);
		}
		dOut.flush();
	}
	*/

	public void sendingColor(OutputStream out, String color) throws IOException {
		DataOutputStream dOut = new DataOutputStream(out);
		dOut.writeUTF(color);
		sendCoor(out);
		dOut.flush();
	}
	
	public void sendCoor(OutputStream out) throws IOException {
		DataOutputStream dOut = new DataOutputStream(out);
	}
	
	
	public void run() {

		try {

			DataInputStream dIn = new DataInputStream(in);
			DataOutputStream dOut = new DataOutputStream(out);
			FileWriter writer = new FileWriter("output.txt");
			while(true) {
				String check = dIn.readUTF();
			//	System.out.println("Received Message :\t" + check );
				if(check.equals("beep")) {
					receivingSound(true);
				}else if(check.equals("stop")){
					receivingSound(false);
				}
//				}else if(check.equals("knn")) {
//					String data;
//					for(int i = 0; i < 3000; i ++) {
//						data = dIn.readUTF();
//						writer.append(data + "\n");
//						System.out.println(i);
//						
////						data[0] = dIn.readDouble();
////						data[1] = dIn.readDouble();
////						data[2] = dIn.readDouble();
////						data[3] = dIn.readDouble();;
////						System.out.println(i + data[0]);
//						//me.getKNN().addDATA(data);
//					}
//					writer.flush();
//				//	System.out.println("GO");
//				//	dOut.writeUTF("GO");
//				}
				else {
					receivingPath(in, out);
				}

				
			}
			
		}

		 catch (IOException e) {
		}
	}

}
