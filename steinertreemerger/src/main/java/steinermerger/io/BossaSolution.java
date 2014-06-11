package steinermerger.io;

import toools.set.DefaultIntSet;
import toools.set.IntSet;

public class BossaSolution {

	private IntSet nodes; 
	private int cost; 
	int numNodes;
	
	public BossaSolution(IntSet nodes, int cost) {
		this.setNodes(nodes);
		this.setCost(cost); 
		this.numNodes = nodes.size();
	}

	public BossaSolution() {
		setNodes(new DefaultIntSet());
	}

	public IntSet getNodes() {
		return nodes;
	}

	public void setNodes(IntSet nodes) {
		this.nodes = nodes;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

}
