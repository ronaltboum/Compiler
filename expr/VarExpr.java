package expr;

import SemanticAnalysis.SemanticType;
import slp.PropagatingVisitor;
import slp.Type;
import slp.Visitor;

/** An AST node for program variables.
 */
public class VarExpr extends Expr {
	public final String name;
	public final Type typeSlp;
	
	public VarExpr(String name , int line_num) {
		super( line_num );

		this.name = name;
		this.typeSlp = null;
	}
	public VarExpr(String name,Type type , int line_num) {
		super( line_num );

		this.name = name;
		this.typeSlp = type;
		if (type != null){
			this.type = new SemanticType(type.typeName);
		}

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
		return name;
	}	
}