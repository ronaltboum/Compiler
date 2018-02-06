package expr;

import slp.PropagatingVisitor;
import slp.Visitor;

public class LengthOfArray extends Expr
{
	
public Expr arrayExpr;

public LengthOfArray( Expr arrayExpr, int line_num){
	super( line_num );
	this.arrayExpr = arrayExpr;

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
