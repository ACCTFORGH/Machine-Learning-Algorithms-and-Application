import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;


public class alpha {
	
	private static void print(Object o){
		System.out.println(o);
	}
	
	private static double logSum(double left, double right){
		
		if(right < left) {
			return left + Math.log1p(Math.exp(right - left));
		}else if(left < right){
			return right + Math.log1p(Math.exp(left - right));
		}else{
			return left + Math.log1p(1);
		}
	}
	
	private static double log(double d){
		return Math.log(d);
	}
	
	public static void main(String[] args) throws InterruptedException{
		
/**********************READING MODEL PARAMETERS*******************************/		
		BufferedReader br;
		
		//Initialization Variables
		Double[] initProb = new Double[8]; //Array of probabilities of initial states
		String[] states = new String[8];
		Map<Double, String> initMatrix = new HashMap<Double,String>(); //key: probability value:init state
		String initState="";
		
		//hmm-trans variables
		String[][] transMatrix = new String[8][8]; //store state and trans probability: row state, column:probability
		Map<String,Integer> transIndexC = new HashMap<String,Integer>(); //key:destination-state value:index 
		Map<String,Integer> transIndexR = new HashMap<String,Integer>();//key: original state value:row index
		
		//hmm-emit variables
		String[][] emitMatrix = new String[0][0]; //store state and symbol probability
		int emitClmnSize=0;
		Map<String,Integer> emitIndexC = new HashMap<String,Integer>(); // key: symbol value: column index
		Map<String,Integer> emitIndexR = new HashMap<String,Integer>(); //key:state value: row index
		
		//dev variables
		ArrayList<ArrayList<String>> devMatrix = new ArrayList<ArrayList<String>>();
		
		
		try{
			String line = ""; //raw input line
			String[] sLine;
			
			//Read hmm-prior
			br = new BufferedReader(new FileReader(args[3]));
			int stateCounter =0;
			while((line=br.readLine())!=null){
				sLine = line.split(" ");
				initMatrix.put(Double.valueOf(sLine[1]), sLine[0]);
				initProb[stateCounter] = Double.valueOf(sLine[1]);
				states[stateCounter]=sLine[0].trim();
				stateCounter +=1;
			}
			
			//Read hmm-trans
			int indexCounter=0;
			int transRow=0;
			int transClmn=0;
			br = new BufferedReader(new FileReader(args[1]));
			String[] sLine2;
			while((line=br.readLine()) != null){
				sLine = line.split(" ");
				transClmn = 0;
				//Index the original states
				transIndexR.put(sLine[0].trim(), transRow);
				//indexing the destination states
				for(String s: Arrays.copyOfRange(sLine,1,(sLine.length))){
					sLine2=s.split(":");
					if (indexCounter <= (sLine.length-2)){
						transIndexC.put(sLine2[0].trim(),indexCounter);
						indexCounter += 1;
					}
					
					//Storing value into the array
					transMatrix[transRow][transClmn] = sLine2[1].trim();
					transClmn += 1;
					
				}
				transRow += 1;
			}

			//Read emit
			int emitRow=0;
			int emitClmn=0;
			indexCounter=0;
			br = new BufferedReader(new FileReader(args[2]));
			while((line=br.readLine()) != null){
				sLine = line.split(" ");
				emitClmn = 0;
				
				//get the number of symbols
				if(emitRow == 0){
					emitClmnSize = sLine.length-1;
					emitMatrix= new String[8][emitClmnSize];
				}
				//indexing the rows (states)
				emitIndexR.put(sLine[0].trim(), emitRow);
				
				//indexing the column (symbols)
				for(String s: Arrays.copyOfRange(sLine,1,(sLine.length))){
					sLine2=s.split(":");
					if (indexCounter <= (sLine.length-2)){
						emitIndexC.put(sLine2[0].trim(),indexCounter);
						indexCounter += 1;
					}
					
					//Storing value into the array
					emitMatrix[emitRow][emitClmn] = sLine2[1].trim();
					emitClmn += 1;
				}
				emitRow += 1;
				
			}
			
			
			//Read dev
			int devRow=0;
			int devClmn=90;
			br = new BufferedReader(new FileReader(args[0]));
			while((line=br.readLine()) != null){
				sLine = line.split(" ");
				devClmn=0;
				for(String s:sLine){
					if (devClmn == 0) devMatrix.add(new ArrayList<String>());
					devClmn += 1;
					devMatrix.get(devRow).add(s.trim());
				}
				devRow += 1;
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
/************************Testing on the dev Matrix******************************/
		double[] atArray = new double[8];
		double[] attArray = new double[8];
		double aij=0;
		double pol=0.0;
		double[] temp = new double[8];
		print("Log Probability of Each Sentence in Dev.txt File:");
		//iterate through each sequence
		for (int k=0;k<devMatrix.size();k++){               
			atArray = new double[8];
			attArray = new double[8];
			//iterate through each symbol
			for(int t=0;t<devMatrix.get(k).size();t++){
				Arrays.fill(attArray, 0.0);
				if(t==0){
					//Traverse according to the sequence in emitIndexR
					for(int i=0;i<emitIndexR.size();i++){
						atArray[i]=log(initProb[i])+log(Double.valueOf(emitMatrix[i][emitIndexC.get(devMatrix.get(k).get(t))]));
					}
				}else{
					//iterate through destination states
					for(int j=0;j<emitIndexR.size();j++){
						//iterate through origin states
						for(int i=0;i<emitIndexR.size();i++){
							aij=Double.valueOf(transMatrix[i][j]);
							temp[i]=atArray[i]+log(aij);
							
							if(i==0){
								attArray[j]=atArray[i]+log(aij);
							}else{
								attArray[j]= logSum(attArray[j],(atArray[i]+log(aij)));
							}
						}
						attArray[j] += log(Double.valueOf(emitMatrix[j][emitIndexC.get(devMatrix.get(k).get(t))]));

					}
					atArray = attArray.clone();
				}
			}
			pol = attArray[0];
			for(int i=1;i<attArray.length;i++){
				pol = logSum(pol,attArray[i]);
			}
			print(pol);
		}
	}

}