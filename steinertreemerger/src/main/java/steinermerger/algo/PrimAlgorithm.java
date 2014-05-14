package steinermerger.algo;

import steinermerger.datastructures.WeightedGrph;
import toools.set.IntSet;
import grph.Grph;
import grph.GrphAlgorithm;
import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;

public class PrimAlgorithm extends GrphAlgorithm<WeightedGrph>{

	NumericalProperty weights;
	public PrimAlgorithm(NumericalProperty weights) {
		this.weights = weights;
	}


	@Override
	public WeightedGrph compute(Grph g) {
		return compute(g,g.getVertices().getGreatest() );
	}

	public WeightedGrph compute(Grph g, int root) {
		WeightedGrph t = new WeightedGrph();
		t.addVertex(root);
		for(int i=1; i<g.getNumberOfVertices(); i++) {
			int minEdge = 0; 
			int minLength = Integer.MAX_VALUE;
			int oldNode = 0;
			int newNode = 0;
			IntSet candidates = g.getNeighbours(t.getVertices());
			for(int c : candidates.toIntArray()) {
				for(int v : t.getVertices().toIntArray()) {
					if(g.areVerticesAdjacent(c, v)) {
						int e = g.getEdgesConnecting(c, v).getGreatest();

						if(weights.getValue(e) < minLength) {
							minEdge = e;
							minLength = weights.getValueAsInt(e);
							newNode = c;
							oldNode = v;
						}
					}
				}
			}
			if(newNode == 0 && oldNode == 0) {
				throw new Error("Minimum spanning tree: no feasible edge found.");
			}
			t.addVertex(newNode);
			t.addUndirectedSimpleEdge(minEdge, oldNode, newNode);
			t.setEdgeWeight(minEdge, minLength);
		}
		return t; 
	}

}
