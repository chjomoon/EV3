package robotics2;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
public class removeDuplicate {
   public static void main(String args[]) throws Exception {
      String filePath = "M:\\git\\RoboticsAssignment2\\assignment2\\src\\java\\data.txt";
      String input = null;
      //Instantiating the Scanner class
      Scanner sc = new Scanner(new File(filePath));
      //Instantiating the FileWriter class
      FileWriter writer = new FileWriter("M:\\git\\RoboticsAssignment2\\assignment2\\src\\java\\output.txt");
      //Instantiating the Set class
      Set set = new HashSet();
      while (sc.hasNextLine()) {
         input = sc.nextLine();
         if(set.add(input)) {
        	 String newInput = input.replaceAll("\\s{2,}", " ");
            writer.append(newInput+"\n");
         }
      }
      writer.flush();
      System.out.println("Contents added............");
   }
}