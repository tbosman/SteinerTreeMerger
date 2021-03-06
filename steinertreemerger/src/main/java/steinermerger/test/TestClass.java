package steinermerger.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import steinermerger.adapters.TWLibWrapperGrph;
import steinermerger.algo.TreeDecomposition;
import steinermerger.datastructures.SteinerGrph;
import steinermerger.datastructures.WeightedGrph;
import steinermerger.io.STPReader;
import toools.set.IntSet;

public class TestClass {
	String dirName =  "C:\\Users\\tbosman\\Dropbox\\School\\Scriptie\\Code\\BenchMarks\\I320\\";
	String instancePrefix = "I320-";
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


	public SteinerGrph constructSPH(SteinerGrph g, int root, int perturbationType, int iterationNumber) {
		SteinerGrph gPerturbed = new SteinerGrph(g);
		Random rng = new Random();
		double maxUsed = Math.max(iterationNumber, 1);
		if(perturbationType == 1) {

			for(int e : g.getEdges().toIntArray()) {
				int newWeight = (int) Math.round(g.getEdgeWeight(e)*rng.nextDouble()*2);
				gPerturbed.setEdgeWeight(e, Math.max(newWeight,1));
			}

		}else if(perturbationType == 2) {
			for(int e : g.getEdges().toIntArray()) {
				double randomizationCoefficient = 1.25 + 0.75*(g.getEdgeWidthProperty().getValue(e)-1)/maxUsed;
				int newWeight = (int) Math.round(g.getEdgeWeight(e)*rng.nextDouble()*randomizationCoefficient);
				gPerturbed.setEdgeWeight(e, Math.max(newWeight,1));
			}
		}else if(perturbationType == 3) {
			for(int e : g.getEdges().toIntArray()) {
				double randomizationCoefficient = 2.0 - 0.75*(g.getEdgeWidthProperty().getValue(e)-1)/maxUsed;
				int newWeight = (int) Math.round(g.getEdgeWeight(e)*rng.nextDouble()*randomizationCoefficient);
				gPerturbed.setEdgeWeight(e, Math.max(newWeight,1));
			}
		}



		SteinerGrph sph =  constructSPH(gPerturbed, root);
		sph.setEdgeWeights(g.getEdgeWeightProperty());
		return sph;
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

	//adds edges from sph to sphUnion until tree width is maxed out 
	public SteinerGrph addPartialSolution(SteinerGrph sphUnion, SteinerGrph sph, int maxTw) {

		int tw = 0; 
		IntSet edges = sph.getEdges().clone(); 
		Random prng = new Random(System.currentTimeMillis());

		TreeDecomposition tdCalculator = new TreeDecomposition();
		while(tw < maxTw && !edges.isEmpty()) {
			int e = edges.pickRandomElement(prng, true);
			int v = sph.getOneVertex(e);
			int w = sph.getTheOtherVertex(e, v);
			int weight = sph.getEdgeWeight(e); 
			if(!sphUnion.containsEdge(e)){
				if(!sphUnion.containsVertex(v)) {
					sphUnion.addVertex(v);
				}
				if(!sphUnion.containsVertex(w)) {
					sphUnion.addVertex(w);
				}
				sphUnion.addWeightedEdge(v, e, w, weight);

			}
			tw = tdCalculator.computeTreeWidth(new TWLibWrapperGrph(sphUnion));
			if(tw > maxTw) {
				sphUnion.pruneSteinerLeafs();
				if(tw > maxTw) {
					if(sphUnion.containsEdge(e)) {//pruning might have removed e
						sphUnion.removeEdge(e);
					}
				}
			}
		}


		return sphUnion;
	}

	public int[] sphAlltoDP(ArrayList<SteinerGrph> sphList, SteinerGrph g, int maxTw) {

		SteinerGrph sphUnion = sphList.get(0);

		Collections.sort(sphList);
		int minSphLength = sphList.get(0).totalLength();
		int numTrees = 1;

		ArrayList<SteinerGrph> unusedList = new ArrayList<SteinerGrph>();

		long start = System.currentTimeMillis(); 
		for(SteinerGrph sph : sphList) {



			SteinerGrph newSphUnion = new SteinerGrph(sphUnion);
			newSphUnion.addSubgraph(sph);
			//			int tw = new TreeDecomposition().computeTreeWidth(new TWLibWrapperGrph(newSphUnion));
			//			if(tw > maxTw) {
			//				newSphUnion.mstPrune(g);
			//				tw = new TreeDecomposition().computeTreeWidth(new TWLibWrapperGrph(newSphUnion));
			//				if(tw > maxTw) {
			//					unusedList.add(sph);
			//				}else {
			//					sphUnion = newSphUnion; 
			//					numTrees++;
			//					System.out.println("#DBG Pruning helped!");
			//				}
			//			}else {
			//				sphUnion = newSphUnion;
			//				numTrees++;
			//			}
			boolean twTractable = new TreeDecomposition().computeTreeWidthIsLEQ(new TWLibWrapperGrph(newSphUnion), maxTw-1);
			if(!twTractable) {
				//newSphUnion.mstPrune(g);
				//twTractable = new TreeDecomposition().computeTreeWidthIsLEQ(new TWLibWrapperGrph(newSphUnion), maxTw);
				if(!twTractable) {
					unusedList.add(sph);
				}else {
					sphUnion = newSphUnion; 
					numTrees++;
					System.out.println("#DBG Pruning helped!");
				}
			}else {
				sphUnion = newSphUnion;
				numTrees++;
			}


		}
		System.out.println("Time adding solutions to union: "+(System.currentTimeMillis()-start));

		if(numTrees < sphList.size()) {
			System.out.println("TW maxed out @ trees used: "+numTrees);
		}

		System.out.println(sphUnion);
		System.out.println("Trying to add partial solutions");
		for(SteinerGrph sph : unusedList) {

			sphUnion = addPartialSolution(sphUnion, sph, maxTw);

		}
		System.out.println(sphUnion);
		System.out.println("pruning");
		sphUnion.pruneSteinerLeafs();


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

	public ArrayList<SteinerGrph> sphAll(SteinerGrph g, int maxIt) throws IOException{
		int[] targetArray = g.getTargetNodes().toIntArray();


		int step = 1; 

		if(targetArray.length/maxIt > 1) {
			step = targetArray.length/maxIt;
		}

		ArrayList<SteinerGrph> sphList = new ArrayList<SteinerGrph>();


		for(int i=0; i<maxIt; i++) {

			long start2 = System.currentTimeMillis();

			int iRoot = i*step;
			while(iRoot >= targetArray.length) {
				iRoot = iRoot - targetArray.length;
			}

			int perturbationStrat;
			if(i<2) {
				perturbationStrat = 0;
			}else if(i>0.9*maxIt) {
				perturbationStrat = 3;//only intensify in last 10% of its
			}else {
				perturbationStrat = new Random().nextInt(3)+1;
			}

			SteinerGrph sph = constructSPH(g, targetArray[iRoot], perturbationStrat, i);
			for(int i1=0;i1<5;i1++) {
				SteinerGrph candSph = constructSPH(g, targetArray[iRoot], perturbationStrat, i1);
				if(candSph.totalLength() < sph.totalLength()) {
					sph = candSph;
				}

			}
			long sphTime = System.currentTimeMillis(); 

			SteinerGrph improvedSph = sph.insertNodeImprovement(g);
			System.out.print("PS: "+perturbationStrat+"\t");
			System.out.print("Improvement procedure: I");
			while(improvedSph.totalLength() < sph.totalLength()) {
				sph = improvedSph;
				improvedSph = sph.insertNodeImprovement(g);

				System.out.print("I");
			}


			long nodeTime = System.currentTimeMillis() - sphTime;
			sphTime = sphTime - start2;

			System.out.println(" - Total length: "+sph.totalLength()+" \t time sph: "+sphTime+", local search time: "+nodeTime);

			sphList.add(sph);

			//Keep count of edge use through the  
			for(int e : sph.getEdges().toIntArray()) {
				long used = g.getEdgeWidthProperty().getValue(e);
				g.getEdgeWidthProperty().setValue(e, used+1);				
			}

		}

		return sphList;
	}

	/**
	 * 
	 * @param g
	 * @return array [min length of sph trees, length of td dp]
	 * @throws IOException
	 */
	public List<TestResult> sphAllandTD(SteinerGrph g, boolean verbose, int maxIt) throws IOException {
		ArrayList<SteinerGrph> sphList = new ArrayList<SteinerGrph>();
		ArrayList<TestResult> allResults = new ArrayList<TestResult>();
		
		int solPerIt = 20;
		
		int accumulatedSphTime = 0;
		
		for(int i= 0; i<maxIt/solPerIt; i++) {
			long startSPH = System.currentTimeMillis();


			int minSphLength = Integer.MAX_VALUE;

			int maxTw = 9;




			ArrayList<SteinerGrph> newSphList = sphAll(g, solPerIt);
			sphList.addAll(newSphList);
			/**
		for(int i=0; i<maxIt; i++) {

			long start2 = System.currentTimeMillis();

			int iRoot = i*step;
			while(iRoot >= targetArray.length) {
				iRoot = iRoot - targetArray.length;
			}

			//for(int i=0; i< targetArray.length && i <step*maxIt ; i=i+step) {
			int perturbationStrat;
			if(i<10) {
				perturbationStrat = 0;
			}else if(i>0.9*maxIt) {
				perturbationStrat = 3;//only intensify in last 10% of its
			}else {
				perturbationStrat = new Random().nextInt(3)+1;
			}

			SteinerGrph sph = constructSPH(g, targetArray[iRoot], perturbationStrat, i);
			for(int i1=0;i1<5;i1++) {
				SteinerGrph candSph = constructSPH(g, targetArray[iRoot], perturbationStrat, i1);
				if(candSph.totalLength() < sph.totalLength()) {
					sph = candSph;
				}

			}
			long sphTime = System.currentTimeMillis(); 

			SteinerGrph improvedSph = sph.insertNodeImprovement(g);
			System.out.print("PS: "+perturbationStrat+"\t");
			System.out.print("Improvement procedure: I");
			while(improvedSph.totalLength() < sph.totalLength()) {
				sph = improvedSph;
				improvedSph = sph.insertNodeImprovement(g);

				System.out.print("I");
			}


			long nodeTime = System.currentTimeMillis() - sphTime;
			sphTime = sphTime - start2;

			System.out.println(" - Total length: "+sph.totalLength()+" \t time sph: "+sphTime+", local search time: "+nodeTime);

			sphList.add(sph);

			//Keep count of edge use through the  
			for(int e : sph.getEdges().toIntArray()) {
				long used = g.getEdgeWidthProperty().getValue(e);
				g.getEdgeWidthProperty().setValue(e, used+1);				
			}

		}
			 **/ 


			Collections.sort(sphList);
			//Elite solution
			boolean doElite = true;
			if(doElite) {

				SteinerGrph elite = new SteinerGrph();
				for(SteinerGrph sph : sphList) {
					if(sph.totalLength() < sphList.get(0).totalLength()*1.01)
						elite.addSubgraph(sph);
				}
				SteinerGrph eliteSph = constructSPH(elite, elite.getTargetNodes().getGreatest());
				SteinerGrph sph = eliteSph;
				System.out.println(" Elite SPh length: "+eliteSph.totalLength());
				SteinerGrph improvedSph = sph.insertNodeImprovement(g);

				System.out.print("Improvement procedure: I");
				while(improvedSph.totalLength() < sph.totalLength()) {
					sph = improvedSph;
					improvedSph = sph.insertNodeImprovement(g);

					System.out.print("I");
				}

				System.out.println("Improeved Elite SPh length: "+sph.totalLength());
				sphList.add(sph);
			}



			long timeSPH = System.currentTimeMillis() - startSPH;
			accumulatedSphTime += timeSPH;
			
			
			minSphLength = sphList.get(0).totalLength();
			for(int tw=maxTw; tw<maxTw+3;tw++) {

				int minTDLength = Integer.MAX_VALUE; 
				long startTD = System.currentTimeMillis();
				int[] tdSolution = sphAlltoDP(sphList, g, tw);
				minTDLength = tdSolution[0];

				long timeDP = System.currentTimeMillis() - startTD;
				// ---

				TestResult result = new TestResult(); 
				result.sphSolution = minSphLength;
				result.dpSolution = minTDLength;
				result.maxTw = tw; 
				result.treesUsed = tdSolution[1];
				result.sphIterations = solPerIt*(i+1);
				result.sphTime = accumulatedSphTime;
				result.dpTime = (int)timeDP;
				
				
				allResults.add(result);
				/**TO REMOVE
				int[] out = new int[5];
				
				out[0] = minSphLength;
				out[1] = minTDLength;

				out[2] = (int) timeSPH;
				out[3] = (int) timeDP;
				out[4] = tdSolution[1];
				**/
			}

		}
		return allResults; 

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
		int mb = 1024*1024;
		System.out.println(Runtime.getRuntime().maxMemory()/mb);

		int maxIt = 10; 
		for(int m=16; m<20; m= m*2) {
			maxIt = m*2;
			String results = "";
			FileWriter writer = new FileWriter("C:\\Users\\tbosman\\git\\steiner\\steinertreemerger\\results\\"+""+instancePrefix+"_results_maxit"+maxIt+"_"+System.currentTimeMillis()+".txt");
			for(int i=342; i<343;i++) {
				try {
					long start = System.currentTimeMillis();
					SteinerGrph g = readInstance(getFilename(i));

					//SteinerGrph g = readInstance(dirName+"bip52p.stp");

					preProcess(g, true);
					long stop = start-System.currentTimeMillis();
					System.out.println("Time reading and preprocessing: "+stop);

					start = System.currentTimeMillis();
					//g.displayGraphstream_0_4_2();
					//int[] solutions = sphAllandTD(g, false, maxIt);
					List<TestResult> testResults = sphAllandTD(g, false, maxIt);
					
					long time = System.currentTimeMillis()-start; 

					//String currentResult = "Instance: "+instancePrefix+"["+i+"] | Min SPH: "+solutions[0]+", DP: "+solutions[1]+", Time SPH+N-A: "+solutions[2]+", Time DP: "+solutions[3]+", Trees Used: "+solutions[4];
				
					String currentResult = "Instance: "+instancePrefix+"["+i+"] : \n";
					for(TestResult result : testResults) {
						currentResult = currentResult+result.toString()+"\n";
					}
					
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
			writer.close();
		}
		
	}

	public static void main(String... args) throws IOException {
		new TestClass().start(); 
	}
}
