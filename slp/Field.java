package slp;

import java.util.ArrayList;
import java.util.List;

public class Field extends ASTNode {
	public final Type 	 type;
	public IDList id_list;
	public Field(int line_num  )
	{
		super( line_num );
		type = null;
	}
	
	public Field( int t, String typeName, String ID, int line_num )
	{
		super( line_num );
		type = new Type( t , typeName, line_num );
		id_list = new IDList(line_num);
		id_list.idList.add( ID );
	}
	
	public Field( int t, String typeName, IDList list, int line_num )
	{
		super( line_num );
		type = new Type( t , typeName, line_num );
		id_list = new IDList(line_num);
		id_list.idList = new ArrayList<String>(list.idList);
	}
	
	public void addID( String ID ) {
		id_list.idList.add(ID);
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
