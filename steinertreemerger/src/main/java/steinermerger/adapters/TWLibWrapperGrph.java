package steinermerger.adapters;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import libtw.input.GraphInput;
import libtw.input.InputException;
import libtw.input.GraphInput.InputData;
import libtw.ngraph.ListGraph;
import libtw.ngraph.ListVertex;
import libtw.ngraph.NGraph;
import libtw.ngraph.NVertex;
import steinermerger.datastructures.SteinerGrph;

public class TWLibWrapperGrph implements GraphInput{
	SteinerGrph in; 

	protected NGraph<InputData> g;
	protected Set<NVertex<InputData>> terminals;
	protected HashMap<Set<NVertex<InputData>>, Integer> edgeWeights;
	protected HashMap<String, NVertex<InputData>> vertices;

	public TWLibWrapperGrph(SteinerGrph in) {
		this.in = in; 
	}

	private void addEdge(int n1, int n2, int weight) {
		NVertex<InputData> v1 = vertices.get(""+n1);
		NVertex<InputData> v2 = vertices.get(""+n2);
		g.addEdge(v1, v2);
		Set<NVertex<InputData>> edge = new HashSet<NVertex<InputData>>();
		edge.add(v1);
		edge.add(v2);
		edgeWeights.put(edge, weight);
	}

	public NGraph<InputData> get()  {

		g = new ListGraph<InputData>();


		vertices = new HashMap<String, NVertex<InputData>>();
		edgeWeights = new HashMap<Set<NVertex<InputData>>, Integer>();
		terminals = new HashSet<NVertex<InputData>>();

		for(int v : in.getVertices().toIntArray()) {
			NVertex<InputData> newVertex = new ListVertex<InputData>(new InputData(vertices.size() ,""+(v)));
			vertices.put(""+(v), newVertex);
			g.addVertex(newVertex);
		}
		
		for(int e : in.getEdges().toIntArray()) {
			int v1 = in.getOneVertex(e);
			int v2 = in.getTheOtherVertex(e, v1);
			int weight = in.getEdgeWeight(e);
			addEdge(v1, v2, weight);
		}
		for(int t : in.getTargetNodes().toIntArray()) {
			terminals.add(vertices.get(""+t));
		}
		return g;
	}

	public Set<NVertex<InputData>> getTerminals(){
		return terminals;
	}

	public HashMap<Set<NVertex<InputData>>, Integer> getWeights(){
		return edgeWeights;
	}
}
