package stmt;

import expr.Expr;
import slp.PropagatingVisitor;
import slp.Visitor;

public class WhileStmt extends Stmt {
	
	public Expr expr;
	public final Stmt stmtBlock;

	public WhileStmt(Expr expr,Stmt stmtBlock, int line_num) {
		super( line_num );
		this.expr = expr;
		this.stmtBlock = stmtBlock;
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
}
