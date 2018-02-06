package SemanticAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sound.midi.Instrument;

import LIR.Instruction;
import LIR.LIRGenerator;
import LIR.LIRProgram;
import LIR.Register;
import LIR.Variable;
import expr.BinaryOpExpr;
import expr.CallExpr;
import expr.Expr;
import expr.LocationExpr;
import slp.ClassBody;
import slp.ClassDecl;
import slp.Formal;
import slp.FormalList;
import slp.FuncArgsList;
import slp.Method;
import slp.PropagatingVisitor;
import slp.sym;
import stmt.CallStmt;
import stmt.IfStmt;
import stmt.Stmt;

public class Utilities 
{
	// A printer function to print stuff to the console and a file
	private static PrintWriter 	writer = null;
	private static String file;
	// A list of classes declared in this program
	public static  HashMap<String,ClassDecl> classTable = new HashMap<String,ClassDecl>(); 

	/**
	 * A bunch of functions to make printing easier
	 */
	public static void setUpPrinter( String fileName ) throws FileNotFoundException
	{
		
		writer = new PrintWriter( fileName );
		file = fileName;
	}
	
	public static void printResult() throws FileNotFoundException, IOException{
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			   String line = null;
			   while ((line = br.readLine()) != null) {
			       System.out.println(line);
			   }
			}

	}
	
	public static void closePrinter( String fileName ) throws FileNotFoundException
	{
		writer.close();
	}
	public static void Printer(Instruction s)
	{
		if(s!=null)
			Printer(s.toString());
	}
	
	public static void Printer( String s )
	{
		System.out.println( s );
		
		if( writer != null )
			writer.println( s );
	}


	
	public static void PrintToStart(String s) throws IOException{
		String contents = readFileIntoString(file);
		
		// delete all files
		PrintWriter writer = new PrintWriter(file);
		writer.print("");
		writer.print(s);
		writer.print(contents);
		writer.close();
		
		
		
	}
	public static String readFileIntoString( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }
	    reader.close();
	    return stringBuilder.toString();
	}
	
	
	public static String getTypeOfArray(String typeName) {
		String [] words = typeName.split(" ");
		return words[words.length-1];
	}
	
	public static boolean isMain( Method m )
	{
		if( m != null  )
		{
			if (m.formalList != null && m.formalList.formals.size() > 0 ){
			Formal f = m.formalList.formals.get(0);
			if( m.funcName.contentEquals("main") 
					&& m.isStatic 
					&& m.isVoid 
					&& f.type.typeName.contentEquals("1 dimensional array of string") 
					&& f.id.contentEquals("args") )
				return true;
				
		}
		}
		return false;
	}
	

	public static String ValidateLibraryCall( SymbolTable methodScope, String methodName, FuncArgsList methodArguments , CallExpr methodCallExpr )
	{
		// If exists
		String returnType = "";
		
		// For call expressions
		boolean isClassReturn = false;
		
		Method m = Utilities.findMethodInClass( methodName, "Library" );
		
		// Method has to be static to be able to be called like that
		if( m == null )
		{		
			return "static function "+methodName+" is undefined in Library class";
		}
		else
		{
			if( !m.isStatic )
			{
				return "Trying to static call a non static function "+methodName;
			}
			
			// Do an arguments check
			if( !checkValidArgs( m.formalList, methodArguments ))
			{
				return "In function:"+methodName+" in class: Library, arguments do not match the declaration";
			}
			
			returnType = m.funcType.typeName;
			isClassReturn =  ( m.funcType.type == sym.CLASS_ID ) ? true : false;
		}
		
		if( methodCallExpr != null )
		{
			methodCallExpr.type = new SemanticType(returnType, isClassReturn );
		}
		
		return "";
	}
	public static String ValidateFunctionCall( SymbolTable methodScope, String classID, String methodName, Expr e , FuncArgsList methodArguments , CallExpr methodCallExpr ) 
	{		
		// If exists
		String returnType = "";
		
		// For call expressions
		boolean isClassReturn = false;
		
		// if stmt.classID is not null, this is a static function call
		if( classID != null )
		{
			if( classID.contentEquals("Library") )
			{
				return ValidateLibraryCall( methodScope, methodName , methodArguments , methodCallExpr );
			}
			Method m = Utilities.findMethodInClass( methodName, classID );
			
			// Method has to be static to be able to be called like that
			if( m == null )
			{		
				return "static function "+methodName+" is undefined in "+classID;
			}
			else
			{
				if( !m.isStatic )
				{
					return "Trying to static call a non static function "+methodName;
				}
				
				// Do an arguments check
				if( !checkValidArgs( m.formalList, methodArguments ))
				{
					return "In function:"+methodName+" arguments do not match the declaration";
				}
				
				returnType = m.funcType.typeName;
				isClassReturn =  ( m.funcType.type == sym.CLASS_ID ) ? true : false;
			}
			
		}
		// This is a virtual call then, either we are calling from within a class scope, or referencing a different class scope with an expression
		else if( e != null )
		{
			// If stmt.e is not null, e is a variable of a certain class instance, so we simply look for the function in the class
			SemanticType t = e.getType();
			if( t != null )
			{
				if( t.isClassInstance )
				{
					Method m = Utilities.findMethodInClass( methodName, t.getId() );
					
					// look for the function in the specific class
					if( m == null )
					{		
						return "function "+methodName+" is undefined in "+classID;
					}
					else
					{
						// Do an arguments check
						if( !checkValidArgs( m.formalList, methodArguments ))
						{
							return "In function:"+methodName+" arguments do not match the declaration";
						}
						
						returnType = m.funcType.typeName;
						isClassReturn =  ( m.funcType.type == sym.CLASS_ID ) ? true : false;
					}
				}
			}
		}
		else
		{
			// Try to find the method in the current class , if not found, try finding it in the base class, it it exists
			if( !methodScope.parentTable.lookupSymbol( methodName ) )
			{
				// Check for a method of this name in a base class, if a base class exists
				String baseClassName = classTable.get( SymbolTable.findEnclosingClass( methodScope ) ).extendedClassName;
				if( baseClassName != null )
				{
					if( !methodScope.parentTable.lookupSymbol( "_"+baseClassName+"_"+methodName ) )
					{
						// Did not find a base class method of this name, so method symbol is undefined
						return "function "+methodName+" is undefined in "+classID;
					}
					else
					{
						// There is a base class method of this name, check if the arguments fit
						Method m = Utilities.findMethodInClass( methodName, baseClassName );
						
						// Do an arguments check
						if( !checkValidArgs( m.formalList, methodArguments ))
						{
							// Arguments do not fit, return an error
							return "In function:"+methodName+" arguments do not match the declaration";
						}
						
						returnType = m.funcType.typeName;
						isClassReturn =  ( m.funcType.type == sym.CLASS_ID ) ? true : false;
					}
				}
				else
				{
					return "function "+methodName+" is undefined in "+classID;
				}
			}
			else
			{
				String enclosingClassName = SymbolTable.findEnclosingClass( methodScope );
				Method m = Utilities.findMethodInClass( methodName, enclosingClassName );
				
				// Do an arguments check
				if( !checkValidArgs( m.formalList, methodArguments ))
				{
					// Arguments might not be valid, try finding a base class function
					// Check for a method of this name in a base class, if a base class exists
					String baseClassName = classTable.get( SymbolTable.findEnclosingClass( methodScope ) ).extendedClassName;
					if( baseClassName != null )
					{
						if( !methodScope.parentTable.lookupSymbol( "_"+baseClassName+"_"+methodName ) )
						{
							// Did not find a base class method of this name, so method symbol is undefined
							return "function "+methodName+" is undefined in "+classID;
						}
						else
						{
							// There is a base class method of this name, check if the arguments fit
							m = Utilities.findMethodInClass( methodName, baseClassName );
							
							// Do an arguments check
							if( !checkValidArgs( m.formalList, methodArguments ))
							{
								// Arguments do not fit, return an error
								return "In function:"+methodName+" arguments do not match the declaration";
							}
							
							returnType = m.funcType.typeName;
							isClassReturn =  ( m.funcType.type == sym.CLASS_ID ) ? true : false;
						}
					}
					else
					{
						return "In function:"+methodName+" arguments do not match the declaration"; 
					}
				}
				
				returnType = m.funcType.typeName;
				isClassReturn =  ( m.funcType.type == sym.CLASS_ID ) ? true : false;
			}
		}
		if( methodCallExpr != null )
		{
			methodCallExpr.type = new SemanticType(returnType, isClassReturn );
		}
		
		// If nothing bad happened until now, there are no errors so return an empty string
		return "";
	}
	
	private static boolean checkValidArgs(FormalList formalList, FuncArgsList args_list) {
		if(formalList==null && args_list==null)
			return true;
		if((formalList==null && args_list!=null) || (formalList!=null && args_list==null))
			return false;
		if(formalList.formals.size()==args_list.expressions.size()){
			for(int i=0;i<formalList.formals.size();i++){
				String typeA =formalList.formals.get(i).type.typeName;
				String typeB = args_list.expressions.get(i).getType().getId();
				if(!typeA.equals(typeB)){
					if(!SymbolTableTreeBuilder.AInhertFromB(typeB, typeA) )
						return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean compareFormalLists( FormalList method1, FormalList method2 )
	{
		// Firstly, check if there is a formal list, obviosuly if both methods are m(), the formal lists are equal
		if( (method1 == null && method2 != null) || (method1 != null && method2 == null) )
			return false;
		else if( method1 == null && method2 == null )
			return true;
		
		// Secondly check by length, if the lengths are different, they are obviously different
		if( method1.formals.size() != method2.formals.size() )
			return false;
		else
		{
			// If the sizes are the same, time to check types and names, its enough to be different in at least one
			for( int i = 0 ; i < method1.formals.size() ; i++ )
			{
				Formal m1Formal = method1.formals.get(i);
				Formal m2Formal = method2.formals.get(i);
				
				if( m1Formal.type.type != m2Formal.type.type )
					return false;
				/*
				 // itay
				 we just need to check types
				if( !m1Formal.id.contentEquals( m2Formal.id ) )
					return false;
				*/
			}
		}
		return true;
	}
	public static boolean findMethod( String methodName, String className )
	{
		ClassDecl decl = classTable.get(className);
		if( decl.bodyList.getScopeTable().lookupSymbol(methodName) )
			return true;
		else return false;
	}
	
	public static Method findMethodInClass( String methodName, String className ) 
	{
		ClassDecl decl = classTable.get(className);
		if (decl != null){
		for(ClassBody cb : decl.bodyList.clList)
		{
			if( cb.method != null )
			{
				if(cb.method.funcName.equals(methodName))
					return cb.method;
			}
		}
		}
		return null;
	}
	
	public static Instruction generateVirtualCallInstruction( LIRProgram lirprog,
														String methodName, 
														FuncArgsList args_list,
														LIRGenerator lirGenerator,
														String classType, 
														Register objectInstance )
	{	
		// Get the index of the method in the dispatch vector
		int indexOfMethod = objectInstance.retrieveVarIndex( methodName );
		
		// Prepare the call operation
		String op1 = objectInstance.name+"."+indexOfMethod+"(";
		
		// Find the method object for the formal list
		Method m = Utilities.findMethodInClass( methodName, classType );
		int argIndex = 0;
		
		// Create registers for every function parameter
		if( args_list != null )
		{
			for( int i = 0;  i < args_list.expressions.size() ; i++)
			{
				Expr e = args_list.expressions.get(i);
				Register eReg = new Register();
				lirprog.registerStack.push( eReg );
				eReg = (Register)e.accept(lirGenerator, eReg);
			
				// Once the register is ready, assign it as a function parameter
				op1 += m.formalList.formals.get( argIndex ).id+"="+eReg.name;
				if (i < args_list.expressions.size()-1){
					op1 += ",";
				}
				argIndex++;
			}
		}
		op1 += ")";
		
		// Add a return value, if any
		Register returnReg = null;
		if( m.isVoid )
			returnReg = new Register( "dummy" );
		else 
		{
			returnReg = new Register();
			returnReg.heldValue = new Variable("ret_"+methodName, null);
			if( m.funcType.type == sym.CLASS_ID )
			{
				returnReg.isClassInstance = true;
				returnReg.generateVariableList( m.funcType.typeName );
			}
			lirprog.registerStack.push( returnReg );
		}
		String op2 = returnReg.toString();
		
		// Create the call instruction
		Instruction callInstr = new Instruction("VirtualCall", op1, op2 );
		return callInstr;
	}
	
	public static Instruction generateStaticCallInstruction( LIRProgram lirprog,
			String methodName, 
			FuncArgsList args_list,
			LIRGenerator lirGenerator,
			String classType
			)
	{	
		// Prepare the call
		String op1 = null;
		boolean isLibrary = classType.contentEquals("Library");
		if(  isLibrary)
		{
			op1 = "__"+methodName+"(";
		}
		else
		{
			op1 = "_"+classType+"_"+methodName+"("; 
		}
			
		
		// Find the method object for the formal list
		Method m = Utilities.findMethodInClass( methodName, classType );
		int argIndex = 0;
		
		// Create registers for every function parameter
		if( args_list != null  )
		{
			for( Expr e : args_list.expressions )
			{
				Register eReg = new Register();
				lirprog.registerStack.push( eReg );
				eReg = (Register)e.accept(lirGenerator, eReg);
			
				if( eReg.isClassInstance )
				{
					// Similar to what we do in binaryOpExpr, for some annoying reason
					// we cannot use a class field directly and pass it
					// to a library function, but must store it in a temp
					// register, this is was we do here
					Register tempFieldRegister = new Register();
					lirprog.registerStack.push(tempFieldRegister);
					Instruction inst = new Instruction( "MoveField", eReg.name, tempFieldRegister.name );
					Utilities.Printer( inst );
						
					eReg = tempFieldRegister;
				}
				
				// Once the register is ready, assign it as a function parameter
				if (!isLibrary)
				{
					op1 += m.formalList.formals.get( argIndex ).id + "=" + eReg.name;
				}
				else 
				{
					op1 += eReg.name;
				}
				argIndex++;
			}
		}

		op1 += ")";
		
		// Add a return value, if any
		Register returnReg = null;
		if( m.isVoid )
			returnReg = new Register( "dummy" );
		else 
		{
			returnReg = new Register();
			returnReg.heldValue = new Variable("ret_"+methodName, null);
			if( m.funcType.type == sym.CLASS_ID )
			{
				returnReg.isClassInstance = true;
				returnReg.generateVariableList( m.funcType.typeName );
			}
			lirprog.registerStack.push( returnReg );
		}
		String op2 = returnReg.toString();
		
		String callString = ( classType.contentEquals("Library") ) ? "Library " : "StaticCall";
		// Create the call instruction
		Instruction callInstr = new Instruction(callString, op1, op2 );
		return callInstr;
	}
	
	public static Register evaluateBooleanExpression( BinaryOpExpr expr , Register left, Register right)
	{
		Register resultRegister = new Register();
		
		Instruction booleanRes = null;
		Instruction booleanJump = null;
		Instruction booleanSet = null;
		

		// After the comparison, perform a jump only if the expression evaluates to false
		// If it evaluates to true, assign the result register to true
		String jumpToLabel = "";
		String jumpType    = "";
		switch( expr.op )
		{
		case LTE: // <=
			jumpToLabel = "_lte";
			jumpType	= "G";
			break;
		case LT: // <
			jumpToLabel = "_lt";
			jumpType	= "GE";
			break;
		case GTE: // >= 
			jumpToLabel = "_gte";
			jumpType	= "L";
			break;
		case GT: // > 
			jumpToLabel = "_gt";
			jumpType	= "LE";
			break;
		case EQUAL: // ==
			jumpToLabel = "_eq"; // x == y iff x - y = 0, but 0 means false
			jumpType	= "True"; // therefore, x != y iff x - y > 0, i.e JumpTrue
			break;
		case NEQUAL: // !=
			jumpToLabel = "_neq";
			jumpType	= "False";
			break;
		case LAND: // &&
			booleanRes = new Instruction("And",right.name , left.name);
			booleanSet = new Instruction("Move",left.name,resultRegister.name);
			Utilities.Printer( booleanRes );
			break;
		case LOR: // ||
			booleanRes = new Instruction("Or",right.name , left.name);
			booleanSet = new Instruction("Move",left.name,resultRegister.name);
			Utilities.Printer( booleanRes );
			break;
		default:
			break;
		}
		
		if( !jumpToLabel.contentEquals("") )
		{
			// Always assume the expression is false at first
			booleanSet = new Instruction("Move","0",resultRegister.name);
			booleanRes = new Instruction("Compare",right.name , left.name);
			
			Utilities.Printer( booleanSet );
			Utilities.Printer( booleanRes );
			
			booleanJump = new Instruction("Jump"+jumpType,jumpToLabel,null);
			Utilities.Printer( booleanJump );
			booleanSet = new Instruction("Move","1",resultRegister.name);
		}
		
		// We either set the result register to 1, or to the result of an and , orr
		Utilities.Printer( booleanSet );
		
		if( !jumpToLabel.contentEquals("") )
		// the label we jumped to if the expression was false
			Utilities.Printer( jumpToLabel+":" );
		
		return resultRegister;
	}
	//itay	
	public static String getArrayID(Expr e1) {
		try{
			LocationExpr e = (LocationExpr)e1;
			if(e.ID!=null)
				return e.ID;
			return getArrayID(e.e1);
		}
		catch(Exception exe){
			return null;
		}
	}
	
	public static boolean accessToArray(LocationExpr locationExpr) {
		return  (locationExpr.e1 != null && locationExpr.e2 != null && locationExpr.ID == null);
		
	}

	public static Method findMethodInExtendsClass(String funcName,String className) {
		Method ret= Utilities.findMethodInClass(funcName, className);
		if(ret == null){
			ClassDecl cd = Utilities.classTable.get(className);
			if(cd.extendedClassName!=null){
				return findMethodInExtendsClass(funcName,cd.extendedClassName);
			}
			return null;
		}
		return ret;
	}

	public static Method findMethodInExtendsClassByName(String funcName) {
		for(ClassDecl cd :Utilities.classTable.values()){
			Method ret= Utilities.findMethodInClass(funcName, cd.className);
			if(ret!=null)
				return ret;
		}	
		return null;
	}
}
