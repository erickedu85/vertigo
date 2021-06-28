package app.utils;

public class TestParing {

	
	public static void main(String[] args) {
		int sourceMultiedge = 110017460;
		int targetMultiedge = 110017470;
		System.out.println(GraphUtil.pairingFunction(sourceMultiedge, targetMultiedge));
		System.out.println(GraphUtil.pairingFunction(targetMultiedge,sourceMultiedge));
		
		
		////EDGE ID FUNCTION
//		System.out.println(GraphUtil.edgeIdFunction2(sourceEdge,targetEdge,typeEdge));
//		System.out.println(GraphUtil.edgeIdFunction2(targetEdge,sourceEdge,typeEdge));
		
		
	}
	
}
