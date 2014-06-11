package steinermerger.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class OptimalSolutionsReader {

	public OptimalSolutionsReader() {
		// TODO Auto-generated constructor stub
	}

	public HashMap<String, Integer> read(String fileName) throws NumberFormatException, IOException{
		HashMap<String, Integer> solutions = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		
		while((line = br.readLine()) != null) {
			String[] tokens = line.split("\\s+");
			String key = tokens[0];
			Integer value = Integer.parseInt(tokens[1]);
			solutions.put(key, value);
		}
		br.close();
		return solutions;
	}
	
	public static void main(String... args) throws NumberFormatException, IOException {
		String optDir = "src/main/resources/SteinLibOptimalSolutions/";
		String optName = "I640.results";
		HashMap<String, Integer> map = new OptimalSolutionsReader().read(optDir+optName);
		System.out.println("Done!");
	}
}
