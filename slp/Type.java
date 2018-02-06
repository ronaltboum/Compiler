package slp;

public class Type extends ASTNode {
	public  int type; // 47 (Class_id)
	public String typeName; // A class Name
	public Type recursiveType;    //ron adds monday
	public String typeOfArray;      				//ron adds teusday
	public int arrayDimension;
	
		
	public Type( int t, String tName, int line_num )
	{
		super( line_num );
		type = t;
		typeName = tName;
		typeOfArray = tName;          				//ron adds teusday
		//System.out.println("in Type first const:  tName = "+tName);
	}
	
	
	//ron changed on teusday!!!:
		public Type( int t, String tName, Type ty, int line_num )
		{
			super( line_num );
			type = t;
		
			this.recursiveType = ty; 
			this.typeOfArray = ty.typeOfArray;          //ron adds teusday
			this.arrayDimension = ty.arrayDimension +1;
			//ron adds teusday!!!!!!!!!!!!!!!!!!!!!!
			this.typeName = String.valueOf(this.arrayDimension) + " dimensional array of " + this.typeOfArray ;
			
		}
		
		//ron adds monday:
		public void increaseArrayDimension (){
			++this.arrayDimension;
		}
	@Override
	public Object accept(Visitor visitor) {
		return visitor.visit(this);

	}

	@Override
	public <DownType, UpType> UpType accept(PropagatingVisitor<DownType, UpType> visitor, DownType context) {
		// TODO Auto-generated method stub
		return null;
	}

}
