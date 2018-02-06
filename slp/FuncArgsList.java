package slp;

import java.util.ArrayList;
import java.util.List;

import expr.Expr;

public class FuncArgsList extends ASTNode {

	public final List<Expr> expressions = new ArrayList<Expr>();
	public FuncArgsList(int line_num) {
		super( line_num );
		
	}
	
	public FuncArgsList(Expr expr, int line_num) {
		super( line_num );
		expressions.add(expr);
	}

	/** Adds a statement to the tail of the list.
	 * 
	 * @param stmt A program statement.
	 */
	public void addExpr(Expr expr) {
		expressions.add(expr);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}

}
