package slp;

import java.util.ArrayList;
import java.util.List;

public class Formal extends ASTNode {
	public final Type 	 type;
	public final String 	 id; 
	
	public Formal( Type t, String ID, int line_num )
	{
		super( line_num );
		type = t;
		id = ID;
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
	public boolean equals (Object obj){
		if(obj instanceof Formal){
			return  ((Formal)obj).type.typeName.equals(this.type.typeName);
		}
		return false;
	}

}
