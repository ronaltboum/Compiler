package slp;

import SemanticAnalysis.SemanticError;
import expr.BinaryOpExpr;
import expr.BooleanExpr;
import expr.CallExpr;
import expr.Expr;
import expr.LengthOfArray;
import expr.LocationExpr;
import expr.NewArrayExpr;
import expr.NullExpr;
import expr.NumberExpr;
import expr.QuoteExpr;
import expr.ThisObject;
import expr.UnaryOpExpr;
import expr.VarExpr;
import expr.newObject;
import stmt.AssignStmt;
import stmt.BreakStmt;
import stmt.CallStmt;
import stmt.ContinueStmt;
import stmt.IfStmt;
import stmt.NestedStmtList;
import stmt.ReturnStmt;
import stmt.Stmt;
import stmt.StmtList;
import stmt.WhileStmt;

/** An interface for AST visitors.
 */
public interface Visitor {
	public Object visit(StmtList stmts);
	public Object visit(Stmt stmt);
	public Object visit(AssignStmt stmt);
	public Object visit(BreakStmt stmt);
	public Object visit(CallStmt stmt);
	public Object visit(ContinueStmt stmt);
	public Object visit(IfStmt stmt);
	public Object visit(NestedStmtList stmt);
	public Object visit(ReturnStmt stmt);
	public Object visit(WhileStmt stmt);
	
	public Object visit(Program program);
	public Object visit(ClassDecl classDecl);
	public Object visit(FormalList formalList);
	public Object visit(Formal formal);
	public Object visit(Type type);
	public Object visit(Field field);
	public Object visit(Method method);
	public Object visit(ClassBodyList classBodyList);
	public Object visit(IDList idList);
	public Object visit(FuncArgsList funcArgsList);
	public Object visit(ClassBody classBody);
	public Object visit(Expr expr);
	public Object visit(BinaryOpExpr expr);
	public Object visit(BooleanExpr expr);
	public Object visit(CallExpr expr);
	public Object visit(LengthOfArray expr);
	public Object visit(LocationExpr expr);
	public Object visit(NewArrayExpr expr);
	public Object visit(newObject expr);
	public Object visit(NullExpr expr);
	public Object visit(NumberExpr expr);
	public Object visit(QuoteExpr expr);
	public Object visit(ThisObject expr);
	public Object visit(VarExpr expr);
	public Object visit(UnaryOpExpr expr);
	
	
}