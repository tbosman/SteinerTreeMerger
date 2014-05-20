package steinermerger.test;

import grph.Grph;
import grph.gui.GraphstreamBasedRenderer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
	String dirName =  "C:\\Users\\tbosman\\Dropbox\\School\\Scriptie\\Code\\BenchMarks\\i080\\";
	String instancePrefix = "I080-";
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


	public SteinerGrph constructSPH(SteinerGrph g, int root, boolean perturbations) {
		if(perturbations) {
			Random rng = new Random();

			SteinerGrph gPerturbed = new SteinerGrph(g);
			for(int e : g.getEdges().toIntArray()) {
				int newWeight = (int) Math.round(g.getEdgeWeight(e)*rng.nextDouble()*2);
				gPerturbed.setEdgeWeight(e, Math.max(newWeight,1));
			}
			SteinerGrph sph =  constructSPH(gPerturbed, root);
			sph.setEdgeWeights(g.getEdgeWeightProperty());
			return sph;
		}else {
			return constructSPH(g, root);
		}
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
		return (int) new TreeDecomposition().computeTreeDP(new TWLibWrapperGrph(g));
	}

	public SteinerGrph fillInTreeDecomposition(SteinerGrph g, SteinerGrph originalGrph) {
		IntSet[] bags = new TreeDecomposition().computeTreeDecomposition(new TWLibWrapperGrph(g));
		SteinerGrph gOut = new SteinerGrph();
		for(IntSet bag : bags) {
			SteinerGrph clique = new SteinerGrph(originalGrph);
			clique.removeAllBut(bag);
			gOut.addSubgraph(clique);
		}
		return gOut; 
	}

	public int[] sphAlltoDP(ArrayList<SteinerGrph> sphList, SteinerGrph g) {
		int maxTw = 10;
		SteinerGrph sphUnion = new SteinerGrph();

		Collections.sort(sphList);
		int minSphLength = sphList.get(0).totalLength();
		int numTrees = 0;
		for(SteinerGrph sph : sphList) {


			SteinerGrph newSphUnion = new SteinerGrph(sphUnion);
			newSphUnion.addSubgraph(sph);
			int tw = new TreeDecomposition().computeTreeWidth(new TWLibWrapperGrph(newSphUnion));
			//	System.out.println("Tw: "+tw);
			if(tw > maxTw) {
				newSphUnion.mstPrune(g);
				tw = new TreeDecomposition().computeTreeWidth(new TWLibWrapperGrph(newSphUnion));
				if(tw > maxTw) {

				}else {
					sphUnion = newSphUnion; 
					numTrees++;
					System.out.println("#DBG Pruning helped!");
				}
			}else {
				sphUnion = newSphUnion;
				numTrees++;
			}



			//			System.out.println("Post reduction");
			//			sphUnion.reduceGraph();

		}
		if(numTrees < sphList.size()) {
			System.out.println("TW maxed out, trees used: "+numTrees);
		}

		System.out.println(sphUnion);
		sphUnion = fillInTreeDecomposition(sphUnion, g);
		System.out.println(sphUnion);
		System.out.println("Shortest sph length: "+minSphLength);







		long startTD = System.currentTimeMillis();

		int[] out = new int[2];

		System.out.println("Doing Tree decomp");
		out[0] = doTreeDP(sphUnion);
		out[1] = numTrees;
		return out;

	}

	/**
	 * 
	 * @param g
	 * @return array [min length of sph trees, length of td dp]
	 * @throws IOException
	 */
	public int[] sphAllandTD(SteinerGrph g, boolean verbose, int maxIt) throws IOException {


		long startSPH = System.currentTimeMillis();

		int root = 1;
		root = g.getTargetNodes().getGreatest();
		int[] targetArray = g.getTargetNodes().toIntArray();

		int minSphLength = Integer.MAX_VALUE;

		int maxTw = 10;
		int step = 1; 

		if(targetArray.length/maxIt > 1) {
			step = targetArray.length/maxIt;
		}
		SteinerGrph sphUnion = new SteinerGrph();
		ArrayList<SteinerGrph> sphList = new ArrayList<SteinerGrph>();
		for(int i=0; i<maxIt; i++) {
			int iRoot = i*step;
			while(iRoot >= targetArray.length) {
				iRoot = iRoot - targetArray.length;
			}

			//for(int i=0; i< targetArray.length && i <step*maxIt ; i=i+step) {
			SteinerGrph sph = constructSPH(g, targetArray[iRoot], true);

			int numNeighbours = 2; 
			SteinerGrph improvedSph = sph.insertNodeImprovement(g,2);

			System.out.print("Improvement procedure: "+numNeighbours);
			while(numNeighbours < 64 || improvedSph.totalLength() < sph.totalLength()) {
				if(improvedSph.totalLength() < sph.totalLength()) {
					sph = improvedSph; 
					improvedSph = sph.insertNodeImprovement(g, numNeighbours);
				}else {
					numNeighbours *= 32;
					improvedSph = sph.insertNodeImprovement(g, numNeighbours);
				}
				System.out.print(numNeighbours);
			}



			System.out.println(" - Total length: "+sph.totalLength());

			sphList.add(sph);
		}
		long timeSPH = System.currentTimeMillis() - startSPH;
		Collections.sort(sphList);
		minSphLength = sphList.get(0).totalLength();


		int minTDLength = Integer.MAX_VALUE; 
		long startTD = System.currentTimeMillis();
		int[] tdSolution = sphAlltoDP(sphList, g);
		minTDLength = tdSolution[0];
		while(tdSolution[1] < sphList.size()){
			Random rng = new Random(System.currentTimeMillis());
			for(int i=0;i<tdSolution[1];i++) {				
				sphList.remove(rng.nextInt(sphList.size()));	
			}

			tdSolution = sphAlltoDP(sphList, g);
			if(tdSolution[0] < minTDLength) {
				minTDLength = tdSolution[0];
			}
		}

		// ---
		int[] out = new int[5];
		out[0] = minSphLength;
		out[1] = minTDLength;
		long timeDP = System.currentTimeMillis() - startTD; 
		out[2] = (int) timeSPH;
		out[3] = (int) timeDP;
		out[4] = tdSolution[1];
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

		int maxIt = 10; 
		for(int m=1; m<5; m= m*2) {
			maxIt = m*10;
			String results = "";
			FileWriter writer = new FileWriter(System.currentTimeMillis()+"_"+instancePrefix+"_results_maxit"+maxIt+".txt");
			for(int i=311; i<320;i++) {
				try {
					SteinerGrph g = readInstance(getFilename(i));
					preProcess(g, true);
					long start = System.currentTimeMillis();
					//g.displayGraphstream_0_4_2();
					int[] solutions = sphAllandTD(g, false, maxIt);

					long time = System.currentTimeMillis()-start; 

					String currentResult = "Instance: "+instancePrefix+"["+i+"] | Min SPH: "+solutions[0]+", DP: "+solutions[1]+", Time SPH+N-A: "+solutions[2]+", Time DP: "+solutions[3]+", Trees Used: "+solutions[4];
					results += currentResult+"\n";

					writer.append(currentResult+"\n");

					writer.flush();

					System.out.println("@@@@@@@@@@@@@ FINISHED INSTANCE @@@@@@@@@@");
					System.out.println(currentResult);
				}catch(IOException e) {
					System.out.println(e.getMessage());
				}




			}
			System.out.println("All results for maxIt: "+maxIt);
			System.out.println(results);
		}
	}

	public static void main(String... args) throws IOException {
		new TestClass().start(); 
	}
}
