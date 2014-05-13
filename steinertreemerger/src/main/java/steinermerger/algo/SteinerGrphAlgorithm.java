package steinermerger.algo;

import steinermerger.datastructures.SteinerGrph;
import grph.Grph;
import grph.GrphAlgorithm;
import grph.properties.NumericalProperty;

public abstract class SteinerGrphAlgorithm<R> extends GrphAlgorithm<R> {

	private NumericalProperty weights;
	private NumericalProperty targets;
	public SteinerGrphAlgorithm(Grph g, NumericalProperty weights, NumericalProperty targets) {
		this.weights = weights;
		this.targets = targets; 
	}
	
	public SteinerGrphAlgorithm(SteinerGrph g) {
		this(g, g.getEdgeWeightProperty(), g.getTargetProperty());
	}
	
	@Override
	public abstract R compute(Grph g);

}
