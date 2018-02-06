package LIR;

import java.util.List;
import java.util.Map;

import SemanticAnalysis.Utilities;
import expr.Expr;
import expr.NumberExpr;
import slp.Formal;
import slp.FuncArgsList;
import slp.Method;

/**
 * THIS CLASS IS PROBABLY NOT NEEDED, DONT DELETE FOR NOW
 * 
 *
 */
public class VirtualCallLabel {
	public VirtualCallLabel(Method method, String classId, FuncArgsList args_list,LIRProgram lirProgram,String initalizedObjectName) {
		this.method = method;
		this.classId = classId;
		this.args_list = args_list;
		this.lirProgram = lirProgram;
		this.initalizedObjectName = initalizedObjectName;
	}
	Method method;
	String classId;
	FuncArgsList args_list;
	LIRProgram lirProgram;
	String initalizedObjectName;
	public String toString()
	{
		return classId;
		/*
		StringBuilder result = new StringBuilder();
		int offset = 0;
		//# f.shine(2) - context is the regiester that holds the value 2
		//VirtualCall R1.0(x=R2) --> needs to print this
		String classToLookFor = "_" + "DV" + "_" + classId;
		DispatchVector dispatchVector =  lirProgram.dv_Map.get(classToLookFor);
		for (int i =0 ; i<dispatchVector.methodList.size();i ++){
			if (dispatchVector.methodList.get(i).methodName.equals(method.funcName)){
				offset = i;
				break;
			}
		}
		List<Formal> formals = method.formalList.formals; // int x, int y, A a
		List<Expr> expressions = args_list.expressions; // 7,a,b
		//Register registerOfObject = lirProgram.objectRegiesterMap.get(initalizedObjectName);
		result.append("R1000"); //registerOfObject.toString()
		result.append(".").append(offset);
		for (int i = 0 ; i < args_list.expressions.size();i++){
			Expr expr = expressions.get(i);
			Register registerOfexpression;
			if (expr instanceof NumberExpr){
				NumberExpr numberExpr = (NumberExpr) expr;
				 //registerOfexpression = new Register();
				Instruction instruction = new Instruction("Move", "" +numberExpr.value, registerOfexpression.toString());
				Utilities.Printer(instruction);
			}
			else{
				// registerOfexpression = lirProgram.objectRegiesterMap.get(expressions.get(i));

			}
			result.append("(").append(formals.get(i).id).append("=").append(registerOfexpression.toString()).append(")");
		}
		
		
		return result.toString();
	}
	*/
	}
}
