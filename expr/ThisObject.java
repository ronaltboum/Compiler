package expr;

import slp.PropagatingVisitor;
import slp.Visitor;

public class ThisObject extends Expr {

	public ThisObject(int line_num ) {
		super(line_num);
		// TODO Auto-generated constructor stub
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
