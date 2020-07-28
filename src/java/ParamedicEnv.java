// Environment code for project doctor2018

import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.logging.*;

public class ParamedicEnv extends Environment {
	
    public static final int GSize = 6; // The bay is a 6x6 grid
    public static final int HOSPITAL  = 8; // hospital code in grid model
    public static final int VICTIM  = 16; // victim code in grid model
	public static final int MEDIC  = 1; // victim code in grid model

    private static List<int[]> victimLocation = new ArrayList<int[]>();
	private static List<int[]> savedLocation = new ArrayList<int[]>();
	private static int locationLeft = 5;
	private static int victimIndex = 0;
	private static int obstacleIndex = 0;
	private static List<Node> path;
	private static List<Node> shortestPath;
	private static int currentVicX = 0;
	private static AStar astar;
    private Logger logger = Logger.getLogger("doctor2018."+ParamedicEnv.class.getName());
	private static int[] currentLoc = {0, 0};
	private static int i = 0;
	private static String color = "";
    private static final Literal atH = Literal.parseLiteral("atH(_)");
	private static final Literal noCritical = Literal.parseLiteral("noCritical(_)");
	private static final Literal noVictim = Literal.parseLiteral("noVictim(_)");
	private static final Literal isWhite = Literal.parseLiteral("white(_)");
	private static final Literal white2 = Literal.parseLiteral("white2(_)");
	private static final Literal savedP = Literal.parseLiteral("savedLocation(_)");
	private static final Literal allVictim = Literal.parseLiteral("allVictim(_)");
	public static Literal askDoctor;
	private static boolean start = false; 
	private static int victimLeft = 3;
	private static int victimFound = 0;
	private static int criticalLeft;
	private static boolean white;
	private static boolean notCritical;
    private RobotBayModel model;
    private RobotBayView view;    
	private boolean saved;
	private EV3Connect ev3;
	private int removeIndex;
	private boolean askDoc = false;
	private int counter = 0;
	private int saveCounter = 0;

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
        //addPercept(ASSyntax.parseLiteral("percept(demo)"));
        model = new RobotBayModel();
        view  = new RobotBayView(model);
        model.setView(view);
		astar = new AStar();
		try{
		ev3 = new EV3Connect();
		} catch(Exception e){
		}
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        try {
			System.out.println("vicimt found: " + victimFound);
			color = "null";
			saved = false;
        	if (action.getFunctor().equals("addVictim")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.addVictim(x,y);
				int[] victimL = {x, y};
				victimLocation.add(victimL);
                logger.info("adding victim at: "+x+","+y);
			//	nextVictim();
            } else if (action.getFunctor().equals("addObstacle")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.addObstacle(x,y);
                astar.setObstacles(x, y);
                obstacleIndex ++;
                logger.info("adding obstacle at: "+x+","+y);
            } else if (action.getFunctor().equals("addHospital")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();                                               
                model.addHospital(x,y);
                logger.info("adding hospital at: "+x+","+y);
            } else if (action.getFunctor().equals("start")) {
				int c = (int)((NumberTerm)action.getTerm(0)).solve();
				criticalLeft = c;
				start = true;
			} else if (action.getFunctor().equals("victimFound")) {
				victimFound ++;
			} else if (action.getFunctor().equals("nextVictim")) {
				removeIndex = 0;
				notCritical = false;


				// System.out.println("Current Location" + currentLoc[0] + " " + currentLoc[1]);
				// System.out.println("Victim location left " + victimLocation.size());
				// System.out.println(victimLeft + " " + savedLocation.size());
				if(savedLocation.size() == victimLeft && victimFound == 3){
					saved = true;
					for(int i = 0; i < savedLocation.size(); i ++){
						int x = savedLocation.get(i)[0]; int y = savedLocation.get(i)[1];
						astar.setStart(currentLoc[0], currentLoc[1]);
						path = astar.getPath(x, y);
				//		System.out.println(path.size());
						if(i == 0){
							shortestPath = path;
						}
						if(path.size() < shortestPath.size()){
							shortestPath = path;
							removeIndex = i;
						}
					}
					savedLocation.remove(removeIndex);

					//	System.out.println("move towards " + savedLocation.get(removeIndex)[0] + " " + savedLocation.get(removeIndex)[1]);						
				}
				else if(victimLocation.size() != 0){
					askDoc = true;
					for(int i = 0; i < victimLocation.size(); i ++){
						int x = victimLocation.get(i)[0]; int y = victimLocation.get(i)[1];
						astar.setStart(currentLoc[0], currentLoc[1]);
						path = astar.getPath(x, y);
						if(i == 0){
							shortestPath = path;
						}
						if(path.size() < shortestPath.size()){
							shortestPath = path;
							removeIndex = i;
						}
					}

				//	System.out.println("move towards " + victimLocation.get(removeIndex)[0] + " " + victimLocation.get(removeIndex)[1]);
				}

				//for(Node cell: shortestPath){
				//	ev3.sendingPath(cell.x, cell.y);
				//}
				color = ev3.sendingPath(shortestPath);
				//System.out.println(color);
				currentLoc[0] = ev3.getX();
				currentLoc[1] = ev3.getY();


			} else if (action.getFunctor().equals("returnHospital")) {
				astar.setStart(currentLoc[0], currentLoc[1]);
				path = astar.getPath(0, 0);
				ev3.sendingPath(path);
				currentLoc[0] = ev3.getX();
				currentLoc[1] = ev3.getY();
				victimLeft --;
				/*CLOSE BUZZER*/
				ev3.sendingBeeps("stop");

				
			} else if (action.getFunctor().equals("pickUpVictim")) {
				ev3.sendingBeeps("beep");
				/*RING BUZZER */
				
			} else if (action.getFunctor().equals("critical")){
				criticalLeft --;
				if(victimLocation.size() != 0){
					victimLocation.remove(removeIndex);
				}
			//	System.out.println(criticalLeft);
			} else if (action.getFunctor().equals("noCritical")){
				 int flag = (int)((NumberTerm)action.getTerm(0)).solve();
				// if(flag == 2){
				// 	if(victimLocation.size() != 0){
				// 		victimLocation.remove(removeIndex);
				// 	}
				// }else{
					// System.out.println("SAVE");
					if(flag == 0){
						savedLocation.add(victimLocation.get(removeIndex));
					}

					// if(victimLocation.size() != 0){
						victimLocation.remove(removeIndex);
					// }
				// }
			}else if (action.getFunctor().equals("white")){
				if(victimLocation.size() != 0){
					victimLocation.remove(removeIndex);
				}

			}
			else {
                logger.info("executing: "+action+", but not implemented!");
                return true;
                // Note that technically we should return false here.  But that could lead to the
                // following Jason error (for example):
                // [ParamedicEnv] executing: addObstacle(2,2), but not implemented!
                // [paramedic] Could not finish intention: intention 6: 
                //    +location(obstacle,2,2)[source(doctor)] <- ... addObstacle(X,Y) / {X=2, Y=2, D=doctor}
                // This is due to the action failing, and there being no alternative.
            }
			model.moveNext(MEDIC);
			updatePercepts();
        } catch (Exception e) {
            e.printStackTrace();
        }
           
        informAgsEnvironmentChanged();
        return true;       
    }


    void updatePercepts() {
        clearPercepts();
		if(start){

			askDoctor = Literal.parseLiteral("doctor(" + currentLoc[0] + "," + currentLoc[1] + "," + color + ");");
			
			Location r1Loc = model.getAgPos(MEDIC);
			Literal pos1 = Literal.parseLiteral("pos(r1," + r1Loc.x + "," + r1Loc.y + ")");
		
			if(currentLoc[0] == 0 && currentLoc[1] == 0){
				addPercept(atH);
			}
			if(criticalLeft == 0){
				addPercept(noCritical);
			}
			if(victimLeft == 0){
				addPercept(noVictim);
			}
			if(victimFound == 3){
				addPercept(allVictim);
			}
			if(saved){
				System.out.println("saved");
			//	if(saveCounter == 0){
					addPercept(savedP);
			//		saveCounter ++
			//	}else if (saveCounter == 1){
				//	addPercept(save2);
			//	}
			}

			if(color.equals("YELLOW")){
				// if(counter == 0){
					// System.out.println("white1");
					addPercept(isWhite);
					// counter ++;
				// }else if(counter == 1){
					// System.out.println("white2");
					// addPercept(white2);
				// }

			}
			else if(askDoc){
				//victimFound ++;
				addPercept(askDoctor);
			}
			addPercept(pos1);
			askDoc = false;
			white = false;
		}
    }
	
    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
    // ======================================================================
    class RobotBayModel extends GridWorldModel {

        private RobotBayModel() {
            super(GSize, GSize, 2);	// The third parameter is the number of agents
			try{
				setAgPos(1,currentLoc[0],invertY(currentLoc[1]));

			}catch(Exception e){
				e.printStackTrace();
			}
			
            // initial location of Obstacles
            // Note that OBSTACLE is defined in the model (value 4), as
            // is AGENT (2), but we have to define our own code for the
            // victim and hospital (uses bitmaps, hence powers of 2)
        }
        
        // The JASON GridWorldView assumes that the origin is in the top left
        // hand corner, but all of the COMP329 descriptions assume 0,0 is in
        // the bottom left.  As we want to simply visualise the map, this fudge
        // ollows us to fix it.
        // It will probably cause issues elsewhere - SO DON'T USE THIS CLASS in
        // your solution!!!
        public int invertY(int x) { return (GSize-1)-x; }

        
        void addVictim(int x, int y) {
            add(VICTIM, x, invertY(y));
        }
        void addHospital(int x, int y) {
            add(HOSPITAL, x, invertY(y));
        }
        void addObstacle(int x, int y) {
            add(OBSTACLE, x, invertY(y));
        }
		
		void moveNext(int MEDIC) throws Exception{
			Thread.sleep(500);
			Location r1 = getAgPos(MEDIC);
			
			r1.x = currentLoc[0];
			r1.y = invertY(currentLoc[1]);
			//System.out.println("Agent Loc x : " + r1.x + "  Y : "+ r1.y);

			setAgPos(1,r1);
		}
		
    }
    
    // ======================================================================
    // This is a simple rendering of the map from the actions of the paramedic
    // when getting details of the victim and obstacle locations
    // You should not feel that you should use this code, but it can be used to
    // visualise the bay layout, especially in the early parts of your solution.
    // However, you should implement your own code to visualise the map.
    class RobotBayView extends GridWorldView {

        public RobotBayView(RobotBayModel model) {
            super(model, "COMP329 6x6 Robot Bay", 300);
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true);
            repaint();
        }
        
        
        /** draw application objects */
        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) {
            case ParamedicEnv.VICTIM:
                drawVictim(g, x, y);
                break;
            case ParamedicEnv.HOSPITAL:
                drawHospital(g, x, y);
                break;
           }
        }
        
        public void drawVictim(Graphics g, int x, int y) {
            //super.drawObstacle(g, x, y);
            g.setColor(Color.black);
            drawString(g, x, y, defaultFont, "V");
        }

        public void drawHospital(Graphics g, int x, int y) {
            //super.drawObstacle(g, x, y);
            g.setColor(Color.blue);
            drawString(g, x, y, defaultFont, "H");
        }
    }
    // ======================================================================
}