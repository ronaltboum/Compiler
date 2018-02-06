package SemanticAnalysis;

import slp.ClassDecl;

public class SemanticType {
	private String id;
	public boolean isClassInstance = false;
	
	public boolean isClass() {
		return isClassInstance;
	}

	public void setClass(boolean isClass) {
		this.isClassInstance = isClass;
	}

	public SemanticType(String id){
		this.id = id;
	}
	
	public SemanticType(String id,boolean isClass){
		this.id = id;
		this.isClassInstance = isClass;
	}
	
	public boolean equals(String comp){
		
		//System.out.println("i'm here at equals(String comp)");
		
		return this.id.equals(comp); // in case of confusion
	}
	public boolean equals(SemanticType comp){
		
		return this.id.equals(comp.id); // in case of confusion
	}
	
	
	public String getId(){
		return id;
	}

	
	
}
