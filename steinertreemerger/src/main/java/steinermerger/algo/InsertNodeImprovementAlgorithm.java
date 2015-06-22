package steinermerger.algo;

import java.util.Random;

import grph.Grph;
import grph.properties.NumericalProperty;
import steinermerger.datastructures.SteinerGrph;
import steinermerger.datastructures.VertexSubGrph;
import steinermerger.datastructures.WeightedGrph;
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



	/**
	 * 
	 * @param g
	 * @param originalGrph
	 * @return
	 */
	public SteinerGrph compute( Grph g, SteinerGrph originalGrph) {

		WeightedGrph newGrph = new WeightedGrph(g);
		IntSet currentVertices = g.getVertices();

		/*
		IntSet candidateNodes = new DefaultIntSet(); 
		for(int v: currentVertices.toIntArray()) {
			IntSet nodes = originalGrph.getKClosestNeighbors(v, numNeighbours, weights);
			candidateNodes.addAll(nodes);

			candidateNodes.add(v);

		}
		 */
		IntSet candidateNodes = new DefaultIntSet();
		for(int v: originalGrph.getVertices().toIntArray()) {
			if(originalGrph.getVertexSizeProperty().getValue(v) == 0 || originalGrph.getVertexSizeProperty().getValue(v) == 1 ) {

			}else {
				candidateNodes.add(v);
			}
		}

		Random prng = new Random(System.currentTimeMillis());

		IntSet addedNodes = new DefaultIntSet(); 
		IntSet removedNodes = new DefaultIntSet();

		SteinerGrph fullGrph = new SteinerGrph(originalGrph); 
		fullGrph.removeAllBut(candidateNodes);

		KruskalAlgorithm kruskal = new KruskalAlgorithm(fullGrph.getEdgeWeightProperty());
		kruskal.sortWeights(originalGrph);

		PruneSteinerLeafAlgorithm pruner = new PruneSteinerLeafAlgorithm(originalGrph);


		while(!candidateNodes.isEmpty()) {
			int v = candidateNodes.pickRandomElement(prng, true);
			if(!newGrph.containsVertex(v) ){
				if(originalGrph.getVertexSizeProperty().getValue(v) != 1 ) {
					IntSet singletonV = new DefaultIntSet();
					singletonV.add(v);
					IntSet connecting = originalGrph.getEdgesConnecting(newGrph.getVertices(), singletonV);
					if(connecting.size() > 1) {//Only feasible for improvement if at least 2 edges connect v with current tree

						newGrph.addVertex(v);
						Grph neighbourGrph = new VertexSubGrph(originalGrph, newGrph.getVertices());
						WeightedGrph proposed; 
						proposed = kruskal.compute(neighbourGrph, kruskal.weightList);
						pruner.compute(proposed);


						if(proposed.totalLength() < newGrph.totalLength()) {
							newGrph = proposed;
							addedNodes.add(v);
						}else {
							newGrph.removeVertex(v);
						}
					}
				}
			}else {
				//node allready in stg
				if(!originalGrph.isTargetNode(v)) {
					if( g.getVertexSizeProperty().getValue(v) != 0) {
						Grph neighbourGrph = new VertexSubGrph(originalGrph, newGrph.getVertices());
						WeightedGrph proposed; 
						if(neighbourGrph.isConnected()) {
							proposed = kruskal.compute(neighbourGrph, kruskal.weightList);
							pruner.compute(proposed);
							if(proposed.totalLength() < newGrph.totalLength()) {
								newGrph = proposed;
								removedNodes.add(v);
							}
						}

					}
				}
			}

		}
		originalGrph.setVerticesSize(new NumericalProperty("vertex size", 16, 10));
		for(int v: addedNodes.toIntArray()) {
			originalGrph.getVertexSizeProperty().setValue(v, 0);//Just added nodes will not be checked for deletion in next it
		}
		for(int v: removedNodes.toIntArray()) {
			originalGrph.getVertexSizeProperty().setValue(v, 1);//Just removed nodes will not be checked for insertion in next it
		}

		SteinerGrph out = new SteinerGrph(newGrph);

		for(int t : getTargetNodes(originalGrph).toIntArray()) {//may be redundant
			out.setTargetNode(t, true);
		}
		return out;
	}

}
