package steinermerger.algo;

import java.util.Random;

import grph.Grph;
import steinermerger.datastructures.SteinerGrph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;

public class InsertNodeImprovementAlgorithm extends SteinerGrphAlgorithm<SteinerGrph> {
	SteinerGrph originalGrph; 

	public InsertNodeImprovementAlgorithm(SteinerGrph g) {
		super(g);
	}

	public void setOriginalGrph(SteinerGrph g) {
		this.originalGrph = g;
	}
	@Override
	public SteinerGrph compute(Grph g) throws IllegalStateException{
		if(originalGrph == null) {
			throw new IllegalStateException("Orginal graph not set");
		}else {
			return compute(g, originalGrph);
		}
	}

	public SteinerGrph compute( Grph g, SteinerGrph originalGrph) {
		SteinerGrph newGrph = new SteinerGrph(g);
		IntSet currentVertices = g.getVertices();

		IntSet candidateNodes = new DefaultIntSet(); 
		for(int v: currentVertices.toIntArray()) {
			IntSet nodes = originalGrph.getKClosestNeighbors(v, 2, weights);
			candidateNodes.addAll(nodes);

		}

		Random prng = new Random(System.currentTimeMillis());
		candidateNodes.pickRandomElement(prng, true);

		IntSet addedTargets = new DefaultIntSet(); 


		while(!candidateNodes.isEmpty()) {
			int v = candidateNodes.pickRandomElement(prng, true);
			if(!newGrph.containsVertex(v) ){
				if(originalGrph.getNeighbours(newGrph.getVertices()).contains(v)) {//Check if there still exists an edge to current tree
					newGrph.addVertex(v);
					newGrph.setTargetNode(v, true);
					SteinerGrph proposedGrph = newGrph.mstPrune(originalGrph);
					if(proposedGrph.totalLength() < newGrph.totalLength()) {
						newGrph = proposedGrph;
						addedTargets.add(v);
					}else {
						newGrph.removeVertex(v);
					}
				}
			}else {
				//node allready in stg
			}

		}
		return newGrph;
	}

}
