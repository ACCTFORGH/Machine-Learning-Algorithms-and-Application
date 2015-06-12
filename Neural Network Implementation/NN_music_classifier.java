import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.*;
import java.lang.Math;

/* Two layer neural network classifier*/
/*Training Takes 80 seconds*/

//Assume all hidden layer neurons are connected to input layer neurons

public class NN_music_classifier {
	
public static void main(String [] args){
	
	//Reading the data
	BufferedReader bufferReader = null;
	ArrayList<String[]> trainData = new ArrayList<String[]>();
	double maxY=0, minY=2999;
	double maxL=0, minL=100;
	try{
		//File newFile = new File(arg);//args[0]
		bufferReader = new BufferedReader(new FileReader(args[0]));//newFile
		String fileLine;
		int counter =1;
		while((fileLine=bufferReader.readLine()) != null){
			//Skip the title line
			if (counter ==1){
					counter += 1;
					continue;
			}
			
			String[] str = fileLine.split(",");
			
			//Get the max year and min year value
			if(Double.valueOf(str[0])>maxY){
				maxY=Double.valueOf(str[0]);
			}else if(Double.valueOf(str[0])<minY){
				minY=Double.valueOf(str[0]);
			}
			
			//get the max length and min length value
			if(Double.valueOf(str[1])>maxL){
				maxL=Double.valueOf(str[1]);
			}else if(Double.valueOf(str[1])<minL){
				minL=Double.valueOf(str[1]);
			}
			
			//change yes/no to 1/0 for Jazz,Rock attribute, and the label
			for(int j=2;j<=4;j++){
				if (str[j].toLowerCase().trim().equals("yes")){
					str[j]="1";
				}else if(str[j].toLowerCase().trim().equals("no")){
					str[j]="0";
				}
			}
			trainData.add(str);
			
		}}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	
	//Create the layers
	int inputSize=4+1;
	int hiddenSize=5+1;
	double lr=0.05; // learning rate
    Node[] hiddenLayer = new Node[hiddenSize];//hidden layer
	double[] oWeightsInit = new double[hiddenSize]; //used to initialize oNode.w
	double[] hWeightsInit = new double[inputSize];
    
    //Output: Initialize the output node weights with random numbers between -1 and 1
	double max=0.1;
	double min=-0.1;
	for (int i=1;i<oWeightsInit.length;i++){
        double rn = min+Math.random()*(max-min);
        oWeightsInit[i]=rn;
    }
	
	//Output: Initialize Output nodes
    Node oNode = new Node(hiddenSize,"output",oWeightsInit); // output node Node(int cArraySize, String type, double[] w)
    
	//Hidden: initialize the hidden layer
	for(int i=0;i<hiddenSize;i++){
		
		for (int j=1;j<hWeightsInit.length;j++){
	        double rn = min+Math.random()*(max-min);
	        hWeightsInit[j]=rn;
	    }
		
		Node iNode = new Node(inputSize,"hidden",hWeightsInit); //Node(int cArraySize, String type, double[] w)
		hiddenLayer[i]=iNode;
	}
	
	long time1=System.currentTimeMillis()/1000;
	long time2=System.currentTimeMillis()/1000;
	String[] input;
	double[] iOutput = new double[inputSize];//input layer output
	double[] hOutput = new double[hiddenSize];//hidden layer output
	ArrayList<String[]> st=trainData;
	
	//Calculate output and update weights
	int counter =0;
	while ((time2-time1) <= 80){
		double lms=0;
		counter += 1;
		for(String[] s:trainData){
			if(counter == 1){
				s[0]=Double.toString((Double.valueOf(s[0])-minY)/(maxY-minY));
				s[1]=Double.toString((Double.valueOf(s[1])-minL)/(maxL-minL));
			}
			//System.out.println(Arrays.toString(s)+"s[0]"+s[0] +" "+"s[1]"+s[1]+" "+" "+maxY+" "+minY+" "+maxL+" "+minL);
			double tk=Double.valueOf(s[s.length-1]);
			
			//the output from the input layer
			iOutput[0]=1.0;
			for(int j=1;j<inputSize;j++){
				iOutput[j] = Double.valueOf(s[j-1]);
			}

			//calculate hidden layer output
			hOutput[0]=1.0;
			for(int j=1;j<hiddenSize;j++){
				hiddenLayer[j].childInput=iOutput.clone();
				hiddenLayer[j].calOutput();
				hOutput[j]=hiddenLayer[j].output;
			}
			//Calculate output node output
			oNode.childInput=hOutput.clone();
			oNode.calOutput();
			
			//Calculate output node error
			oNode.kValue=tk;
			oNode.calONError();

			//Calculate hidden layer node error
			for(int j=0;j<hiddenLayer.length;j++){
				hiddenLayer[j].calHLError(oNode.error,oNode.w[j]);
				//System.out.println("hidden layer"+Double.toString(hiddenLayer[j].error));
			}
			//Update output node weight w
			for(int j=0;j<oNode.w.length;j++){
				oNode.w[j]+= lr*oNode.error*oNode.childInput[j];
			}
			
			//Update hidden layer weight w
			for(int j=0;j<hiddenSize;j++){
				for(int k=0;k<inputSize;k++){
					hiddenLayer[j].w[k] += lr*hiddenLayer[j].error*hiddenLayer[j].childInput[k];
				}
			}
			
			//Calculate LMS
			lms += (1/2.0)*Math.pow((tk-oNode.output), 2);
			
		}
		System.out.println(lms);
		time2=System.currentTimeMillis()/1000;
	}
	
	System.out.println("TRAINING COMPLETED! NOW PREDICTING.");
	
	//Read the dev file
	BufferedReader devBR = null;
	ArrayList<String[]> devData = new ArrayList<String[]>();
	double devmaxY=0, devminY=2999;
	double devmaxL=0, devminL=100;
	try{
		//File devFile = new File(arg[1]);//args[0]
		devBR = new BufferedReader(new FileReader(args[1]));//newFile
		String devFileLine;
		counter =1;
		while((devFileLine=devBR.readLine()) != null){
			if (counter ==1){
				counter += 1;
				continue;
			}	
			
			String[] devstr = devFileLine.split(",");
			
			//Get the max year and min year value
			if(Double.valueOf(devstr[0])>devmaxY){
				devmaxY=Double.valueOf(devstr[0]);
			}else if(Double.valueOf(devstr[0])<devminY){
				devminY=Double.valueOf(devstr[0]);
			}
			
			//get the max length and min length value
			if(Double.valueOf(devstr[1])>devmaxL){
				devmaxL=Double.valueOf(devstr[1]);
			}else if(Double.valueOf(devstr[1])<devminL){
				devminL=Double.valueOf(devstr[1]);
			}
			
			//change yes/no to 1/0 for Jazz,Rock attribute
			for(int j=2;j<=3;j++){
				if (devstr[j].toLowerCase().trim().equals("yes")){
					devstr[j]="1";
				}else if(devstr[j].toLowerCase().trim().equals("no")){
					devstr[j]="0";
				}
			}
			
			devData.add(devstr);

		}}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	
	//Predicting based on the trained model
	for(String[] s:devData){

		s[0]=Double.toString((Double.valueOf(s[0])-minY)/(maxY-minY));
		s[1]=Double.toString((Double.valueOf(s[1])-minL)/(maxL-minL));
		
		//Output from the input layer
		iOutput[0]=1.0;
		for(int j=1;j<inputSize;j++){
			iOutput[j] = Double.valueOf(s[j-1]);
		}
		
		//calculate hidden layer output
		hOutput[0]=1.0;
		for(int j=1;j<hiddenSize;j++){
			hiddenLayer[j].childInput=Arrays.copyOfRange(iOutput, 0, iOutput.length);
			hiddenLayer[j].calOutput();
			hOutput[j]=hiddenLayer[j].output;
		}
		
		//Calculate output node output
		oNode.childInput=hOutput.clone();
		oNode.calOutput();
		
		//Print the predicted output value
		if(oNode.output>0.5){
			System.out.println("yes");
		}else{
			System.out.println("no");
		}
		
	}}
}

class Node {
	int cArraySize; // Number of children of the node
	String type; 
	double output;
	double error;
	double kValue;
	double[] childInput;
	double[] w; 

	Node(int cArraySize, String type, double[] w){ 
		this.cArraySize=cArraySize;
		this.type = type;
		this.output =0.0;
		this.error=0.0;
		this.childInput= new double[cArraySize]; //store the input from the children nodes	
		this.w=w;
	}
	
	//Calculate the error term for the output node
	public void calONError(){
		if(this.type=="output"){
			error=output*(1-output)*(kValue-output);
		}
		else {
			System.out.println("wrong type");
			return;
		}
	}

	//calculate the error term for either hidden layer node
	public void calHLError(double errorTerm, double weight){
		if(this.type == "hidden"){
			double sumK=errorTerm*weight;
			error=output*(1-output)*sumK;
		}
		else{
			System.out.println("Wrong type.");
			return;
		}
	}

	public void calOutput(){
		
		double x=0; // the argument of the sigmoid function
		for(int i=0;i<this.w.length;i++){
			x+=this.w[i]*this.childInput[i];
		}
		this.output= 1/(1+Math.exp(-x));
	}
}