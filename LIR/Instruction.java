package LIR;

public class Instruction {
	public Instruction(String instruction, String op1, String op2) {
		Instruction = instruction;
		Op1 = op1;
		Op2 = op2;
		Op3=null;
	}
	
	public Instruction(String instruction, String op1, String op2,String op3) {
		Instruction = instruction;
		Op1 = op1;
		Op2 = op2;
		Op3=op3;
	}

	String Instruction;
	String Op1;
	String Op2;
	String Op3;
	
	
	public String toString(){
		String result;
		result = (Op2 != null ) ? Instruction + " " + Op1 + "," + Op2 : Instruction + " " + Op1;
		//itay
		//moveArray Instruction
		if(Op3!=null){
			if(Instruction.equals("MoveArray2"))
				result = "MoveArray"+ " " + Op1 + "," + Op2 + "["   +Op3 + "]";
			else
				result = Instruction +" " + Op1 + "["   +Op2 + "]" + "," + Op3;
		}
		return result;
	}
	
	public static String sToString( String inst, String op1, String op2 )
	{
		return inst + " " + op1 + "," + op2;
	}
}