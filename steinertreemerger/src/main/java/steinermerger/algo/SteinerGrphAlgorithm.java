package steinermerger.algo;

import steinermerger.datastructures.SteinerGrph;
import toools.set.IntSet;
import grph.Grph;
import grph.GrphAlgorithm;
import grph.properties.NumericalProperty;

public abstract class SteinerGrphAlgorithm<R> extends GrphAlgorithm<R> {

	private NumericalProperty weights;
	private NumericalProperty targets;
	public SteinerGrphAlgorithm(NumericalProperty weights, NumericalProperty targets) {
		this.weights = weights;
		this.targets = targets; 
	}
	
	public SteinerGrphAlgorithm(SteinerGrph g) {
		this(g.getEdgeWeightProperty(), g.getTargetProperty());
	}
	
	

	public boolean isTarget(int v) {
		return (targets.getValue(v) == 1);
	}
	
	public int getWeight(int e) {
		return weights.getValueAsInt(e);
	}
	
	public IntSet getTargetNodes(Grph g) {
		return targets.findElementsWithValue(1, g.getVertices());
	}
}
