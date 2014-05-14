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
		IntSet pruned = new DefaultIntSet();
		IntSet leafs = g.getVerticesOfDegree(1);
		int lastPrunedSize = -1;//to check wether any leafs were pruned in last ieration
		while(pruned.size() > lastPrunedSize) {
			lastPrunedSize = pruned.size(); 
			for(int v : leafs.toIntArray()) {
			if(!isTarget(v)	) {
				g.removeVertex(v);
				pruned.add(v);
			}
		}
			leafs = g.getVerticesOfDegree(1);
		}
		return pruned;
	}


}
