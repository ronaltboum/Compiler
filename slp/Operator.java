package slp;

/** An enumeration containing all the operation types in the SLP language.
 */
public enum Operator {
	MINUS, PLUS, MULT, DIV, MOD, LNEG, LT, GT, LTE, GTE, EQUAL, NEQUAL, LAND, LOR;
	
	/** Prints the operator in the same way it appears in the program.
	 */
	public String toString() {
		switch (this) {
		case MINUS: return "Sub";
		case PLUS: return "Add";
		case MULT: return "Mul";
		case DIV: return "Div";
		case LNEG: return "!";
		case MOD: return "Mod";
		case LT: return "<";
		case GT: return ">";
		case LTE: return "<=";
		case GTE: return ">=";
		case EQUAL: return "==";
		case NEQUAL: return "!=";
		case LAND: return "&&";
		case LOR: return "||";
		default: throw new RuntimeException("Unexpted value: " + this.name());
		}
	}
	
	public boolean isBoolOperator()
	{
		switch (this) {
		case MINUS: return false;
		case PLUS: return false;
		case MULT: return false;
		case DIV: return false;
		case LNEG: return false;
		case MOD: return false;
		case LT: return true;
		case GT: return true;
		case LTE: return true;
		case GTE: return true;
		case EQUAL: return true;
		case NEQUAL: return true;
		case LAND: return true;
		case LOR: return true;
		default: throw new RuntimeException("Unexpted value: " + this.name());
		}
	}
}