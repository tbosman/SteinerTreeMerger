package steinermerger.test;

import grph.Grph;
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
	String dirName =  "C:\\Users\\tbosman\\Dropbox\\School\\Scriptie\\Code\\BenchMarks\\E\\";
	String instanceName = "e17.stp";
	String fileName = dirName+instanceName;
//	String fileName = "C:\\Users\\tbosman\\Dropbox\\School\\Scriptie\\Code\\BenchMarks\\E\\e17.stp";
	TestClass() throws IOException{
	
		
		
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
	
	public int totalLength(WeightedGrph g) {
		int length = 0; 
		for(int e : g.getEdges().toIntArray()) {
			length += g.getEdgeWeight(e);
		}
		return length;
	}
	public void doTreeDP(SteinerGrph g) {
		new TreeDecomposition().computeTreeDP(new TWLibWrapperGrph(g));
	}

	public void sphAtoDP(SteinerGrph g) throws IOException {


		//		System.out.println("Pruned from input graph: "+pruned);
		
//		WeightedGrph t = g.computeMinimumSpanningTree();
//		GrphTools.copyProperties(g,t);
		
		int root = 1;
		root = g.getTargetNodes().getGreatest();
		int[] targetArray = g.getTargetNodes().toIntArray();
		
		int step = 1; 
		step = targetArray.length/10;
		int maxIt = 100;
		SteinerGrph sphUnion = new SteinerGrph();
		for(int i=0; i< targetArray.length && i <step*maxIt ; i=i+step) {
			SteinerGrph sph = constructSPH(g, targetArray[i]);
			sphUnion.addSubgraph(sph);
			System.out.println("Iteration "+i);
			System.out.println("Graph size of union: ");
			System.out.println(sphUnion);
		}
//		System.out.println("Adding minimum spanning tree to union: ");
//		sphUnion.addSubgraph(t);
//		sphUnion.pruneSteinerLeafs();
		System.out.println(sphUnion);
		/*
		System.out.println("Minimum spanning Tree reduction: ");
		WeightedGrph mst = sphUnion.computeMinimumSpanningTree();
		GrphTools.copyProperties(sphUnion, mst);
		sphUnion = new SteinerGrph(mst);
		
		System.out.println(sphUnion);
		System.out.println(sphUnion.pruneSteinerLeafs());
		System.out.println(sphUnion);
		*/
		sphUnion.displayGraphstream_0_4_2();
		
		System.out.println("Original graph: ");
		System.out.println(g+" length: "+totalLength(g));
		
		System.out.println("Doing Tree decomp");
		doTreeDP(sphUnion);
	}
	
	public void sphAddNeighbours(SteinerGrph g) {
		int[] targetArray = g.getTargetNodes().toIntArray();
		int root = targetArray[0];
		
		SteinerGrph sph = constructSPH(g, targetArray[root]);
		
		SteinerGrph augTree = new SteinerGrph(g);
		IntSet candidates = sph.getVertices().clone(); 
		candidates.addAll(g.getNeighbours(candidates));
		augTree.removeAllBut(candidates);
		
		doTreeDP(augTree);
	}
	
	SteinerGrph readInstance(String fileName) throws IOException {
		SteinerGrph g;
		STPReader in;
		System.out.println("processing " + fileName);
		in = new STPReader(fileName);
		g = in.get();
		System.out.println(g.getVertexShapeProperty().findElementsWithValue(1,g.getVertices()));
		System.out.println(g);
		return g;
	}
	
	public void preProcess(SteinerGrph g, boolean verbose) {
		System.out.println("Initial problem: "+g);
		IntSet prunedSet = g.pruneSteinerLeafs();
		IntSet contractedSet = g.contractDegree2();
		System.out.println("#Pruned leafs: "+prunedSet.size());
		System.out.println("#Contracted degr. 2 nodes: "+g.contractDegree2().size());
		System.out.println("#Removed infeasible edges (sp criterium): "+g.spReduction().size());
		prunedSet = g.pruneSteinerLeafs();
		contractedSet = g.contractDegree2();
		System.out.println("#Pruned leafs: "+prunedSet.size());
		System.out.println("#Contracted degr. 2 nodes: "+g.contractDegree2().size());
			
		
		System.out.println("Problem after preprocessing: "); 
		System.out.println(g);
	}
	
	public void start() throws IOException {
		fileName = dirName+"e18"+".stp";
		SteinerGrph g = readInstance(fileName);

		preProcess(g, true);
		
		g.displayGraphstream_0_4_2();
		
		sphAtoDP(g);
		
//		sphAddNeighbours(g);
	}

	public static void main(String... args) throws IOException {
		new TestClass().start(); 
	}
}
