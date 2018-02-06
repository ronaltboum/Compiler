package stmt;

import expr.Expr;
import expr.LocationExpr;
import expr.VarExpr;
import slp.PropagatingVisitor;
import slp.Visitor;

/**
 * An AST node for assignment statements.
 */
public class AssignStmt extends Stmt {
	public final VarExpr varExpr;
	public final LocationExpr locationExpr;
	public final Expr rhs;
	
	public AssignStmt(VarExpr varExpr, int line_num) {
		super( line_num );
		this.varExpr = varExpr;
		this.rhs = null;
		this.locationExpr = null;
	}

	public AssignStmt(VarExpr varExpr, Expr rhs, int line_num) {
		super( line_num );
		this.varExpr = varExpr;
		this.rhs = rhs;
		this.locationExpr = null;
	}
	public AssignStmt(LocationExpr locationExpr, Expr rhs, int line_num) {
		super( line_num );
		this.varExpr = null;
		this.rhs = rhs;
		this.locationExpr = locationExpr;
	}

	/**
	 * Accepts a visitor object as part of the visitor pattern.
	 * 
	 * @param visitor
	 *            A visitor.
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