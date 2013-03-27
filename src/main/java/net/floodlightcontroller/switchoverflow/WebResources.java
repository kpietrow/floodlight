package net.floodlightcontroller.switchoverflow;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class WebResources implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
        router.attach("/start/", SwitchOverflowResource.class); // G      
        router.attachDefault(SwitchOverflowResource.class);
        return router;
	}

	@Override
	public String basePath() {
		return "/switchoverflow";
	}
	
}