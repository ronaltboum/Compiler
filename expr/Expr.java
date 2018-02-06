package expr;

import SemanticAnalysis.SemanticType;
import slp.ASTNode;
import slp.PropagatingVisitor;
import slp.Visitor;

/** A base class for AST nodes for expressions.
 */
public abstract class Expr extends ASTNode {
	
	public SemanticType type;
	public boolean isLeft = false;//or is it not?
	public String varAssignment; // when we assign var to array we need the name of the var

	public SemanticType getType() {
		return type;
	}

	public void setType(SemanticType type) {
		this.type = type;
	}

	public Expr(int line_num) {
		super(line_num);
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