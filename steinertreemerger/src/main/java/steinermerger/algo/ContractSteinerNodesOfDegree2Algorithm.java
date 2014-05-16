package steinermerger.algo;

import grph.Grph;
import steinermerger.datastructures.SteinerGrph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;

/**
 * algorithm to contract all steiner nodes of degree 2, retaining the shortest edges length in case of ambiguity
 * @author tbosman
 *
 */
public class ContractSteinerNodesOfDegree2Algorithm extends
		SteinerGrphAlgorithm<IntSet> {


	public ContractSteinerNodesOfDegree2Algorithm(SteinerGrph g) {
		super(g);
	}

	@Override
	public IntSet compute(Grph g) {
		IntSet nodes2 = g.getVerticesOfDegree(2);
		IntSet contracted = new DefaultIntSet();
		for(int v : nodes2.toIntArray()) {
			if(!isTarget(v) && g.getVertexDegree(v) == 2) {//dont remove target nodes, and check whether vertices hasn't been already contracted
				IntSet edges = g.getEdgesIncidentTo(v);
				int e1 = edges.toIntArray()[0]; 
				int e2 = edges.toIntArray()[1];
				int weight12 = getWeight(e1)+getWeight(e2);
				int w1 = g.getTheOtherVertex(e1, v);
				int w2 = g.getTheOtherVertex(e2, v);
				int e12; 
				if(!g.areVerticesAdjacent(w1, w2)) {
					e12 = g.addUndirectedSimpleEdge(w1, w2);
					weights.setValue(e12, weight12);
				}else {
					e12 = g.getEdgesConnecting(w1, w2).getGreatest();
					if(getWeight(e12) > weight12) {
						weights.setValue(e12, weight12);
					}
				}
				g.removeVertex(v);
				contracted.add(v);
			}
		}
		
		return contracted;
	}

}
