package slp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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

/** Pretty-prints an SLP AST.
 */
public class PrettyPrinter implements Visitor, sym {
	protected final 		ASTNode root;
	private   int   		treeDepth = 0;
	private   PrintWriter 	writer;
	
	private void  Tab( int depth )
	{
		for( int i = 0 ; i< depth ; i++ )
		{
			System.out.print(" ");
			writer.print(" ");
		}
	}
	
	private void Printer( String s )
	{
		System.out.println( s );
		writer.println( s );
		
	}
	/** Constructs a printin visitor from an AST.
	 * 
	 * @param root The root of the AST.
	 */
	public PrettyPrinter(ASTNode root) {
		this.root = root;
	}

	/** Prints the AST with the given root.
	 */
	public void print() 
	{
		
		try {
			writer = new PrintWriter("AST.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		root.accept(this);


		writer.close();

	}
	
	public Object visit(StmtList stmts) {
		Tab( treeDepth );
		//Printer("["+stmts.CreationLine+"]"+"Block of Statements");
		treeDepth++;
		for( int i = stmts.statements.size() - 1 ; i >=0 ;i-- ) 
		{
			
			stmts.statements.get(i).accept(this);
			
		}
		treeDepth--;
		
		return null;
	}

	public Object visit(Stmt stmt) 
	{
		Stmt s = stmt;
		
		if( s instanceof AssignStmt )
		{
			AssignStmt aStmt = (AssignStmt)s;
			aStmt.accept(this);
		}
		else if( s instanceof BreakStmt )
		{
			BreakStmt st = (BreakStmt)s;
			st.accept(this);
		}
		else if( s instanceof CallStmt )
		{
			CallStmt st = (CallStmt)s;
			st.accept(this);
		}
		else if( s instanceof ContinueStmt )
		{
			ContinueStmt st = (ContinueStmt)s;
			st.accept(this);
		}
		else if( s instanceof IfStmt )
		{
			IfStmt st = (IfStmt)s;
			st.accept(this);
		}
		else if( s instanceof ReturnStmt )
		{
			ReturnStmt st = (ReturnStmt)s;
			st.accept(this);
		}
		else if( s instanceof WhileStmt )
		{
			WhileStmt st = (WhileStmt)s;
			st.accept(this);
		}
		else if( s instanceof NestedStmtList )
		{
			NestedStmtList st = (NestedStmtList)s;
			st.accept(this);
		}
		
		return null;
	}
	
	public Object visit(NestedStmtList stmts) {
		Tab( treeDepth );
		//Printer("["+stmts.CreationLine+"]"+"Block of statements");
		treeDepth++;
		for( int i = stmts.stmtList.statements.size() - 1 ; i >=0 ;i-- )
		{
			
			stmts.stmtList.statements.get(i).accept(this);
			
		}
		treeDepth--;
		
		return null;
	}
	
	public Object visit(WhileStmt stmt) 
	{
		Tab( treeDepth );
		Printer("["+stmt.CreationLine+"]"+"While Statement");
		
		treeDepth++;
		stmt.expr.accept(this);
		treeDepth--;
		
		treeDepth++;
		stmt.stmtBlock.accept(this);
		treeDepth--;
		
		return null;
		
	}
	
	public Object visit(BreakStmt stmt) 
	{
		Tab( treeDepth );
		Printer("["+stmt.CreationLine+"]"+"Break Statement");
		
		return null;
	}
	
	public Object visit(CallStmt stmt) 
	{
		Tab( treeDepth );
		Printer("["+stmt.CreationLine+"]"+"Method Call Statement");
		
		treeDepth++;
		Tab( treeDepth );
		if( stmt.isStatic )
			Printer("["+stmt.CreationLine+"]"+"Call to static method: "+stmt.funcName+", in class: "+stmt.classID );
		else 
		{
			if( stmt.e == null )
				Printer("["+stmt.CreationLine+"]"+"Call to virtual method: "+stmt.funcName);
			else Printer("["+stmt.CreationLine+"]"+"Call to virtual method: "+stmt.funcName+", in external scope");
		}
		treeDepth--;
		
		if( stmt.e != null )
		{
			treeDepth++;
			stmt.e.accept(this);
			treeDepth--;
		}
			
		if( stmt.args_list != null)
		{
			treeDepth++;
			stmt.args_list.accept(this);
			treeDepth--;
		}
		
		return null;
	}
	
	public Object visit(ContinueStmt stmt) 
	{
		Tab( treeDepth );
		Printer("["+stmt.CreationLine+"]"+"Continue Statement");
		
		return null;
	}
	
	public Object visit(IfStmt stmt) 
	{
		Tab( treeDepth );
		Printer("["+stmt.CreationLine+"]"+"If Statement");
		
		treeDepth++;
		stmt.expr.accept(this);
		treeDepth--;
		
		treeDepth++;
		if( stmt.s1 != null )
			stmt.s1.accept(this);
		treeDepth--;
		
		treeDepth++;
		
		if( stmt.s2 != null ){
			Tab( treeDepth );
			Printer("["+stmt.CreationLine+"]"+"Else Statement");
			stmt.s2.accept(this);
		}
			
		treeDepth--;
		
		return null;
	}
	
	public Object visit(ReturnStmt stmt) 
	{
		Tab( treeDepth );
		Printer("["+stmt.CreationLine+"]"+"Return Statement");
		
		treeDepth++;
		if(stmt.expr != null)
			stmt.expr.accept(this);
		treeDepth--;
		
		return null;
		
	}
	
	public Object visit(AssignStmt stmt) {
		
		if( (stmt.varExpr != null) && (stmt.rhs == null) && (stmt.locationExpr == null) )
		{ //for example:  int n;
			Tab( treeDepth );
			Printer("["+stmt.CreationLine+"]"+"Declaration of local variable: "+stmt.varExpr.name);
			treeDepth++;
			stmt.varExpr.typeSlp.accept(this);
			treeDepth--;
		}
				
		else if( (stmt.varExpr != null) && (stmt.rhs != null) && (stmt.locationExpr == null)  )
		{ //for example:  int n = 9;
			Tab( treeDepth );
			Printer("["+stmt.CreationLine+"]"+"Declaration of local variable: "+stmt.varExpr.name+", with initial value" );
			++treeDepth;
			stmt.varExpr.typeSlp.accept(this);
			stmt.rhs.accept(this);
			treeDepth--;
		}
		
		
		else if( (stmt.varExpr == null) && (stmt.rhs != null) && (stmt.locationExpr != null)  )
		{  //for example: n= 9
			Tab( treeDepth );
			Printer("["+stmt.CreationLine+"]"+"Assignment Statement: " );
			treeDepth++;
			stmt.locationExpr.accept(this);
			stmt.rhs.accept(this);
			treeDepth--;
		}
		
		return null;
	}
	
	public Object visit(Expr expr) 
	{
		if( expr instanceof LocationExpr )
		{
			LocationExpr ex = (LocationExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof BinaryOpExpr )
		{
			BinaryOpExpr ex = (BinaryOpExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof BooleanExpr )
		{
			BooleanExpr ex = (BooleanExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof CallExpr )
		{
			CallExpr ex = (CallExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof LengthOfArray )
		{
			LengthOfArray ex = (LengthOfArray)expr;
			ex.accept(this);
		}
		else if( expr instanceof NewArrayExpr )
		{
			NewArrayExpr ex = (NewArrayExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof newObject )
		{
			newObject ex = (newObject)expr;
			ex.accept(this);
		}
		else if( expr instanceof NullExpr )
		{
			NullExpr ex = (NullExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof NumberExpr )
		{
			NumberExpr ex = (NumberExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof QuoteExpr )
		{
			QuoteExpr ex = (QuoteExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof ThisObject )
		{
			ThisObject ex = (ThisObject)expr;
			ex.accept(this);
		}
		else if( expr instanceof UnaryOpExpr )
		{
			UnaryOpExpr ex = (UnaryOpExpr)expr;
			ex.accept(this);
		}
		else if( expr instanceof VarExpr )
		{
			VarExpr ex = (VarExpr)expr;
			ex.accept(this);
		}
		
		return null;
	}	
		
	@Override
	public Object visit(BinaryOpExpr expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Binary operation: "+ expr.op.toString() );
		
		treeDepth++;
		expr.lhs.accept(this);
		treeDepth--;
		
		treeDepth++;
		expr.rhs.accept(this);
		treeDepth--;
		
		return null;
	}
	
	@Override
	public Object visit(LocationExpr expr) 
	{	
		Tab( treeDepth );
		
		if( expr.ID != null && expr.e1 == null && expr.e2 == null ) // A reference to a variable in current scope
		{
			Printer("["+expr.CreationLine+"]"+"Reference to variable: "+expr.ID);
		}
		else if( expr.ID != null && expr.e1 != null && expr.e2 == null ) // A reference to a variable in external scope
		{
			Printer("["+expr.CreationLine+"]"+"Reference to variable: "+expr.ID+", in external scope");
			treeDepth++;
			expr.e1.accept(this);
			treeDepth--;
		}
		else if( expr.ID == null && expr.e1 != null && expr.e2 != null ) // A reference to an array
		{
			Printer("["+expr.CreationLine+"]"+"Reference to an array");
			treeDepth++;
			expr.e1.accept(this);
			treeDepth--;
			
			treeDepth++;
			expr.e2.accept(this);
			treeDepth--;
		}
		
		return null;
			
	}
	
	@Override
	public Object visit(VarExpr expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Declaration of variable: "+expr.name);
		
		treeDepth++;
		expr.typeSlp.accept(this);
		treeDepth--;
		
		return null;
	}
	
	@Override
	public Object visit(NumberExpr expr)
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Integer literal: "+expr.value);
		
		return null;
	}
	
	@Override
	public Object visit(UnaryOpExpr expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Unary expression: "+expr.op.toString());
		
		treeDepth++;
		expr.operand.accept(this);
		treeDepth--;
		
		return null;
	}
	
	@Override
	public Object visit(BooleanExpr expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Boolean literal: "+expr.value);
		
		return null;
		
	}
	@Override
	public Object visit(CallExpr expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Method Call Statement");
		
		treeDepth++;
		Tab( treeDepth );
		if( expr.isStatic )
			Printer("["+expr.CreationLine+"]"+"Call to static method: "+expr.funcName+", in class: "+expr.classID );
		else 
		{
			if( expr.e == null )
				Printer("["+expr.CreationLine+"]"+"Call to virtual method: "+expr.funcName);
			else Printer("["+expr.CreationLine+"]"+"Call to virtual method: "+expr.funcName+", in external scope");
		}
		treeDepth--;
		
		if( expr.e != null )
		{
			treeDepth++;
			expr.e.accept(this);
			treeDepth--;
		}
			
		if( expr.args_list != null)
		{
			treeDepth++;
			expr.args_list.accept(this);
			treeDepth--;
		}
			
		return null;	
	}
	@Override
	public Object visit(LengthOfArray expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Length of array expression");
		
		return null;
		
	}
	@Override
	public Object visit(NewArrayExpr expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Array allocation");
		
		treeDepth++;
		expr.arrayType.accept(this);
		treeDepth--;
		
		treeDepth++;
		expr.arrayLength.accept(this);
		treeDepth--;
		
		return null;
		
	}
	@Override
	public Object visit(newObject expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Instantiation of class: "+expr.className);
		
		return null;
	}
	@Override
	public Object visit(NullExpr expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"Null literal");
		
		return null;
	}
	@Override
	public Object visit(QuoteExpr expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"String literal:"+"\""+expr.quote+"\"");
		return null;
	}
	@Override
	public Object visit(ThisObject expr) 
	{
		Tab( treeDepth );
		Printer("["+expr.CreationLine+"]"+"'this' object reference");
		return null;
	}

	@Override
	public Object visit(Program program) {
		for( ClassDecl s : program.class_declarations )
		{
			s.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(ClassDecl classDecl) {
		
		if( classDecl.extendedClassName != null )
			Printer("["+classDecl.CreationLine+"]"+"Declaration of class: "+classDecl.className+" extends "+classDecl.extendedClassName);
		else Printer("["+classDecl.CreationLine+"]"+"Declaration of class: "+classDecl.className);
		
		treeDepth++;
		classDecl.bodyList.accept(this);
		treeDepth--;
		
		return null;
	}

	@Override
	public Object visit(FormalList formalList) {
		for( int i = formalList.formals.size() - 1 ; i >=0 ;i-- )
		{	
			Formal f = formalList.formals.get(i);
			treeDepth++;
			f.accept(this);
			treeDepth--;
		}
		
		return null;
		
	}

	@Override
	public Object visit(Formal formal) {
		Tab( treeDepth );
		
		Printer("["+formal.CreationLine+"]"+"Parameter: "+formal.id);
		
		treeDepth++;
		formal.type.accept(this);
		treeDepth--;
		
		return null;
	}

	@Override
	public Object visit(Type type) {
		Tab( treeDepth );
		if(type.typeName.equals("int") || type.typeName.equals("string") 
				|| type.typeName.equals("boolean"))
			Printer("["+type.CreationLine+"]"+"Primitive data type: "+type.typeName);
		else
			Printer("["+type.CreationLine+"]"+"Data type: "+type.typeName);
		
		return null;
	}

	@Override
	public Object visit(Field field) {
		Tab( treeDepth );
		Printer("["+field.CreationLine+"]"+"Declaration of field: "+field.id_list.idList);
		
		treeDepth++;
		field.type.accept(this);
		treeDepth--;
		
		return null;
		
	}

	@Override
	public Object visit(Method method) {
		Tab( treeDepth );
		if( !method.isStatic )
			Printer("["+method.CreationLine+"]"+"Declaration of virtual method: "+method.funcName );
		else Printer("["+method.CreationLine+"]"+"Declaration of static method: "+method.funcName );
		
		treeDepth++;
		if( method.funcType != null )
			method.funcType.accept(this);
		treeDepth--;
		
		treeDepth++;
		if( method.formalList != null )
			method.formalList.accept(this);
		treeDepth--;
		
		treeDepth++;
		if( method.stmtList != null )
			method.stmtList.accept(this);
		treeDepth--;
		
		return null;
	}

	@Override
	public Object visit(ClassBodyList classBodyList) {
		for( int i = classBodyList.clList.size() - 1 ; i >=0 ;i-- )
		{
			ClassBody bd = classBodyList.clList.get(i);
			
			bd.accept(this);
			
		}
		
		return null;
	}

	@Override
	public Object visit(IDList idList) {
		Tab( treeDepth );
		for( int i = idList.idList.size() - 1 ; i >=0 ;i-- )
		{
			Printer("["+idList.CreationLine+"]"+"Declaration of variable: "+idList.idList.get(i));
		}
		
		return null;
	}

	@Override
	public Object visit(FuncArgsList funcArgsList) {
		treeDepth++;
		for( int i = funcArgsList.expressions.size() - 1 ; i >=0 ;i-- )
		{
			
			funcArgsList.expressions.get(i).accept(this);
			
		}	
		treeDepth--;
		
		return null;
	}

	@Override
	public Object visit(ClassBody classBody) {
		if( classBody.field != null )
		{
			treeDepth++;
			classBody.field.accept(this);
			treeDepth--;
		}
			
		if( classBody.method != null )
		{
			treeDepth++;
			classBody.method.accept(this);
			treeDepth--;
		}
			
		return null;
	}


}