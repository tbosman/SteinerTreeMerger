package steinermerger.io;

import grph.Grph;






import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import steinermerger.datastructures.SteinerGrph;


/**
 * For reading .stp files and creating grph object	
 * Terminals have shape property 1, steiner points shape property 0
 * Adapted from StpReader class from twlib package
 * @author tbosman
 *
 */
public class STPReader {
	
	private String filename;
	private SteinerGrph g;

	public STPReader(String fname) {
		this.filename = fname; 
	}

	public SteinerGrph get() throws IOException {
		
		g = new SteinerGrph();
		
		FileReader fr;
		try	{
			fr = new FileReader(filename);
		}
		catch (FileNotFoundException e)	{
			throw  (e);
		}
		LineNumberReader in = new LineNumberReader( fr );
		String line;
		try{
			line = in.readLine();

			String[] tokens = line.split("\\s+");
			if (!tokens[0].toLowerCase().equals("33d32945") ){
				throw new Error("not an stp file");
			}		
		}
		catch (IOException e) {
			throw (e);
		}
		
		Boolean reading = true;
		
		try{
			while( (line=in.readLine()) !=null && reading){
				
				String[] tokens = line.split("\\s+");
				assert tokens.length > 0;
				
				String command = tokens[0];
				
				if( command.equals("") )
				{	// empty line
					continue;
				} else if (command.toLowerCase().equals("section")) {
					String section = tokens[1].toLowerCase();
					if (section.equals("comment")){
						skipSection(in);
					} else if (section.equals("graph")){
						readGraph(in);
					} else if (section.equals("terminals")){
						readTerminals(in);
					} else if (section.equals("coordinates")){
						skipSection(in);
					} else{
						throw new Error("unknown section");
					}
				} else if (command.equals("EOF")) {
					reading = false;
				} else {
					System.out.println( "Unknown command '" + command + "' at line " + in.getLineNumber() );
				}
			}
		}
		catch (IOException e) {
			throw (e);
		}		
		return g;
	}
	
	private void skipSection(LineNumberReader in) throws IOException{	
		String line;
		Boolean reading = true;
		while( (line=in.readLine()) !=null && reading){	
			String[] tokens = line.split("\\s+");
			String command = tokens[0].toLowerCase();	
			if (command.equals("end")){
				reading = false;
			} 
		}
	}
	
	private void readGraph(LineNumberReader in) throws IOException{
		
		String line;
		Boolean reading = true;
		int currentEdges = 0;
		int edges = 0;
		while( (line=in.readLine()) !=null && reading){	
			String[] tokens = line.split("\\s+");
			String command = tokens[0].toLowerCase();	
			if (command.equals("nodes")){
				for (int i = 1; i <= Integer.parseInt(tokens[1]); i++){
					g.addVertex(i);
				}
			} else if (command.equals("edges")) {
				edges = Integer.parseInt(tokens[1]);
			} else if (command.equals("e")){
				currentEdges++;
				int v1 = Integer.parseInt(tokens[1]);
				int v2 = Integer.parseInt(tokens[2]);
				int weight = Integer.parseInt(tokens[3]);
				g.addUndirectedSimpleEdge(currentEdges, v1, v2);
				g.setEdgeWeight(currentEdges, weight);
			} else if (command.equals("arcs")) {
				throw new Error("arcs are currently not supported");
			} else if (command.equals("a")){
				throw new Error("arcs are currently not supported");
			}
			else if (command.equals("end")){
				reading = false;
			} else{
				System.out.println( "Unknown command '" + command + "' at line " + in.getLineNumber() );
			}
		}
		
		if (edges != currentEdges){
			throw new Error("missing edges");
		}
	}
	
	private void readTerminals(LineNumberReader in) throws IOException{
		String line;
		Boolean reading = true;
		int currentTerminals = 0;
		int totalTerminals = 0;
		while( (line=in.readLine()) !=null && reading){	
			String[] tokens = line.split("\\s+");
			String command = tokens[0].toLowerCase();	
			if (command.equals("terminals")){
				totalTerminals = Integer.parseInt(tokens[1]);
			} else if (command.equals("t")){
				g.getVertexShapeProperty().setValue(Integer.parseInt(tokens[1]), 1);
				g.getVertexColorProperty().setValue(Integer.parseInt(tokens[1]), 1);
				currentTerminals++;
			} else if (command.equals("end")){
				reading = false;
			} else{
				System.out.println( "Unknown command '" + command + "' at line " + in.getLineNumber() );
			}
		}
		if (totalTerminals != currentTerminals){
			throw new Error("missing terminals");
		}
	}
	
}
