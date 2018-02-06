package slp;

import SemanticAnalysis.SemanticType;
import expr.Expr;
import expr.New;
import slp.PropagatingVisitor;
import slp.Type;
import slp.Visitor;

//represnts an array expression node in AST
public class NewArrayExpr extends New {

	public final Type arrayType;
	public final Expr arrayLength;
	
	
	public NewArrayExpr( Type arrType, Expr arrLength , int line_num) {
		super( line_num );
		arrayType = arrType;
		arrayLength = arrLength;
		
		//ron adds:
			if (this.arrayType != null){
				//this.type = new SemanticType(this.arrayType.typeName);
				//System.out.println(this.arrayType.typeName);
				
				//ron exchanged the 2 lines above with the line below on Teusday:
				//we'll have to change this when we fix the slp cup to support more than one dimension
				
				if(this.arrayType.arrayDimension > 0){  //ron added friday
					this.type = new SemanticType(String.valueOf(this.arrayType.arrayDimension)+ "dimensional array of "+this.arrayType.typeName );
				}
				else
					this.type = new SemanticType("1 dimensional array of "+this.arrayType.typeName );
			}

		
	}
	
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/** Accepts a propagating visitor parameterized by two types.
	 * 
	 * @param <DownType> The type of the object holding the context.
	 * @param <UpType> The type of the result object.
	 * @param visitor A propagating visitor.
	 * @param context An object holding context information.
	 * @return The result of visiting this node.
	 */
	@Override
	public <DownType, UpType> UpType accept(
			PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		return visitor.visit(this, context);
	}

}
