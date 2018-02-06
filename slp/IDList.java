package slp;

import java.util.ArrayList;
import java.util.List;

public class IDList extends ASTNode {
	
	public List<String> idList = new ArrayList<String>();
	
	public IDList( String id, int line_num ) {
		super( line_num );
		idList.add(id);
	}

	public IDList(int line_num) {
		super( line_num );
		// TODO Auto-generated constructor stub
	}

	public void add( String id ) {
		idList.add(id);
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
