package steinermerger.datastructures;

import java.util.Collection;
import toools.NotYetImplementedException;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import grph.Grph;
import grph.properties.Property;

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
	@Override
	public DIRECTION getNavigation() {
		return g.getNavigation();
	}
	@Override
	public IntSet getVertices() {
		return vertices;
	}
	@Override
	public int getNextVertexAvailable() {
		throw new NotYetImplementedException();
	}
	@Override
	public void removeVertex(int v) {
		vertices.remove(v);;
	}
	@Override
	public void addVertex(int v) {
		vertices.add(v);
	}
	@Override
	public boolean containsVertex(int v) {
		return vertices.contains(v);
	}
	@Override
	public IntSet getOutOnlyElements(int v) {
		return g.getOutOnlyElements(v);
	}
	@Override
	public IntSet getInOnlyElements(int v) {
		return g.getInOnlyElements(v);
	}
	@Override
	public IntSet getInOutOnlyElements(int v) {
		return g.getInOutOnlyElements(v);
	}
	@Override
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
	@Override
	public boolean containsEdge(int e) {
		if( g.containsEdge(e)) {
			return isSubGraphEdge(e);
		}
		return false;
			
	}
	@Override
	public int getNextEdgeAvailable() {
		return g.getNextEdgeAvailable();
	}
	@Override
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
	@Override
	public int getOneVertex(int e) {
		int v = g.getOneVertex(e);
		assert vertices.contains(v);
		return v;
	}
	@Override
	public int getTheOtherVertex(int e, int v) {
		int w =  g.getTheOtherVertex(e, v);
		assert vertices.contains(w);
		return w;
	}
	
	@Override
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
	
	
	@Override
	public int hashCode() {
		return g.hashCode();
	}
	@Override
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
	
	
	@Override
	public String toString() {
		return vertices.size()+" vertices, "+getEdges().size()+" edges";
	}



}
