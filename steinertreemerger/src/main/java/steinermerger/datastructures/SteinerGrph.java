package steinermerger.datastructures;

import grph.properties.NumericalProperty;
import steinermerger.algo.SPHAlgorithm;
import steinermerger.algo.SteinerGrphAlgorithm;
import steinermerger.util.GrphTools;
import toools.set.IntSet;

/**
 * Datastructure for inputgraphs for the steiner problem in graphs
 * Has methods for target and steiner points
 * @author tbosman
 *
 */
public class SteinerGrph extends WeightedGrph {

	public transient final SPHAlgorithm sphAlgorithm = new SPHAlgorithm(this);
	
	public SteinerGrph() {
		super();
	}
	/**
	 * copy constructor	
	 * @param gIn steinerGraph to clone data from 
	 */
	public SteinerGrph(SteinerGrph gIn) {
		addVertices(gIn.getVertices());
		for(int e : gIn.getEdges().toIntArray()) {
			int v = gIn.getOneVertex(e);
			int w = gIn.getTheOtherVertex(e, v);
			addSimpleEdge(v, e, w, false);
			
		}
		GrphTools.copyProperties(gIn, this);
	}
	
	public boolean isTargetNode(int v) {
		return getVertexShapeProperty().getValue(v) == 1; 
	}
	
	public IntSet getTargetNodes() {
		return getVertexShapeProperty().findElementsWithValue(1, getVertices());
	}

	public NumericalProperty getTargetProperty() {
		return getVertexShapeProperty();
	}
	
	public void setTargetNode(int v, boolean target) {
		if(target) {
			getTargetProperty().setValue(v, 1);
			getVertexColorProperty().setValue(v, 1);
		}else {
			getTargetProperty().setValue(v, 0);
			getVertexColorProperty().setValue(v,0);
		}
		
	}

	public SteinerGrph computeSPHGraph(int root) {
		return sphAlgorithm.compute(this, root);
	}
	
}
