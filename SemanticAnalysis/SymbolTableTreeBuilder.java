package SemanticAnalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
import slp.ASTNode;
import slp.ClassBody;
import slp.ClassBodyList;
import slp.ClassDecl;
import slp.Field;
import slp.Formal;
import slp.FormalList;
import slp.FuncArgsList;
import slp.IDList;
import slp.Method;
import slp.Program;
import slp.PropagatingVisitor;
import slp.Type;
import slp.sym;
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

public class SymbolTableTreeBuilder implements PropagatingVisitor<SymbolTable, SymbolTable>, sym 
{
	private	 final 			ASTNode root;
	
	public SymbolTableTreeBuilder(ASTNode root) {
		this.root = root;
		
	}

	public  static boolean  AInhertFromB(String classNameA, String classNameB){// 
		
		if (classNameA.equals("null") || classNameA.equals(classNameB)){
			return true; 
		}
		ClassDecl ClassA = Utilities.classTable.get(classNameA);
		
		if(ClassA == null){  // ron added saturday !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
			return false;   // because int, string, boolean don't have extendedClassName field
		}
		
		String ClassCName = ClassA.extendedClassName;
		ClassDecl Cclass = Utilities.classTable.get(ClassCName);
		if (Cclass == null){
			return false; // A doesnt inhert from anyone
		}
		else{
		return AInhertFromB(ClassCName,classNameB);
		}
	}
		/** Builds a symbol table for every scope
	 * 
	 */
	public void buildTables() 
	{
		root.accept(this,null);
	}
	
	@Override
	public SymbolTable visit(Program program, SymbolTable context) 
	{
		// Create the program scope, will contain symbols of classes
		SymbolTable programScope = new SymbolTable("ProgramScope");
		program.setScopeTable( programScope );
			
		for( ClassDecl s : program.class_declarations )
		{
			// Insert every class declaration in the static list for ease of access to class definitions
			Utilities.classTable.put(s.getClassName(),s);
			s.accept(this, programScope);
		}	
		
		// Add the Library class
		ClassBodyList LibraryCBL = new ClassBodyList(0);
		
		// println
		FormalList mFormalList = new FormalList(0);
		mFormalList.addFormal( new Formal(new Type(sym.STRING,"string",0), "s", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(null, "println", mFormalList, true, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// print
		mFormalList.addFormal( new Formal(new Type(sym.STRING,"string",0), "s", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(null, "print", mFormalList, true, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// printi
		mFormalList.addFormal( new Formal(new Type(sym.INT,"int",0), "i", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(null, "printi", mFormalList, true, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// printb
		mFormalList.addFormal( new Formal(new Type(sym.BOOLEAN,"boolean",0), "b", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(null, "printb", mFormalList, true, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// readi
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(sym.INT,"int",0), "readi", null, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// readln
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(sym.STRING,"string",0), "readln", null, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// eof
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(sym.BOOLEAN,"boolean",0), "eof", null, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// stoi
		mFormalList.addFormal( new Formal(new Type(sym.STRING,"string",0), "s", 0));
		mFormalList.addFormal( new Formal(new Type(sym.INT,"int",0), "n", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(sym.INT,"int",0), "stoi", mFormalList, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// itos
		mFormalList.addFormal( new Formal(new Type(sym.INT,"int",0), "i", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(sym.STRING,"string",0), "itos", mFormalList, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// stoa
		mFormalList.addFormal( new Formal(new Type(sym.STRING,"string",0), "s", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(200,"1 dimensional array of int",0), "stoa", mFormalList, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// atos
		mFormalList.addFormal( new Formal(new Type(200,"array",0), "a", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(sym.STRING,"string",0), "atos", mFormalList, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// random
		mFormalList.addFormal( new Formal(new Type(sym.INT,"int",0), "i", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(sym.INT,"int",0), "random", mFormalList, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// time
		LibraryCBL.addClassBody(new ClassBody( new Method(new Type(sym.STRING,"string",0), "time", null, false, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		// exit
		mFormalList.addFormal( new Formal(new Type(sym.INT,"int",0), "i", 0));
		LibraryCBL.addClassBody(new ClassBody( new Method(null, "exit", mFormalList, true, true, null, 0 ), 0));
		mFormalList = new FormalList(0);
		
		ClassDecl Library = new ClassDecl("Library", null, LibraryCBL, 0);
		Utilities.classTable.put("Library", Library);
		Library.accept(this,programScope);
		return null;
	}
	
	@Override
	public SymbolTable visit(ClassDecl classDecl, SymbolTable context) 
	{
		// Set class declaration to be in program scope - context = programTable
		classDecl.setScopeTable(context); 
		
		
		Symbol classSymbol = new Symbol(classDecl.className, "class", Kind.CLASS);
		if( !classSymbol.getSymbolName().equals("Library") )
		{
			
			if( classDecl.getScopeTable().lookupSymbol(classSymbol))
			{
				try {
					throw new SemanticError( "Class " + classDecl.getClassName() + " is already defined", classDecl.CreationLine );
				} catch (SemanticError e) {
					System.out.println(e.getErrMessage());
					System.exit(-1);
				}
				
			} 
			
			if ( classDecl.getClassName().equals(classDecl.extendedClassName)){
				try {
					throw new SemanticError( "Class " + classDecl.getClassName() + " can not extends itself", classDecl.CreationLine );
				} catch (SemanticError e) {
					System.out.println(e.getErrMessage());
					System.exit(-1);

				}
			}
			
			if (!Utilities.classTable.containsKey(classDecl.extendedClassName) && classDecl.extendedClassName != null){
				try {
					throw new SemanticError( "Class " + classDecl.extendedClassName + " does not exist", classDecl.CreationLine );
				} catch (SemanticError e) {
					System.out.println(e.getErrMessage());
					System.exit(-1);

				}
			}
			
		}
		// Add the class to the program scope symbol table
		context.addSymbol(classSymbol);
		
		// Create the class scope and propagate it forward
		SymbolTable classScope = new SymbolTable("Class_"+classDecl.className+"_Scope", context);
		classScope.isClassTable = true;
		classDecl.bodyList.accept(this, classScope);
		
		// If this class derives from a base class, we need to copy all the symbols from the base class
		if( classDecl.extendedClassName != null )
		{
			SymbolTable.copyBaseTableToDerivedTable( SymbolTable.findClassSymbolTable( classDecl.extendedClassName ), classScope,classDecl.extendedClassName );
		}
		
		return null;
	}
	
	@Override
	public SymbolTable visit(ClassBodyList classBodyList, SymbolTable context) 
	{
		// Set the scope of this node to the class scope
		// Set the bodylist of the class to class scope
		classBodyList.setScopeTable(context);
		
		for( int i = classBodyList.clList.size() - 1 ; i >=0 ;i-- )
		{
			ClassBody bd = classBodyList.clList.get(i);
			bd.accept(this,context);
		}	
		
		
		
		return null;
	}
	
	@Override
	public SymbolTable visit(ClassBody classBody, SymbolTable context) 
	{
		classBody.setScopeTable(context);
		if( classBody.field != null )
		{
			classBody.field.accept(this,context);
		}
			
		if( classBody.method != null )
		{
			classBody.method.accept(this,context);
		}
		return null;
	}

	@Override
	public SymbolTable visit(Field field, SymbolTable context) 
	{
		field.setScopeTable(context);
		// Add an entry in the symbol table for every id in this fields id list
		for( int i = field.id_list.idList.size() - 1 ; i >=0 ;i-- )
		{
			Symbol fieldSymbol =  new Symbol( field.id_list.idList.get(i), field.type.typeName, Kind.FIELD );
			
			// See if a field like this have been defined before
			if( field.getScopeTable().lookupSymbol(fieldSymbol) )
			{
				try {
					throw new SemanticError("field "+field.id_list.idList.get(i)+" is already defined",field.CreationLine);
				} catch (SemanticError e) {
					System.out.println(e.getErrMessage());
				}
			}
			
			context.addSymbol(fieldSymbol);
		}	
		return null;
	}
	
	@Override
	public SymbolTable visit(Method method, SymbolTable context) 
	{
		Symbol methodSymbol = null;
		String typeId = "void";
		if (!method.isVoid){
			typeId = method.funcType.typeName;
		}
		if( !method.isStatic )
			methodSymbol = new Symbol( method.funcName, typeId, Kind.VMETHOD );
		else methodSymbol = new Symbol( method.funcName, typeId, Kind.SMETHOD );
		
		// Set the class scope symbol tree to be this method symbol tree
		method.setScopeTable( context );
		
		// See if a function like this have been defined before
		if( method.getScopeTable().lookupSymbol(methodSymbol) )
		{
			try {
				throw new SemanticError("method "+method.funcName+" is already defined",method.CreationLine);
			} catch (SemanticError e) {
				System.out.println(e.getErrMessage());
			}
		}
			
		// Add the method symbol to the symbol table
		context.addSymbol( methodSymbol );
		
		// Create the method scope
		String scopeID = (method.funcType == null ) ? "VoidMethod_"+method.funcName+"_Scope" : "RetMethod_"+method.funcName+"_Scope";
		SymbolTable methodScope = new SymbolTable( scopeID, context );
		methodScope.isMethodTable = true;
		
		if( method.formalList != null )
		{
			if( method.formalList.formals.isEmpty() )
				method.formalList = null;
			else method.formalList.accept(this,methodScope);
		}

		if( method.stmtList != null )
			method.stmtList.accept(this,methodScope);
		return null;
	}
	
	@Override
	public SymbolTable visit(FormalList formalList, SymbolTable context) 
	{
		formalList.setScopeTable( context );
		for( int i = formalList.formals.size() - 1 ; i >=0 ;i-- )
		{	
			Formal f = formalList.formals.get(i);
			f.accept(this, context);
		}
		return null;
	}
	
	@Override
	public SymbolTable visit(Formal formal, SymbolTable context) 
	{
		// Add the formal to the method scope
		context.addSymbol( new Symbol(formal.id, formal.type.typeName, Kind.FORMAL ));
		return null;
	}

	@Override
	public SymbolTable visit(StmtList stmtList, SymbolTable context) 
	{
		stmtList.setScopeTable( context );
		for( int i = stmtList.statements.size() - 1 ; i >=0 ;i-- ) 
		{	
			stmtList.statements.get(i).accept(this,context);
		}
		return null;
	}

	@Override
	public SymbolTable visit(Stmt stmt, SymbolTable context) 
	{
		Stmt s = stmt;
		
		// Create a statement scope for statements that create scope
		// Also add any relevant definitions to the method scope symbol table
		if( s instanceof AssignStmt )
		{
			AssignStmt aStmt = (AssignStmt)s;
			// An assignment statement has a variable definition
			aStmt.accept(this, context);
		}
		else if( s instanceof BreakStmt )
		{
			BreakStmt st = (BreakStmt)s;
			st.accept(this,context);
		}
		else if( s instanceof CallStmt )
		{
			CallStmt st = (CallStmt)s;
			st.accept(this,context);
		}
		else if( s instanceof ContinueStmt )
		{
			ContinueStmt st = (ContinueStmt)s;
			st.accept(this,context);
		}
		else if( s instanceof IfStmt )
		{
			IfStmt st = (IfStmt)s;
			st.accept(this,context);
		}
		else if( s instanceof ReturnStmt )
		{
			ReturnStmt st = (ReturnStmt)s;
			st.accept(this,context);
		}
		else if( s instanceof WhileStmt )
		{
			WhileStmt st = (WhileStmt)s;
			st.accept(this,context);
		}
		else if( s instanceof NestedStmtList )
		{
			NestedStmtList st = (NestedStmtList)s;
			st.accept(this,context);
		}
		return null;
	}
	
	@Override
	public SymbolTable visit(WhileStmt whileStmt, SymbolTable context) 
	{
		// Context = a method scope table
		whileStmt.setScopeTable(context);
		
		// A while statement opens a scope, so open a symbol table for it
		SymbolTable whileStmtTable = new SymbolTable("WhileStmt", context );
		
		// The expression inside the while is still in method scope
		whileStmt.expr.accept(this,context);
		whileStmt.stmtBlock.accept(this,whileStmtTable);
		
		return null;
	}


	@Override
	public SymbolTable visit(AssignStmt assignStmt, SymbolTable context) 
	{
		assignStmt.setScopeTable( context );
		if( (assignStmt.varExpr != null) && (assignStmt.rhs == null) && (assignStmt.locationExpr == null) )
		{ //for example:  int n;
			
			Symbol varDecl = new Symbol( assignStmt.varExpr.name, assignStmt.varExpr.getType().getId(), Kind.VARDECL );
			// See if a variable like this have been defined before
			if( assignStmt.getScopeTable().Table.containsKey( varDecl.getSymbolName() ) )
			{
				try {
					throw new SemanticError("local variable "+assignStmt.varExpr.name+" is already defined",assignStmt.CreationLine);
				} catch (SemanticError e) {
					System.out.println(e.getErrMessage());
					System.exit(-1);
				}
			}
			
			context.addSymbol( varDecl );
			
			assignStmt.varExpr.accept(this,context);
		}
				
		else if( (assignStmt.varExpr != null) && (assignStmt.rhs != null) && (assignStmt.locationExpr == null)  )
		{ //for example:  int n = 9;
			Symbol varDecl = new Symbol( assignStmt.varExpr.name, assignStmt.varExpr.getType().getId(), Kind.VARDECL );
			// See if a variable like this have been defined before
			if( assignStmt.getScopeTable().Table.containsKey( varDecl.getSymbolName() )  )
			{
				try {
					throw new SemanticError("local variable "+assignStmt.varExpr.name+" is already defined",assignStmt.CreationLine);
				} catch (SemanticError e) {
					System.out.println(e.getErrMessage());
					System.exit(-1);
				}
			}
			
			context.addSymbol( varDecl );
			assignStmt.rhs.accept(this, context);
			assignStmt.varExpr.accept(this,context);
		}
		
		
		else if( (assignStmt.varExpr == null) && (assignStmt.rhs != null) && (assignStmt.locationExpr != null)  )
		{  //for example: n= 9
			assignStmt.rhs.accept(this, context);
			assignStmt.locationExpr.accept(this, context);
		}
		return null;
	}

	@Override
	public SymbolTable visit(BreakStmt breakStmt, SymbolTable context) {
		breakStmt.setScopeTable( context );
		return null;
	}

	@Override
	public SymbolTable visit(CallStmt callStmt, SymbolTable context) 
	{
		callStmt.setScopeTable(context);
		

		if( callStmt.e != null )
		{
			callStmt.e.accept(this, context);
		}
			
		if( callStmt.args_list != null)
		{
			callStmt.args_list.accept(this, context);
		}
		
		
		return null;
	}

	@Override
	public SymbolTable visit(ContinueStmt continueStmt, SymbolTable context) 
	{
		continueStmt.setScopeTable( context );
		return null;
	}

	@Override
	public SymbolTable visit(IfStmt ifStmt, SymbolTable context)
	{
		// Context is still method scope 
		ifStmt.setScopeTable( context );
		
		// The expression inside 
		ifStmt.expr.accept(this,context);
		if( ifStmt.s1 != null )
		{
			// An if statement creates a scope
			SymbolTable ifStmtTable = new SymbolTable("IfStmt", context );
			ifStmt.s1.accept(this, ifStmtTable );
		}
		
		// Else 
		if( ifStmt.s2 != null )
		{
			// An else statement creates a scope
			SymbolTable elseStmtTable = new SymbolTable("ElseStmt", context );
			ifStmt.s2.accept(this, elseStmtTable );
		}
			
		return null;
	}
	
	@Override
	public SymbolTable visit(ReturnStmt returnStmt, SymbolTable context) {
		returnStmt.setScopeTable( context );
		if (returnStmt.expr != null){
			returnStmt.expr.accept(this,context);

		}
		return null;
	}

	@Override
	public SymbolTable visit(NestedStmtList nestedStmtList, SymbolTable context) 
	{
		nestedStmtList.setScopeTable( context );
		for( int i = nestedStmtList.stmtList.statements.size() - 1 ; i >=0 ;i-- )
		{
			
			nestedStmtList.stmtList.statements.get(i).accept(this,context);
			
		}
		return null;
	}


	@Override
	public SymbolTable visit(Expr expr, SymbolTable context) 
	{
		// For expressions we just need to set to which scope they belong to
		if( expr instanceof LocationExpr )
		{
			LocationExpr ex = (LocationExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof BinaryOpExpr )
		{
			BinaryOpExpr ex = (BinaryOpExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof BooleanExpr )
		{
			BooleanExpr ex = (BooleanExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof CallExpr )
		{
			CallExpr ex = (CallExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof LengthOfArray )
		{
			LengthOfArray ex = (LengthOfArray)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof NewArrayExpr )
		{
			NewArrayExpr ex = (NewArrayExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof newObject )
		{
			newObject ex = (newObject)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof NullExpr )
		{
			NullExpr ex = (NullExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof NumberExpr )
		{
			NumberExpr ex = (NumberExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof QuoteExpr )
		{
			QuoteExpr ex = (QuoteExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof ThisObject )
		{
			ThisObject ex = (ThisObject)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof UnaryOpExpr )
		{
			UnaryOpExpr ex = (UnaryOpExpr)expr;
			ex.accept(this,context);
		}
		else if( expr instanceof VarExpr )
		{
			VarExpr ex = (VarExpr)expr;
			ex.accept(this,context);
		}
		
		return null;
	}
	
	@Override
	public SymbolTable visit(BinaryOpExpr binaryOpExpr, SymbolTable context) {
		binaryOpExpr.setScopeTable(context);
		binaryOpExpr.lhs.accept(this,context);
		binaryOpExpr.rhs.accept(this,context);
		return null;
	}

	@Override
	public SymbolTable visit(BooleanExpr booleanExpr, SymbolTable context) {
		booleanExpr.setScopeTable(context);
		return null;
	}

	@Override
	public SymbolTable visit(CallExpr callExpr, SymbolTable context) {
		callExpr.setScopeTable(context);
		
		if( callExpr.e != null )
		{
			callExpr.e.accept(this, context);
		}
			
		if( callExpr.args_list != null)
		{
			callExpr.args_list.accept(this, context);
		}
		
		return null;
		
	}

	@Override
	public SymbolTable visit(LengthOfArray lengthOfArray, SymbolTable context) {
		lengthOfArray.setScopeTable(context);
		lengthOfArray.arrayExpr.accept(this,context);
		return null;
	}

	@Override
	public SymbolTable visit(LocationExpr locationExpr, SymbolTable context) {
		locationExpr.setScopeTable(context);
		
		if( locationExpr.ID != null && locationExpr.e1 == null && locationExpr.e2 == null ) // A reference to a variable in current scope
		{
			
		}
		else if( locationExpr.ID != null && locationExpr.e1 != null && locationExpr.e2 == null ) // A reference to a variable in external scope
		{
			locationExpr.e1.accept(this,context);
		}
		else if( locationExpr.ID == null && locationExpr.e1 != null && locationExpr.e2 != null ) // A reference to an array
		{
			locationExpr.e1.accept(this,context);
			locationExpr.e2.accept(this,context);
		}
		return null;
	}

	@Override
	public SymbolTable visit(NewArrayExpr newArrayExpr, SymbolTable context) {
		newArrayExpr.setScopeTable(context);
		newArrayExpr.arrayLength.accept(this,context);
		newArrayExpr.arrayType.accept(this,context);
		return null;
	}

	@Override
	public SymbolTable visit(newObject newObject, SymbolTable context) {
		newObject.setScopeTable(context);
	
		return null;
	}

	@Override
	public SymbolTable visit(NullExpr nullExpr, SymbolTable context) {
		nullExpr.setScopeTable(context);
		return null;
	}

	@Override
	public SymbolTable visit(NumberExpr numberExpr, SymbolTable context) {
		numberExpr.setScopeTable(context);
		return null;
	}

	@Override
	public SymbolTable visit(QuoteExpr quoteExpr, SymbolTable context) {
		quoteExpr.setScopeTable(context);
		return null;
	}

	@Override
	public SymbolTable visit(ThisObject thisObject, SymbolTable context) {
		thisObject.setScopeTable(context);
		return null;
	}

	@Override
	public SymbolTable visit(UnaryOpExpr unaryOpExpr, SymbolTable context) {
		unaryOpExpr.setScopeTable(context);
		unaryOpExpr.operand.accept(this,context);
		return null;
	}

	@Override
	public SymbolTable visit(VarExpr varExpr, SymbolTable context) {
		varExpr.setScopeTable(context); //here
		return null;
	}


	@Override
	public SymbolTable visit(FuncArgsList funcArgsList, SymbolTable context) {
		funcArgsList.setScopeTable(context);
		for( int i = funcArgsList.expressions.size() - 1 ; i >=0 ;i-- )
		{
			funcArgsList.expressions.get(i).accept(this,context);
		}	
		return null;
	}

	@Override
	public SymbolTable visit(IDList idList, SymbolTable context) {
		idList.setScopeTable(context);
		return null;
	}


}
