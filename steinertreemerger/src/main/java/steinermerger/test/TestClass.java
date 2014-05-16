package steinermerger.test;

import grph.Grph;
import grph.gui.GraphstreamBasedRenderer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

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
	String dirName =  "C:\\Users\\tbosman\\Dropbox\\School\\Scriptie\\Code\\BenchMarks\\i640\\";
	String instancePrefix = "I640-";
	int instanceDigits = 3;
	String instanceNumber = "015";
	String fileName = dirName+instancePrefix+instanceNumber+".stp";
//	String fileName = "C:\\Users\\tbosman\\Dropbox\\School\\Scriptie\\Code\\BenchMarks\\E\\e17.stp";
	TestClass() throws IOException{
	
		
		
	}
	
	public String getFilename(int instanceNumber) {
		String format = "%0"+instanceDigits+"d";
		String instanceName = String.format(format, instanceNumber);
		return dirName+instancePrefix+instanceName+".stp";
				
	}

	public SteinerGrph constructSPH(SteinerGrph g, int root) {
		//System.out.println("Constructing from root: "+root);
		SteinerGrph sphTree = g.computeSPHGraph2(root);
		int steinerLength = 0; 
		for(int e : sphTree.getEdges().toIntArray()) {
			steinerLength += sphTree.getEdgeWeight(e);
		}
		//System.out.println("Length of tree according to algo: "+steinerLength);
		return sphTree;
	}
	
	public int totalLength(WeightedGrph g) {
		int length = 0; 
		for(int e : g.getEdges().toIntArray()) {
			length += g.getEdgeWeight(e);
		}
		return length;
	}
	public int doTreeDP(SteinerGrph g) {
		return new TreeDecomposition().computeTreeDP(new TWLibWrapperGrph(g));
	}

	/**
	 * 
	 * @param g
	 * @return array [min length of sph trees, length of td dp]
	 * @throws IOException
	 */
	public int[] sphAtoDP(SteinerGrph g, boolean verbose) throws IOException {


		//		System.out.println("Pruned from input graph: "+pruned);
		
//		WeightedGrph t = g.computeMinimumSpanningTree();
//		GrphTools.copyProperties(g,t);
		
		long startSPH = System.currentTimeMillis();
		
		int root = 1;
		root = g.getTargetNodes().getGreatest();
		int[] targetArray = g.getTargetNodes().toIntArray();
		
		int minSphLength = Integer.MAX_VALUE;
		
		int maxTw = 10;
		int step = 1; 
		int maxIt = 20;
		if(targetArray.length/maxIt > 1) {
			step = targetArray.length/maxIt;
		}
		SteinerGrph sphUnion = new SteinerGrph();
		ArrayList<SteinerGrph> sphList = new ArrayList<SteinerGrph>();
		for(int i=0; i< targetArray.length && i <step*maxIt ; i=i+step) {
			SteinerGrph sph = constructSPH(g, targetArray[i]);
			
			
			SteinerGrph improvedSph = sph.insertNodeImprovement(g);
			
			if(improvedSph.totalLength() < sph.totalLength()) {
				int improvement = improvedSph.totalLength() - sph.totalLength();
				System.out.println("Node improvement: "+improvement);
				sph = improvedSph;
			}
			sphList.add(sph);
		}
		Collections.sort(sphList);
		Collections.reverse(sphList);
		minSphLength = sphList.get(0).totalLength();
		int numTrees = 0;
		for(SteinerGrph sph : sphList) {
			//sph = sph.mstPrune(g);
			
			
			SteinerGrph newSphUnion = new SteinerGrph(sphUnion);
			newSphUnion.addSubgraph(sph);
			int tw = new TreeDecomposition().computeTreeWidth(new TWLibWrapperGrph(newSphUnion));
		//	System.out.println("Tw: "+tw);
			if(tw > maxTw) {
				System.out.println("TW maxed out aborting generation of trees @ union of "+numTrees+" trees"); 
				break;
			}else {
				sphUnion = newSphUnion;
				numTrees++;
			}
			

		
//			System.out.println("Post reduction");
//			sphUnion.reduceGraph();

		}
		
		System.out.println("Shortest sph length: "+minSphLength);
		
		long timeSPH = System.currentTimeMillis() - startSPH;
		
//		System.out.println("Adding minimum spanning tree to union: ");
//		sphUnion.addSubgraph(t);
//		sphUnion.pruneSteinerLeafs();
		if(verbose) {
		System.out.println(sphUnion);
		System.out.println("Post reduction");
		sphUnion.reduceGraph();
		System.out.println(sphUnion);
		sphUnion.displayGraphstream_0_4_2();
		}
//		System.out.println("Trimming degree: ");
//		trimDegree(sphUnion);
		
	/*	
		System.out.println("Minimum spanning Tree reduction: ");
		WeightedGrph mst = sphUnion.computeMinimumSpanningTree();
		GrphTools.copyProperties(sphUnion, mst);
		sphUnion = new SteinerGrph(mst);
		
		System.out.println(sphUnion);
		*/
//		System.out.println(sphUnion.pruneSteinerLeafs());

		long startTD = System.currentTimeMillis();


		
		System.out.println("Doing Tree decomp");
		
		int[] out = new int[4];
		out[0] = minSphLength;
		out[1] = doTreeDP(sphUnion);
		long timeDP = System.currentTimeMillis() - startTD; 
		out[2] = (int) timeSPH;
		out[3] = (int) timeDP;
		
		return out; 
	}
	
	
	public void trimDegree(SteinerGrph g) {
		int maxDegree = 5; 
		System.out.println("max degree: "+ g.getMaxOutVertexDegrees());
		int numEdges = g.getNumberOfEdges()+1;
		while(g.getMaxOutVertexDegrees() > maxDegree && g.getNumberOfEdges() < numEdges) {
			numEdges = g.getNumberOfEdges();
			IntSet vertices = g.getVerticesOfDegreeAtLeast(10);
			for(int v: vertices.toIntArray()) {
				IntSet edges = g.getEdgesIncidentTo(v);
				for(int e : edges.toIntArray()) {
					int w = g.getTheOtherVertex(e, v);
					int weight = g.getEdgeWeight(e);
					g.removeEdge(e);
					if(!g.isConnected()) {
						g.addSimpleEdge(v, e, w, false);
						g.setEdgeWeight(e, weight);
					}
				}
			}
		}
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
		System.out.println("#Contracted degr. 2 nodes: "+contractedSet.size());
		//System.out.println("#Removed infeasible edges (sp criterium): "+g.spReduction().size());
		
		prunedSet = g.pruneSteinerLeafs();
		contractedSet = g.contractDegree2();
//		System.out.println("#Pruned leafs: "+prunedSet.size());
//		System.out.println("#Contracted degr. 2 nodes: "+g.contractDegree2().size());
//			
		
		System.out.println("Problem after preprocessing: "); 
		System.out.println(g);
		System.out.println("#DBG Still connected? "+g.isConnected());
	}
	
	public void start() throws IOException {
//		fileName = dirName+"c01"+".stp";
		String results = "";
		FileWriter writer = new FileWriter(System.currentTimeMillis()+"_"+instancePrefix+"_results.txt");
		for(int i=1; i<200;i++) {
			try {
			SteinerGrph g = readInstance(getFilename(i));
			preProcess(g, true);
			long start = System.currentTimeMillis();
			//g.displayGraphstream_0_4_2();
			int[] solutions = sphAtoDP(g, false);
			
			long time = System.currentTimeMillis()-start; 
			
			String currentResult = "Instance: "+instancePrefix+"["+i+"] | Min SPH: "+solutions[0]+", DP: "+solutions[1]+", Time SPH+N-A: "+solutions[2]+", Time DP: "+solutions[3];
			results += currentResult+"\n";

			writer.append(currentResult+"\n");
			
			writer.flush();
			
			System.out.println("@@@@@@@@@@@@@ FINISHED INSTANCE @@@@@@@@@@");
			System.out.println(currentResult);
			}catch(IOException e) {
				System.out.println(e.getMessage());
			}
			
		
			
			
		}
		System.out.println("All results");
		System.out.println(results);
//		sphAddNeighbours(g);
	}

	public static void main(String... args) throws IOException {
		new TestClass().start(); 
	}
}
