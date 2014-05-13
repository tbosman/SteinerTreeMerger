package steinermerger.algo;

import toools.set.IntSet;
import grph.Grph;
import grph.GrphAlgorithm;
import grph.algo.search.DijkstraAlgorithm;
import grph.algo.search.SearchResult;
import grph.properties.NumericalProperty;

/**
 * Calculate shortest paths from a subset of the graph 
 * Currently works by copying the input graph and making the source set connected with zero edge weights
 * Only works for non-negative edge weights
 * @author tbosman
 *
 */
public class SubgraphShortestPathsAlgorithm extends GrphAlgorithm<SearchResult> {

	private NumericalProperty weights;
	public SubgraphShortestPathsAlgorithm(NumericalProperty weights) {
		this.weights = weights;
	}

	public SearchResult compute(Grph g, IntSet sourceSet) {
		g = g.clone();
		//connect all vertices in sourceset sequentially and set weights to zero
		int[] sourceArray = sourceSet.toIntArray();
		for(int i=0; i<sourceArray.length-1;i++) {
			int a = sourceArray[i];
			int b = sourceArray[i+1];
			if(g.getEdgesConnecting(a, b).isEmpty()){
				int e = g.addSimpleEdge(sourceArray[i], sourceArray[i+1], false);
				weights.setValue(e, 0);
			}else {
				weights.setValue(g.getEdgesConnecting(a, b).getGreatest(), 0);
			}
		}
		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(weights);
		SearchResult result = dijkstra.compute(g, sourceArray[0]);

		return result;
	}

	@Override
	public SearchResult compute(Grph g) {
		// TODO Handling algorithm call without source specification
		return null;
	}

}
