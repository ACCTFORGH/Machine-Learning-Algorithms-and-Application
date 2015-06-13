import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class viterbi {

	private static double logSum(double left, double right){
		
		if(right < left) {
			return left + Math.log1p(Math.exp(right - left));
		}else if(left < right){
			return right + Math.log1p(Math.exp(left - right));
		}else{
			return left + Math.log1p(1);
		}
	}
	
	private static void print(Object o){
		System.out.println(o);
	}

	private static double log(double d){
		return Math.log(d);
	}
	
	public static void main(String[] args){
		
/**********************READING MODEL PARAMETERS*******************************/		
		BufferedReader br;
		
		//Initialization Variables
		Double[] initProb = new Double[8]; //Array of probabilities of initial states
		String[] states = new String[8];
		Map<Double, String> initMatrix = new HashMap<Double,String>(); //key: probability value:init state
		String initState="";
		
		//hmm-trans variables
		double[][] transMatrix = new double[8][8]; //store state and trans probability: row state, column:probability
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
			//print(Arrays.asList(states));
			//Arrays.sort(initProb);
			
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
					transMatrix[transRow][transClmn] = Double.valueOf(sLine2[1].trim());
					transClmn += 1;
					
				}
				transRow += 1;
			}
			//print(Arrays.asList(transMatrix[7]));
			//print(transIndexR);
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
			//print(emitIndexR);
			
			
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
		
/****************************Testing on dev dataset*******************************************/
		//tracking variables
		String[][] q;
		String[][] qt;
		double[] vp;
		double[] vpt;
		int T;
		double maxVP;
		double tempVP;
		int k;
		for(int l=0;l<devMatrix.size();l++){
			//initialize tracking variables for each sentence
			T=devMatrix.get(l).size();
			q= new String[states.length][T];
			qt = new String[states.length][T];
			vp= new double[states.length];
			vpt = new double[states.length];
			double aji=0;
			for(int t=0;t<T;t++){
				qt = new String[states.length][T];
				vpt = new double[states.length];
				//initialize vp
				if(t==0){
					for(int i=0;i<8;i++){
						vp[i]=log(initProb[i])+log(Double.valueOf(emitMatrix[i][emitIndexC.get(devMatrix.get(l).get(0))]));
						//print(vp[i]);
						q[i][t]=states[i];
					}
				}else{
					for(int i=0;i<states.length;i++){
						maxVP=Double.NEGATIVE_INFINITY;
						k=states.length;
						for(int j=0;j<states.length;j++){
							aji=transMatrix[j][i];
							tempVP=vp[j]+log(aji)+log(Double.valueOf(emitMatrix[i][emitIndexC.get(devMatrix.get(l).get(t))]));
							if(tempVP>maxVP){
								maxVP=tempVP;
								k=j;
							}
						}
						vpt[i]=maxVP;
						for(int m=0;m<t;m++){
							qt[i][m]=q[k][m];
						}
						qt[i][t]=states[i];
					}
					vp=vpt.clone();
					q=qt.clone();
				}
			}
			maxVP=Double.NEGATIVE_INFINITY;
			k=states.length;
			for(int i=0;i<states.length;i++){
				if(vpt[i]>maxVP){
					maxVP=vpt[i];
					k=i;
				}
			}
			for(int i=0;i<qt[k].length;i++){
				if(i==qt[k].length-1){
					System.out.print(devMatrix.get(l).get(i)+"_"+qt[k][i]+"\n");
				}else{
					System.out.print(devMatrix.get(l).get(i)+"_"+qt[k][i]+" ");
				}
			}
		}
	}
}
