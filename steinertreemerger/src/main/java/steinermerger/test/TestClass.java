package steinermerger.test;

import grph.gui.GraphstreamBasedRenderer;

import java.io.IOException;

import libtw.input.GraphInput.InputData;
import libtw.input.InputException;
import libtw.ngraph.NGraph;
import steiner.StpReader;
import steinermerger.adapters.TWLibWrapperGrph;
import steinermerger.algo.TreeDecomposition;
import steinermerger.datastructures.SteinerGrph;
import steinermerger.datastructures.WeightedGrph;
import steinermerger.io.STPReader;
import steinermerger.util.GrphTools;
import toools.set.IntSet;

public class TestClass {
	String fileName = "C:\\Users\\tbosman\\Dropbox\\School\\Scriptie\\Code\\BenchMarks\\c\\c01.stp";
	TestClass() throws IOException{
		STPReader in;
		SteinerGrph g;

		System.out.println("processing " + fileName);
		in = new STPReader(fileName);
		g = in.get();
		System.out.println(g.getVertexShapeProperty().findElementsWithValue(1,g.getVertices()));
		System.out.println(g);
		IntSet prunedSet = g.pruneSteinerLeafs();
		System.out.println("Problem after pruning: ");
		System.out.println(g);
		g.displayGraphstream_0_4_2();

		//		System.out.println("Pruned from input graph: "+pruned);
		/*
		WeightedGrph t = g.computeMinimumSpanningTree();
		GrphTools.copyProperties(g,t);
		t.displayGraphstream_0_4_2();
		 */
		int root = 1;
		root = g.getTargetNodes().getGreatest();
		int[] targetArray = g.getTargetNodes().toIntArray();

		SteinerGrph sphUnion = new SteinerGrph();
		for(int i=0; i< targetArray.length; i++) {
			SteinerGrph sph = constructSPH(g, targetArray[i]);
			sphUnion.addSubgraph(sph);
			System.out.println("Graph size of union: ");
			System.out.println(sphUnion);
		}
		System.out.println("Minimum spanning Tree reduction: ");
		WeightedGrph mst = sphUnion.computeMinimumSpanningTree();
		GrphTools.copyProperties(sphUnion, mst);
		sphUnion = new SteinerGrph(mst);
		
		System.out.println(sphUnion);
		sphUnion.pruneSteinerLeafs();
		System.out.println(sphUnion);
		sphUnion.displayGraphstream_0_4_2();
		
		
		System.out.println("Doing Tree decomp");
		doTreeDP(sphUnion);
		
		
	}

	public SteinerGrph constructSPH(SteinerGrph g, int root) {
		System.out.println("Constructing from root: "+root);
		SteinerGrph sphTree = g.computeSPHGraph(root);
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
		return sphTree;
	}
	
	public void doTreeDP(SteinerGrph g) {
		new TreeDecomposition().computeTreeDP(new TWLibWrapperGrph(g));
	}

	public void start() {

	}

	public static void main(String... args) throws IOException {
		new TestClass().start(); 
	}
}
