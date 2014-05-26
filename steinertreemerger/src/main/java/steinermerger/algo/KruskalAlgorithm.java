package steinermerger.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import steinermerger.datastructures.VertexIntValue;
import steinermerger.datastructures.WeightedGrph;
import steinermerger.util.GrphTools;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import grph.Grph;
import grph.GrphAlgorithm;
import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;

public class KruskalAlgorithm extends GrphAlgorithm<WeightedGrph>{
	List<VertexIntValue> weightList;
	NumericalProperty weights;
	public KruskalAlgorithm(NumericalProperty weights) {
		this.weights = weights;
	}


	public void sortWeights(Grph g) {
		List<VertexIntValue> weightList = new ArrayList<VertexIntValue>();
		for(int e : g.getEdges().toIntArray()) {
			weightList.add(new VertexIntValue(e, weights.getValueAsInt(e)));
		}
		long start = System.currentTimeMillis();				
		Collections.sort(weightList);
		this.weightList = weightList; 
	}
	@Override
	public WeightedGrph compute(Grph g) {
		sortWeights(g);
		return compute(g, weightList); 				
	}
	
	

	public WeightedGrph compute(Grph g, List<VertexIntValue> weightList) {
		//keep track of which connected component each vertex belongs to
		//List<IntSet> components = new ArrayList<IntSet>();
		IntSet[] componentArray = new IntSet[g.getVertices().getGreatest()+1];
		for(int v: g.getVertices().toIntArray()) {
			IntSet c = new DefaultIntSet();
			c.add(v);
			componentArray[v] = c;
		}
		
		WeightedGrph t = new WeightedGrph(); 
		t.addVertices(g.getVertices());
		int edgesAdded =0;
		int numVertices = g.getVertices().size();
		
		
		for(int i=0; i<weightList.size() && edgesAdded < numVertices-1 ;i++) {
			int e = weightList.get(i).id;
			if(g.containsEdge(e)) {
				int v = g.getOneVertex(e);
				int w = g.getTheOtherVertex(e, v);
				

				if(!componentArray[v].contains(w)) {
					componentArray[v].addAll(componentArray[w]);
					for(int h : componentArray[v].toIntArray()) {
						componentArray[h] = (componentArray[v]);
					}				
					edgesAdded++;
					t.addWeightedEdge(v, e, w, weights.getValueAsInt(e));
				}
				
				

	
			}

		}
		for(int v : t.getVertices().toIntArray()) {
			GrphTools.copyVertexProperties(v, g, t);
		}
		assert edgesAdded == numVertices-1;
		assert t.isConnected();
		return t; 
	}



}
