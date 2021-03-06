package steinermerger.algo;

import steinermerger.datastructures.SteinerGrph;
import toools.set.IntSet;
import grph.Grph;
import grph.algo.search.SearchResult;

/**
 * Naive implementation
 * Construction heuristic for finding steiner tree 
 * Use shortest path heuristic: iteratively adds the closest target point to the tree
 * @author tbosman
 *
 */
public class SPHAlgorithm extends SteinerGrphAlgorithm<SteinerGrph> {
	SteinerGrph g;
	int root;
	public SPHAlgorithm(SteinerGrph g) {
		super(g);
	}

	
	
	public SteinerGrph compute(Grph gIn, int root) {
		this.root = root;
		//create a new graph to store progress by updating edges used in tree to zero edge weight
		this.g = new SteinerGrph(gIn);
		//TODO, ensure weights and targets from corresponding fields in algo

		IntSet targets = getTargetNodes(g);
		SteinerGrph s = new SteinerGrph(); 

		//INIT: add root vertex and calculate shortest path to first target vertex
		s.addVertex(root); 
		if(targets.contains(root)) targets.remove(root);

		if(g.isTargetNode(root))
			s.setTargetNode(root, true);
		
//		s.displayGraphstream_0_4_2();
		while(!targets.isEmpty()) {
			growTree(s, targets); 
		}
		
		return s;
	}
	
	
	private void growTree(SteinerGrph s, IntSet targets) {
		IntSet currentNodes = s.getVertices(); 
		SearchResult result = g.computeShortestPaths(root);
		//find minimum distance target node
		int minTarget =0; 
		int minDistance= Integer.MAX_VALUE;
		for(int t : targets.toIntArray() ) {
			int dist = result.distances[t];
			if(dist < minDistance) {
				minTarget = t;
				minDistance = dist;
			}
		}

		s.addVertex(minTarget);
		s.setTargetNode(minTarget, true);
		targets.remove(minTarget);
		//backtrack path to current tree from target node, and set corresponding weights to zero in input graph
		boolean connected = false; 
		int lastNode = minTarget; 
		while(!connected) {
			int newNode = result.predecessors[lastNode];
			
			if(s.containsVertex(newNode)) {
				connected =true; 
			}else {
				s.addVertex(newNode);	
			}
			
			int e = g.getEdgesConnecting(lastNode, newNode).getGreatest();		
			//add edge to tree 
			s.addSimpleEdge(lastNode, e, newNode, false);
			s.setEdgeWeight(e, g.getEdgeWeight(e));
			if(g.getEdgeWeight(e) == 0) {
				System.out.println("#DBG edgeweight 0?");
			}
			//update input graph
			g.setEdgeWeight(e, 0);
			
			lastNode = newNode;
		}
	}

	@Override
	public SteinerGrph compute(Grph g) {
		return compute(g, g.getVertices().pickRandomElement(new java.util.Random()));
	}

}
