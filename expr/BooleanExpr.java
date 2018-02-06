package expr;

import SemanticAnalysis.SemanticType;
import slp.PropagatingVisitor;
import slp.Visitor;

public class BooleanExpr extends Expr
{
	public final boolean value;
	
	public BooleanExpr( boolean value, int line_num) {
		super( line_num );
		this.value = value;
		this.type = new SemanticType("boolean");   // ron adds friday night!!!!!!!!!!!
	}
	
	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}
	
	public boolean getValue() {
		return value;
	}

	/** Accepts a propagating visitor parameterized by two types.
	 * 
	 * @param <DownType> The type of the object holding the context.
	 * @param <UpType> The type of the result object.
	 * @param visitor A propagating visitor.
	 * @param context An object holding context information.
	 * @return The result of visiting this node.
	 */
	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}
	
	public String toString() {
		return "" + value;
	}

}
