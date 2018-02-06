package SemanticAnalysis;

public class SemanticError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String errMessage;
	
	public SemanticError( String errM )
	{
		setErrMessage("[ICCompiler_SemError]: "+errM);
	}
	
	public SemanticError( String errM, int line )
	{
		setErrMessage("[ICCompiler_SemError] line "+line+": "+errM);
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

}
