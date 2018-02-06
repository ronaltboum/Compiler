package expr;

import SemanticAnalysis.SemanticType;
import slp.PropagatingVisitor;
import slp.Visitor;

/** An expression denoting a constant integer.
 */
public class NumberExpr extends Expr {
	/** The constant represented by this expression.
	 * 
	 */
	public final int value;
	
	public NumberExpr( int value , int line_num) {
		super( line_num );
		this.value = value;
		this.type = new SemanticType("int");
	}
	
	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
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