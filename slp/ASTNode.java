package slp;

import SemanticAnalysis.SymbolTable;

/** The base class of all AST nodes in this package.
 */
public abstract class ASTNode {
	
	public int CreationLine;
	private   SymbolTable scopeTable;
	
	public ASTNode(int line_num) {
		CreationLine = line_num;
	}

	/** Accepts a visitor object as part of the visitor pattern.
	 * @param visitor A visitor.
	 */
	public abstract Object accept(Visitor visitor);
	
	/** Accepts a propagating visitor parameterized by two types.
	 * 
	 * @param <DownType> The type of the object holding the context.
	 * @param <UpType> The type of the result object.
	 * @param visitor A propagating visitor.
	 * @param context An object holding context information.
	 * @return The result of visiting this node.
	 */
	public abstract <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context);

	public SymbolTable getScopeTable() {
		return scopeTable;
	}

	public void setScopeTable(SymbolTable scopeTable) {
		this.scopeTable = scopeTable;
	}
}