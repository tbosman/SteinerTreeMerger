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
import steinermerger.util.GrphTools;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.UnmodifiableIntSet;

/**
 * In memory graph object supporting integer weighted edges			
 * @author tbosman
 *
 */
public class WeightedGrph extends InMemoryGrph implements Comparable<WeightedGrph>{
	private NumericalProperty edgeWeight = new NumericalProperty("edge weights",32, 0 ); 


	public transient final GrphAlgorithm<WeightedGrph> primAlgorithm = new PrimAlgorithm(edgeWeight);
	public transient final GrphAlgorithm<SearchResult> subgraphShortestPathsAlgorithm = new SubgraphShortestPathsAlgorithm(edgeWeight);
	public transient final GrphAlgorithm<SearchResult[]> dijkstraAlgorithm = new DijkstraAlgorithm(edgeWeight);

	/**
	 * Copy constructor, clones grph topology and properties
	 * @param grph
	 */
	public WeightedGrph(Grph gIn) {

		addVertices(gIn.getVertices());
		for(int e : gIn.getEdges().toIntArray()) {
			int v = gIn.getOneVertex(e);
			int w = gIn.getTheOtherVertex(e, v);
			addSimpleEdge(v, e, w, false);

		}
		GrphTools.copyProperties(gIn, this);
	}
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
	
	/**
	 * Takes union of this and input graph
	 * Assumes that both graphs subgraphs from the same graph
	 * i.e. elements equal vertex/edge id iff they refer to the same element
	 * @param g
	 * @return
	 */
	public WeightedGrph addSubgraph(WeightedGrph g) {
		
		for(int v: g.getVertices().toIntArray()) {
			if(!this.containsVertex(v)) {
				addVertex(v);
				GrphTools.copyVertexProperties(v, g, this);
			}
		}
		for(int e: g.getEdges().toIntArray()) {
			if(!this.containsEdge(e)){
				int v = g.getOneVertex(e);
				int w = g.getTheOtherVertex(e, v);
				this.addSimpleEdge(v, e, w, false);
				GrphTools.copyEdgeProperties(e, g, this);
				this.setEdgeWeight(e, g.getEdgeWeight(e));
			}
		}
		//GrphTools.copyProperties(g, this);
		return this;
	}
	//Overriding methods to circumvent bugs/problems in Grph library
	@Override
	public IntSet getVerticesOfDegree(int degree) {
		IntSet out = new DefaultIntSet();
		for(int v : getVertices().toIntArray()) {
			if(this.getVertexDegree(v) == degree) {
				out.add(v);
			}
		}
		return out;
	}
	
	/**
	 * removes all vertices except for specified set
	 * @param vertices
	 */
	public void removeAllBut(IntSet vertices) {
		
		for(int v: getVertices().toIntArray()) {
			if(!vertices.contains(v)) {
				removeVertex(v);
			}
		}
	}
	
			
	public int totalLength() {
		int length = 0; 
		for(int e : getEdges().toIntArray()) {
			length += getEdgeWeight(e);
		}
		return length;
	}
	/**
	 * Compares based on total graph length (Sum of edge weights)
	 * @param otherGrph
	 * @return
	 */
	public int compareTo(WeightedGrph otherGrph){
		return this.totalLength() - otherGrph.totalLength(); 
	}
}
