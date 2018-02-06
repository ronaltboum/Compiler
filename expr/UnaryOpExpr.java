package expr;

import SemanticAnalysis.SemanticType;
import slp.Operator;
import slp.PropagatingVisitor;
import slp.Type;
import slp.Visitor;

/** An AST node for unary expressions.
 */
public class UnaryOpExpr extends Expr {
	public final Operator op;
	public final Expr operand;
	
	public UnaryOpExpr( Expr operand, Operator op , int line_num) {
		super( line_num );
		this.operand = operand;
		this.op = op;
		
		//ron adds friday night:  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if(this.operand.type != null){
			String typeId = this.operand.type.getId();
			if(typeId != null){
				if(typeId.equals("int"))
					this.type = new SemanticType("int");
				else if(typeId.equals("boolean"))
					this.type = new SemanticType("boolean");
			}
		}
		
		//System.out.println("in Unary const and this.operand.type.getId() = "+ this.operand.type.getId());
		
	}
	
	
	/*public VarExpr(String name,Type type , int line_num) {
		super( line_num );

		this.name = name;
		this.typeSlp = type;
		if (type != null){
			this.type = new SemanticType(type.typeName);
		}

	}*/

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
		return op + operand.toString();
	}
}