//package methodsTestCases;
//
//import org.junit.*;
//
//import ServerImpl.ConcordiaImpl;
//import ServerImpl.McGillImpl;
//import ServerImpl.MontrealImpl;
//
//public class MethodsTestCase {
//	private ConcordiaImpl concordiaImpl;
//	private McGillImpl mcgillImpl;
//	private MontrealImpl montrealImpl;
//	
//	@Before
//	public void before() {
//		concordiaImpl=new ConcordiaImpl();
//		mcgillImpl=new McGillImpl();
//		montrealImpl=new MontrealImpl();
//		mcgillImpl.addItem("MCGM1111", "MCG6231", "Distributed", 1);
//		concordiaImpl.addItem("CONM1111", "CON6231", "Distributed", 2);
//		montrealImpl.addItem("MONM1111", "MON6231", "Distributed", 5);
//		
//	}
//	@Test
//	void testAddItem() {
//		assertEquals(1,);
//		assertEquals(2, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
//		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6441").getQuantity());
//		assertEquals(5, montrealImpl.getMontrealBooksData().get("MON6231").getQuantity());
//	}
//
//}
