package slp;

import java.util.ArrayList;
import java.util.List;

public class ClassBodyList extends ASTNode {
	
	public final List<ClassBody> clList = new ArrayList<ClassBody>();
	
	
	
	public ClassBodyList(ClassBody cl, int line_num) {
		super( line_num );
		clList.add(cl);
	}

	public ClassBodyList(int line_num) {
		super( line_num );
		// TODO Auto-generated constructor stub
	}

	public void addClassBody(ClassBody cl) {
		clList.add(cl);
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
