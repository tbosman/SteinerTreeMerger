package steinermerger.datastructures;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grph.Grph;
import grph.GrphAlgorithm;
import grph.algo.search.DijkstraAlgorithm;
import grph.algo.search.SearchResult;
import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;
import steinermerger.algo.*;
import toools.set.IntSet;

/**
 * In memory graph object supporting integer weighted edges			
 * @author tbosman
 *
 */
public class WeightedGrph extends InMemoryGrph{
	private NumericalProperty edgeWeight = new NumericalProperty("edge weights",32, 0 ); 
	

	public transient final GrphAlgorithm<WeightedGrph> primAlgorithm = new PrimAlgorithm(edgeWeight);
	public transient final GrphAlgorithm<SearchResult> subgraphShortestPathsAlgorithm = new SubgraphShortestPathsAlgorithm(edgeWeight);
	public transient final GrphAlgorithm<SearchResult[]> dijkstraAlgorithm = new DijkstraAlgorithm(edgeWeight);
	
	
	public WeightedGrph(){
	}
	/**
	 * 
	 * @param e id of edge
	 * @return weight
	 */
	public int getEdgeWeight(int e) {
		assert getEdges().contains(e);
		return edgeWeight.getValueAsInt(e);
	}

	/**
	 * 
	 * @param e id of edge
	 * @param newWeight weight of edge
	 */
	public void setEdgeWeight(int e, int newWeight) {
		assert getEdges().contains(e);
		edgeWeight.setValue(e, newWeight);
		getEdgeLabelProperty().setValue(e, "e"+e+":"+newWeight);
	}
	
	public WeightedGrph computeMinimumSpanningTree() {
		return primAlgorithm.compute(this);
	}
	
	
	public SearchResult computeSubgraphShortestPaths(IntSet source) {
		return ((SubgraphShortestPathsAlgorithm)subgraphShortestPathsAlgorithm).compute(this, source);
	}
	
	public SearchResult computeShortestPaths(int source) {
		return ((DijkstraAlgorithm)dijkstraAlgorithm).compute(this, source);
	}
	
	public NumericalProperty getEdgeWeightProperty() {
		return edgeWeight;
	}
	
	public void setEdgeWeights(NumericalProperty weights) {
		this.edgeWeight = weights;
	}
}
