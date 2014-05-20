package steinermerger.algo;

import java.util.Random;

import grph.Grph;
import steinermerger.datastructures.SteinerGrph;
import steinermerger.util.GrphTools;
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
			return compute(g, originalGrph, 5);
		}
	}



	/**
	 * 
	 * @param g
	 * @param originalGrph
	 * @return
	 */
	public SteinerGrph compute( Grph g, SteinerGrph originalGrph, int numNeighbours) {
		SteinerGrph newGrph = new SteinerGrph(g);
		IntSet currentVertices = g.getVertices();

		long start = System.currentTimeMillis();
		IntSet candidateNodes = new DefaultIntSet(); 
		for(int v: currentVertices.toIntArray()) {
			IntSet nodes = originalGrph.getKClosestNeighbors(v, numNeighbours, weights);
			candidateNodes.addAll(nodes);
			
				candidateNodes.add(v);
			
		}

		Random prng = new Random(System.currentTimeMillis());

		IntSet addedTargets = new DefaultIntSet(); 

		SteinerGrph fullGrph = new SteinerGrph(originalGrph); 
		fullGrph.removeAllBut(candidateNodes);
		KruskalAlgorithm kruskal = new KruskalAlgorithm(fullGrph.getEdgeWeightProperty());
		kruskal.sortWeights(originalGrph);

		while(!candidateNodes.isEmpty()) {
			int v = candidateNodes.pickRandomElement(prng, true);
			if(!newGrph.containsVertex(v) ){

				IntSet singletonV = new DefaultIntSet();
				singletonV.add(v);
				IntSet connecting = originalGrph.getEdgesConnecting(newGrph.getVertices(), singletonV);
				if(connecting.size() > 1) {//Only feasible for improvement if at least 2 edges connect v with current tree
					newGrph.addVertex(v);
					newGrph.setTargetNode(v, true);
					SteinerGrph proposedGrph = new SteinerGrph(originalGrph);
					proposedGrph.removeAllBut(newGrph.getVertices());

					proposedGrph = new SteinerGrph(kruskal.compute(proposedGrph, kruskal.weightList));
					proposedGrph.pruneSteinerLeafs();

					if(proposedGrph.totalLength() < newGrph.totalLength()) {
						newGrph = proposedGrph;
						addedTargets.add(v);
					}else {
						newGrph.removeVertex(v);
					}
				}
			}else {
				//node allready in stg
				if(!originalGrph.isTargetNode(v)) {
					SteinerGrph proposedGrph = new SteinerGrph(originalGrph);
					proposedGrph.removeAllBut(newGrph.getVertices());
					proposedGrph.removeVertex(v);
					if(proposedGrph.isConnected()) {
						proposedGrph = new SteinerGrph(kruskal.compute(proposedGrph, kruskal.weightList));
						proposedGrph.pruneSteinerLeafs();
						if(proposedGrph.totalLength() < newGrph.totalLength()) {
							newGrph = proposedGrph;
						}

					}
				}
			}

		}
		return newGrph;
	}

}
