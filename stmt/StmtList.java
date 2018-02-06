package stmt;

import java.util.List;

import slp.ASTNode;
import slp.PropagatingVisitor;
import slp.Visitor;

import java.util.ArrayList;

/** An AST node for a list of statements.
 */
public class StmtList extends ASTNode {
	public final List<Stmt> statements = new ArrayList<Stmt>();
	public StmtList(int line_num) {
		super( line_num );
	}
	
	public StmtList(Stmt stmt, int line_num) {
		super( line_num );
		statements.add(stmt);
	}

	/** Adds a statement to the tail of the list.
	 * 
	 * @param stmt A program statement.
	 */
	public void addStmt(Stmt stmt) {
		statements.add(stmt);
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