package LIR;

import java.util.List;

import expr.Expr;
import expr.NumberExpr;
import slp.Formal;
import slp.FuncArgsList;
import slp.Method;

public class StaticCallLabel {
	public StaticCallLabel(Method method, String classId, FuncArgsList args_list) {
		this.method = method;
		this.classId = classId;
		this.args_list = args_list;
	}

	Method method;
	String classId;
	FuncArgsList args_list;
	
	public String toString(){
		String result = "";
		result = result + classId + "_" + method.funcName + "_" + "(";
		List<Formal> formals = method.formalList.formals;
		for(int i =0;i<formals.size();i++){
			Expr expr = args_list.expressions.get(i);
			
			if (expr instanceof NumberExpr){
				NumberExpr numberExpr = (NumberExpr) expr;
				result = result + formals.get(i).id + "=" + numberExpr;

			}
			if (i != formals.size()-1){
			result = result + ",";
			}
		}
		result = result + ")";
		
		return result;
	}
}
