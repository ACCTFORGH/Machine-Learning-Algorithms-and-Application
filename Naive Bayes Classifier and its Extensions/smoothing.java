import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.lang.Math;

public class smoothing {
	
	public static double calLogProb(int nk, int n, int volc, double q){
		double prob=0;
		
		prob = Math.log((double)(nk+q)/(double)(n+q*volc));
		
		return prob;
	}
	
	public static void main(String[] args) {
		
		//read train file list
		BufferedReader br = null;
		List<String> trainFile = new LinkedList<String>();
		List<String> testFile = new LinkedList<String>();
		
		//
		int stopRange = 0; //Q3 Adjustment 
		double q = Double.valueOf(args[2]); //Q4 Adjustment
		
		//Loading file list
		try{
			
			for(int i=0;i<2;i++){
				br = new BufferedReader(new FileReader(args[i]));
				String fileLine ="";
				
				while(i==0 && (fileLine=br.readLine())!=null){
					trainFile.add(fileLine);
				}
				
				while(i==1 && (fileLine=br.readLine())!=null){
					testFile.add(fileLine);
				}}
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//read and parse training files into word and label counts
		HashMap<String, Integer> libMap = new HashMap<String, Integer>();
		HashMap<String,Integer> conMap = new HashMap<String, Integer>();
		int conPosition=0;
		int libPosition=0;
		
		try{
			for(String s:trainFile){
				br= new BufferedReader(new FileReader(s));
				String tempLine ="";
				while(s.startsWith("con") && (tempLine=br.readLine()) != null){
					tempLine = tempLine.toLowerCase().trim();
					conPosition +=1;
					if(conMap.containsKey(tempLine)){
						conMap.put(tempLine,conMap.get(tempLine)+1);
					}else{
						conMap.put(tempLine, 1);
					}}
				
				while(s.startsWith("lib") && (tempLine=br.readLine()) != null){
					tempLine = tempLine.toLowerCase().trim();
					libPosition +=1;
					if(libMap.containsKey(tempLine)){
						libMap.put(tempLine,libMap.get(tempLine)+1);
					}else{
						libMap.put(tempLine, 1);
					}}
				
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Calculating the vocabulary size
		
		HashSet<String> libKeys = new HashSet<String>(libMap.keySet());
		HashSet<String> conKeys = new HashSet<String>(conMap.keySet());
		HashSet<String> keys = (HashSet<String>) libKeys.clone();
		keys.addAll(conKeys);
		int volcSize = keys.size();

		
		//HashMap to store the calculated probability of each word of each label
		HashMap<String, Double> conProb = new HashMap<String, Double>();
		HashMap<String, Double> libProb = new HashMap<String, Double>();
		double prob = 0.0;
		
		for(String key:conMap.keySet()){
			prob = calLogProb(conMap.get(key), conPosition, volcSize,q);
			conProb.put(key,prob);
		}
		
		for(String key:libMap.keySet()){
			prob = calLogProb(libMap.get(key), libPosition, volcSize,q);
			libProb.put(key, prob);
		}
		

		//Use the trained model to classify the test data
		try{

			int errorCounter = 0;
			int counter =0;

			for(String filename:testFile){

				br = new BufferedReader(new FileReader(filename));
				String tempLine ="";
				double libScore =0;
				double conScore =0;
				filename = filename.substring(0,3);

				//Read words from a file
				while ((tempLine=br.readLine()) != null){

					tempLine = tempLine.toLowerCase().trim();
					if (tempLine != null){

						if(libKeys.contains(tempLine)){
							libScore += libProb.get(tempLine);
						}else if (keys.contains(tempLine)){
							libScore += calLogProb(0,libPosition,volcSize,q);
						}
					
						if(conKeys.contains(tempLine)){
							conScore += conProb.get(tempLine);
						}else if (keys.contains(tempLine)){
							conScore += calLogProb(0,conPosition,volcSize,q);
					}}
					
				}

				boolean acc = true;


				if (libScore >= conScore){
					if (filename.equals("con")){
						acc = false;
						errorCounter += 1;
					}
					System.out.println("L");

				}else{
					if (filename.equals("lib")){
						acc = false;
						errorCounter += 1;
					}
					System.out.println("C");
				}
				counter += 1;
			}
			
			System.out.println("Accurary: "+ (double)Math.round((1-(double)errorCounter/(double)counter)*10000)/10000);


		}catch(Exception e){
			e.printStackTrace();
		}
				
	}
		}
