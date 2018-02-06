package LIR;

public class StmtLabel {
	
	public final String stmtName;
	public final int	stmtNum;
	private static int 		numberOfStmtLabels = 1;
	
	public StmtLabel(String name) {
		this.stmtName = name;  
		stmtNum = numberOfStmtLabels;
		numberOfStmtLabels++;
	}
	
	public String toString()
	{
		return "_"+stmtName+"_"+stmtNum;
	}
	
	public String toStringEnd()
	{
		return "_"+stmtName+"_"+stmtNum+"_end";
	}
}
