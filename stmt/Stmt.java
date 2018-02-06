package stmt;

import slp.ASTNode;
import slp.PropagatingVisitor;
import slp.Visitor;

/** The super class of all AST node for program statements.
 */
public abstract class Stmt extends ASTNode {
	boolean hasReturnStmt = false;
	
	public Stmt(int line_num) {
		super(line_num);
		// TODO Auto-generated constructor stub
	}

	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
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