package slp;

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

/** An interface for a propagating AST visitor.
 * The visitor passes down objects of type <code>DownType</code>
 * and propagates up objects of type <code>UpType</code>.
 */
public interface PropagatingVisitor<DownType,UpType> {

	UpType visit(Program program, DownType context);
	UpType visit(ClassDecl classDecl, DownType context);
	UpType visit(ClassBodyList classBodyList, DownType context);
	UpType visit(ClassBody classBody, DownType context);
	UpType visit(Field field, DownType context);
	UpType visit(Method method, DownType context);
	UpType visit(FormalList formalList, DownType context);
	UpType visit(Formal formal, DownType context);
	UpType visit(StmtList stmtList, DownType context);
	UpType visit(Stmt stmt, DownType context);
	UpType visit(WhileStmt whileStmt, DownType context);
	UpType visit(AssignStmt assignStmt, DownType context);
	UpType visit(BreakStmt breakStmt, DownType context);
	UpType visit(CallStmt callStmt, DownType context);
	UpType visit(ContinueStmt continueStmt, DownType context);
	UpType visit(IfStmt ifStmt, DownType context);
	UpType visit(ReturnStmt returnStmt, DownType context);
	UpType visit(NestedStmtList nestedStmtList, DownType context);
	UpType visit(Expr expr, DownType context);
	UpType visit(BooleanExpr booleanExpr, DownType context);
	UpType visit(BinaryOpExpr binaryOpExpr, DownType context);
	UpType visit(CallExpr callExpr, DownType context);
	UpType visit(LengthOfArray lengthOfArray, DownType context);
	UpType visit(LocationExpr locationExpr, DownType context);
	UpType visit(NewArrayExpr newArrayExpr, DownType context);
	UpType visit(newObject newObject, DownType context);
	UpType visit(NullExpr nullExpr, DownType context);
	UpType visit(NumberExpr numberExpr, DownType context);
	UpType visit(QuoteExpr quoteExpr, DownType context);
	UpType visit(ThisObject thisObject, DownType context);
	UpType visit(UnaryOpExpr unaryOpExpr, DownType context);
	UpType visit(VarExpr varExpr, DownType context);
	UpType visit(IDList idList, DownType context);
	UpType visit(FuncArgsList funcArgsList, DownType context);

	

	

}