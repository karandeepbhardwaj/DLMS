package junit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.comp6231.project.model.MessageData;

import constants.ConstantValues;
import sequencerImplementation.CreateSequence;
import serverInterfaceImplementation.ConcordiaLibraryImplementation;
import serverInterfaceImplementation.McgillLibraryImplementation;
import serverInterfaceImplementation.MontrealLibraryImplementation;

public class SequencerTest {

	CreateSequence createSequence;
	ConcordiaLibraryImplementation concordiaImpl;
	McgillLibraryImplementation mcgillImpl;
	MontrealLibraryImplementation montrealImpl;
	
	@Before
	public void beforeEachRun() {
		concordiaImpl=new ConcordiaLibraryImplementation();
		mcgillImpl=new McgillLibraryImplementation();
		montrealImpl = new MontrealLibraryImplementation();
		MessageData messageData = new MessageData();
		messageData.setItemId("CON1111");
		messageData.setItemName("DS");
		messageData.setUserId("CONM1111");
		messageData.setQuantity(2);
		messageData.setErrorCounter(0);
		//messageData.setSequenceCounter(0);
		messageData.setMethodName(ConstantValues.ADD_ITEM);
		createSequence = new CreateSequence();
		createSequence.sequencerToReplicas(messageData);
	}
	
	@Test
	public void sequenceTest() {
		assertEquals(1, createSequence.getSequenceCounter());
	}

}
