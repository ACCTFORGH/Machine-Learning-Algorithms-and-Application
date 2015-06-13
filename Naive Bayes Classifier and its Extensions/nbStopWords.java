import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.lang.Math;
import java.text.DecimalFormat;

public class nbStopWords {
	
	public static double calLogProb(int nk, int n, int volc, int q){
		double prob=0;
		
		prob = Math.log((double)(nk+q)/(double)(n+q*(volc+1)));
		//System.out.println(prob);
		
		return prob;
	}

	public static List sortMap(HashMap hmap, int stopRange){

		List list = new LinkedList(hmap.entrySet());
		Collections.sort(list, new MapComparator());
		list = list.subList(0, stopRange);
		return list;

	}
	
	/*public static HashMap<String, Double> truncMap(HashMap libmap, HashMap conmap, int stopRange){

		List<Map.Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(libmap.entrySet());

		Collections.sort(list,new MapComparator());
		list = list.subList(stopRange,list.size());
		
		HashMap<String, Double> truncMap = new LinkedHashMap<String, Double>();
		for (int i=0;i<list.size();i++){
			truncMap.put(((Entry<String, Double>)list.get(i)).getKey(),((Entry<String,Double>)list.get(i)).getValue());
		}
		return truncMap;

	}*/
	
	public static void main(String[] args) {
		
		//read train file list
		BufferedReader br = null;
		List<String> trainFile = new LinkedList<String>();
		List<String> testFile = new LinkedList<String>();
		
		//
		int stopRange = Integer.valueOf(args[2]); //Q3 Adjustment 
		int q = 1; //Q4 Adjustment
		
		/*******Loading file list**********/
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
		
		/****************Training*****************/
		//read and parse training files into word and label counts
		HashMap<String, Integer> libMap = new HashMap<String, Integer>();
		HashMap<String,Integer> conMap = new HashMap<String, Integer>();
		HashMap<String, Integer> totMap = new HashMap<String, Integer>();
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
					}
					
					if(totMap.containsKey(tempLine)){
						totMap.put(tempLine,totMap.get(tempLine)+1);
					}else{
						totMap.put(tempLine, 1);
					}
				}
				
				while(s.startsWith("lib") && (tempLine=br.readLine()) != null){
					tempLine = tempLine.toLowerCase().trim();
					libPosition +=1;
					if(libMap.containsKey(tempLine)){
						libMap.put(tempLine,libMap.get(tempLine)+1);
					}else{
						libMap.put(tempLine, 1);
					}
					
					if(totMap.containsKey(tempLine)){
						totMap.put(tempLine,totMap.get(tempLine)+1);
					}else{
						totMap.put(tempLine, 1);
					}
				}
				
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Pre-process the maps to remove the top N words
		List<Map.Entry<String, Double>> remList = sortMap(totMap, stopRange);
		for (Entry e:remList){
			String key = (String) e.getKey();
			//System.out.println(key);
			if(conMap.keySet().contains(key)){
				conPosition -= conMap.get(key);
				conMap.remove(key);
			}
			
			if(libMap.keySet().contains(key)){
				libPosition -= libMap.get(key);
				libMap.remove(key);
			}
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
		
		for(String key:conKeys){
			prob = calLogProb(conMap.get(key), conPosition, volcSize,q);
			conProb.put(key,prob);
		}
		
		for(String key:libKeys){
			prob = calLogProb(libMap.get(key), libPosition, volcSize,q);
			libProb.put(key, prob);
		}

					
		/************************Testing*****************************/
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
					if(libKeys.contains(tempLine)){
						libScore += libProb.get(tempLine);
					}else if(keys.contains(tempLine)){
						libScore += calLogProb(0,libPosition,volcSize,q);
					}
					
					if(conKeys.contains(tempLine)){
						conScore += conProb.get(tempLine);
					}else if(keys.contains(tempLine)){
						conScore += calLogProb(0,conPosition,volcSize,q);
					}

					
				}

				if (libScore >= conScore){
					if (filename.equals("con")){
						errorCounter += 1;
					}
					System.out.println("L");
				}else{
					if (filename.equals("lib")){
						errorCounter += 1;
					}
					System.out.println("C");
				}
				counter += 1;
			}

			DecimalFormat df = new DecimalFormat("#.0000");
			System.out.println("Accurary: "+ df.format(1-(double)errorCounter/(double)counter));


		}catch(Exception e){
			e.printStackTrace();
		}
				
	}
		}

class MapComparator implements Comparator{

	public int compare(Object o1, Object o2) {
  
        o1 = ((Map.Entry)o1).getValue();
        o2 = ((Map.Entry)o2).getValue();

        return ((Comparable)o2).compareTo(o1);
	}
}
