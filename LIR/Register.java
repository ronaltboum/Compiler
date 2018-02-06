package LIR;

import java.util.ArrayList;
import java.util.List;

import SemanticAnalysis.Kind;
import SemanticAnalysis.Symbol;
import SemanticAnalysis.SymbolTable;
import SemanticAnalysis.Utilities;
import slp.ClassBody;
import slp.ClassDecl;

public class Register extends Opr 
{
	// A reference to the variable this register will hold
	public Variable 	    heldValue;
	// Register name, starts with a "R"
	public String 		    name = "";
	// Serial number of this register
	public int 			    RegisterNumber;
	
	// This list is only created if the register holds a pointer to a created class object
	private List<String>  	createdObjectVars = null;
	public  boolean		 	isClassInstance = false;
	public  String			typeOfClassInstance;
	
	// This register might hold a pointer to an array
	public  boolean		 	isArrayInstance = false;
	
	// Updated for all registers to provide convinient naming
	private static int 		numberOfRegisters = 1;
	
	public Register()
	{
		
		RegisterNumber = numberOfRegisters;
		name = "R"+RegisterNumber;
		numberOfRegisters++;
	}
	
	public Register( Variable hVal )
	{
		
		RegisterNumber = numberOfRegisters;
		name = "R"+RegisterNumber;
		numberOfRegisters++;
		
		heldValue = hVal;

	}
	
	public Register( String name ) 
	{
		this.name = name;
		if (!name.equals("dummy")){
			numberOfRegisters++;
		}
	}
	
	/**
	 * Generates an indexed variable list for this register, only if this register holds 
	 * a reference to a created object of a certain class
	 * @param className
	 */
	public void generateVariableList( String className )
	{
		// 
		typeOfClassInstance = className;
		
		//
		createdObjectVars = new ArrayList<String>();
		ClassDecl cClass = Utilities.classTable.get( className );
		SymbolTable classTable = cClass.bodyList.getScopeTable();
		
		List<String> fieldList = new ArrayList<String>();
		List<String> methodList = new ArrayList<String>();
		
		// Fill up the lists, make sure its in a certain order
		for( Symbol sy : classTable.Table.values() ) 
		{
			if( sy.getSymbolKind() == Kind.FIELD )
			{
				fieldList.add( sy.getSymbolName() );
			}
			else if( sy.getSymbolKind() == Kind.VMETHOD )
			{
				// This is created in the same order that the dispatch table is created
				methodList.add( sy.getSymbolName() );
			}
		}
		
		// First add the methods
		for( String s : methodList ){
			createdObjectVars.add( s );
		}
		
		// Make sure the fields are stored after the methods in memory
		// this is because the dispatch table is always R#.0
		for( String s : fieldList ){
			createdObjectVars.add( s );
		}
	}
	
	public int retrieveVarIndex( String variableName )
	{
		if( createdObjectVars != null )
		{
			return createdObjectVars.indexOf( variableName );
		}
		else return -1;
	}
	
	/** Returns the total number of registers created so far.
	 */
	public static int getNumberOfRegisters() {
		return numberOfRegisters;
	}
	
	public static void decrementRegisterNumber(){
		numberOfRegisters--;
	}
	
	public String toString(){
		if (name.equals("dummy")){
			return "Rdummy";
		}
		else{
				return "R" + RegisterNumber;
		}
	}
}
