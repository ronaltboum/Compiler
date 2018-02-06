package SemanticAnalysis;

import slp.Type;

public class Symbol {
	private String symbolName;
	private Kind   symbolKind;
	private SemanticType   type;
	public String fatherClassName = ""; // for inherted methods/ fields - so we can name them right
	public boolean inherted = false;
	public SemanticType getType() {
		return type;
	}

	public void setType(SemanticType type) {
		this.type = type;
	}

	public Symbol( String name, String typeName, Kind k )
	{
		symbolName = name;
		symbolKind = k;
		type = new SemanticType(typeName);
	}
	
	public String getSymbolName() {
		return symbolName;
	}
	public void setSymbolName(String symbolName) {
		this.symbolName = symbolName;
	}
	
	public Kind getSymbolKind() {
		return symbolKind;
	}
	public void setSymbolKind(Kind symbolKind) {
		this.symbolKind = symbolKind;
	}

}
