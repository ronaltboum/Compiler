package slp;

import java.util.List;

import SemanticAnalysis.SymbolTable;

import java.util.ArrayList;

public class Program extends ASTNode {
	public final List<ClassDecl> class_declarations = new ArrayList<ClassDecl>();
	
	public List<ClassDecl> getClass_declarations() {
		return class_declarations;
	}

	public Program(ClassDecl decl, int line_num) {
		super( line_num );
		class_declarations.add(decl);
	}

	public void addDecl(ClassDecl decl) {
		class_declarations.add(decl);
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
