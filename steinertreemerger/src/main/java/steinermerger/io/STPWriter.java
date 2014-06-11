package steinermerger.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import steinermerger.datastructures.SteinerGrph;

public class STPWriter {

	BufferedWriter br; 
	SteinerGrph g;
	String remark = ""; 
	
	public STPWriter(String fileName, SteinerGrph g) throws IOException {
		FileWriter fw = new FileWriter(fileName);
		br = new BufferedWriter(fw);
		this.g = g;
	}
	
	public void write() throws IOException {
		writeHead();
		writeGraph();
		writeTerminals();
		writeLine("EOF");
		br.close();
	}
	
	private void writeLine(String line) throws IOException {
		br.write(line);
//		br.write("\n");
		br.newLine();
	}
	
	private void writeGraph() throws IOException {
		writeLine("SECTION Graph");
		writeLine("Nodes "+g.getVertices().getGreatest());
		writeLine("Edges "+g.getNumberOfEdges());
		for(int e : g.getEdges().toIntArray()) {
			int v = g.getOneVertex(e);
			int w = g.getTheOtherVertex(e, v);
			int weight = g.getEdgeWeight(e);
			writeLine("E "+v+" "+w+" "+weight);
		}
		writeLine("END\n");
	}
	
	private void writeTerminals() throws IOException {
		writeLine("SECTION Terminals");
		writeLine("Terminals "+g.getTargetNodes().size());
		for(int t : g.getTargetNodes().toIntArray()) {
			writeLine("T "+t);
		}
		writeLine("END\n");
	}
	
	public void addRemark(String remark) {
		this.remark += remark;
	}
	private void writeHead() throws IOException {
		writeLine("33D32945 STP File, STP Format Version 1.0\n");
		writeLine("SECTION Comment");
		writeLine("Name \"todo\"");
		writeLine("Creator \"SteinerMerger\"");
		writeLine("Remark \""+remark+"\"");
		writeLine("END\n");
	}
}
