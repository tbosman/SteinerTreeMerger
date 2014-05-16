package steinermerger.algo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;

import org.miv.util.Random;

import steinermerger.datastructures.SteinerGrph;
import steinermerger.datastructures.VertexIntValue;
import steinermerger.util.GrphTools;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import grph.Grph;
import grph.GrphAlgorithm;
import grph.algo.search.SearchResult;

/**
 * Faster implementation
 * Construction heuristic for finding steiner tree 
 * Use shortest path heuristic: iteratively adds the closest target point to the tree
 * @author tbosman
 *
 */
public class ConstructSPHAlgorithm extends SteinerGrphAlgorithm<SteinerGrph> {
	SteinerGrph stg;
	Grph g;
	int root;

	private int[] distances; 
	private int[] predecessors; 
	IntSet finalNodes = new DefaultIntSet(); //set of nodes in the steiner tree from which the shortest paths have been calculated
	VertexIntValue[] sdArray;//Array of references to shortest know distance from tree 
	
	public ConstructSPHAlgorithm(SteinerGrph g) {
		super(g);
	}



	public SteinerGrph compute(Grph gIn, int root) {
		this.root = root;
		//create a new graph to store progress by updating edges used in tree to zero edge weight
		this.stg = new SteinerGrph(gIn);
		//TODO, ensure weights and targets from corresponding fields in algo

		IntSet targets = getTargetNodes(stg);
		SteinerGrph s = new SteinerGrph(); 

		//INIT: add root vertex and calculate shortest path to first target vertex
		s.addVertex(root); 
		if(targets.contains(root)) targets.remove(root);

		if(stg.isTargetNode(root))
			s.setTargetNode(root, true);

		//		s.displayGraphstream_0_4_2();
		while(!targets.isEmpty()) {
			growTree(s, targets); 
		}

		return s;
	}

	
	private void recomputeForSubSet(IntSet vset) {
		PriorityQueue<VertexIntValue> sdHeap = new PriorityQueue<VertexIntValue>();
		
		for(int v: vset.toIntArray()) {
			sdArray[v].setValue(0);//Set all distances to nodes in the partial tree to 0 
			sdHeap.add(sdArray[v]);
		}
		
		IntSet visited = finalNodes;
		while(visited.size() < g.getNumberOfVertices() && sdHeap.size() > 0) {
			shortestPathStep(visited, sdHeap);
		}

		finalNodes.addAll(vset);//Add set to permanent node list
		
	}
	
	
	private void shortestPathStep(IntSet visited, PriorityQueue<VertexIntValue> sdHeap) {
		
			VertexIntValue newNode = sdHeap.poll();
			int cNode = newNode.id;
			int cDist = newNode.getValue();
			visited.add(cNode);
			distances[cNode] =  cDist;

			
			IntSet edges = g.getEdgesIncidentTo(cNode);

			for(int e: edges.toIntArray()){
				int uNode = g.getTheOtherVertex(e, cNode);
				if(!visited.contains(uNode)) {
				int uWeight = getWeight(e);
				VertexIntValue uSD = sdArray[uNode]; 
				if(uSD == null) {
					uSD = new VertexIntValue(uNode, uWeight+cDist); 
					sdArray[uNode] = uSD; 
					sdHeap.add(uSD);
					predecessors[uNode] =  cNode;
				}else {
					int newUDist = cDist + uWeight;
					if(newUDist < uSD.getValue()) {
						uSD.setValue(newUDist);
						predecessors[uNode] =  cNode;
					}
				}
				}

			}

	}
	
	private void computeInitialShortestPaths(Grph g, int root) {
		IntSet visited = new DefaultIntSet(); 

		predecessors = new int[g.getVertices().getGreatest()+1];
		distances = new int[g.getVertices().getGreatest()+1];

		PriorityQueue<VertexIntValue> sdHeap = new PriorityQueue<VertexIntValue>();//for sorting shortest know distances 
		sdArray = new VertexIntValue[g.getVertices().getGreatest()+1];//for referencing by node id 

		//add root to visited 
		predecessors[root] =  -1;
		distances[root] = 0; 
		visited.add(root);
		finalNodes.add(root);
		IntSet edges = g.getEdgesIncidentTo(root);

		for(int e: edges.toIntArray()){
			int uWeight = getWeight(e);
			int uNode = g.getTheOtherVertex(e, root);
			VertexIntValue uSD  = new VertexIntValue(uNode, uWeight); 
			sdArray[uNode] = uSD; 
			sdHeap.add(uSD);
			predecessors[uNode] =  root;
		}

		
		
		while(visited.size() < g.getNumberOfVertices()) {
			shortestPathStep(visited, sdHeap);
		}



	}

	public SteinerGrph compute2(Grph g, int root) {
		this.g = g;
		IntSet targets = getTargetNodes(g);
		SteinerGrph s = new SteinerGrph(); 

		//INIT: add root vertex and calculate shortest path to first target vertex
		s.addVertex(root); 
		if(targets.contains(root)) targets.remove(root);

		if(isTarget(root))
			s.setTargetNode(root, true);
		
		computeInitialShortestPaths(g, root);
		
		int minTarget = findClosestTargetNode(targets);
		IntSet justAdded = connectTargetTo(minTarget, s);
		targets.remove(minTarget);
		
		while(targets.size() > 0 ) {
			recomputeForSubSet(justAdded);
			minTarget = findClosestTargetNode(targets);
			justAdded = connectTargetTo(minTarget, s);
			targets.remove(minTarget);
		}

		return s;

	}
	

	
	private int findClosestTargetNode(IntSet targets) {
		int minTarget =0; 
		int minDistance= Integer.MAX_VALUE;
		for(int t : targets.toIntArray() ) {
			int dist = distances[t];
			if(dist < minDistance) {
				minTarget = t;
				minDistance = dist;
			}
		}
		return minTarget;
		
	}
	
	private IntSet connectTargetTo(int target, SteinerGrph s) {
		IntSet addedNodes = new DefaultIntSet();
		addedNodes.add(target);
		s.addVertex(target);
		s.setTargetNode(target, true);
		boolean connected = false; 
		int lastNode = target; 
		while(!connected) {
			int newNode = predecessors[lastNode];

			if(s.containsVertex(newNode)) {
				connected =true; 
			}else {
				s.addVertex(newNode);
				addedNodes.add(newNode);
			}

			int e = g.getEdgesConnecting(lastNode, newNode).getGreatest();		
			//add edge to tree 
			s.addSimpleEdge(lastNode, e, newNode, false);
			
			s.setEdgeWeight(e, getWeight(e));

			lastNode = newNode;
		}
		return addedNodes;
	}
	

	private void growTree(SteinerGrph s, IntSet targets) {
		IntSet currentNodes = s.getVertices(); 
		SearchResult result = stg.computeShortestPaths(root);
//		SearchResult result = new SearchResult(g.getVertices().getGreatest()+1);
//		computeInitialShortestPaths(g, root);
//		for(int i=0; i<distances.length;i++) {
//			result.distances[i] = this.distances[i];
//			result.predecessors[i] = this.predecessors[i];
//		}

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

			int e = stg.getEdgesConnecting(lastNode, newNode).getGreatest();		
			//add edge to tree 
			s.addSimpleEdge(lastNode, e, newNode, false);
			s.setEdgeWeight(e, stg.getEdgeWeight(e));
			if(stg.getEdgeWeight(e) == 0) {
				System.out.println("#DBG edgeweight 0?");
			}
			//update input graph
			stg.setEdgeWeight(e, 0);

			lastNode = newNode;
		}
	}

	public SteinerGrph compute(Grph g) {
		return compute(g, g.getVertices().pickRandomElement(new java.util.Random()));
	}

}
