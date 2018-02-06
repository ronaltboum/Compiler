package SemanticAnalysis;

public enum Kind {
	
	CLASS("Class"),
	VMETHOD("VirtualMethod"),
	SMETHOD("StaticMethod"),
	FIELD("Field"),
	PARAMETER("Parameter"),
	FORMAL("Formal"),
	VARDECL("Variable Declaration"); // statement
	
	private String kindName;
	
	Kind( String name )
	{
		setKindName(name);
	}

	public String getKindName() {
		return kindName;
	}

	public void setKindName(String kindName) {
		this.kindName = kindName;
	}

}
