package steinermerger.algo;

import grph.Grph;
import grph.algo.distance.DistanceMatrix;
import grph.algo.distance.FloydWarshallAlgorithm;
import grph.algo.distance.StackBasedBellmanFordWeightedMatrixAlgorithm;
import grph.algo.distance.WeightedDistanceMatrixAlgorithm;
import steinermerger.datastructures.SteinerGrph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;

/**
 * Reduction heuristic, removes all edges e=(v,w) for which 
 * weight(e) > weight(sp(v,w)) 
 * Where sp is the shortest path from v to w
 * @author tbosman
 *
 */
public class SPReductionAlgorithm extends SteinerGrphAlgorithm<IntSet> {

	public SPReductionAlgorithm(SteinerGrph g) {
		super(g);
	}

	@Override
	public IntSet compute(Grph g) {
		WeightedDistanceMatrixAlgorithm fw;	
		g.addVertex(0);//Floyd warshall implementation works only on vertex set that run from 0 to size
		for(int i=0; i<g.getVertices().getGreatest();i++) {
			if(!g.containsVertex(i)) {
				g.addVertex(i);
			}
		}
		if(g.getVertices().isContiguous()) { 
			fw = new FloydWarshallAlgorithm(weights);
		}else {
			fw = new StackBasedBellmanFordWeightedMatrixAlgorithm(weights);
		}

		DistanceMatrix dist = fw.compute(g);
		System.out.println("#DBG Dist mat computed with"+fw.getClass().getName());
		IntSet removed = new DefaultIntSet();

		for(int e : g.getEdges().toIntArray()) {
			int v1 = g.getOneVertex(e);
			int v2 = g.getTheOtherVertex(e, v1);
			float sp =  dist.getDistance(v1, v2);
			int w = getWeight(e);
			if(w > sp) {
				g.removeEdge(e);
				removed.add(e);
			}
		}
		for(int v : g.getVerticesOfDegree(0).toIntArray()) {
			g.removeVertex(v);
		}
		
		return removed;
	}

}
