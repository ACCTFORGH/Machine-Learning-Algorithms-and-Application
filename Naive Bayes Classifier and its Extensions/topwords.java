import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.lang.Math;
import java.text.DecimalFormat;


public class topwords{

	public static double calProb(int nk, int n, int volc, int q){
		double prob=0;
		
		prob = (double)(nk+q)/(double)(n+q*volc);
		
		return prob;
	}

	public static List sortMap(HashMap hmap, int topRange){

		List list = new LinkedList(hmap.entrySet());
		Collections.sort(list, new MapComparator());
		list = list.subList(0, topRange);
		return list;

	}
	
	public static void main(String[] args) {
		
		//read train file list
		BufferedReader br = null;
		List<String> trainFile = new LinkedList<String>();
		
		//
		int stopRange = 0; //Q3 Adjustment 
		int q = 1; //Q4 Adjustment
		
		//Loading file list
		try{

			br = new BufferedReader(new FileReader(args[0]));
			String fileLine ="";
				
			while((fileLine=br.readLine())!=null){
				trainFile.add(fileLine);
			}
							
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
			prob = calProb(conMap.get(key), conPosition, volcSize,q);
			conProb.put(key,prob);
		}
		
		for(String key:libMap.keySet()){
			prob = calProb(libMap.get(key), libPosition, volcSize,q);
			libProb.put(key, prob);
		}

		//System.out.println(libMap.get("helga"));
		
		List<Map.Entry> conList = new LinkedList<Map.Entry>();
		List<Map.Entry> libList = new LinkedList<Map.Entry>();

		conList = sortMap(conProb,20);
		libList = sortMap(libProb,20);

		DecimalFormat decimalformat = new DecimalFormat("#.0000");
		for(int i=0;i<libList.size();i++)
		System.out.println(libList.get(i).getKey()+" "+decimalformat.format(libList.get(i).getValue()));
		System.out.println();
		for(int i=0;i<conList.size();i++)
		System.out.println(conList.get(i).getKey()+" "+decimalformat.format(conList.get(i).getValue()));

	}
}

class MapComparator implements Comparator{

	public int compare(Object o1, Object o2) {
  
        o1 = ((Map.Entry)o1).getValue();
        o2 = ((Map.Entry)o2).getValue();

        return ((Comparable)o2).compareTo(o1);
	}
}


