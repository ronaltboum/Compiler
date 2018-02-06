package SemanticAnalysis;



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
import slp.Type;
import slp.Visitor;
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

public class SemanticCheck implements Visitor {
	
	protected final 		ASTNode root;
	
	/** 
	 * 
	 * @param root The root of the AST.
	 */
	public SemanticCheck(ASTNode root) {
		this.root = root;
	}

	private void Printer( String s )
	{
		System.out.println( s );
	}
	
	/** 
	 */
	
	public void SymTableCheck() 
		{
			boolean mainFound = false;
			// Before we begin, look for a main function declaration in any of the classes
			for( ClassDecl s : Utilities.classTable.values() )
			{
				Method m = Utilities.findMethodInClass("main", s.className );
				if( m != null )
				{
					Formal f = m.formalList.formals.get(0);
					if( m.isStatic && m.isVoid && f.type.typeName.contentEquals("1 dimensional array of string") && f.id.contentEquals("args") )
						mainFound = true;
						
				}
			}
			
			if( mainFound )
				root.accept(this);
			else
			{
				try {
					throw new SemanticError("main function is undefined", 0 );
				} catch (SemanticError e) {
					Utilities.Printer(e.getErrMessage());
					System.exit(-1);
				}
			}
	}
	
	public Object visit(StmtList stmts) 
	{
		for( int i = stmts.statements.size() - 1 ; i >=0 ;i-- ) 
		{
			stmts.statements.get(i).accept(this);
		}
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
	
	public Object visit(NestedStmtList stmts) 
	{
		for( int i = stmts.stmtList.statements.size() - 1 ; i >=0 ;i-- )
		{
			stmts.stmtList.statements.get(i).accept(this);
		}
		return null;
	}
	
	public Object visit(WhileStmt stmt) 
	{
		if( stmt.expr instanceof LengthOfArray )
		{
			LengthOfArray l = (LengthOfArray)stmt.expr;
			BinaryOpExpr op = (BinaryOpExpr) l.arrayExpr;
			
			l.arrayExpr = op.rhs;
			BinaryOpExpr newOp = new BinaryOpExpr(op.lhs, l, op.op, 0);
			stmt.expr = newOp;
		}
		else if( stmt.expr instanceof LocationExpr ) // Array bug, it thinks that ( a[i] > a[i+1] ) is the location expr-> a[ i > i+1 ]
		{
			LocationExpr l = (LocationExpr)stmt.expr;
			Expr e2 = l.e2;
			
			BinaryOpExpr op = (BinaryOpExpr) l.e1;
			Expr saveRHS = op.rhs;
			op.rhs = new LocationExpr(saveRHS, e2, 0);
			BinaryOpExpr newOp = new BinaryOpExpr(op.lhs, op.rhs, op.op, 0);
			stmt.expr = newOp;
		}
		
		stmt.expr.accept(this);
		if (!stmt.expr.getType().equals("boolean")){  // ron changed saturday !!!!!!!!!!!!!!
			try {
				throw new SemanticError( "while must recieve a boolean argument", stmt.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
				System.exit(1);
			}

		}
		stmt.stmtBlock.accept(this);
		return null;
	}
	
	public Object visit(BreakStmt stmt)
	{
		SymbolTable symTable = stmt.getScopeTable();
		if( !symTable.lookupScopeID("While"))
		{
			try {
				throw new SemanticError( "Break statement has to be defined inside a while statement", stmt.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
				System.exit(-1);
			}
		}
		return null;
	}
	
	public Object visit(CallStmt stmt) 
	{
		if( stmt.e != null )
			stmt.e.accept(this);
		
		if( stmt.args_list != null)
		{
			stmt.args_list.accept(this);
		}
		
		String checkResult = Utilities.ValidateFunctionCall( stmt.getScopeTable(), stmt.classID, stmt.funcName, stmt.e, stmt.args_list, null);
		if( checkResult != "")
		{
			try {
				throw new SemanticError( checkResult, stmt.CreationLine );
			} 
			catch (SemanticError e) 
			{
				Printer( e.getErrMessage() );
				System.exit( -1 );
			}
		}
		return null;
	}
	
	public Object visit(ContinueStmt stmt) 
	{
		SymbolTable symTable = stmt.getScopeTable();
		if( !symTable.lookupScopeID("While"))
		{
			try {
				throw new SemanticError( "Continue statement has to be defined inside a while statement", stmt.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
				System.exit(1);
			}
		}
		return null;
	}
	
	public Object visit(IfStmt stmt) 
	{
		if( stmt.expr instanceof LengthOfArray )
		{
			LengthOfArray l = (LengthOfArray)stmt.expr;
			BinaryOpExpr op = (BinaryOpExpr) l.arrayExpr;
			
			l.arrayExpr = op.rhs;
			BinaryOpExpr newOp = new BinaryOpExpr(op.lhs, l, op.op, 0);
			stmt.expr = newOp;
		}
		else if( stmt.expr instanceof LocationExpr ) // Array bug, it thinks that ( a[i] > a[i+1] ) is the location expr-> a[ i > i+1 ]
		{
			LocationExpr l = (LocationExpr)stmt.expr;
			Expr e2 = l.e2;
			
			BinaryOpExpr op = (BinaryOpExpr) l.e1;
			Expr saveRHS = op.rhs;
			op.rhs = new LocationExpr(saveRHS, e2, 0);
			BinaryOpExpr newOp = new BinaryOpExpr(op.lhs, op.rhs, op.op, 0);
			stmt.expr = newOp;
		}
		stmt.expr.accept(this);
		
		
		if (!stmt.expr.getType().equals("boolean")){      // RON CHANGED FRIDAY!!!!!!!!!!!!!!!!!!
			try {
				throw new SemanticError( "if must recieve a boolean argument", stmt.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
				System.exit(1);
			}

		}
		// If
		if( stmt.s1 != null )
			stmt.s1.accept(this);
		
		// else
		if( stmt.s2 != null ){
			stmt.s2.accept(this);
		}
		
		return null;
	}
	
	
	public Object visit(ReturnStmt stmt) 
	{
		// Can only return from a method scope with a return type
		SymbolTable symTable = stmt.getScopeTable();
		if( !symTable.lookupScopeID("RetMethod"))
		{
			try {
				throw new SemanticError( "Cannot return from a 'void' function", stmt.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
				System.exit(-1);
			}
		}
		
	//   itay changed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		
			String funcName = SymbolTable.findEnclosingMethod( stmt.getScopeTable() );
			//
			String className = SymbolTable.findEnclosingClass( stmt.getScopeTable() );
			String funcType = Utilities.findMethodInClass( funcName ,className ).funcType.typeName;

			if(stmt.expr != null)
				stmt.expr.accept(this);
			else{
				if(funcType.equals("void"))
					return null;
				try {
					throw new SemanticError( funcName + " return type decalred is " + 
							funcType + " and not void" , stmt.CreationLine );
				} catch (SemanticError e) {
					Printer( e.getErrMessage() );
					System.exit(-1);

				}
			}
			if(Utilities.classTable.containsKey(funcType) && Utilities.classTable.containsKey(stmt.expr.getType().getId())){
				if (!SymbolTableTreeBuilder.AInhertFromB(stmt.expr.getType().getId(),funcType)){
					try {
						throw new SemanticError( funcName + " return type decalred is " + 
								funcType + " and not " + stmt.expr.getType().getId(), stmt.CreationLine );
					} catch (SemanticError e) {
						Printer( e.getErrMessage() );
						System.exit(-1);

					}

				}
			}

		else if(!stmt.expr.getType().equals(funcType)){
			try {
				throw new SemanticError( funcName + " return type decalred is " + 
						funcType + " and not " + stmt.expr.getType().getId(), stmt.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
				System.exit(-1);

			}
		}
		return null;
	}
	
	public Object visit(AssignStmt stmt) {
		
		if( (stmt.varExpr != null) && (stmt.rhs == null) && (stmt.locationExpr == null) )
		{ //for example:  int n; rhs is the number
			
			stmt.varExpr.accept(this);
	
		}
		else if( (stmt.varExpr != null) && (stmt.rhs != null) && (stmt.locationExpr == null)  )
		{ //for example:  int n = 9;
			// B[] n = new C[];
			// type of varExpr - varexpr.ge
			stmt.varExpr.accept(this);
			stmt.rhs.accept(this);

			// check if they are both classes
			if( stmt.varExpr.getType()!= null && stmt.rhs.getType()!= null )
			{
				String varExpr_type = stmt.varExpr.getType().getId();
				String rhs_type =  stmt.rhs.getType().getId();
				if((stmt.varExpr.typeSlp.type == sym.CLASS_ID) && stmt.rhs.getType().isClassInstance)
				{
					if (!SymbolTableTreeBuilder.AInhertFromB(stmt.rhs.getType().getId(),stmt.varExpr.getType().getId()))
					{
						// A a = new C(); ----> check if c inherts from A
						try {
							throw new SemanticError("Semantic Error:  " + stmt.rhs.getType().getId() + " is not a subtype of" + stmt.varExpr.getType().getId() , stmt.CreationLine );
									
						} catch (SemanticError e) {
							Printer( e.getErrMessage() );
							System.exit(-1);
						}
						
	
					}
				}
				else if (stmt.varExpr.getType().getId().contains("array"))
				{  //left side is an array side
					//check if right side is also an array
				

					if( !stmt.rhs.getType().getId().contains("array") )
					{
						try {
							throw new SemanticError("Semantic Error:  " + stmt.varExpr.name + " is of type "
								+ stmt.varExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()  , stmt.CreationLine	);
									
						} catch (SemanticError e) {
							Printer( e.getErrMessage() );
							System.exit(-1);
						}	
					}
					else
					{  //we know both sides are arrays.  Need to verify both sides are of same type and dimension:
						String [] leftArr = varExpr_type.split(" ");
						String [] rightArr = rhs_type.split(" ");
		;
						boolean isInherit = SymbolTableTreeBuilder.AInhertFromB(rightArr[rightArr.length -1], leftArr[leftArr.length -1]);
						if(isInherit == false){
							//System.out.println("in in false inherit");
							try {
								throw new SemanticError("Semantic Error:  " + stmt.varExpr.name + " is of type "
									+ stmt.varExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()   , stmt.CreationLine	);
										
							} catch (SemanticError e) {
								Printer( e.getErrMessage() );
								System.exit(-1);
							}	
						}
						if (!leftArr[0].equals(rightArr[0])){
							try {
								throw new SemanticError("Semantic Error:  " + stmt.varExpr.name + " is assigned a wrong size "
									+ stmt.varExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()   , stmt.CreationLine	);
										
							} catch (SemanticError e) {
								Printer( e.getErrMessage() );
								System.exit(-1);
							}	
						}
					}
					
					
				}  //closes case where left side's type is an array
				else if (stmt.rhs.getType().getId().contains("array")){  //right side is an array but left side isn't:
					try {
						throw new SemanticError("Semantic Error:  " + stmt.varExpr.name + " is of type "
							+ stmt.varExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()   , stmt.CreationLine	);
								
					} catch (SemanticError e) {
						Printer( e.getErrMessage() );
						System.exit(-1);
					}	
					
				}
				else if (!stmt.varExpr.getType().equals(stmt.rhs.getType()))
				{ 
					try {
						throw new SemanticError("Semantic Error:  " + stmt.varExpr.name + " is of type "
							+ stmt.varExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()   , stmt.CreationLine	);
								
					} 
					catch (SemanticError e) 
					{
						Printer( e.getErrMessage() );
						System.exit(-1);
					}	
				}
			}
		}
		else if( (stmt.varExpr == null) && (stmt.rhs != null) && (stmt.locationExpr != null)  )
		{  //for example: n= 9 locationExpr is the variable name rhs is the nubmer
			stmt.locationExpr.accept(this);
			stmt.rhs.accept(this);
			SemanticType typeOfVariable = stmt.locationExpr.getType();
			SemanticType typeOfValue = stmt.rhs.getType();
		
			if( typeOfVariable != null && typeOfValue != null )
			{
			boolean typeOfVariableIsClass = Utilities.classTable.containsKey(typeOfVariable.getId());
			boolean typeOfValueIsClass = Utilities.classTable.containsKey(typeOfValue.getId());
				if (typeOfValueIsClass && typeOfVariableIsClass){
					//System.out.println("rrrrrrrrrrrrr");
					if (!SymbolTableTreeBuilder.AInhertFromB( typeOfValue.getId(),typeOfVariable.getId())){
						
						try {
							throw new SemanticError("Semantic Error:  " + typeOfVariable.getId() + " is not a subtype of " + typeOfValue.getId()   , stmt.CreationLine	);
							
						} catch (SemanticError e) {
							Printer( e.getErrMessage() );
							System.exit(-1);
						}	
					}
				}  //end of case where both types are classes and not arrays
				
			else if (!typeOfVariable.equals(typeOfValue)){
				
// ron changed saturday !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				
				// handle array first:
				String left_type = typeOfVariable.getId();
				String rhs_type =  typeOfValue.getId();
				
				//System.out.println("left_type =    "+ left_type);
				//System.out.println("right side type = "+rhs_type );
				
				int is_misType = 0;   //if is_misType ==1  we have an
				
				if(left_type != null  &&  rhs_type != null){  // make sure string names aren't null
					if (left_type.contains("array")){  //left side is an array side
					//check if right side is also an array
					if( !rhs_type.contains("array") ){
						try {
							//System.out.println("111111111111111111111111");
							throw new SemanticError("Semantic Error:  " + stmt.locationExpr.ID + " is of type "
								+ stmt.locationExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()  , stmt.CreationLine 	);
									
						} catch (SemanticError e) {
							Printer( e.getErrMessage() );
							System.exit(-1);
						}	
					}else{  //we know both sides are arrays.  Need to verify both sides are of same type and dimension:
						String [] leftArr = left_type.split(" ");
						String [] rightArr = rhs_type.split(" ");
						//System.out.println(leftArr[leftArr.length -1]);
						//System.out.println(rightArr[rightArr.length -1]);
						boolean isInherit = SymbolTableTreeBuilder.AInhertFromB(rightArr[rightArr.length -1], leftArr[leftArr.length -1]);
						if(isInherit == false){
							//System.out.println("iiiiiiiiiiiiiiiiiiiiiii       in in false inherit");
							try {
								//System.out.println("222222222222222222222222");
								throw new SemanticError("Semantic Error:  " + stmt.locationExpr.ID + " is of type "
									+ stmt.locationExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()  , stmt.CreationLine 	);
										
							} catch (SemanticError e) {
								Printer( e.getErrMessage() );
								System.exit(-1);
							}	
						}
					}
					
					
				}  //closes case where left side's type is an array
				else if (rhs_type.contains("array")){  //right side is an array but left side isn't:
					try {
						//System.out.println("33333333333333333333333333333");
						throw new SemanticError("Semantic Error:  " + stmt.locationExpr.ID + " is of type "
							+ stmt.locationExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()   , stmt.CreationLine	);
								
					} catch (SemanticError e) {
						Printer( e.getErrMessage() );
						System.exit(-1);
					}	
					
				}else{ //both sides aren't arrays
					try {     // both sides aren't arrays  and aren't of the same type
						//System.out.println("qqqqqqqqqqqqqqqqqqqqqq");
						throw new SemanticError("Semantic Error:  " + stmt.locationExpr.ID + " is of type "
							+ stmt.locationExpr.getType().getId() + " and was assigned a type of " + stmt.rhs.getType().getId()   , stmt.CreationLine	);
								
					} catch (SemanticError e) {
						Printer( e.getErrMessage() );
						System.exit(-1);
					}	
				}
					
				
				}  //closes case of both sides string id's aren't null.  
			
				
				
			}
			}
		}
		//System.out.println("i'm in line 281");
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
			
			//if(ex != null)
			
				ex.accept(this);
		}
		else if( expr instanceof newObject )
		{
			newObject ex = (newObject)expr;
			
			//if(ex != null)
			
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
		expr.lhs.accept(this);
		expr.rhs.accept(this);
		SemanticType operandType1 = expr.lhs.getType();
		SemanticType operandType2 = expr.rhs.getType();
		
		Operator operator = (Operator)expr.op;
		
		//////////////////////////////////////////////        RON ADDS FRIDAY //////////////////////
		if(operator.compareTo(Operator.EQUAL) == 0 || operator.compareTo(Operator.NEQUAL) == 0 ){
			//according to IC SPECIFICATION we need to make sure both types are the same
			if( !(operandType1.equals(operandType2)) ){
				try {
					throw new SemanticError("The  binary operation '" 
							+ operator.toString() 
							+ "' must get 2 operands  of the same type", expr.CreationLine );
				} catch (SemanticError e) {
					Printer( e.getErrMessage() );
					System.exit(-1);
				}
				
			}else
				expr.setType(new SemanticType("boolean"));
		}
		
		
		////////////  below ron changed friday from if to else if
		
		else if(operator.compareTo(Operator.LAND) == 0 || operator.compareTo(Operator.LOR) == 0 ){
			if(operandType1.getId().equals("boolean") && operandType2.getId().equals("boolean")){
				expr.setType(new SemanticType("boolean"));
			}
			
			else{
				try {
					throw new SemanticError("The  binary operation '" 
							+ operator.toString() 
							+ "' must get 2 operands  of a boolean type", expr.CreationLine );
				} catch (SemanticError e) {
					Printer( e.getErrMessage() );
					System.exit(-1);

				}
			}
		}
		
		else if(operator.compareTo(Operator.LT) == 0 || operator.compareTo(Operator.LTE) == 0 || operator.compareTo(Operator.GT) == 0 || operator.compareTo(Operator.GTE) == 0){
			if(operandType1.getId().equals("int") && operandType2.equals("int")){
				expr.setType(new SemanticType("boolean"));
				//System.out.println("im in visit(Binary) in line 448");

			}
			else{
				try {
					
					//System.out.println("im in visit(Binary) in SemanticError in line 454");
					throw new SemanticError("The  binary operation '" 
							+ operator.toString() 
							+ "' must get 2 operands  of a int type", expr.CreationLine );
				} catch (SemanticError e) {
					Printer( e.getErrMessage() );
					System.exit(-1);

				}	
			}

		} 
		else if(operator.compareTo(Operator.PLUS) == 0){
			if(operandType1.getId().equals("string") && operandType2.equals("string")){
				expr.setType(new SemanticType("string"));

			}
			
			else if(operandType1.getId().equals("int") && operandType2.equals("int")){
				expr.setType(new SemanticType("int"));

			}
			else{
				try {
					throw new SemanticError("The  binary operation '" 
							+ operator.toString() 
							+ "' must get 2 operands  of an int type or 2 operands of a string type", expr.CreationLine );
				} catch (SemanticError e) {
					Printer( e.getErrMessage() );
					System.exit(-1);

				}	
			}
		}

		// If the operator is one of {-,/,*,%}
		else{
			 if(operandType1.getId().equals("int") && operandType2.equals("int")){
				expr.setType(new SemanticType("int"));

			}
			else{
				try {
					throw new SemanticError("The  binary operation '" 
							+ operator.toString() 
							+ "' must get 2 operands  of an int type ", expr.CreationLine );
				} catch (SemanticError e) {
					Printer( e.getErrMessage() );
					System.exit(-1);

				}	
			}
		}
		 return null;
	}
	
	@Override
	public Object visit(LocationExpr expr) 
	{	
		if( expr.ID != null && expr.e1 == null && expr.e2 == null ) // A reference to a variable in current scope
		{
			// Check if ID was defined in any above scope
			
			if( !expr.getScopeTable().lookupSymbol(expr.ID) )
			{
				try {
					throw new SemanticError(expr.ID+" is undefiend", expr.CreationLine );
				} catch (SemanticError e) {
					Printer( e.getErrMessage() );
					System.exit(-1);
			
				}
			}
			else 
			{ // var was defined
				Symbol s = expr.getScopeTable().getSymbol(expr.ID);
				if( Utilities.classTable.containsKey( s.getType().getId() ) )
					s.getType().isClassInstance = true;
				SemanticType typeForVariable = s.getType();
				expr.setType(typeForVariable);
			}
		}
		else if( expr.ID != null && expr.e1 != null && expr.e2 == null ) // A reference to a variable in external scope
		{		
			expr.e1.accept(this);
			
			if( expr.e1.type.isClassInstance )
			{
				// Find the class, and try to find the field in it
				SymbolTable classTable = SymbolTable.findClassSymbolTable( expr.e1.type.getId() );
				
				if( !classTable.lookupSymbol(expr.ID) )
				{
					try {
						throw new SemanticError(expr.ID+" is undefiend in class: "+classTable.determineClassNameFromID(), expr.CreationLine );
					} catch (SemanticError e) {
						Printer( e.getErrMessage() );
						System.exit(-1);
				
					}
				}
				else
				{
					// Such a field exists, assign a type to this expression
					Symbol s = classTable.getSymbol(expr.ID);
					expr.setType( s.getType() );
				}
			}
		}
		else if( expr.ID == null && expr.e1 != null && expr.e2 != null ) // A reference to an array
		{        
			
			expr.e1.accept(this);
			expr.e2.accept(this);           
			
			if(expr.e1.getType() != null){                        //ron changed saturday!!!!!!!!!!
				String e1_Type =  expr.e1.getType().getId();
				if(e1_Type != null){
					if(e1_Type.contains("array")){               //type is array of something:
						//System.out.println("contains array");
						String[] strArr = e1_Type.split(" "); 
						//for example:  1 dimensional array of int
						if(strArr.length > 1){ 
							 //expr.setType(new SemanticType(strArr[strArr.length-1]));  //the last word is
                            int dimension_of_array = Integer.parseInt(strArr[0]);
                            if(dimension_of_array > 1){
                                int expr_dimension = dimension_of_array - 1;
                                String expr_dim = String.valueOf(expr_dimension);
                                String updated_type = expr_dim +" " + strArr[1]+" "+ strArr[2]+" " 
                                + strArr[3]+ " "+strArr[4];
                                //System.out.println("In SemanticCheck visit(Location) and updated_type = "+ updated_type);
                                expr.setType( new SemanticType(updated_type) );   //ron changed DECEMBER 25!!!!!!!!!!!!
 
                            }
                            else if(dimension_of_array == 1){
                                //                          expr.setType( new SemanticType(e1_Type) );   //ron changed DECEMBER 25!!!!!!!!!!!!
                                expr.setType( new SemanticType(strArr[4]) );   //ron changed DECEMBER 25!!!!!!!!!!!!
                                //System.out.println("In SemanticCheck visit(Location) inside else if and strArr[4] = "+ strArr[4]);
                                
                            }
							if(expr.e2.getType()!= null)
							{
								String e2_Type =  expr.e2.getType().getId();
								
								if(e2_Type != null) 
								{

									if(!(e2_Type.equals("int")) )
									{
										try {
											throw new SemanticError("Semantic Error:  Array index should be of type int ", expr.CreationLine );
										} catch (SemanticError e) {
											Printer( e.getErrMessage() );
										}	
									}
								}
							}
						} 
					}
				}
			}
		}
		return null;	
	}
	
	
	@Override
	public Object visit(VarExpr expr) 
	{
			
		if( !expr.getScopeTable().lookupSymbol(expr.name) )
		{
			try {
				throw new SemanticError(expr.name+" is undefined", expr.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
			}
		}

	
		
		 //ron adds monday:
        if(expr.typeSlp != null){
           
             return expr.typeSlp.accept(this);
        }
        return null;  //will get here in case the expr is:  x,  and not int x ?
        //return expr.getType().getId();     //returns a string depicting the semantic type
        
        
        
        
	}
	
	@Override
	public Object visit(NumberExpr expr)       //ron changed saturday!!!!!!!!!!!!!!!!!!!!
	{
		
		
		expr.setType(new SemanticType("int"));
//		Tab( treeDepth );
//		Printer("["+expr.CreationLine+"]"+"Integer literal: "+expr.value);
		//return null;        //
		return expr.value;               //ron changed saturday!!!!!!!!!!!!!!!!!!!!!
	}
	
	
	@Override
	public Object visit(UnaryOpExpr expr) 
	{
	//	expr.setType(new SemanticType("int"));
		
		expr.operand.accept(this); // Ron changed satuday !!!!!!!!!!!!!!!!!!!!
		
		//if(expr.operand.getType() == null)
			//System.out.println("in visit UnaryOpExp and expr.operand.getType()== null");
		
		if(expr.operand.getType() != null){  // Ron changed satuday !!!!!!!!!!!!!!!!!!!!
			String exprId="";
			if(expr.operand instanceof CallExpr){
				CallExpr ce = (CallExpr)expr.operand;
				Method m = Utilities.findMethodInExtendsClassByName(ce.funcName);
				if(m!=null)
					exprId=m.funcType.typeName;
			}
			else
				exprId = expr.operand.getType().getId();
			if(expr.op.compareTo(Operator.MINUS) == 0){
				if(!(exprId.equals("int")) ){
					try {
						throw new SemanticError("The  Unary operation - must get an operand  of a int type", expr.CreationLine );
					} catch (SemanticError e) {
						Printer( e.getErrMessage() );
						System.exit(1);
					}
				}
				else{
					expr.setType(new SemanticType("int"));
					//int numberVal = (int)expr.operand.accept(this); // Ron changed satuday !!!!!!!!!!!!!!!!!!!! 
					
					
					
				}
			}else if( expr.op.compareTo(Operator.LNEG) == 0 ){
				if(!(exprId.equals("boolean")) ){
					try {
						throw new SemanticError("The  Unary operation ! must get an operand  of a boolean type", expr.CreationLine );
					} catch (SemanticError e) {
						Printer( e.getErrMessage() );
						System.exit(1);
					}
				}else{
					expr.setType(new SemanticType("boolean"));
				}
			}
			
		}
		//return expr.operand.accept(this);
		//return null;   
		return expr.operand.accept(this);          //ron changed saturday!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	@Override
	public Object visit(NewArrayExpr expr) 
	{
		//ron adds:
		//System.out.println("im in vist of NewArrayExpr in SemanticCheck.java");
		
		//we need to make sure that expr.arrayLength is of type int
		expr.arrayType.accept(this);
		expr.arrayLength.accept(this);
		
		
///////////////////////              ron  adds saturday at 1 oclock !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		if(expr.arrayLength.getType() != null){
			String arrayLengthType = expr.arrayLength.getType().getId();
			if( !arrayLengthType.equals("int")){
				try {
					throw new SemanticError("Semantic Error: Length of array should be of int type", expr.arrayLength.CreationLine );
				} catch (SemanticError e) {
					Printer( e.getErrMessage() );
					System.exit(-1);
				}
			}//else{
				//expr.setType(new SemanticType("int"));
			//}
		}
		return null;
	}
	@Override
	public Object visit(newObject expr) 
	{
		
		if(!Utilities.classTable.containsKey(expr.className))
		{
			try {
				throw new SemanticError("class "+expr.className+" is undefined", expr.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
				System.exit(-1);
			}
		}
		
		
		//System.out.println("im here at the end of func");
		return null;
	}
	@Override
	public Object visit(NullExpr expr) 
	{
//		Tab( treeDepth );
//		Printer("["+expr.CreationLine+"]"+"Null literal");
		return null;
	}
	@Override
	public Object visit(QuoteExpr expr) 
	{
		expr.setType(new SemanticType("string"));

//		Tab( treeDepth );
//		Printer("["+expr.CreationLine+"]"+"String literal:"+"\""+expr.quote+"\"");
		return null;
	}
	@Override
	public Object visit(ThisObject expr) 
	{
		// This object can only be reference inside a class scope
		if( !expr.getScopeTable().lookupScopeID("Class"))
		{
			try {
				throw new SemanticError( ".this expression used outside of a class", expr.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
			}
		}
		
		expr.type = new SemanticType( SymbolTable.findEnclosingClass( expr.getScopeTable() ), true  );
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
	public Object visit(ClassDecl classDecl) 
	{
		classDecl.bodyList.accept(this);
		return null;
	}

	@Override
	public Object visit(FormalList formalList) {
		for( int i = formalList.formals.size() - 1 ; i >=0 ;i-- )
		{	
			Formal f = formalList.formals.get(i);
			f.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(Formal formal) 
	{
		return null;
	}

	@Override
	public Object visit(Type type) 
	{
//check for class type if its defined 		
		if (type.type == sym.CLASS_ID){
			if (!Utilities.classTable.containsKey(type.typeName)){
				try {
					throw new SemanticError( "Class " + type.typeName + " isn't defined", type.CreationLine );
				} catch (SemanticError e) {
					System.out.println(e.getErrMessage());
					System.exit(-1);
				}	
			}
		}
		// Resereved for typing
		return null;
	}

	@Override
	public Object visit(Field field) 
	{
		field.type.accept(this);
		return null;
	}

	@Override
	public Object visit(Method method) 
	{
		// Can only declare methods inside classes
		if( !method.getScopeTable().lookupScopeID("Class"))
		{
			try {
				throw new SemanticError( "function "+method.funcName+" defined outside of a class", method.CreationLine );
			} catch (SemanticError e) {
				Printer( e.getErrMessage() );
				System.exit(1);
			}
		}
		if( method.funcType != null )
			method.funcType.accept(this);
		
		if( method.formalList != null )
			method.formalList.accept(this);
		
		if( method.stmtList != null )
			method.stmtList.accept(this);
		// function returns a type and it has return stmt
		//itay
		if(method.isVoid)
			return null;
		if(method.MethodhasReturnStmt())
			return null;
		if(method.checkReturnPaths())
			return null;
		try {
			throw new SemanticError( method.funcName + " must have return values at every path", method.CreationLine );
		} catch (SemanticError e) {
			System.out.println(e.getErrMessage());
			System.exit(-1);
		}	
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
	public Object visit(IDList idList) 
	{
		return null;
	}

	@Override
	public Object visit(FuncArgsList funcArgsList) {
		for( int i = funcArgsList.expressions.size() - 1 ; i >=0 ;i-- )
		{
			funcArgsList.expressions.get(i).accept(this);
		}
		return null;
	}

	@Override
	public Object visit(ClassBody classBody) {
		if( classBody.field != null )
		{
			classBody.field.accept(this);
		}
			
		if( classBody.method != null )
		{
			classBody.method.accept(this);
		}
			
		return null;
	}

	@Override
	public Object visit(BooleanExpr expr) 
	{
		expr.setType(new SemanticType("boolean"));

//		Tab( treeDepth );
//		Printer("["+expr.CreationLine+"]"+"Boolean literal: "+expr.value);
		return null;
	}
	
	@Override
	public Object visit(CallExpr expr) 
	{
		if( expr.e != null )
			expr.e.accept(this);
		
		if( expr.args_list != null)
		{
			expr.args_list.accept(this);
		}
		
		String checkResult = Utilities.ValidateFunctionCall( expr.getScopeTable(), expr.classID, expr.funcName, expr.e, expr.args_list, expr);
		if( checkResult != "")
		{
			try {
				throw new SemanticError( checkResult, expr.CreationLine );
			} 
			catch (SemanticError e) 
			{
				Printer( e.getErrMessage() );
				System.exit( -1 );
			}
		}
		
		return null;
	}
	

	@Override
public Object visit(LengthOfArray expr) {          //ron changed saturday !!!!!!!!!!!!!!!!!!!!!!
		
		expr.arrayExpr.accept(this);
		
		if(expr.arrayExpr.getType() != null){
			//need to check if expr is of type int.   Otherwise we have an error
			String exprType = expr.arrayExpr.getType().getId();
			if(exprType != null){
				if(!exprType.contains("array")){
					try {
						throw new SemanticError("expression must be an array", expr.arrayExpr.CreationLine );
					} catch (SemanticError e) {
						System.out.println(e.getErrMessage());
						System.exit(-1);
					}	
				}else{
					expr.setType(new SemanticType("int"));
				}
			}
		}
		
		//System.out.println("hereeeee" + expr.getType().getId());
		
		
		return null;
	}
	
}
