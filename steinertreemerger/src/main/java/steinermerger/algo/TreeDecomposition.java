/*
 * Copyright (C) 2013
 * 
 * Stefan Fafianie
 *
 * This package is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * This package makes use of a selection of classes from the libtw library 
 * which is also published under the GNU Lesser General Public license.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this package; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */

package steinermerger.algo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import order.OrderDP;
import steiner.*;
import steinermerger.adapters.TWLibWrapperGrph;
import toools.set.DefaultIntSet;
import toools.set.IntSet;
import libtw.algorithm.Constructive;
import libtw.algorithm.GreedyDegree;
import libtw.algorithm.PermutationToTreeDecomposition;
import libtw.algorithm.MaxTwGreedyDegree;
import libtw.input.GraphInput;
import libtw.input.InputException;
import libtw.input.GraphInput.InputData;
import libtw.ngraph.NGraph;
import libtw.ngraph.NTDBag;
import libtw.ngraph.NVertex;

/* Run a single experiment for a given .stp instance. Use the file path as input argument.
 * All 5 algorithms run on the same nice tree decomposition. 
 * Running time and number of generated partial solutions are measured */
public class TreeDecomposition {

	public boolean computeTreeWidthIsLEQ(TWLibWrapperGrph in, int maxTw) {
		NGraph<InputData> g;
		g = in.get();
		MaxTwGreedyDegree<InputData> twCalculator = new MaxTwGreedyDegree<InputData>(); 
		twCalculator.setInput(g);
		twCalculator.setMaxTw(maxTw);
		twCalculator.run();
		int tw = twCalculator.getUpperBound();
		if(tw > maxTw) {
			return false;
		}else {
			return true;
		}	
	}
	
	public int computeTreeWidth(TWLibWrapperGrph in ) {
		NGraph<InputData> g;
		g = in.get();

		Constructive<InputData> theAlgorithm = new PermutationToTreeDecomposition<InputData>( new GreedyDegree<InputData>() );
		theAlgorithm.setInput(g);
		theAlgorithm.run();
		NGraph<NTDBag<InputData>> td = theAlgorithm.getDecomposition();	

		int tw = 0;
		for (int i = 0; i < td.getNumberOfVertices(); i++){
			if (td.getVertex(i).data.vertices.size()>tw){
				tw = td.getVertex(i).data.vertices.size();
			}
		}
		return tw;
	}

	public IntSet[] computeTreeDecomposition(TWLibWrapperGrph in) {
		NGraph<InputData> g;
		g = in.get();

		Constructive<InputData> theAlgorithm = new PermutationToTreeDecomposition<InputData>( new GreedyDegree<InputData>() );
		theAlgorithm.setInput(g);
		theAlgorithm.run();
		NGraph<NTDBag<InputData>> td = theAlgorithm.getDecomposition();
		IntSet[] bags = new IntSet[td.getNumberOfVertices()];
		for (int i = 0; i < td.getNumberOfVertices(); i++){
			bags[i] = new DefaultIntSet();
			Iterator<NVertex<InputData>> bagIterator = td.getVertex(i).data.vertices.iterator();
			while(bagIterator.hasNext()) {
				bags[i].add(Integer.parseInt(bagIterator.next().data.name));
			}			

		}
		return bags;
	}


	public long computeTreeDP(TWLibWrapperGrph in) {
		NGraph<InputData> g;

		g = in.get();

		Constructive<InputData> theAlgorithm = new PermutationToTreeDecomposition<InputData>( new GreedyDegree<InputData>() );
		theAlgorithm.setInput(g);
		theAlgorithm.run();
		NGraph<NTDBag<InputData>> td = theAlgorithm.getDecomposition();	

		int tw = 0;
		for (int i = 0; i < td.getNumberOfVertices(); i++){
			if (td.getVertex(i).data.vertices.size()>tw){
				tw = td.getVertex(i).data.vertices.size();
			}
		}

		int v = g.getNumberOfVertices();
		int e = g.getNumberOfEdges();

		Set<NVertex<InputData>> terminals = in.getTerminals();
		int t = terminals.size();

		System.out.println("tw: " + (tw-1) + " |V|: " + v + " |E|: " + e + " |T|: " + t);

		NVertex<InputData> terminal = terminals.iterator().next();
		HashMap<Set<NVertex<InputData>>, Integer> weights = in.getWeights();

		TreeDecompositionToNiceTreeDecomposition<InputData> ntd = new TreeDecompositionToNiceTreeDecomposition<InputData>(terminal);
		ntd.setInput(td);
		ntd.run();		
		NGraph<NTDNiceBag<InputData>> niceDecomposition = ntd.getNiceDecomposition();

		RowDP<InputData> tddp = new RowDP<InputData>();

		long time = System.nanoTime();

		//tddp.input(g.getNumberOfVertices(), niceDecomposition, terminals, weights);
		tddp.setInput(niceDecomposition, terminals, weights);
		tddp.run();
		long SPLIT_time = System.nanoTime()-time;
		long SPLIT_size = tddp.operations;


		System.out.println("split - finished");
		System.out.println("time: " + SPLIT_time + " size: " + SPLIT_size + " solution: " + tddp.solution);
		return tddp.solution;
		//		
		//		RowDP<InputData> row = new RowDP<InputData>();
		//		row.setInput(niceDecomposition, terminals, weights);
		//		row.always = false;
		//		
		//		time = System.nanoTime();
		//		row.run();
		//		long ROWC_time = System.nanoTime()-time;
		//		long ROWC_size = row.operations;
		//		
		//		System.out.println("row conditional - finished");
		//		System.out.println("time: " + ROWC_time + " size: " + ROWC_size + " solution: " + row.solution);
		//				
		//		row = new RowDP<InputData>();
		//		row.setInput(niceDecomposition, terminals, weights);
		//		row.always = true;
		//		
		//		time = System.nanoTime();
		//		row.run();
		//		long ROWA_time = System.nanoTime()-time;
		//		long ROWA_size = row.operations;
		//		
		//		System.out.println("row always - finished");
		//		System.out.println("time: " + ROWA_time + " size: " + ROWA_size + " solution: " + row.solution); 
		//		
		//		RankBasedSteinerTreeDP<InputData> rbdp = new RankBasedSteinerTreeDP<InputData>();
		//		rbdp.setInput(niceDecomposition, terminals, weights);
		//		rbdp.setCondition(false);	
		//		
		//		time = System.nanoTime();
		//		rbdp.run();
		//		long RBC_time = System.nanoTime()-time;
		//		int RBC_size = rbdp.totalsize;
		//				
		//		System.out.println("rank based conditional - finished");
		//		System.out.println("time: " + RBC_time + " size: " + RBC_size + " solution: " + rbdp.getSolution());
		//		
		//		rbdp = new RankBasedSteinerTreeDP<InputData>();
		//		rbdp.setInput(niceDecomposition, terminals, weights);
		//		rbdp.setCondition(true);	
		//		
		//		time = System.nanoTime();
		//		rbdp.run();
		//		long RBA_time = System.nanoTime()-time;
		//		int RBA_size = rbdp.totalsize;
		//			
		//		System.out.println("rank based always - finished");
		//		System.out.println("time: " + RBA_time + " size: " + RBA_size + " solution: " + rbdp.getSolution());
		//		
		//		SteinerTreeDP<InputData> dp = new SteinerTreeDP<InputData>();
		//		dp.setInput(niceDecomposition, terminals, weights);
		//		
		//		time = System.nanoTime();
		//		dp.run();
		//		long FL_time = System.nanoTime()-time;
		//		int FL_size = dp.totalsize;
		//			
		//		System.out.println("folklore - finished");
		//		System.out.println("time: " + FL_time + " size: " + FL_size + " solution: " + dp.getSolution());		

	}
	public static void main(String[] args) throws IOException, InputException {

		GraphInput in;


	}

}
