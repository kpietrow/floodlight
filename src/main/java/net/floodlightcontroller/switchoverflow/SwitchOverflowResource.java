package net.floodlightcontroller.switchoverflow;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class SwitchOverflowResource  extends ServerResource {
		protected static String dpid = "00:00:00:00:00:00:00:01";
			
		@Get("json")
	    public void start(String postData) {       
	    	
	    	SwitchOverflowTest.pushFLows(dpid);
	   
	}

}