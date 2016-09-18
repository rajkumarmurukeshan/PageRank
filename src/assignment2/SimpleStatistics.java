package assignment2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SimpleStatistics {
	public static LinkedHashMap<String, ArrayList<String>> input = new LinkedHashMap<String,ArrayList<String>>();
	public static ArrayList<String> sinkNodes = new ArrayList<String>();	
	public static LinkedHashMap<String,Integer> pagesWithItsOutlinks = new LinkedHashMap<String,Integer>();
	public static LinkedHashMap<String,Integer> pagesWithItsInlinks = new LinkedHashMap<String,Integer>();
	public static String inputFile = "WG1";

	
	public static void getPagesWithItsOutlinks(){
		for (String key : input.keySet()){
			pagesWithItsOutlinks.put(key, 0);
		}
		
		for (ArrayList<String> values : input.values()){
			for(String element: values){
				pagesWithItsOutlinks.put(element, (pagesWithItsOutlinks.get(element) + 1));
			}
		}
	}


	public static void getSinkNodes(){
		Set<String> s = pagesWithItsOutlinks.keySet();
		for (String key: s){
			if(pagesWithItsOutlinks.get(key) == 0)
				sinkNodes.add(key);
		}
	}
	
	public static void getPagesWithItsInlinkCount(){
		for(String key : input.keySet()){
			pagesWithItsInlinks.put(key,input.get(key).size());
		}
	}
	
	
	public static void main(String[] args) {
		String fileName = inputFile + ".txt";
		File file = new File(fileName);

		// This block scans the input file and inserts the key and values to a HashMap input
		try {
			Scanner sc = new Scanner(file);
			sc.useDelimiter("\n");
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
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		getPagesWithItsOutlinks(); // This function gets the Pages with its out-links Count
		getPagesWithItsInlinkCount(); // This function gets the pages with its in-links count
		getSinkNodes(); // This functions gets the sink nodes and adds it to the ArrayList sinkNodes
		
		double inlinkProportion = 0.00;
		int pagesWithoutInlinks = 0;
		for(String key : pagesWithItsInlinks.keySet()){
			if(pagesWithItsInlinks.get(key) == 0)
				pagesWithoutInlinks++;
		}
		inlinkProportion = (double)pagesWithoutInlinks/(double)pagesWithItsInlinks.size();
				
		System.out.println("Proportion Of Pages With No Inlinks: " + inlinkProportion);
		System.out.println("Proportion Of Pages With No Outlinks: " + ((double)sinkNodes.size()/(double)input.size()));
		
		SortedSet<Integer> inlink1 = new TreeSet<Integer>();
		
		for(String key : pagesWithItsInlinks.keySet()){
			inlink1.add(pagesWithItsInlinks.get(key));
		}
		
		
		HashMap<Integer,Integer> compare = new HashMap<Integer,Integer>();
		
		for(Integer number: inlink1){
			compare.put(number, 0);
		}
		
		for(String key : pagesWithItsInlinks.keySet()){
			Integer inlnk = pagesWithItsInlinks.get(key);
			compare.put(inlnk, (compare.get(inlnk) + 1));
		}
		
		compare.remove(0);
		
		HashMap<Double,Double> out = new HashMap<Double,Double>();
		for(Integer key : compare.keySet()){
			out.put(Math.log(key),Math.log(compare.get(key)));
		}
		
		for(Double key : out.keySet()){
			System.out.println(key + "\t" + out.get(key));
		}


	}

}
