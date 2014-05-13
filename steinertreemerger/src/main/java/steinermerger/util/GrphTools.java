package steinermerger.util;

import grph.Grph;
import grph.properties.NumericalProperty;
import grph.properties.Property;

public class GrphTools {



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
	
}
