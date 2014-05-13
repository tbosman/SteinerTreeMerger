package steinermerger.test;

import grph.gui.GraphstreamBasedRenderer;

import java.io.IOException;

import libtw.input.GraphInput.InputData;
import libtw.input.InputException;
import libtw.ngraph.NGraph;
import steiner.StpReader;
import steinermerger.datastructures.SteinerGrph;
import steinermerger.datastructures.WeightedGrph;
import steinermerger.io.STPReader;
import steinermerger.util.GrphTools;

public class TestClass {
	String fileName = "C:\\Users\\tbosman\\Dropbox\\School\\Scriptie\\Code\\BenchMarks\\c\\c04.stp";
	TestClass() throws IOException{
		STPReader in;
		SteinerGrph g;

		System.out.println("processing " + fileName);
		in = new STPReader(fileName);
		g = in.get();
		System.out.println(g.getVertexShapeProperty().findElementsWithValue(1,g.getVertices()));
		System.out.println(g);
		g.displayGraphstream_0_4_2();
		
		/*
		WeightedGrph t = g.computeMinimumSpanningTree();
		GrphTools.copyProperties(g,t);
		t.displayGraphstream_0_4_2();
		*/
		int root = 1;
		root = g.getTargetNodes().getGreatest();
		SteinerGrph sphTree = g.computeSPHGraph(root);
		//sphTree.displayGraphstream_0_4_2();
		int steinerLength = 0; 
		for(int e : sphTree.getEdges().toIntArray()) {
			steinerLength += sphTree.getEdgeWeight(e);
		}
		System.out.println("Length of tree according to algo: "+steinerLength);
		steinerLength = 0; 
		for(int e : sphTree.getEdges().toIntArray()) {
			steinerLength += g.getEdgeWeight(e);
		}
		System.out.println("Length of tree according to input: "+steinerLength);
	}

	public void start() {

	}

	public static void main(String... args) throws IOException {
		new TestClass().start(); 
	}
}
