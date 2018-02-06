package expr;

import SemanticAnalysis.SemanticType;
import slp.PropagatingVisitor;
import slp.Type;
import slp.Visitor;

//represents new class object node in AST
public class newObject extends New {
	// legal if its a subtype of class
	public final String className;
	
	public newObject( String className , int line_num){
		super( line_num );
		this.className = className;
		if (className != null){
			this.type = new SemanticType(className,true);
			
		}
	
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

	public String getClassName() {
		return className;
	}

	
}
