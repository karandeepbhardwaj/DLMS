package com.comp6231.project.testCases;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.comp6231.project.frontend.FrontEndImpl;
import com.comp6231.project.impl.ConcordiaLibraryImpl;
import com.comp6231.project.impl.McgillLibraryImpl;
import com.comp6231.project.impl.MontrealLibraryImpl;
import com.comp6231.project.model.MessageData;
import com.comp6231.project.sequencerImpl.CreateSequence;



public class SequencerTest {
	CreateSequence createSequence;
	ConcordiaLibraryImpl concordiaImpl;
	McgillLibraryImpl mcgillImpl;
	MontrealLibraryImpl montrealImpl;
	
	@Before
	public void beforeEachRun() {
		concordiaImpl=new ConcordiaLibraryImpl();
		mcgillImpl=new McgillLibraryImpl();
		montrealImpl = new MontrealLibraryImpl();
		MessageData messageData = new MessageData();
		messageData.setItemId("CON1111");
		messageData.setItemName("DS");
		messageData.setUserId("CONM1111");
		messageData.setQuantity(2);
		messageData.setErrorCounter(0);
		messageData.setSequenceCounter(0);
		messageData.setMethodName(com.comp6231.project.constants.LibraryManagementConstants.ADD_ITEM);
		FrontEndImpl feImpl=new FrontEndImpl();
		createSequence=new CreateSequence();
		feImpl.sendMessageToSequencer(messageData);
	}


	
	@Test
	public void addItemThreadTest() {
		
		Runnable addItemImplConc = () ->{
			mcgillImpl.addItem("MCGM1111", "MCG6231", "Distributed", 1);
			assertEquals(1, mcgillImpl.getMcgillBooksData().get("MCG6231").getQuantity());
		};
		new Thread(addItemImplConc).start();
		Runnable addItemImplMcg = () ->{
			montrealImpl.addItem("MONM1111", "MON6231", "Distributed", 5);
			assertEquals(1, montrealImpl.getMontrealBooksData().get("MON6231").getQuantity());
		};
		new Thread(addItemImplMcg).start();
		Runnable addItemImplMon = () ->{
			concordiaImpl.addItem("CONM1111", "CON6441", "APP", 1);
			assertEquals(1, concordiaImpl.getConcordiaBooksData().get("CON6231").getQuantity());
		};
		new Thread(addItemImplMon).start();

	
	}
	
	@Test
	public void listItemThreadTest() {
		Runnable listItemImplConc = () ->{
			mcgillImpl.listItemAvailability("MCGM1111");
		};
		new Thread(listItemImplConc).start();
		Runnable listItemImplMcg = () ->{
			montrealImpl.listItemAvailability("MONM1111");
		};
		new Thread(listItemImplMcg).start();
		Runnable listItemImplMon = () ->{
			concordiaImpl.listItemAvailability("CONM1111");
		};
		new Thread(listItemImplMon).start();

		
		
	}
	
	@Test
	public void removeItemThreadTest() {
		Runnable removeItemImplConc = () ->{
			mcgillImpl.removeItem("MCGM1111", "MCG6231", 1);
		};
		new Thread(removeItemImplConc).start();
		Runnable removeItemImplMcg = () ->{
			montrealImpl.removeItem("MONM1111", "MON6231", 1);
		};
		new Thread(removeItemImplMcg).start();
		Runnable removeItemImplMon = () ->{
			concordiaImpl.removeItem("CONM1111", "CON6231", 1);
		};
		new Thread(removeItemImplMon).start();

		
		
	}
	
	@Test
	public void borrowItemList() {
		Runnable borrowItemImplConc = () ->{
			concordiaImpl.borrowItem("CONM1111", "CON6231");
		};
		new Thread(borrowItemImplConc).start();
		Runnable borrowItemImplMcg = () ->{
			mcgillImpl.borrowItem("MCGM1111","MCG6231");
		};
		new Thread(borrowItemImplMcg).start();
		Runnable borrowItemImplMon = () ->{
			montrealImpl.borrowItem("MONM1111", "MON6231");
		};
		new Thread(borrowItemImplMon).start();

		
		
	}
	
	@Test
	public void returnItemThreadTest() {
		Runnable returnItemImplConc = () ->{
			concordiaImpl.returnItem("CONM1111", "CON6231");
		};
		new Thread(returnItemImplConc).start();
		Runnable returnItemImplMcg = () ->{
			mcgillImpl.returnItem("MCG1111", "MCG6231");
		};
		new Thread(returnItemImplConc).start();
		Runnable returnItemImplMon = () ->{
			montrealImpl.returnItem("MONM1111", "MON6231");
		};
		new Thread(returnItemImplMon).start();

		
		
	}
	
	@Test
	public void findItemThreadTest() {
		Runnable findItemImplConc = () ->{
			concordiaImpl.findItem("CONM1111", "DISTRIBUTED",true);
		};
		new Thread(findItemImplConc).start();
		Runnable findItemImplMcg = () ->{
			mcgillImpl.findItem("MCG1111","DISTRIBUTED",true);
		};
		new Thread(findItemImplMcg).start();
		Runnable findItemImplMon = () ->{
			montrealImpl.findItem("MONM1111", "DISTRIBUTED",true);
		};
		new Thread(findItemImplMon).start();

		
		
	}

	@Test
	public void exchangeItemThreadTest() {
		Runnable exchangeItemImplConc = () ->{
			concordiaImpl.exchangeItem("CONM1111", "CON6441",  "CON6231");
		};
		new Thread(exchangeItemImplConc).start();
		Runnable exchangeItemImplMcg = () ->{
			mcgillImpl.exchangeItem("MCGM1111", "MCG6440",  "MCG6231");
		};
		new Thread(exchangeItemImplMcg).start();
		Runnable exchangeItemImplMon = () ->{
			montrealImpl.exchangeItem("MONM1111", "MON6441",  "MON6231");
		};
		new Thread(exchangeItemImplMon).start();

		
		
	}

	
	
}

