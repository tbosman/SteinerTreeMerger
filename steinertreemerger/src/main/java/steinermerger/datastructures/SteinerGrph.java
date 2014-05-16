package steinermerger.datastructures;

import grph.Grph;
import grph.properties.NumericalProperty;
import steinermerger.algo.ConstructSPHAlgorithm;
import steinermerger.algo.ContractSteinerNodesOfDegree2Algorithm;
import steinermerger.algo.PruneSteinerLeafAlgorithm;
import steinermerger.algo.SPHAlgorithm;
import steinermerger.algo.SPReductionAlgorithm;
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

	public transient final ConstructSPHAlgorithm sphAlgorithm = new ConstructSPHAlgorithm(this);
	protected transient final SteinerGrphAlgorithm<IntSet> pruneSteinerLeafAlgorithm  = new PruneSteinerLeafAlgorithm(this);
	protected transient final SteinerGrphAlgorithm<IntSet> contractSteinerNodesAlgorithm = new ContractSteinerNodesOfDegree2Algorithm(this); 
	protected transient final SteinerGrphAlgorithm<IntSet> spReductionAlgorithm = new SPReductionAlgorithm(this);

	public SteinerGrph() {
		super();
	}



	/**Copy constructor, clone topology and properties
	 * @param gIn
	 */
	public SteinerGrph(Grph gIn) {
		super(gIn);
	}



	//Graph property methods
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
	//Graph mutation
	public IntSet pruneSteinerLeafs() {
		return pruneSteinerLeafAlgorithm.compute(this);
	}

	//Algorithms
	public SteinerGrph computeSPHGraph(int root) {
		return sphAlgorithm.compute(this, root);
	}
	
	public SteinerGrph computeSPHGraph2(int root) {
		return sphAlgorithm.compute2(this, root);
	}

	public String toString() {
		return super.toString()+", "+getTargetNodes().size()+" targets";
	}

	public IntSet contractDegree2() {
		return contractSteinerNodesAlgorithm.compute(this);
	}
	
	public IntSet spReduction() {
		return spReductionAlgorithm.compute(this);
	}
	
	/**
	 * Calls all reduction technique on graph :
	 * Removes degree 1 and contracts 2 steiner nodes
	 */
	public void reduceGraph() {
		contractDegree2();
		pruneSteinerLeafs();
		spReduction();
	}
}
