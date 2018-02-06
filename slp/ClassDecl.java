package slp;

import SemanticAnalysis.SymbolTableTreeBuilder;
import expr.Expr;

public class ClassDecl extends Expr{
	public String className;
	public String extendedClassName;
	public ClassBodyList bodyList;
	
	public ClassDecl(String name, String exname ,ClassBodyList cbl, int line_num) {
		super( line_num );
		className = name;
		bodyList = cbl;
		extendedClassName = exname;
	}
	public ClassDecl(String name, String exname , int line_num) {
		super( line_num );
		className = name;
		bodyList = new ClassBodyList(line_num);
		extendedClassName = exname;
	}
	/*public boolean subTypeOf(String className){
		ClassDecl superClass = SymbolTableTreeBuilder.classTable.get(extendedClassName);
		if (superClass.classname.equals(className)){
			return true;
		}
		return false;
	}*/
	public String getClassName() {
		return className;
	}


	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
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
