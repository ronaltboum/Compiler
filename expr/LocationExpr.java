package expr;

import SemanticAnalysis.SemanticType;
import slp.PropagatingVisitor;
import slp.Visitor;

public class LocationExpr extends Expr {
	public final String ID;
	public final Expr e1;
	public final Expr e2;
	public boolean isInstanceObject = false;
	//itay
	public boolean isField = false;
	
	public LocationExpr( String Id , int line_num) // A variable
	{
		super( line_num );
		this.ID = Id;
		e1= null;
		e2 = null;
	}
	public LocationExpr( Expr e ,String ID , int line_num) 
	{
		super( line_num );
		this.ID = ID;
		this.e1= e;
		this.e2 = null;
		//itay
		this.isField=true;
	}
	
	public LocationExpr( Expr e1 ,Expr e2 , int line_num) {
		super( line_num );
		this.ID = null;
		this.e1= e1;
		this.e2 = e2;
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