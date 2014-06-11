package steinermerger.algo;

import java.io.IOException;
import java.util.ArrayList;

import steinermerger.adapters.TWLibWrapperGrph;
import steinermerger.datastructures.SteinerGrph;
import steinermerger.io.STPWriter;
import toools.set.IntSet;

public class DPMergeSolutions {

	private ArrayList<SteinerGrph> solutionsList;
	private SteinerGrph original;
	public DPMergeSolutions(ArrayList<SteinerGrph> solutionsList, SteinerGrph original) {
		this.solutionsList = solutionsList;
		this.original = original; 
	}
	
	public void mergeAndWriteToFile(String fileName) {
		SteinerGrph union = mergeAll(); 
		TreeDecomposition td = new TreeDecomposition();
		int tw = td.computeTreeWidth(new TWLibWrapperGrph(union));
		System.out.println("TW = "+tw);
		
		try {
			STPWriter writer = new STPWriter(fileName, union);
			writer.addRemark("TW: "+tw);
			writer.write();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	
	public void pairWiseMerge() {
		int initSize = solutionsList.size();
		for(int i=0; i < initSize-1;i++) {
			for(int j=i+1; j < initSize;j++) {
				SteinerGrph init = solutionsList.get(i);
				SteinerGrph guide = solutionsList.get(j);
				IntSet differenceGuide = guide.getVertices().clone();
				for(int v : init.getVertices().toIntArray()) {
					if(differenceGuide.contains(v)) {
						differenceGuide.remove(v);
					}
				}
				IntSet differenceInit = init.getVertices().clone();
				for(int v : guide.getVertices().toIntArray()) {
					if(differenceInit.contains(v)) {
						differenceInit.remove(v);
					}
				}
				
				SteinerGrph preMerge = new SteinerGrph(solutionsList.get(i));
				preMerge.addSubgraph(solutionsList.get(j));
				int preTw = new TreeDecomposition().computeTreeWidth(new TWLibWrapperGrph(preMerge));
				
				System.out.println("Difference "+j+"-"+i+": "+differenceGuide);
				System.out.println("Difference "+i+"-"+j+": "+differenceInit);
				
				IntSet connecting =  original.getEdgesConnecting(differenceInit, differenceGuide);
				System.out.println("Connecting edges: "+ connecting);
				for(int v : differenceInit.toIntArray()) {
					for(int w: differenceGuide.toIntArray()) {
						if(original.areVerticesAdjacent(v, w)) {
							int e = original.getEdgesConnecting(v, w).getGreatest();
							int weight = original.getEdgeWeight(e);
							preMerge.addWeightedEdge(v, e, w, weight);
							
						}
					}
				}
				int postTw = new TreeDecomposition().computeTreeWidth(new TWLibWrapperGrph(preMerge));
				
				System.out.println("Pre/post TW: "+preTw+"/"+postTw);
				solutionsList.add(preMerge);
				if(postTw<10) {
				int postSol = (int) new TreeDecomposition().computeTreeDP(new TWLibWrapperGrph(preMerge));
				System.out.println("postSol: "+postSol);
				}
			}
		}
	}
	
	private SteinerGrph mergeAll() {
		SteinerGrph union = new SteinerGrph();
		for(SteinerGrph stg : solutionsList) {
			union.addSubgraph(stg);
		}
		
		return union; 
	}

	
}
