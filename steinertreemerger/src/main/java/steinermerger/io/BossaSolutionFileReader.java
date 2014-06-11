package steinermerger.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BossaSolutionFileReader {

	public BossaSolutionFileReader() {
		// TODO Auto-generated constructor stub
	}

	public BossaSolution readSolutionFile(String filename) throws FileNotFoundException {
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		BossaSolution solution = new BossaSolution(); 

		try {
			String line;
			while((line = br.readLine()) != null) {
				readLine(line, solution);
			}
			br.close();
			fr.close();

		} catch (IOException e) {
			e.printStackTrace();
		} 


		assert solution.numNodes == solution.getNodes().size();


		return solution; 
	}

	void readLine(String line, BossaSolution solution) {
		String[] tokens = line.split("\\s+");
		switch(tokens[0]) {
		case "n":
			solution.numNodes = Integer.parseInt(tokens[1]);
			break;
		case "c":
			solution.setCost(Integer.parseInt(tokens[1]));
			break;
		case "v":
			solution.getNodes().add(Integer.parseInt(tokens[1]));
			break;
		case "": 
			break;//empty line?
		default:
			throw new Error("Unknown command: "+tokens[0]);				
		}
	}


}
