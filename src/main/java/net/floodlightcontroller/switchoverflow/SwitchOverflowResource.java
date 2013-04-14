package net.floodlightcontroller.switchoverflow;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class SwitchOverflowResource  extends ServerResource {			
		@Get("json")
	    public void start(String postData) {       
	    	
	    	SwitchOverflowTest.pushFLows();
	   
	}

}