package slp;

import SemanticAnalysis.Utilities;
import stmt.CallStmt;
import stmt.IfStmt;
import stmt.NestedStmtList;
import stmt.ReturnStmt;
import stmt.Stmt;
import stmt.StmtList;

public class Method extends ASTNode {

	public final Type funcType;
	public final String funcName;
	public final Boolean isVoid;
	public final Boolean isStatic;
	public FormalList formalList;
	public final StmtList   stmtList;
	
	public Method( Type t, String fName, FormalList fList, Boolean isV, Boolean isS, StmtList list , int line_num )
	{
		super( line_num );
		if (t == null ){
			funcType = new Type(0,"void",line_num);
		}
		else{
			funcType = t;

		}
		funcName = fName;
		formalList = fList;
		isVoid = isV;
		isStatic = isS;
		stmtList = list;
	}
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);

	}


	public boolean MethodhasReturnStmt(){

			for (int i = 0; i <stmtList.statements.size();i++){
				Stmt stmt = stmtList.statements.get(i);
				if (stmt instanceof ReturnStmt ){
					ReturnStmt returnStmt = (ReturnStmt) stmt;
					if (returnStmt.expr != null){
						return true;
					}
				}
			
				if (stmt instanceof CallStmt){
					CallStmt callStmt = (CallStmt) stmt;
					String funcName = callStmt.funcName;
					String className = callStmt.classID; 
					Method m = Utilities.findMethodInClass(funcName, className);

					if (m.MethodhasReturnStmt()){
						return true;
					}
				}
			}
			return false;
	}
	
	public boolean checkIfReturnValueStmt(Stmt stmt){
		if (stmt == null){
		return false;	
		}
		if (stmt instanceof ReturnStmt ){
			ReturnStmt returnStmt = (ReturnStmt) stmt;
			if (returnStmt.expr != null){
				return true;
			}
		}
		if (stmt instanceof NestedStmtList ){
			NestedStmtList nestedStmt = (NestedStmtList) stmt;
			for(Stmt nestedSt :nestedStmt.stmtList.statements){
				if (nestedSt instanceof ReturnStmt ){
					ReturnStmt returnStmt = (ReturnStmt) nestedSt;
					if (returnStmt.expr != null){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean checkReturnPaths(){
		
		for (int i = 0; i <stmtList.statements.size();i++){
			Stmt stmt = stmtList.statements.get(i);
			//itay
			//System.out.println("here");

			if (stmt instanceof IfStmt ){
				IfStmt ifStmt = (IfStmt) stmt;
				if(!checkIfReturnValueStmt(ifStmt.s1) && !checkIfReturnValueStmt(ifStmt.s2)){
					return false;
			}
		}
		
	} 
		return true;
	}
	
	
	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}

}
