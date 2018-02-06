package SemanticAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.rmi.CORBA.Util;

import slp.ClassDecl;
import slp.Method;

public class SymbolTable {
	
	public  LinkedHashMap <String,Symbol> Table;
	public  String             scopeID;
	public  SymbolTable        parentTable;
	
	public  boolean 		   isClassTable;
	public  boolean			   isMethodTable;
	public  boolean            isStmtTable;
		
	
	public SymbolTable( String sID )
	{
		setScopeID(sID);
		Table = new LinkedHashMap <String,Symbol>();
		parentTable = null;
	}
	
	public SymbolTable( String sID , SymbolTable pTable )
	{
		setScopeID(sID);
		Table = new LinkedHashMap <String,Symbol>();
		parentTable = pTable;
	}
	
	public void addSymbol( Symbol s )
	{
	//	if (!Table.containsKey(key))
			Table.put(s.getSymbolName(), s);
			
	}
	
	/**
	 * Finds whether a symbol exists based on a symbol name
	 * @param key
	 * @return
	 */
	public boolean lookupSymbol( String key )
	{
		if( !Table.containsKey(key) )
		{
			if( parentTable != null )
			{
				return parentTable.lookupSymbol(key);
			}
			else return false;
		}
		else return true;
			
	}
	
	/**
	 * Retrieves a symbol from the current scope or a parent scope, if the symbol exists
	 * @param key
	 * @return
	 */
	public Symbol getSymbol( String key )
	{
		if( !Table.containsKey(key) )
		{
			if( parentTable != null )
			{
				return parentTable.getSymbol(key);
			}
			else return null;
		}
		else return Table.get(key);
			
	}
	
	/**
	 * Finds a certain scope by its ID
	 * @param id
	 * @return
	 */
	public boolean lookupScopeID( String id )
	{
	
		if( !scopeID.contains(id) )
		{
			if( parentTable != null )
			{
				
				return parentTable.lookupScopeID(id);
			}
			else return false;
		}
		else return true;
	}
	
	/**
	 * Looks for a symbol in the hierarchy, usually used to check for double definition
	 * @param s
	 * @return
	 */
	public boolean lookupSymbol( Symbol s )
	{
		if( !Table.containsKey(s.getSymbolName()) )
		{
			if( parentTable != null )
			{
				return parentTable.lookupSymbol(s);
			}
			else return false;
		}
		else
		{
			Symbol st = Table.get(s.getSymbolName()); // in case of same name, check for same kind
			if( st.getSymbolKind().equals(s.getSymbolKind()) || (st.getSymbolKind().equals(Kind.FIELD) && s.getSymbolKind().equals(Kind.VARDECL) )
					|| (st.getSymbolKind().equals(Kind.VARDECL) && s.getSymbolKind().equals(Kind.FIELD))){
					
					return true;
			}
			else return false;
		}
			
	}
	
	public static SymbolTable 	  findClassSymbolTable( String className )
	{
		return Utilities.classTable.get(className).bodyList.getScopeTable();
	}
	
	/**
	 * If a class A inherits from class B, he should be able to access all the symbols defined in B, or overide existing ones
	 * @param baseTable
	 * @param derivedTable
	 */
	public static void			  copyBaseTableToDerivedTable( SymbolTable baseTable, SymbolTable derivedTable , String fatherClassName )
	{
		// If this class derives from a base class, we need to copy all the symbols from the base class
		for( Symbol s : baseTable.Table.values() )
		{
			// Find the same symbol name in the derived class table
			Symbol sInDerived = derivedTable.Table.get( s.getSymbolName() );
			
			// Check if we want to add a FIELD kind symbol, fields cannot be overidden with the same name
			if( s.getSymbolKind() == Kind.FIELD )
			{
				if( sInDerived != null )
				{
						// This is a semantic error, a function cannot overide a base class field 
						try {
							throw new SemanticError( "Cannot override a base class field: "+s.getSymbolName() );
						} catch (SemanticError e) {
							System.out.println(e.getErrMessage());
							
						}
				}
				
			    else {
			    	s.fatherClassName = fatherClassName;
			    	s.inherted = true;
			    	derivedTable.addSymbol( s );
			    }
			}
			
			// Check if we want to add a METHOD kind symbol, methods can be overriden if they are not static
			// If the method in the derived class does not match exactly the same formal list and return type
			// we add the method from the base class and add a _CLASSNAME_ identifier to the symbol name
			if( s.getSymbolKind() == Kind.VMETHOD )
			{
				if( sInDerived != null )
				{	
					Method derivedMethodInfo = Utilities.findMethodInClass( sInDerived.getSymbolName(), derivedTable.determineClassNameFromID() );
					Method baseMethodInfo = Utilities.findMethodInClass( sInDerived.getSymbolName(), baseTable.determineClassNameFromID() );
					
					if( Utilities.compareFormalLists(derivedMethodInfo.formalList, baseMethodInfo.formalList) )
					{
						// If the formals match, what is left to check is the return type
						if( derivedMethodInfo.funcType.type == baseMethodInfo.funcType.type )
							derivedTable.addSymbol( s );
						else
						{
							try {
								throw new SemanticError( "Cannot override a base method with different return types: "+s.getSymbolName() );
							} catch (SemanticError e) {
								System.out.println(e.getErrMessage());
								System.exit(-1);
							}
						}
					}
					else
					{
						try {
							throw new SemanticError( "Cannot override a base method with different formals: "+s.getSymbolName() );
						} catch (SemanticError e) {
							System.out.println(e.getErrMessage());
							System.exit(-1);

						}
					}
					
				}
				else{
					s.fatherClassName = fatherClassName;
					s.inherted = true;
					derivedTable.addSymbol( s );
				}
				
				// If we didn't add the symbol to the derived class, it means the derived class overrides the base class function
			}
		}
	}
	
	public String  determineClassNameFromID()
	{
		if( isClassTable )
		{
			// If this table is a class symbol table, the scope id is of the form: Class_CLASSNAME_Scope, so we get the substring between the two "_"
			// Hardcoded 6 is the length of "Class_"
			return scopeID.substring( scopeID.indexOf("_") + 1, scopeID.indexOf("_", 6 ));
		}
		
	/*	else{
			return this.parentTable.determineClassNameFromID();
		}*/
		
		else return null;
	}
	
	public String  determineMethodNameFromID()
	{
		if( isMethodTable )
		{
			// If this table is a class symbol table, the scope id is of the form: Class_CLASSNAME_Scope, so we get the substring between the two "_"
			// Hardcoded 10 is the length of "VoidMethod_"
			return scopeID.substring( scopeID.indexOf("_") + 1, scopeID.indexOf("_", 10 ));
		}
		else return null;
	}

	
	public static String findEnclosingClass( SymbolTable currentScope )
	{
		if( !currentScope.isClassTable )
		{
			return findEnclosingClass( currentScope.parentTable );
		}
		else
		{
			return currentScope.determineClassNameFromID();
		}
	}
	
	public static String findEnclosingMethod( SymbolTable currentScope )
	{
		if( !currentScope.isMethodTable )
		{
			return findEnclosingMethod( currentScope.parentTable );
		}
		else
		{
			return currentScope.determineMethodNameFromID();
		}
	}
	
	public String getScopeID() {
		return scopeID;
	}

	public void setScopeID(String scopeID) {
		this.scopeID = scopeID;
	}
}
