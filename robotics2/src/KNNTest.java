package kNNTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

public class KNNTest {
	
	private static double[] distance = new double[4];
	private static double[] questionDistances;
	
	public static ArrayList<double[]> DATA = new ArrayList<double[]>();
//			{0.024509804, 0.04019608, 0.026470589, 0}, {0.02745098, 0.039215688, 0.026470589, 0}, {0.02745098, 0.038235296, 0.026470589, 0},
//			{0.02745098, 0.038235296, 0.026470589, 0}, {0.026470589, 0.038235296, 0.02745098, 0},
//			{0.016666668, 0.050980393, 0.021568628, 1}, {0.015686275, 0.05, 0.020588236, 1}, {0.015686275, 0.05, 0.020588236, 1},
//			{0.01764706, 0.05, 0.01764706, 1}, {0.016666668, 0.048039217, 0.016666668, 1},
//			{0.03137255, 0.18333334, 0.22254902, 2}, {0.032352943, 0.18627451, 0.21862745, 2}, {0.032352943, 0.1872549, 0.21764706, 2},
//			{0.03137255, 0.19019608, 0.21666667, 2}, {0.033333335, 0.19313726, 0.21666667, 2}
////			{0.015686275, 0.050980393, 0.01764706, 9} Test data
////			0.023529412 0.039215688 0.028431373 0
////			0.051960785 0.25588235  0.22352941  2
//	};
	
	private static int k = 6;
	private static int CLASSFIY = 3;
	
	public KNNTest() throws Exception {
		File file = new File("E:\\COMP329 Assignment2\\kNNTest\\src\\kNNTest\\z_dataSet1.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String s;
		String[] dataSet;
		
		int count = 0;
		while ((s = br.readLine()) != null) {
			dataSet = s.split("\\s+");
			double[] stringToDouble = new double[4];
			for(int i = 0; i < 4; i ++) {
				stringToDouble[i] = Double.parseDouble(dataSet[i]);
//				System.out.print(stringToDouble[i] + " ");
			}
//			System.out.println("");
			DATA.add(stringToDouble);
		}
		System.out.println(DATA.get(1)[0] + " " + DATA.get(1)[1] + " "  + DATA.get(1)[2] + " " + DATA.get(1)[3] + " " );
		System.out.println(DATA.get(1000)[0] + " " + DATA.get(1000)[1] + " "  + DATA.get(1000)[2] + " " + DATA.get(1000)[3] + " " );
	}
	
	public static String getNearest() {
		questionDistances = new double[DATA.size()];
		for(int i = 0; i < DATA.size(); i ++) {
			questionDistances[i] = distance(DATA.get(i), distance);
		}
		int[] distanceArray = paraseKDistince(questionDistances, k);
		for(int distance : distanceArray) {
		//	System.out.println(distance);
		}
		int nearest = vote(distanceArray);
		System.out.println(nearest);
		switch(nearest) {
		case 0:
			return "BLACK";
		case 1:
			return "BURGUNDY";
		case 2:
			return "CYAN";
		case 3:
			return "YELLOW";
		}
		return "WHITE";
	}
	public static void main(String[] args) throws Exception {
		
		//12345678 can be replaced by any numbers which are not out of the range of double
		//For now, when used the data {0, 0, 0, 0}, is showed is burgundy, but is should be black.
		
//		KNNTest knn = new KNNTest();
//		
//		double[] questionDistinces = new double[DATA.length];
//		
//		for (int i = 0; i < DATA.length; i++) {
//			double[] item = DATA[i];
//			questionDistinces[i] = knn.distance(item, distance);
//		}
//		
////		System.out.println("临近距离集合：" + Arrays.toString(questionDistinces));
//		
//		int nearest[] = knn.paraseKDistince(questionDistinces, k);
////		System.out.println("K 个最临近距离下标集合：" + Arrays.toString(nearest));
//		System.out.println("{0.051960785 0.25588235  0.22352941 x}的 x 位置求解为：" + knn.vote(nearest));
//		
//		if (knn.vote(nearest) == 0) {
//			isBlack();
//		} else if (knn.vote(nearest) == 1) {
//			isBurgundy();
//		} else if (knn.vote(nearest) == 2) {
//			isCyan();
//		} else {
//			System.out.println("The Colour is Empty");
//		}
//		System.out.println(getNearest());
		KNNTest k = new KNNTest();
		setTest(0.029411765, 0.05980392, 0.023529412);
		System.out.println(k.getNearest());
	}
	
	public static void setTest(double R, double G, double B) {
		distance[0] = R;
		distance[1] = G;
		distance[2] = B;
		distance[3] = 123;
	}
	
	//计算临近距离[除开求解分类]
	public static double distance(double[] paraFirstData, double[] paraSecondData){
		
		double tempDistince = 0;
		
		
		if ((paraFirstData != null && paraSecondData != null) && paraFirstData.length == paraSecondData.length) {
		
			for (int i = 0; i < paraFirstData.length-1; i++) {
				tempDistince += Math.abs(paraFirstData[i] - paraSecondData[i]);
			}
		} else {
			System.out.println("False!!!");
		}
		return tempDistince;
	}
	
	//对临近距离排序,从小到大[这里采用冒泡排序]
	public double[] sortDistinceArray(double []paraDistinceArray){
	
		if (paraDistinceArray != null && paraDistinceArray.length > 0) {
		
			for (int i = 0; i < paraDistinceArray.length; i++) {
			
				for (int j = i + 1; j < paraDistinceArray.length; j++) {
				
					if (paraDistinceArray[i] > paraDistinceArray[j]) {
						double temp = paraDistinceArray[i];
						paraDistinceArray[i] = paraDistinceArray[j];
						paraDistinceArray[j] = temp;
					}
				}
			}
		}
		
		return paraDistinceArray;
	}
	
	//获取临近值数组中，从近到远获取k个值为新数组
	public double[] paraseKDistince(double[] sortedDistinceArray, String sortTypeStr, int k) {
	
		double[] kDistince = new double[k];
		
		if (SortType.ASC.equals(sortTypeStr)) {
		
			for (int i = 0; i < k; i++) {
				kDistince[i] = sortedDistinceArray[i];
			}
		}
	
		if (SortType.DES.equals(sortTypeStr)) {
		
			for (int i = 0; i < k; i++) {
				kDistince[i] = sortedDistinceArray[sortedDistinceArray.length - i - 1];
			}
		}
	
		return kDistince;
	}
	
	//获取临近距离中的K的距离的下标数组
	public static int[] paraseKDistince(double[] distinceArray,int k) {
	
		double[] tempDistince = new double[k + 2];
		int[] tempNearest = new int[k + 2];
		
		//初始化两个数组
		tempDistince[0] = Double.MIN_VALUE;
		
		for (int i = 1; i < k + 2; i++) {
			tempDistince[i] = Double.MAX_VALUE;
			tempNearest[i] = -1;
	
		}
		
		//准备筛选临近距离
		for (int i = 0; i < distinceArray.length; i++) {
	
			for (int j = k; j >= 0; j--) {
			
				if (distinceArray[i] < tempDistince[j]) {
					tempDistince[j + 1] = tempDistince[j];
					tempNearest[j + 1] = tempNearest[j];
				} else {
					tempDistince[j + 1] = distinceArray[i];
					tempNearest[j + 1] = i;
					break;
				}
			}
		}
	
		int[] returnNearests = new int[k];
		
		for (int i = 0; i < k; i++) {
			returnNearests[i] = tempNearest[i + 1];
		}
	
		return returnNearests;
	}
	

	public static int getClasssify(int index){
		return (int)DATA.get(index)[3];
	}
	

	public static int vote(int[] nearestIndex) {
	
		int[] votes = new int[CLASSFIY];
		
		for (int i = 0; i < nearestIndex.length; i++) {
			votes[getClasssify(nearestIndex[i])]++;
		}
	
//		System.out.println("分类投票数集合：" + Arrays.toString(votes));
		int tempMajority = -1;
		int tempMaximalVotes = -1;
		
		for (int i = 0; i < votes.length; i++) {
		
			if (votes[i] > tempMaximalVotes) {
				tempMaximalVotes = votes[i];
				tempMajority = i;
			}
		}
//		System.out.println("投票数最高：" + tempMaximalVotes + ",分类是：" + tempMajority);
		return tempMajority;
	}
	
	public class SortType {
		
		public static final String DES = "des";
		public static final String ASC = "asc";
	}
	
	public static void isBlack() {
		System.out.println("The Colour is Black");
	}
	
	public static void isBurgundy() {
		System.out.println("The Colour is Burgundy");
	}

	public static void isCyan() {
		System.out.println("The Colour is Cyan");
	}
	
//	public boolean isBlack1() {
//		
//		double distince[] = {0.051960785, 0.25588235, 0.22352941, 0};
//		
//		KNNTest knn = new KNNTest();
//		
//		double[] questionDistinces = new double[DATA.length];
//		
//		for (int i = 0; i < DATA.length; i++) {
//			double[] item = DATA[i];
//			questionDistinces[i] = knn.distince(item, distince);
//		}
//		
//		int nearest[] = knn.paraseKDistince(questionDistinces, k);
//		
//		if (knn.vote(nearest) == 0) {
//			System.out.println("The Colour is Black");
//			return true;
//		} else {
//			return false;
//		}
//	}
}










