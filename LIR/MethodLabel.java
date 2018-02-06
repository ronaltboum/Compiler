package LIR;

public class MethodLabel extends Opr {

	public final String methodName;
	public final String callingClass;
	
	public MethodLabel(String callingClass, String name) {
		this.callingClass = callingClass;
		this.methodName = name;  
	}
	
	public String toString()
	{
		return "_"+callingClass+"_"+methodName;
	}
}
