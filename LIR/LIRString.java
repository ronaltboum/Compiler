package LIR;

public class LIRString { // string literals at started - allocated at heap.
public final String name;
	
	private static int numberOfVars = 1;
	public int currentRegNumber;
	public LIRString(String name) {
		this.name = name;
		currentRegNumber = numberOfVars;
		++numberOfVars;
		
	}
	
	/** Returns the total number of variables created so far.
	 */
	public static int getNumberOfVars() {
		return numberOfVars;
	}	

	public String toString() {
		return "str"  +   currentRegNumber + ": " + "\"" + name  + "\"";
	}
	
}
