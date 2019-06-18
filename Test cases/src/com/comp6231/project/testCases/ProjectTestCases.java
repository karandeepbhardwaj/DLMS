package com.comp6231.project.testCases;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.comp6231.project.impl.ConcordiaLibraryImpl;
import com.comp6231.project.impl.McgillLibraryImpl;
import com.comp6231.project.impl.MontrealLibraryImpl;

public class ProjectTestCases {
	private ConcordiaLibraryImpl concordiaImpl;
	private McgillLibraryImpl mcgillImpl;
	private MontrealLibraryImpl montrealImpl;
	
	@Before
	public void setUpBeforeEach() {
		concordiaImpl = new ConcordiaLibraryImpl();
		mcgillImpl = new McgillLibraryImpl();
		montrealImpl = new MontrealLibraryImpl();
		mcgillImpl.addItem("MCGM1111", "MCG6231", "Distributed", 1);
		mcgillImpl.addItem("MCGM1111", "MCG6441", "Smiley", 3);
		concordiaImpl.addItem("CONM1111", "CON6231", "Distributed", 2);
		concordiaImpl.addItem("CONM1111", "CON6441", "APP", 1);
		montrealImpl.addItem("MONM1111", "MON6231", "Distributed", 5);
		montrealImpl.addItem("MONM1111", "MON6441", "JAVA", 2);
		
		
	}

	@Ignore
	public void testEverything() {
		assertEquals(1, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		assertEquals(2, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6441").getQuantity());
		assertEquals(1, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		montrealImpl.borrowItem("MONU1111", "CON6231");
		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
		montrealImpl.borrowItem("MONU1111", "MCG6231");
		assertEquals(0, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		montrealImpl.borrowItem("MONU1111", "CON6441");
		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6441").getQuantity());
	}
	
	@Test
	public void testAddItem() {
		assertEquals(1, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		assertEquals(2, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6441").getQuantity());
		assertEquals(5, montrealImpl.getMontrealBooksData().get("MON6231").getQuantity());
	}
	
	@Test
	public void testListItemAvailability() {
		assertEquals(6,mcgillImpl.getMcgillBooksData().size());
		assertEquals(7, concordiaImpl.getConcordiaBooksData().size());
		assertEquals(6, montrealImpl.getMontrealBooksData().size());
	}
	
	@Test
	public void testBorrowItem() {
		concordiaImpl.borrowItem("CONU1111", "CON6231");
		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
		mcgillImpl.borrowItem("MCGU1111", "MCG6231");
		assertEquals(0, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		montrealImpl.borrowItem("MONU1111", "MON6231");
		assertEquals(4, montrealImpl.getMontrealBooksData().get("MON6231").getQuantity());
	}
	
	@Test
	public void testReturnItem() {
		concordiaImpl.borrowItem("CONU1111", "CON6231");
		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
		mcgillImpl.borrowItem("MCGU1111", "MCG6231");
		assertEquals(0, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		montrealImpl.borrowItem("MONU1111", "MON6231");
		assertEquals(4, montrealImpl.getMontrealBooksData().get("MON6231").getQuantity());
		concordiaImpl.returnItem("CONU1111", "CON6231");
		assertEquals(2, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
		mcgillImpl.returnItem("MCGU1111", "MCG6231");
		assertEquals(1, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		montrealImpl.returnItem("MONU1111", "MON6231");
		assertEquals(5, montrealImpl.getMontrealBooksData().get("MON6231").getQuantity());
	}
	
	@Test
	public void testRemoveItems() {
		concordiaImpl.removeItem("CONM1111", "CON6231", 1);
		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
		concordiaImpl.removeItem("CONM1111", "CON6231", -1);
		assertEquals(false, concordiaImpl.getConcordiaBooksData().containsKey("CON6231"));
		mcgillImpl.removeItem("MCGM1111", "MCG6231", 1);
		assertEquals(0, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		mcgillImpl.removeItem("MCGM1111", "MCG6231", -1);
		assertEquals(false, mcgillImpl.getMcgillBooksData().containsKey("MCG6231"));
		montrealImpl.removeItem("MONM1111", "MON6231", 1);
		assertEquals(4, montrealImpl.getMontrealBooksData().get("MON6231").getQuantity());
		montrealImpl.removeItem("MONM1111", "MON6231", 4);
		assertEquals(0, montrealImpl.getMontrealBooksData().get("MON6231").getQuantity());
		montrealImpl.removeItem("MONM1111", "MON6231", -1);
		assertEquals(false, montrealImpl.getMontrealBooksData().containsKey("MON6231"));
	}
	
	@Test
	public void testExchangeItem() {
		concordiaImpl.borrowItem("CONU1111", "CON6231");
		concordiaImpl.exchangeItem("CONU1111", "CON6441", "CON6231");
		assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6441").getQuantity());
		mcgillImpl.borrowItem("MCGU1111", "MCG6231");
		montrealImpl.exchangeItem("MCGU1111", "MCG6441", "MCG6231");
		assertEquals(1, mcgillImpl.getMcgillBooksData().get("MCG6441").getQuantity());
		montrealImpl.borrowItem("MONU1111", "MON6231");
		concordiaImpl.exchangeItem("MONU1111", "MON6441", "MON6231");
		assertEquals(1, montrealImpl.getMontrealBooksData().get("MON6441").getQuantity());
		
	}

}
