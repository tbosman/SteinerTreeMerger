package steinertreemerger.adapters;

import java.util.Iterator;
import java.util.Set;

import grph.Grph;
import grph.in_memory.InMemoryGrph;
import libtw.input.GraphInput.InputData;
import libtw.ngraph.*;


/**
 * 
 * @author tbosman
 * Class responsible for converting objects between different incompatible libraries
 * 
 *
 */
public class GraphObjectConverter {
	
	Grph TWLibToGrph(NGraph<InputData> graphIn) {
		Grph g = new InMemoryGrph();
		
		Iterator<NVertex<InputData>> vertices = graphIn.getVertices();
		while(vertices.hasNext()) {			
			g.addVertex(vertices.next().data.id);
		}
		
		
		Iterable<NEdge<InputData>> edges = graphIn.edges();
		for(NEdge<InputData> edge : edges) {
			g.addSimpleEdge(edge.a.data.id, 0, edge.b.data.id, false);
		}
				return null;
	}
}
