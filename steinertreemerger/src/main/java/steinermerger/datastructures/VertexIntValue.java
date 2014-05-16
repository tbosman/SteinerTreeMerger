package steinermerger.datastructures;

/**
 * stores integer value for an immutable key, compares based on value
 * id is immutable
 * @author tbosman
 *
 */
public class VertexIntValue implements Comparable<VertexIntValue> {

	public final int id; 
	private int value; 
	public VertexIntValue(int id, int value) {
		this.id = id; 
		this.setValue(value);
	}
	
	
	
	public int compareTo(VertexIntValue v) {
		return this.value - v.value;
		
	}



	public int getValue() {
		return value;
	}



	public void setValue(int value) {
		this.value = value;
	}
	
	public String toString() {
		return "V"+id+":"+value;
	}
	

}
