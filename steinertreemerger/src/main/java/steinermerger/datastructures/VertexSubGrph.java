package steinermerger.datastructures;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JComponent;

import org.miv.graphstream.ui.GraphViewerRemote;

import com.carrotsearch.hppc.IntArrayList;

import toools.NotYetImplementedException;
import toools.collections.IntMap;
import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.math.Distribution;
import toools.math.IntMatrix;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import toools.set.IntSetFilter;
import grph.Cache;
import grph.Grph;
import grph.GrphAlgorithm;
import grph.TopologyListener;
import grph.VertexPair;
import grph.algo.AdjacencyMatrix;
import grph.algo.MaxFlowAlgorithmResult;
import grph.algo.clustering.Cluster;
import grph.algo.distance.DistanceMatrix;
import grph.algo.distance.PageRank;
import grph.algo.partitionning.metis.Gpmetis.Ctype;
import grph.algo.partitionning.metis.Gpmetis.Iptype;
import grph.algo.partitionning.metis.Gpmetis.Objtype;
import grph.algo.partitionning.metis.Gpmetis.Ptype;
import grph.algo.search.BFSAlgorithm;
import grph.algo.search.SearchResult;
import grph.gui.GraphstreamBasedRenderer;
import grph.in_memory.InMemoryGrph;
import grph.io.GraphvizImageWriter.COMMAND;
import grph.io.GraphvizImageWriter.OUTPUT_FORMAT;
import grph.path.ArrayListPath;
import grph.path.Path;
import grph.path.SearchResultWrappedPath;
import grph.properties.NumericalProperty;
import grph.properties.Property;
import grph.stepper.AbstractStepper;
import grph.util.Matching;

/**
 * abstraction class to handle a vertexset induced subgraph of a grph object without copying it 
 * @author tbosman
 *
 */
public class VertexSubGrph extends Grph {

	public final IntSet vertices;
	public final Grph g;
	public VertexSubGrph(Grph g, IntSet vertices)
	{
		this.g = g;
		this.vertices = vertices; 
	}
	public DIRECTION getNavigation() {
		return g.getNavigation();
	}
	public IntSet getVertices() {
		return vertices;
	}
	public int getNextVertexAvailable() {
		throw new NotYetImplementedException();
	}
	public void removeVertex(int v) {
		vertices.remove(v);;
	}
	public void addVertex(int v) {
		vertices.add(v);
	}
	public boolean containsVertex(int v) {
		return vertices.contains(v);
	}
	public IntSet getOutOnlyElements(int v) {
		return g.getOutOnlyElements(v);
	}
	public IntSet getInOnlyElements(int v) {
		return g.getInOnlyElements(v);
	}
	public IntSet getInOutOnlyElements(int v) {
		return g.getInOutOnlyElements(v);
	}
	public boolean storeEdges() {
		return g.storeEdges();
	}
	private boolean isSubGraphEdge(int e) {
		if(vertices.contains(g.getOneVertex(e))) {
			int v = g.getOneVertex(e);
			if(vertices.contains(g.getTheOtherVertex(e, v))) {
				return true;
			}
		}
		return false;
	}
	public boolean containsEdge(int e) {
		if( g.containsEdge(e)) {
			return isSubGraphEdge(e);
		}
		return false;
			
	}
	public int getNextEdgeAvailable() {
		return g.getNextEdgeAvailable();
	}
	public IntSet getEdges() {
		IntSet edges = g.getEdges();
		IntSet subEdges = new DefaultIntSet();
		for(int e: edges.toIntArray()) {
			if(isSubGraphEdge(e)) {
				subEdges.add(e);
			}
		}
		return subEdges;

	}
	public int getOneVertex(int e) {
		int v = g.getOneVertex(e);
		assert vertices.contains(v);
		return v;
	}
	public int getTheOtherVertex(int e, int v) {
		int w =  g.getTheOtherVertex(e, v);
		assert vertices.contains(w);
		return w;
	}
	
	public boolean isConnected() {
		IntSet component = new DefaultIntSet(); 
		int root = vertices.getGreatest();
		component.add(root);
		
		IntSet subEdges = getEdges();
		IntSet candidateNodes = new DefaultIntSet(); 
		candidateNodes.add(root);
		
		while(component.size() < vertices.size() && !candidateNodes.isEmpty()) {
			int v = candidateNodes.getGreatest();
			IntSet incidentEdges = g.getEdgesIncidentTo(v);
			for(int e: incidentEdges.toIntArray()) {
				if(subEdges.contains(e)) {
					subEdges.remove(e);
					int w = getTheOtherVertex(e, v);
					if(!component.contains(w)) {
						component.add(w);
						candidateNodes.add(w);
					}
				}
			}
			candidateNodes.remove(v);
		}
		
	
		boolean connected = component.size() == vertices.size();		
		return connected;
	}
	
	
	public int hashCode() {
		return g.hashCode();
	}
	public Collection<Property> getProperties() {
		return g.getProperties();
	}
	
	@Override
	public void removeEdge(int e) {
		throw new NotYetImplementedException();
	}

	@Override
	public int getNumberOfUndirectedSimpleEdges() {
		throw new NotYetImplementedException();
	}
	@Override
	public void addUndirectedSimpleEdge(int e, int v1, int v2) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public boolean isUndirectedSimpleEdge(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public int getNumberOfDirectedSimpleEdges() {
		throw new NotYetImplementedException();
	}
	@Override
	public void addDirectedSimpleEdge(int src, int e, int dest) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public boolean isDirectedSimpleEdge(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public int getDirectedSimpleEdgeTail(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public int getDirectedSimpleEdgeHead(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public int getNumberOfUndirectedHyperEdges() {
		throw new NotYetImplementedException();
	}
	@Override
	public void addUndirectedHyperEdge(int e) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public boolean isUndirectedHyperEdge(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public void addToUndirectedHyperEdge(int edge, int vertex) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public void removeFromHyperEdge(int e, int v) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public IntSet getUndirectedHyperEdgeVertices(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public void addDirectedHyperEdge(int e) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public boolean isDirectedHyperEdge(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public int getNumberOfDirectedHyperEdges() {
		throw new NotYetImplementedException();
	}
	@Override
	public IntSet getDirectedHyperEdgeHead(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public IntSet getDirectedHyperEdgeTail(int e) {
		throw new NotYetImplementedException();
	}
	@Override
	public void addToDirectedHyperEdgeTail(int e, int v) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public void addToDirectedHyperEdgeHead(int e, int v) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public void removeFromDirectedHyperEdgeHead(int e, int v) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public void removeFromDirectedHyperEdgeTail(int e, int v) {
		throw new NotYetImplementedException();
		
	}
	@Override
	public void removeEdge(int u, int v) {
		throw new NotYetImplementedException();
		
	}
	
	
	public String toString() {
		return vertices.size()+" vertices, "+getEdges().size()+" edges";
	}



}
