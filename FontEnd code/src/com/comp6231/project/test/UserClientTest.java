package com.comp6231.project.test;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.comp6231.project.client.UserMethodsImplementation;
import com.comp6231.project.frontend.FrontEndImpl;

public class UserClientTest {
	static FrontEndImpl frontEnd;
	UserMethodsImplementation userMethodsImplementation;
	
	@BeforeClass
	public static void setupBeforeClass() {
	}
	
	@Test
	public void test() {
		frontEnd = new FrontEndImpl();
		System.out.println(frontEnd.addItem("CONM1111", "CON6231", "DS", 2));
		assertEquals("Item added to the library", frontEnd.addItem("CONM1111", "CON6231", "DS", 2));
		assertEquals("Item added to the library", frontEnd.addItem("MONM1111", "MON6231", "DS", 2));
		assertEquals("Item added to the library", frontEnd.addItem("MCGM1111", "MCG6231", "DS", 2));
		
		assertEquals("Item added to the library", frontEnd.addItem("CONM1111", "CON6441", "APP", 2));
		assertEquals("Item added to the library", frontEnd.addItem("MONM1111", "MON6441", "APP", 2));
		assertEquals("Item added to the library", frontEnd.addItem("MCGM1111", "MCG6441", "APP", 2));
		
		assertEquals("Item quantity decreased successfully", frontEnd.removeItem("CONM1111", "CON6231", 1));
		assertEquals("Item quantity decreased successfully", frontEnd.removeItem("MONM1111", "MON6231", 1));
		assertEquals("Item quantity decreased successfully", frontEnd.removeItem("MCGM1111", "MCG6231", 1));
		
		assertEquals("Item removed successfully", frontEnd.removeItem("CONM1111", "CON6231", -1));
		assertEquals("Item removed successfully", frontEnd.removeItem("MONM1111", "MON6231", -1));
		assertEquals("Item removed successfully", frontEnd.removeItem("MCGM1111", "MCG6231", -1));
		
		assertEquals("Item added to the library", frontEnd.addItem("CONM1111", "CON6231", "DS", 2));
		assertEquals("Item added to the library", frontEnd.addItem("MONM1111", "MON6231", "DS", 2));
		assertEquals("Item added to the library", frontEnd.addItem("MCGM1111", "MCG6231", "DS", 2));
		
		assertEquals("Item added to the library", frontEnd.addItem("CONM1111", "CON6441", "APP", 2));
		assertEquals("Item added to the library", frontEnd.addItem("MONM1111", "MON6441", "APP", 2));
		assertEquals("Item added to the library", frontEnd.addItem("MCGM1111", "MCG6441", "APP", 2));
		
		assertEquals("Book issued successfully", frontEnd.borrowItem("CONU1111", "CON6231"));
		assertEquals("Book issued successfully", frontEnd.borrowItem("MONU1111", "MON6231"));
		assertEquals("Book issued successfully", frontEnd.borrowItem("MCGU1111", "MCG6231"));
		
		assertEquals("Book issued successfully", frontEnd.exchangeItem("CONU1111", "CON6441", "CON6231"));
		assertEquals("Book issued successfully", frontEnd.exchangeItem("MONU1111", "MON6441", "MON6231"));
		assertEquals("Book issued successfully", frontEnd.exchangeItem("MCGU1111", "MCG6441", "MCG6231"));
		
		assertEquals("Book Returned", frontEnd.returnItem("CONU1111", "CON6441"));
		assertEquals("Book Returned", frontEnd.returnItem("MONU1111", "MON6441"));
		assertEquals("Book Returned", frontEnd.returnItem("MCGU1111", "MCG6441"));
	}

}
