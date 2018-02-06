package slp;

import java_cup.runtime.Symbol;

/** Adds line number and name information to scanner symbols.
 */
public class Token extends Symbol {
	private final String name;

	public Token(int line, String name, int id, Object value) {
		super(id, value);
		this.name = name;
		this.left = line;
	}
	
	public Token(int line, String name, int id) {
		super(id, null);
		this.name = name;
		this.left = line;
	}
	
	public String toString() {
		String val = value != null ? "(" + value + ")" : "";
		return name +  val;
	}
	
	public int getLine() {
		return this.left;
	}
	
	public String getString()
	{
		return name;
	}
}