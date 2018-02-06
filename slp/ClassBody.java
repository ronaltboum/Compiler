package slp;

public class ClassBody extends ASTNode {

	public final Field field;
	public final Method method;
	
	
	public ClassBody( Field fl, int line_num )
	{
		super( line_num );
		field = fl;
		method = null;
	}
	
	public ClassBody( Method ml, int line_num )
	{
		super( line_num );
		field = null;
		method = ml;
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
