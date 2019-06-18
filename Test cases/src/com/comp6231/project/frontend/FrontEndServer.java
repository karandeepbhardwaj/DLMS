package com.comp6231.project.frontend;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.comp6231.project.constants.LibraryManagementConstants;

import FrontEndIdl.FrontEnd;
import FrontEndIdl.FrontEndHelper;

public class FrontEndServer {

	public static void main(String[] args) {
		FrontEndImpl frontEndImpl = new FrontEndImpl();
		ORB orb = ORB.init(args, null);
		try {
			POA rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootPoa.the_POAManager().activate();
			
			frontEndImpl.setOrb(orb);
			
			FrontEnd href = FrontEndHelper.narrow(rootPoa.servant_to_reference(frontEndImpl));
			
			NamingContextExt namingContextReference = NamingContextExtHelper.narrow(orb.resolve_initial_references("NameService"));
			NameComponent[] path = namingContextReference.to_name(LibraryManagementConstants.FRONT_END);
			
			namingContextReference.rebind(path, href);
			System.out.println("Front End is Ready");
			while(true) {
				orb.run();
			}
			
		} catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName | ServantNotActive | WrongPolicy | NotFound | CannotProceed e) {
			System.out.println("Something went wrong in concordai server: "+e.getMessage());
		}
	}

}
