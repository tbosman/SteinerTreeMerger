package steinermerger.test;

public class TestResult {

	public int maxTw; 
	public int sphIterations; 
	
	public int sphSolution; 
	public int dpSolution; 

	public int sphTime;
	public int dpTime;
	
	public int treesUsed; 
	public int optimalSolution; 
	
	public TestResult() {
		// TODO Auto-generated constructor stub
	}
	
	public String toString() {
		return "Sols: "+sphIterations+", Min SPH: "+sphSolution+", DP: "+dpSolution+", Opt: "+optimalSolution+", Time SPH: "+sphTime+", DP: "+dpTime+", maxTW: "+maxTw+", trees used: "+treesUsed;
	}
	

}
