package assignment2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class PageRank {

	public static LinkedHashMap<String, ArrayList<String>> input = new LinkedHashMap<String,ArrayList<String>>();
	public static ArrayList<String> sinkNodes = new ArrayList<String>();	
	public static LinkedHashMap<String,Integer> pagesWithItsOutlinks = new LinkedHashMap<String,Integer>();
	public static final double d = 0.85;
	public static LinkedHashMap<String,Double> pagesWithItsPageRank = new LinkedHashMap<String,Double>();
	public static LinkedHashMap<String,Double> newPageRank = new LinkedHashMap<String,Double>();
	public static double perplexity = 0.00;
	public static int counter = 0;
	public static FileWriter output;
	public static int iteration = 0;
	public static String inputFile = "WG2";

	public static void startPageRank() throws IOException{
		System.out.println("---- Starting PageRank Algorithm..... -----");
		String perplexityFileName = "Perplexity-" + inputFile + ".txt";
		output = new FileWriter(perplexityFileName);
		for(String page : input.keySet()){
			double pageRank = 1/(double)input.size();
			pagesWithItsPageRank.put(page,pageRank);
			newPageRank.put(page,0.00);
		}
		perplexity = findPerplexity();
		int counter = 4;
		while(counter > 0){
			double sinkPR = 0.00;
			for(String sink: sinkNodes){
				double d1 = pagesWithItsPageRank.get(sink);
				sinkPR += d1;
			}
			for(String page: input.keySet()){
				double newPR = (1-d)/(double)input.size();
				newPageRank.put(page,newPR);
				newPR += d*sinkPR/(double)input.size();
				newPageRank.put(page,newPR);
				for(String inlinks: input.get(page)){
					newPR += d*pagesWithItsPageRank.get(inlinks)/pagesWithItsOutlinks.get(inlinks);
				}
				newPageRank.put(page,newPR);
			}
			for (String page : input.keySet()){
				pagesWithItsPageRank.put(page,newPageRank.get(page));
			}
			double newPerplexity = findPerplexity();
			if (Math.abs((newPerplexity - perplexity))<1)
				counter --;
			else counter = 4;
			perplexity = newPerplexity;
		}
		output.close();
		System.out.println("Page Rank Algorithm is succeffuly completed !!!!");
	}


	public static double findPerplexity() throws IOException{
		double perplex;
		double entropy = 0.00;
		for(String page: pagesWithItsPageRank.keySet()){
			double currentPR = pagesWithItsPageRank.get(page);
			entropy += currentPR * (Math.log(currentPR)/Math.log(2));
		}
		entropy = 0 - entropy;
		perplex = Math.pow(2,entropy);
		if(iteration>0){
			output.write("Perplexity Value at Iteration " + iteration + " is : " + perplex + ";  ");
			output.write("Change in perplexity value is : " + (Math.abs(perplex-perplexity)) + "\n");
		}
		iteration++;
		return perplex;
	}



	public static int getOutlinksCount(String node){
		int count = 0;
		Set<String> s1 = input.keySet();
		for(String key : s1){
			ArrayList<String> values = new ArrayList<String>();
			values = input.get(key);
			if(values.contains(node))
				count++;
		}		
		return count;
	}

	public static void getPagesWithItsOutlinks(){
		System.out.println("Pages and Its Outlink Counts Calculation is Initiated....");

		for (String key : input.keySet()){
			pagesWithItsOutlinks.put(key, 0);
		}
		
		for (ArrayList<String> values : input.values()){
			for(String element: values){
				pagesWithItsOutlinks.put(element, (pagesWithItsOutlinks.get(element) + 1));
			}
		}
		
		System.out.println("Calculation is done !");
	}


	public static void getSinkNodes(){
		System.out.println("Sink Nodes are Fetching...");
		Set<String> s = pagesWithItsOutlinks.keySet();
		for (String key: s){
			if(pagesWithItsOutlinks.get(key) == 0)
				sinkNodes.add(key);
		}
		System.out.println("End Of Fetch !!!");
	}

	public static void getOutput() throws IOException{
		System.out.println("Output File Writing is Initiated...");
		Comparator<Map.Entry<String, Double>> byMapValues = new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Map.Entry<String, Double> left, Map.Entry<String, Double> right) {
				return left.getValue().compareTo(right.getValue());
			}
		};

		List<Map.Entry<String, Double>> temp = new ArrayList<Map.Entry<String, Double>>();
		temp.addAll(pagesWithItsPageRank.entrySet());

		Collections.sort(temp,byMapValues);

		String pageRankFileName = "PageRank-" + inputFile + ".txt";
		output = new FileWriter(pageRankFileName);
		for(int i = temp.size()-1; i >= (temp.size() - 50); i--){
			output.write(temp.get(i).getKey() + " : " + temp.get(i).getValue() + "\n");
		}
		output.close();
		System.out.println("End of File Write !! ");
	}

	public static void main(String[] args) throws IOException {
		String fileName = inputFile + ".txt";
		File file = new File(fileName);

		// This block scans the input file and inserts the key and values to a HashMap input
		try {
			Scanner sc = new Scanner(file);
			sc.useDelimiter("\n");
			System.out.println("Input File Scan is Initiated....");
			while (sc.hasNext()){
				String s = sc.next();
				s = s.trim();
				Scanner sc1 = new Scanner(s);
				sc1.useDelimiter(" ");
				ArrayList<String> arr = new ArrayList<String>();
				while(sc1.hasNext()){
					String s1 = sc1.next();
					if (!arr.contains(s1))
						arr.add(s1);
				}
				String key = arr.remove(0);
				input.put(key, arr);
				sc1.close();
			}
			System.out.println("------- End Of File Scan -------");
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		getPagesWithItsOutlinks(); // This function gets the Pages with its out-links Count
		getSinkNodes(); // This functions gets the sink nodes and adds it to the ArrayList sinkNodes

		startPageRank(); // This function starts the PageRank algorithm and generates the output	

		getOutput();

	}

}
