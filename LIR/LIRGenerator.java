package LIR;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ldap.Rdn;

import SemanticAnalysis.Kind;
import SemanticAnalysis.SemanticType;
import SemanticAnalysis.Symbol;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.Utilities;
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
import slp.Operator;
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

public class LIRGenerator implements PropagatingVisitor<Register, Object>, sym {

	private	 final 			ASTNode root;
	private  final			LIRProgram lirprog;
	public String NEWLINE = "\n";

	
	
	public LIRGenerator(ASTNode root) throws FileNotFoundException {
		this.root = root;
		lirprog = new LIRProgram();
		Utilities.setUpPrinter("RESULT.LIR" );
	}
	
	public void generateLIR() throws IOException 
	{
		
		root.accept(this,null);
		Utilities.closePrinter(null);


		Utilities.PrintToStart(lirprog.ToStringDVMap());
		Utilities.PrintToStart(lirprog.ToStringLIRStringMap());

		Utilities.printResult();
		
		
	}
	
	@Override
	public Object visit(Program program, Register context) {
		
		for( ClassDecl s : program.class_declarations )
		{

			s.accept(this, context);
			
		}	
		return null;
	}

	@Override
	public Object visit(ClassDecl classDecl, Register context) {
		classDecl.bodyList.accept(this, null);
		return null;
	}

	@Override
	public Object visit(ClassBodyList classBodyList, Register context) {
		
		List<MethodLabel> methodLabelList = new ArrayList<MethodLabel>();
		
		// Generate a dispatch vector for every class, we do that by scanning the class symbol table for virtual methods
		for( Symbol sy : classBodyList.getScopeTable().Table.values() )
		{
			// If the method is virtual, we can insert it into the dispatch vector for this class
			if( sy.getSymbolKind() == Kind.VMETHOD )
			{
				String callingClass =  classBodyList.getScopeTable().determineClassNameFromID();
				String methodName  = sy.getSymbolName();
				if (sy.inherted){
					callingClass = sy.fatherClassName;
				}
				MethodLabel methodLabel =  new MethodLabel(callingClass, methodName ) ;
				methodLabelList.add(methodLabel);
			}
		}
		
		// Create a new dispatch vector and put the method label list in it
		lirprog.dv_Map.put("_DV_"+classBodyList.getScopeTable().determineClassNameFromID(), 
							new DispatchVector( "_DV_"+classBodyList.getScopeTable().determineClassNameFromID(), methodLabelList) );
		
		for( int i = classBodyList.clList.size() - 1 ; i >=0 ;i-- )
		{
			ClassBody bd = classBodyList.clList.get(i);
			bd.accept(this,context);
		}	

		return null;

	}

	@Override
	public Object visit(ClassBody classBody, Register context) 
	{
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
	public Object visit(Method method, Register context) 
	{
		
		String methodEnclosingClass =  method.getScopeTable().determineClassNameFromID();
		
		// A method creates a new scope, so push a dummy register to identify the end of this scope
		Register dummy = new Register("dummy");
		lirprog.registerStack.push( dummy );
		// The main function has a special label: _ic_main
		if( Utilities.isMain( method ) )
		{
			
			Utilities.Printer("############ "+methodEnclosingClass+"."+method.funcName+" ############");
			Utilities.Printer("_ic_main:");
			Utilities.Printer("#####################################\n");
		}
		else
		{
			// CAN ALSO JUST GET METHOD LABELS FROM lirprog.dv_Map
			Utilities.Printer("############ "+methodEnclosingClass+"."+method.funcName+" ############");
			MethodLabel ml = new MethodLabel( methodEnclosingClass , method.funcName );
			Utilities.Printer( ml+":");
			Utilities.Printer("#####################################\n");
		}
		
		if( method.formalList != null )
		{
			if( method.formalList.formals.isEmpty() )
				method.formalList = null;
			else method.formalList.accept(this,context);
		}

		if( method.stmtList != null )
			method.stmtList.accept(this,context);
		
		if(  Utilities.isMain( method ) )
			Utilities.Printer("Library __exit(0),Rdummy");
		else if( method.isVoid )
			Utilities.Printer("Return 0");
		Utilities.Printer("#####################################\n");
		
		lirprog.popScope();
		return null;
	}
	
	@Override
	public Object visit(Field field, Register context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FormalList formalList, Register context) {
		for (int i = 0; i < formalList.formals.size();i++)
		{
			formalList.formals.get(i).accept(this,context);
		}
		return null;
	}

	@Override
	public Object visit(Formal formal, Register context) {
		// Set a register for every formal
		return null;
	}

	@Override
	public Object visit(StmtList stmtList, Register context) {
		for(int i = stmtList.statements.size() - 1 ; i >= 0 ;i--){
			stmtList.statements.get(i).accept(this,context);
		}
		return null;
	}

	@Override
	public Object visit(Stmt stmt, Register context) 
	{
		Stmt s = stmt;
		
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
	public Object visit(WhileStmt whileStmt, Register context) 
	{
		// Push a dummy register to the register stack, this is to identify this new scope
		Register dumm = new Register("dummy");
		lirprog.registerStack.push( dumm );
		
		StmtLabel nl = new StmtLabel("while");
		lirprog.whileLabelStack.push( nl );
		Utilities.Printer( nl.toString()+":"  );
		
		Register boolExprResult = new Register();
		lirprog.registerStack.push( boolExprResult );

		boolExprResult = (Register)whileStmt.expr.accept(this , boolExprResult );
		
		// Depending on the result of the boolExprResult,we either jump to the end label, or keep executing 
		// instructions
		Instruction booleanRes = new Instruction("Compare","0" , boolExprResult.name); //  boolExprResult - 0 
		Instruction booleanJump = new Instruction("JumpFalse",nl.toStringEnd(),null );
		
		Utilities.Printer( booleanRes );
		Utilities.Printer( booleanJump );
		
		
		whileStmt.stmtBlock.accept( this, context );
		
		// If we reached the end of the loop, we go back up
		Instruction jumpToWhileTop = new Instruction("Jump",nl.toString(),null );
		Utilities.Printer( jumpToWhileTop );
		
		// This is the end of the loop when the condition is false
		Utilities.Printer( nl.toStringEnd()+":" );
		
		// While statements create new scopes, so in the end of the scope, remove all assigned registers
		lirprog.popScope();
		lirprog.popLatestWhileLabel();
		return null;
	}


	@Override
	public Object visit(AssignStmt assignStmt, Register context) 
	{
		
	
		// This defines a local variable, we need to store a local variable in a register
		if( (assignStmt.varExpr != null) && (assignStmt.rhs == null) && (assignStmt.locationExpr == null) )
		{
			assignStmt.varExpr.accept(this,context);
			
		}
		// This defines and assigns a local variable  int[] a = new int[];
		else if( (assignStmt.varExpr != null) && (assignStmt.rhs != null) && (assignStmt.locationExpr == null)  )
		{ 
			// Create a register to hold the result of the rhs expression
			Register rhs = new Register();
			//itay 27/12 we need to "matk" the register for next steps (locationExpr)
			rhs.heldValue= new Variable(assignStmt.toString(),null);
			lirprog.registerStack.push( rhs );
			
			// Determine the value of the rhs
			// BUG HERE
			rhs = (Register) assignStmt.rhs.accept(this,rhs);
			//itay
			//if we do assignment to newArray (varExpr!=null && isArray) we need to "mark" the register
			boolean isNewArray = assignStmt.varExpr!=null && assignStmt.varExpr.typeSlp.typeName.contains("array");
			if( assignStmt.rhs instanceof newObject || assignStmt.rhs instanceof NewArrayExpr || isNewArray  )	
			
			{
				// If this assignment creates an object instance, then the allocation was handled
				// in the newObject expression part. Here we assign a variable to the register so we 
				// can identify the register by the variable later
				
				rhs.heldValue = new Variable( assignStmt.varExpr.name, null );
			}
			else
			{
				// rhs can be a reference to an array, if it is , we need to do MoveArray
				// But we also need to have the varExpr be a register because we cannot MoveArray to memory
				// Therefore the annoying way is to move
				// Assign the register to the variable
				// if rhs is an array, we need to move the location variable to a temp register
				/*
				if( rhs.isArrayInstance )
				{
					Register locationTemp = new Register();
					Instruction locationToTemp = new Instruction("Move",assignStmt.varExpr.name, locationTemp.name );
					Utilities.Printer( locationToTemp );
					
					Instruction assignTemp = new Instruction("MoveArray",rhs.name, locationTemp.name );
					Utilities.Printer( assignTemp );
					
					rhs = locationTemp;
				}
				*/
				Instruction instruction = new Instruction("Move", rhs.toString(), assignStmt.varExpr.name);
				Utilities.Printer(instruction);
			}

			
		}
		// This assigns an already defined variable, can either be a class field or a local variable
		// n = 9 /  arr[2] = 3; //arr[2] = arr[3];
		else if( (assignStmt.varExpr == null) && (assignStmt.rhs != null) && (assignStmt.locationExpr != null)  )
		{  
	
			
			boolean isArray = (assignStmt.locationExpr.ID == null 
					&& assignStmt.locationExpr.e1 != null 
					&& assignStmt.locationExpr.e2 != null);
			// Create a register to hold the result of the rhs expression
			Register rhs = new Register();
			//itay 27/12 we need to "matk" the register for next steps (locationExpr)
			rhs.heldValue= new Variable(assignStmt.toString(),null);
			lirprog.registerStack.push( rhs );
			
			// Determine the value of the rhs
			rhs = (Register)assignStmt.rhs.accept(this,rhs);
			
			if( assignStmt.rhs instanceof newObject || assignStmt.rhs instanceof NewArrayExpr || isArray )
			{
				// If this assignment creates an object instance, then the allocation was handled
				// in the newObject expression part. Here we assign a variable to the register so we 
				// can identify the register by the variable later
				String id = assignStmt.locationExpr.ID;
				if (Utilities.accessToArray(assignStmt.locationExpr)){ 
					LocationExpr locationExprE1 = (LocationExpr) assignStmt.locationExpr.e1;
					id = locationExprE1.ID;
				}
				rhs.heldValue = new Variable( assignStmt.locationExpr.ID, null );
			}
			
			else
			{
				// if you read this, the varaiable has been declared
				// Check if this location expression is just a non field local variable, 
				// if it is , no need to create any registers 
				boolean isVar = (assignStmt.locationExpr.ID != null 
						&& assignStmt.locationExpr.e1 == null 
						&& assignStmt.locationExpr.e2 == null);
			
				
				boolean isLocal = false;
				if( isVar || isArray )
				{
					Symbol s = null;
					if( isVar )
						s = assignStmt.locationExpr.getScopeTable().getSymbol( assignStmt.locationExpr.ID );
					else if( isArray )
					{
						LocationExpr e1 = (LocationExpr) assignStmt.locationExpr.e1;
						//itay
						s = assignStmt.locationExpr.getScopeTable().getSymbol( Utilities.getArrayID(e1 ));
						//itay
					}
						
					
					if( s.getSymbolKind() != Kind.FIELD )
					{
						isLocal = true;
					}
				}
				
				String moveTask = "Move";
				String moveTo   = assignStmt.locationExpr.ID;
						
				//itay
				Register location=null;///////////////////////////////////////////////////////
				if(isArray || assignStmt.locationExpr.isField || !isLocal){
					location = new Register();
					lirprog.registerStack.push( location );
					assignStmt.locationExpr.isLeft = true;
					assignStmt.locationExpr.varAssignment=rhs.name;
					location = (Register) assignStmt.locationExpr.accept(this, location);		
				}
				//itay
				
				
				// If the location expression is a field, we need to treat the assignment differently
				if( !isLocal )
				{
					
					// If we assign onto a field, we need to use moveField
					// Also, if we assign to a field, or an array, we need the offset which is saved in the register
					// If we assign to a local variable, we can simply use the location ID
					//MoveField R3.2,R4
					//moveTo   =  rhs.name;
					//itay
					moveTo   = location.name;
					moveTask = "MoveField"; 
					
					// The field might be an array, if that is the case we must first move it to a temporary register
					if( rhs.isArrayInstance  || location.isArrayInstance )
					{
						
						moveTask = "MoveArray"; //
					}
				}
				else
				{
					// If its a local, check if its an object instance
					// if it is, the left hand side is also an object instance
					// which we now need to override to point towards the rhs
					if( rhs.isClassInstance && !isLocal)
					{
						// Register that holds a pointer to the object instance on the left
						Register reg = lirprog.findVariableRegister(assignStmt.locationExpr.ID);
						
						// We need that every time this variable shows up in the code, it will reference
						// the object on the right, so just change names
						//reg.heldValue.name = rhs.heldValue.name;
						
						// 
						moveTo = reg.name;
					}
					// This might be a locally created array
					else if( rhs.isArrayInstance )
					{
						// if rhs is an array, we need to move the location variable to a temp register
						Register locationTemp = new Register();
						Instruction locationToTemp = new Instruction("Move",assignStmt.locationExpr.ID, locationTemp.name );
						Utilities.Printer( locationToTemp );
						
						Instruction assignTemp = new Instruction("MoveArray",rhs.name, locationTemp.name );
						Utilities.Printer( assignTemp );
						
						rhs = locationTemp;
						
						moveTask = "Move";
					}
				}
				
				// Assign the register to the variable
				Instruction inst = new Instruction(moveTask, rhs.name, moveTo );
			
				Utilities.Printer(inst);
			}

		}

		
		return null;
	}
	
	@Override
	public Object visit(BreakStmt breakStmt, Register context) 
	{
		// We simple jump to the end of the enclosing while
		Instruction breakInstr = new Instruction("Jump",lirprog.peekEnclosingWhile().toStringEnd(), null );
		Utilities.Printer( breakInstr );
		return null;
	}

	@Override
	public Object visit(CallStmt callStmt, Register context) 
	{
		if( callStmt.classID != null )
		{
			// This is a static call
			Instruction callInstr = Utilities.generateStaticCallInstruction(lirprog, 
					callStmt.funcName, 
					callStmt.args_list, 
					this, 
					callStmt.classID);
			Utilities.Printer("#static call");
			Utilities.Printer(callInstr);
		}
		else if( callStmt.e != null )
		{
			// This is a virtual call, called from an object given in the expression
			SemanticType t = callStmt.e.getType();
			if( t != null )
			{
				if( t.isClassInstance )
				{
					// Find the register containing a reference to the expression which is a class instance
					// The expression can either be a location expression or a call expression
					// We make sure a call expression and a location expression return a register
					// containing an object instance of this class
					Register exprReg = new Register();
					lirprog.registerStack.push( exprReg );
					Register objectInstance = (Register)callStmt.e.accept( this, exprReg );
					
					Instruction callInstr = Utilities.generateVirtualCallInstruction(lirprog, 
																				callStmt.funcName, 
																				callStmt.args_list, 
																				this, 
																				t.getId(), 
																				objectInstance);
					Utilities.Printer( callInstr );
				}
			}
		}
		else
		{
			// This function is called using a 'this' object
			
			// First find the enclosing class name, so we can know where to look for the function
			String enclosingClassName = SymbolTable.findEnclosingClass( callStmt.getScopeTable() );
			
			
			// Create a register for the this pointer
			Register thisReg = new Register();
			lirprog.registerStack.push( thisReg );
			thisReg.isClassInstance = true;
			thisReg.generateVariableList( enclosingClassName );
			
			//
			Instruction inst = new Instruction("Move","this",thisReg.name);
			Utilities.Printer(inst);
			
			Instruction callInstr = Utilities.generateVirtualCallInstruction(lirprog, 
					callStmt.funcName, 
					callStmt.args_list, 
					this, 
					enclosingClassName, 
					thisReg);
			
			Utilities.Printer( callInstr );
			
		}
		return null;
	}
	
	@Override
	public Object visit(ContinueStmt continueStmt, Register context) {
		// We simple jump to the start of the enclosing while
		Instruction breakInstr = new Instruction("Jump",lirprog.peekEnclosingWhile().toString(), null );
		Utilities.Printer( breakInstr );
		return null;
	}

	@Override
	public Object visit(IfStmt ifStmt, Register context) 
	{
	
		StmtLabel nl = new StmtLabel("if");
		StmtLabel elseBlock = new StmtLabel("else");
		Utilities.Printer( nl.toString()+":"  );
		
		// Push a dummy register to the register stack, this is to identify this new scope
		Register dumm = new Register("dummy");
		lirprog.registerStack.push( dumm );
		
		// Evaluate the boolean expression and save it into a register
		Register boolExprResult = new Register();
		lirprog.registerStack.push( boolExprResult );

		boolExprResult = (Register)ifStmt.expr.accept(this , boolExprResult );
		
		// Depending on the result of the boolExprResult,we either jump to the else label, or keep executing 
		// instructions, or jump to the end label, if else does not exist
		Instruction booleanRes = new Instruction("Compare","0" , boolExprResult.name); //  boolExprResult - 0 
		Instruction booleanJump = null;
				
		// Else block does not exist, jump to the end of the if
		if( ifStmt.s2 == null)
			booleanJump = new Instruction("JumpFalse",nl.toStringEnd(),null );
		else
		{
			booleanJump = new Instruction("JumpFalse",elseBlock.toString(),null );
		}
			
		Utilities.Printer( booleanRes );
		Utilities.Printer( booleanJump );
		
		// Go over statements in the if block
		ifStmt.s1.accept( this, context );
		
		// Start of the else block
		if( ifStmt.s2 != null )
		{
			// Push a dummy register to the register stack, this is to identify this new scope
			Register dummy = new Register("dummy");
			lirprog.registerStack.push( dummy );
			
			Utilities.Printer( elseBlock.toString()+":"  );
			ifStmt.s2.accept( this, context );
			
			// else statement blocks create new scopes, so in the end of the scope, remove all assigned registers
			lirprog.popScope();
		}
		else 
		{
			// This is the end of the if block
			Utilities.Printer( nl.toStringEnd()+":" );
		}
		
		// if statement blocks create new scopes, so in the end of the scope, remove all assigned registers
		lirprog.popScope();
		return null;
	}

	@Override
	public Object visit(ReturnStmt returnStmt, Register context) 
	{
		
		Register returnRegister=null;
		//itay 
		if(returnStmt.expr!=null){ 
			returnRegister = new Register();
			lirprog.registerStack.push( returnRegister );
			returnRegister = (Register)returnStmt.expr.accept(this, returnRegister);
			Utilities.Printer("Return " + returnRegister );
		}
		return returnRegister;
	}

	@Override
	public Object visit(NestedStmtList nestedStmtList, Register context) 
	{
		for( int i = nestedStmtList.stmtList.statements.size() - 1 ; i >=0 ;i-- )
		{
			
			nestedStmtList.stmtList.statements.get(i).accept(this,context);
			
		}
		return null;
	}

	//@Override
		public Object visit(Expr expr, Register context) {
			
				if( expr instanceof LocationExpr )
				{
					LocationExpr ex = (LocationExpr)expr;
					ex.accept(this, context);
				}
				 if( expr instanceof BinaryOpExpr )
				{
					BinaryOpExpr ex = (BinaryOpExpr)expr;
					String translate_expr = (String)ex.accept(this, context);
					return translate_expr;
				}
				 else if( expr instanceof UnaryOpExpr )
				{
					UnaryOpExpr ex = (UnaryOpExpr)expr;
					String translate_expr = (String)ex.accept(this, context); 
					return translate_expr; 
				}
				else if( expr instanceof NumberExpr )
				{
					NumberExpr ex = (NumberExpr)expr;
					String translate_expr = (String)ex.accept(this, context);
					return translate_expr;
				}
				else if( expr instanceof BooleanExpr )
				{
					BooleanExpr ex = (BooleanExpr)expr;
					String translate_expr = (String)ex.accept(this, context);
					return translate_expr;
				}
				else if( expr instanceof newObject )
				{
					newObject ex = (newObject)expr;
					ex.accept(this, context);
				}
				else if( expr instanceof CallExpr )
				{
					CallExpr ex = (CallExpr)expr;
					ex.accept(this, context);
				}
				else if( expr instanceof LengthOfArray )
				{
					LengthOfArray ex = (LengthOfArray)expr;
					ex.accept(this, context);
				}
				else if( expr instanceof NewArrayExpr )
				{
					NewArrayExpr ex = (NewArrayExpr)expr;
					ex.accept(this, context);
				}
				else if( expr instanceof NullExpr )
				{
					NullExpr ex = (NullExpr)expr;
					ex.accept(this, context);
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

				return null;
		}

	@Override
	public Object visit(BooleanExpr booleanExpr, Register context) {
		String booleanExprValue;
		if (booleanExpr.value){
			booleanExprValue = "1";
		}
		else{
			booleanExprValue = "0";
		}
		Instruction inst = new Instruction("Move",   booleanExprValue , context.name );
		Utilities.Printer( inst );
		return context;
	
		
	}

	@Override
	//@Override
	public Object visit(BinaryOpExpr binaryOpExpr, Register context) 
	{	
			//itay
			boolean handleClassInstanceBeforeAccept = context.isClassInstance;
			Register left = (Register)binaryOpExpr.lhs.accept(this, context);
			Register right = new Register();
			lirprog.registerStack.push( right );
			right = (Register)binaryOpExpr.rhs.accept(this, right);
			
			// Check if we are performing an operation on a register which is a class instance
			// If we are, move it to a temp register to just hold the value
			if( context.isClassInstance && handleClassInstanceBeforeAccept )
			{
				Register tempFieldRegister = new Register();
				lirprog.registerStack.push(tempFieldRegister);
				Instruction inst = new Instruction( "MoveField", context.name, tempFieldRegister.name );
				Utilities.Printer( inst );
				
				left = tempFieldRegister;
			}
			
			if( right!= null &&  right.isClassInstance  )
			{
				Register tempFieldRegister = new Register();
				lirprog.registerStack.push(tempFieldRegister);
				Instruction inst = new Instruction( "MoveField", right.name, tempFieldRegister.name );
				Utilities.Printer( inst );
				
				right = tempFieldRegister;
			}
			
			// Treat a boolean expression differently
			if( binaryOpExpr.op.isBoolOperator() )
			{
				left = Utilities.evaluateBooleanExpression( binaryOpExpr , left, right);
			}
			else
			{
				// Check for the edge case of adding two strings
				if( binaryOpExpr.lhs.getType().getId().contentEquals("string") &&
					binaryOpExpr.rhs.getType().getId().contentEquals("string") &&
					binaryOpExpr.op == Operator.PLUS )
				{
					Register returnReg = new Register();
					lirprog.registerStack.push( returnReg );
					// We use a library function called __stringCat
					Instruction inst = new Instruction("Library" , "__stringCat("+left.name+","+right.name+")"
														,returnReg.name 
														 );
					
					Utilities.Printer( inst );
					
					left = returnReg;
				}
				else
				{
					// If we are here, this is not a boolean expression
					Instruction inst = new Instruction( binaryOpExpr.op.toString(), right.name, left.name );
					Utilities.Printer( inst );
				}

			}
			return left;
		}

	@Override
	public Object visit(CallExpr callExpr, Register context) 
	{
		if( callExpr.classID != null )
		{
			// This is a static call
			Instruction callInstr = Utilities.generateStaticCallInstruction(lirprog, 
					callExpr.funcName, 
					callExpr.args_list, 
					this, 
					callExpr.classID);

			Utilities.Printer(callInstr);
		}
		else if( callExpr.e != null )
		{
			// This is a virtual call, called from an object given in the expression
			SemanticType t = callExpr.e.getType();
			if( t != null )
			{
				if( t.isClassInstance || true)
				{
					// Find the register containing a reference to the expression which is a class instance
					// The expression can either be a location expression or a call expression
					// We make sure a call expression and a location expression return a register
					// containing an object instance of this class
					Register exprReg = new Register();
					lirprog.registerStack.push( exprReg );
					Register objectInstance = (Register)callExpr.e.accept( this, exprReg );
					
					Instruction callInstr = Utilities.generateVirtualCallInstruction(lirprog, 
																				callExpr.funcName, 
																				callExpr.args_list, 
																				this, 
																				t.getId(), 
																				objectInstance);
					Utilities.Printer( callInstr );
				}
			}
		}
		else
		{
			// This function is called using a 'this' object
			
			// First find the enclosing class name, so we can know where to look for the function
			String enclosingClassName = SymbolTable.findEnclosingClass( callExpr.getScopeTable() );
			
			
			// Create a register for the this pointer
			Register thisReg = new Register();
			lirprog.registerStack.push( thisReg );
			thisReg.isClassInstance = true;
			thisReg.generateVariableList( enclosingClassName );
			
			//
			Instruction inst = new Instruction("Move","this",thisReg.name);
			Utilities.Printer(inst);
			
			Instruction callInstr = Utilities.generateVirtualCallInstruction(lirprog, 
					callExpr.funcName, 
					callExpr.args_list, 
					this, 
					enclosingClassName, 
					thisReg);
			
			Utilities.Printer( callInstr );
			
		}
		
		// The different between a call statement and an expression, is that an expression can evaluate to a value
		// Therefore, if there is a return value, return the return value register
		// To find this register, look for a register with a variable name: ret_METHODNAME
		return lirprog.findVariableRegister("ret_"+callExpr.funcName);
	}

	@Override
	public Object visit(LengthOfArray lengthOfArray, Register context) {
		Register r=null;
		Symbol s = lengthOfArray.getScopeTable().getSymbol( Utilities.getArrayID(lengthOfArray.arrayExpr));
		if(s.getSymbolKind() == Kind.FIELD){ //its a fieldArray
			//itay
			Register fieldArray = new Register();
			lirprog.registerStack.push(fieldArray);
			//move "this" to new fieldArray
			fieldArray = (Register)lengthOfArray.arrayExpr.accept(this,fieldArray);
			
			Register tempArray = new Register();
			lirprog.registerStack.push(tempArray);
			//move to right field in "this" to tempArray
			Instruction tempInstr = new Instruction("MoveField",fieldArray.name, tempArray.name );
			Utilities.Printer(tempInstr);
			r=tempArray;
		}
		else{
			LocationExpr locationExprCast = (LocationExpr) lengthOfArray.arrayExpr;
			r = lirprog.findVariableRegister(locationExprCast.ID);
		}
		Instruction instruction = new Instruction("ArrayLength",r.toString(), context.toString());
		Utilities.Printer(instruction);
		return context;
	}

	@Override
	public Object visit(LocationExpr locationExpr, Register context) 
	{
		
		if( locationExpr.ID != null && locationExpr.e1 == null && locationExpr.e2 == null ) // A reference to a variable in current scope or parent scope
		{
			// First check if the reference value is a field variable, if it is we need to access the this object
			Symbol s = locationExpr.getScopeTable().getSymbol( locationExpr.ID );
			if( s.getSymbolKind() == Kind.FIELD )
			{
				// Second check if a 'this' register already exists:
				Register thisObj = lirprog.findVariableRegister( "this" );
				
				if( thisObj == null )
				{
					// Create a register to hold a pointer to the this object
					thisObj = new Register();
					lirprog.registerStack.push( thisObj );
					
					Instruction inst = new Instruction("Move","this",thisObj.name);
					Utilities.Printer(inst);
					
					thisObj.generateVariableList( SymbolTable.findEnclosingClass( locationExpr.getScopeTable() ));
					thisObj.heldValue = new Variable("this",locationExpr.ID);

				}
				// Rename the context register, which is the register which will hold this location
				// To a certain offset inside the thisObj register
				context.name = thisObj.name+"."+thisObj.retrieveVarIndex( locationExpr.ID );
				context.isClassInstance = true;
				context.heldValue = new Variable(locationExpr.ID, null );
				//Instruction inst = new Instruction("MoveField", locationExpr.ID, context.name );
				//Utilities.Printer( inst );
			}
			else
			{
				Register knownLocation = lirprog.findVariableRegister( locationExpr.ID );
				if( knownLocation != null )
				{
					// This variable was already assigned a register so simply return the register
					return knownLocation;
				}
				else
				{
					Instruction inst = new Instruction("Move", locationExpr.ID, context.name );
					Utilities.Printer( inst );
				}
			}


		}
		// A reference to a variable in external scope
		else if( locationExpr.ID != null && locationExpr.e1 != null && locationExpr.e2 == null ) 
		{
			// Will either return a register pointing to an object created with new
			// Or will return a register of a call expression return register
			Register knownLocation;
			if (locationExpr.e1 instanceof ThisObject){
				Register thisObj = lirprog.findVariableRegister( "this" );
				
				// Second check if a 'this' register already exists:
				
				if( thisObj == null )
				{
					// Create a register to hold a pointer to the this object
					thisObj = new Register();
					lirprog.registerStack.push( thisObj );
					
					Instruction inst = new Instruction("Move","this",thisObj.name);
					Utilities.Printer(inst);
					
					thisObj.generateVariableList( SymbolTable.findEnclosingClass( locationExpr.getScopeTable() ));
					thisObj.heldValue = new Variable("this",locationExpr.ID);

				}
				// Rename the context register, which is the register which will hold this location
				// To a certain offset inside the thisObj register
				context.name = thisObj.name+"."+thisObj.retrieveVarIndex( locationExpr.ID );
				context.isClassInstance = true;
				context.heldValue = new Variable(locationExpr.ID, null );
				//Instruction inst = new Instruction("MoveField", locationExpr.ID, context.name );
				//Utilities.Printer( inst );
			
				knownLocation = thisObj;
				
			}
			else{
				 knownLocation = (Register)locationExpr.e1.accept(this,context);

			}

			context.name = knownLocation.name+"."+knownLocation.retrieveVarIndex( locationExpr.ID );
			context.isClassInstance = true;
			
		}
		// A reference to an array
		else if( locationExpr.ID == null && locationExpr.e1 != null && locationExpr.e2 != null ) 
		{
			Register knownArray = (Register)locationExpr.e1.accept(this,context);
			Register indexToArray = new Register();
			indexToArray = (Register) locationExpr.e2.accept(this, indexToArray);
			
			// This reference to an array might be a reference to a field array
			// We cannot use a field array before passing it to a temporary register
			if( knownArray.isClassInstance )
			{
				Register tempArray = new Register();
				lirprog.registerStack.push( tempArray );
				tempArray.isArrayInstance = true;	
				Instruction tempInstr = new Instruction("MoveField",knownArray.name, tempArray.name );
				Utilities.Printer(tempInstr);
				knownArray = tempArray;
				knownArray.isClassInstance = false;
			}
			
			// The index to the array might be a field 
			if( indexToArray.isClassInstance )
			{
				Register tempArray = new Register();
				lirprog.registerStack.push( tempArray );
				tempArray.isClassInstance = true;	
				Instruction tempInstr = new Instruction("MoveField",indexToArray.name, tempArray.name );
				Utilities.Printer(tempInstr);
				indexToArray = tempArray;
				indexToArray.isClassInstance = false;
			}
					
			// Save the reference to the array in a temporary register
			context.name = knownArray.name+"["+indexToArray.name+"]";
			
			
			Instruction tempInstr = null;
			Register tempArray =null;
			if (!locationExpr.isLeft){ // if its right   i = a[2] movearray R2[R3],R4
				 tempArray = new Register();
				lirprog.registerStack.push( tempArray ); 
				tempInstr = new Instruction("MoveArray",context.name, tempArray.name );

			}

			else if (locationExpr.isLeft){ // a[2] = a[2] = i   movearray R4 ,R2[R3]       i - so we need to fill an array - to move something in it
				tempArray= lirprog.findVariableRegister(locationExpr.varAssignment);
				tempInstr = new Instruction("MoveArray", tempArray.name ,context.name);

			}
			Utilities.Printer(tempInstr);
			
			context = tempArray;
			context.isArrayInstance = true;
		}
		return context;
	}

	@Override
	public Object visit(NewArrayExpr newArrayExpr, Register context) 
	{
		Type arrayType = newArrayExpr.arrayType;
		Expr arrayLength = newArrayExpr.arrayLength;
		
		// Generate a register for the array length
		Register arrayLen = new Register();
		lirprog.registerStack.push( arrayLen );
		BinaryOpExpr mult = new BinaryOpExpr( arrayLength, new NumberExpr(4,0), Operator.MULT, 0 );
		arrayLen = (Register) mult.accept( this, arrayLen );
		
		Instruction instruction1 = new Instruction("Library"
				, "__" + "allocateArray(" + arrayLen.name + ")"
				, context.name );
		context.isArrayInstance = true;
		Utilities.Printer(instruction1);
		return context;
	}

	@Override
	public Object visit(newObject newObject, Register context) 
	{
		ClassDecl classDecl =  Utilities.classTable.get(newObject.className);
		
		// Start from one for the dispatch table
		int objectSize = 1;
		for( Symbol sy : classDecl.bodyList.getScopeTable().Table.values() ) 
		{
			if( sy.getSymbolKind() == Kind.FIELD )
			{
				objectSize++;
			}
		}
		// Transform to bytes
		objectSize = objectSize*4; 
		// Allocation
		Instruction instruction1 = new Instruction("Library"
													, "__" + "allocateObject(" + objectSize + ")"
													, context.toString() );
		// Move the dispatch table pointer to R#.0
		Instruction instruction2 = new Instruction("MoveField"
													, "_DV_" + newObject.className
													, context.toString() + ".0");
				
		Utilities.Printer(instruction1);
		Utilities.Printer(instruction2);
		
		// Make sure the register holding the reference is properly marked as a class instance
		context.isClassInstance = true;
		context.generateVariableList( newObject.className );

		return context;
	}

	@Override
	public Object visit(NullExpr nullExpr, Register context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NumberExpr numberExpr, Register context) 
	{
		Instruction inst = new Instruction("Move", numberExpr.toString(), context.name );
		Utilities.Printer( inst );
		return context;
	}

	@Override
	public Object visit(QuoteExpr quoteExpr, Register context) 
	{
		LIRString lirString = new LIRString(quoteExpr.quote);
		lirprog.StringLiteralList.add(lirString);
		
		// There is probably a better way to do this, this just returns a register with its name being the string literal
		// Mainly used for function calls, when the quote is the parameter, because function calls get registers as parameters
		Register dummyRegister = new Register( "str"  + lirString.currentRegNumber);
		Register.decrementRegisterNumber();
		return dummyRegister;
	}

	@Override
	public Object visit(ThisObject thisObject, Register context) {
		return null;
	}

	//@Override
		public Object visit(UnaryOpExpr unaryOpExpr, Register context) 
		{

			String translate_operand = unaryOpExpr.operand.accept(this, context).toString();
			String unary_type = unaryOpExpr.getType().getId();
			//check if visit on operand returns a register:
			if(this.isRegister(translate_operand) == false){
				Instruction moveInst = new Instruction("Move",translate_operand, context.name);
				Utilities.Printer(moveInst.toString() + NEWLINE  );
			}
			
			if(unary_type.equals("int")){
				Utilities.Printer("Neg "+ context.name + NEWLINE  );
			}
			else{
				Utilities.Printer("Not "+ context.name + NEWLINE  );
			}
			
			return context;
		}

	@Override
	public Object visit(VarExpr varExpr, Register context) {
		// TODO Auto-generated method stub if A 
		return null;
	}

	@Override
	public Object visit(IDList idList, Register context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FuncArgsList funcArgsList, Register context) {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isRegister(String visit_return){
		if(visit_return!=null){
			if(visit_return.startsWith("R")) 
				return true;
		}
		return false;
	}
	
	
		
	
}
