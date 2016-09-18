package assignment2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkAnalysis {

	public static String seed = "https://en.wikipedia.org/wiki/Sustainable_energy";
	public final static int maxDepth = 5;
	public static int pagesCrawled; 
	public static FileWriter output;
	public static ArrayList<String> frontier = new ArrayList<String>();
	public static LinkedHashMap<String , ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();

	public static void main(String[] args) {
		unfocusedCrawling();  // this methods perform unfocused crawling and generates 1000 URLS in the frontier
		performLinkAnalysis();	// this method performs Link Analysis and generates the graph for 1000 URLS
			
		// This block writes the graph to an Output file WG1.TXT
		try {
			output = new FileWriter("WG1.txt");
			Set<String> s = map.keySet();
			for (String key : s){				
				output.write(key);
				for (String value : map.get(key)){
					output.write(" " + value);
				}
				output.write("\n");
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		}
	}

	public static void unfocusedCrawling(){

		// Clearing the Collections before the Crawler is initiated
		pagesCrawled = 0;
		frontier.clear();

		// Seed is registered to the output
		registerURL(seed,1);
	}

	public static void performLinkAnalysis(){
		for (String s : frontier){
			s= s.substring(30);
			ArrayList<String> value = new ArrayList<String>();
			map.put(s, value);
		}

		for (String s : frontier){
			String URL = s.substring(30);
			try {
				Document doc = Jsoup.connect(s).get();
				Elements tags = doc.select("a[href]");
				for (Element e : tags){
					String link = e.attr("abs:href");
					link = link.split("#")[0];
					if ((link.lastIndexOf(":") < 6 ) 
							&& link.startsWith("https://en.wikipedia.org/wiki")
							&& (!link.matches("https://en.wikipedia.org/wiki/Main_Page.*$"))
							&& !link.equals(s))
					{
						for (String f : frontier){
							if (link.equals(f)){
								ArrayList<String> arr = new ArrayList<String>();
								String docID = f.substring(30);
								arr = map.get(docID);
								if (!arr.contains(URL) ){
									arr.add(URL);
									map.put(docID,arr);
								}
							}
						}
					}	
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}


	public static void registerURL(String url, int depth){
		if(pagesCrawled < 1000 && !frontier.contains(url)){
			frontier.add(url);
			pagesCrawled++;
			processHyperLinks(url,depth);
		}
		else return;

	}

	public static void processHyperLinks(String url, int depth){
		if (depth < maxDepth){
			try {
				Document doc = Jsoup.connect(url).get();
				Thread.sleep(0);
				Elements links = doc.select("a[href]");
				for (Element link : links){
					String nextURL = link.attr("abs:href");
					nextURL = nextURL.split("#")[0];
					if ((nextURL.lastIndexOf(":") < 6 ) 
							&& nextURL.startsWith("https://en.wikipedia.org/wiki")
							&& (!nextURL.matches("https://en.wikipedia.org/wiki/Main_Page.*$"))){
						registerURL(nextURL,depth+1);  // recursive call 
					}					
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		else return;
	}

}
