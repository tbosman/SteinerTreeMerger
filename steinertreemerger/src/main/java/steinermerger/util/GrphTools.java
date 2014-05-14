package steinermerger.util;

import grph.Grph;
import grph.properties.NumericalProperty;
import grph.properties.Property;

public class GrphTools {


	

	/**
	 * copies all properties to other graph
	 * @param a graph from
	 * @param b graph to
	 */
	public static void copyProperties(Grph a, Grph b) {
		//copy vertex shape and color properties
		for(Property p : a.getProperties()) {
			Property p2 = b.findPropertyByName(p.getName()) ;
			
			if(p2 != null) {
				p.cloneValuesTo(p2);
			}
			
			for(int id = 0; id<=Math.max(b.getVertices().getGreatest(), b.getEdges().getGreatest());id++) {
				if(p.isSetted(id)) {
					p2.setStatus(id, true);
				}
			}
		}
	}
	
	/**
	 * Copies vertex properties van graph a to graph b
	 * @param v vertex id
	 * @param a graph from
	 * @param b graph to 
	 */
	public static void copyVertexProperties(int v, Grph a, Grph b) {
		b.getVertexColorProperty().setValue(v, a.getVertexColorProperty().getValue(v));
		b.getVertexShapeProperty().setValue(v, a.getVertexShapeProperty().getValue(v));
		b.getVertexLabelProperty().setValue(v, a.getVertexLabelProperty().getValueAsString(v));
		b.getVertexSizeProperty().setValue(v, a.getVertexSizeProperty().getValue(v));
		
	}
	/**
	 * Copies edge properties van graph a to graph b
	 * @param e edge id 
	 * @param a graph from
	 * @param b graph to 
	 */
	public static void copyEdgeProperties(int e, Grph a, Grph b) {
		b.getEdgeColorProperty().setValue(e, a.getEdgeColorProperty().getValue(e));
		b.getEdgeStyleProperty().setValue(e, a.getEdgeStyleProperty().getValue(e));
		b.getEdgeLabelProperty().setValue(e, a.getEdgeLabelProperty().getValueAsString(e));
		b.getEdgeWidthProperty().setValue(e, a.getEdgeWidthProperty().getValue(e));
		
		
	}
	
}
