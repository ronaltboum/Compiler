package LIR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DispatchVector {
	public final String vName;
	public final List<MethodLabel> methodList;
	
	private static int numOfVectors;
	
	public DispatchVector(String name, List<MethodLabel> labels) {
		vName = name;
		methodList = reverse(labels);
		numOfVectors++;
	}
	
	public static int getNumberOfDispatchTables() {
		return numOfVectors;
	}
	
	public String toString() {
		String mList = "";
		
		return vName + ":" + methodList;
	}
	private List  reverse (List<MethodLabel> list){
		 List invertedList = new ArrayList();
		    for (int i = list.size() - 1; i >= 0; i--) {
		        invertedList.add(list.get(i));
		    }
		    return invertedList;
	}
}
