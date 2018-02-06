package LIR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import SemanticAnalysis.Utilities;

public class LIRProgram {
	
	public Map<String,DispatchVector> dv_Map; // DV_A_ [_A_xxxx, _A_yyy]
	public ArrayList<LIRString>		  StringLiteralList; 
	
	public List<Instruction>		  instrList;  
	public Stack<Register> 		      registerStack;
	public Stack<StmtLabel>			  whileLabelStack;
	
	//public Map<Stri
	public LIRProgram()
	{
		dv_Map = new HashMap< String, DispatchVector >();
		StringLiteralList = new ArrayList<LIRString>();
		registerStack = new Stack<>();
		whileLabelStack = new Stack<>();
	}
	
	public void   popScope()
	{
		// Start popping until we reach the first dummy register
		while( !registerStack.pop().name.contentEquals("dummy")) { Register.decrementRegisterNumber(); }
	}

	public StmtLabel peekEnclosingWhile(){
		return whileLabelStack.peek();
	}
	public void 	 popLatestWhileLabel(){
		whileLabelStack.pop();
	}
	public Register findVariableRegister( String varName )
	{
		// Will look for a register in the stack which equals the variable name
		// Iterate the stack in a reverse list order, to iterate from the top of the stack to the bottom
		for( int i = registerStack.size() - 1 ; i >= 0 ; i-- )
		{
			Register r = registerStack.get( i );
			//itay
			if( r.heldValue != null && r.heldValue.name != null)
			{
				if( r.heldValue.name.contentEquals( varName ) )
					return r;
			}
		}
		
		return null;
	}
	public String ToStringDVMap()
	{
		String result = "";
		result += "###############################\n";
		result += "# Dispatch Vectors\n";
		for( DispatchVector dv : dv_Map.values() )
		{
			result += dv +"\n";
		}
		result += "###############################\n";
		
		return result;
	}
	
	public String ToStringLIRStringMap()
	{
		String result = "";
		for (int i =0; i<StringLiteralList.size();i++ ){
			result = result + StringLiteralList.get(i).toString() + "\n";
		}
		
		return result;
	}
	
	

}
