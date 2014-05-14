package steinermerger.algo;


import grph.Grph;
import steinermerger.datastructures.SteinerGrph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;

/**
 * prune all leafs that are not target nodes
 * @author tbosman
 *
 */
public class PruneSteinerLeafAlgorithm extends SteinerGrphAlgorithm<IntSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PruneSteinerLeafAlgorithm(SteinerGrph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	/**
	 * prunes all leafs that are not targets (i.e. steiner nodes)
	 * @param g steiner grph
	 * @return set of pruned vertices
	 */
	public IntSet compute(Grph g) {
		IntSet leafs = g.getVerticesOfDegree(1);
		IntSet pruned = new DefaultIntSet();
		for(int v : leafs.toIntArray()) {
			if(!isTarget(v)	) {
				g.removeVertex(v);
				pruned.add(v);
			}
		}
		return pruned;
	}


}
