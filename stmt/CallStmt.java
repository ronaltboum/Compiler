package stmt;

import expr.Expr;
import slp.FuncArgsList;
import slp.PropagatingVisitor;
import slp.Visitor;

public class CallStmt extends Stmt {
	
	public final String funcName;
	public final FuncArgsList args_list;
	public final Expr e;
	public final Boolean isStatic;
	public final String  classID;
	
	
	public CallStmt( Expr e, String n , FuncArgsList args, boolean is_static, String class_id, int line_num ) {
		super( line_num );
		this.funcName = n;
		this.args_list= args;
		this.e = e;
		isStatic = is_static;
		classID = class_id;
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