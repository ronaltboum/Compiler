package slp;

import java.util.ArrayList;
import java.util.List;

public class FormalList extends ASTNode {

	public final List<Formal> formals = new ArrayList<Formal>();
	
	public FormalList(Formal formal, int line_num) {
		super( line_num );
		formals.add(formal);
	}

	public FormalList(int line_num) {
		super( line_num );
		// TODO Auto-generated constructor stub
	}

	public void addFormal(Formal formal) {
		formals.add(formal);
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);

	}

	public boolean equals(FormalList comp){
		if(comp instanceof FormalList){
			FormalList compFormailList = (FormalList) comp;
			if(compFormailList.formals.size()!=formals.size())
				return false;
			for (int i = 0 ; i < formals.size(); i++){
				if(!compFormailList.formals.get(i).equals(formals.get(i)))
					return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}

}
