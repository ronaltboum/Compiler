package LIR;

public class Variable extends Opr 
{
	public String name;
	public final String value;
	
	public Variable(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String toString() {
		return name+"="+value;
	}
}
