package net.floodlightcontroller.cli.commands;

/*
* Copyright (c) 2013, California Institute of Technology
* ALL RIGHTS RESERVED.
* Based on Government Sponsored Research DE-SC0007346
* Author Michael Bredel <michael.bredel@cern.ch>
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*     http://www.apache.org/licenses/LICENSE-2.0
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
* LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
* A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
* HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
* INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
* BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
* OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
* AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
* LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
* WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
* 
* Neither the name of the California Institute of Technology
* (Caltech) nor the names of its contributors may be used to endorse
* or promote products derived from this software without specific prior
* written permission.
*/

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.cli.IConsole;
import net.floodlightcontroller.cli.utils.StringTable;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.annotations.LogMessageDoc;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
import net.floodlightcontroller.staticflowentry.StaticFlowEntryPusher;
import net.floodlightcontroller.staticflowentry.web.StaticFlowEntryPusherResource;
import net.floodlightcontroller.storage.IStorageSourceService;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFType;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Adds static flow.
 * 
 * @author Kevin Pietrow <kpietrow@gmail.com>
 */
public class AddFlowCmd extends ServerResource implements ICommand{
	/** The command string. */
	private String commandString = "add flow";
	/** The command's arguments. */
	private String arguments = null;
	/** The command's help text. */
	private String help = "Adds static flow";

	protected static IStaticFlowEntryPusherService sfp;

	protected static IFloodlightProviderService floodlightProvider;
	
	
	@Override
	public String getCommandString() {
		return commandString;
	}
	
	@Override
	public String getArguments() {
		return arguments;
	}

	@Override
	public String getHelpText() {
		return help;
	}

	public synchronized String execute(IConsole console, String fmJson) {
		if (fmJson.length() == 0 || fmJson.trim().equalsIgnoreCase("all"))
			return "No flow specs entered.";	
		
		IStorageSourceService storageSource =
                (IStorageSourceService)getContext().getAttributes().
                    get(IStorageSourceService.class.getCanonicalName());
        
        Map<String, Object> rowValues;
        try {
            rowValues = StaticFlowEntries.jsonToStorageEntry(fmJson);
            String status = null;
            if (!checkMatchIp(rowValues)) {
                status = "Warning! Pushing a static flow entry that matches IP " +
                        "fields without matching for IP payload (ether-type 2048) will cause " +
                        "the switch to wildcard higher level fields.";
               // log.error(status);
            } else {
                status = "Entry pushed";
            }
            storageSource.insertRowAsync(StaticFlowEntryPusher.TABLE_NAME, rowValues);
            return ("{\"status\" : \"" + status + "\"}");
        } catch (IOException e) {
           // log.error("Error parsing push flow mod request: " + fmJson, e);
            e.printStackTrace();
            return "{\"status\" : \"Error! Could not parse flod mod, see log for details.\"}";
        }
		
		/*
        
		
		Map<Long, IOFSwitch> activeswitches = floodlightProvider.getSwitches();
    	String currentswitch = (activeswitches.get(new Long(1)).toString());
    	int index = currentswitch.indexOf("DPID");
    	System.out.println(index);
    	String dpid = currentswitch.substring(index + 5, index + 28);
    	System.out.println(dpid);
		
    	OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
		OFMatch ofMatch = new OFMatch();
		fm.setMatch(ofMatch);

		sfp.addFlow(arguments, fm, dpid);

		return "poop";
		*/
		
		/* The Restlet client resource, accessed using the REST API. */
		/*
		ClientResource cr = new ClientResource("http://localhost:8080/wm/staticflowentrypusher/json");
		
		if (arguments.length() == 0 || arguments.trim().equalsIgnoreCase("all"))
			return "No flow specs entered.";
		
		cr.post(arguments);
		
		
		return "yay";
		
		// If no argument is given
		
		if (arguments.length() == 0 || arguments.trim().equalsIgnoreCase("all"))
			return this.execute(console, cr);
		
		try {	
			jsonData = this.filterJsonData(this.parseJson(cr.get().getText()), arguments);
		} catch (ResourceException e) {
			System.out.println("Resource not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Return string.
		return result;
		*/
	}
    
    /**
     * Checks to see if the user matches IP information without
     * checking for the correct ether-type (2048).
     * @param rows The Map that is a string representation of
     * the static flow.
     * @return True if they checked the ether-type, false otherwise
     */
    private boolean checkMatchIp(Map<String, Object> rows) {
        boolean matchEther = false;
        String val = (String) rows.get(StaticFlowEntryPusher.COLUMN_DL_TYPE);
        if (val != null) {
            int type = 0;
            // check both hex and decimal
            if (val.startsWith("0x")) {
                type = Integer.parseInt(val.substring(2), 16);
            } else {
                try {
                    type = Integer.parseInt(val);
                } catch (NumberFormatException e) { /* fail silently */}
            }
            if (type == 2048) matchEther = true;
        }
        
        if ((rows.containsKey(StaticFlowEntryPusher.COLUMN_NW_DST) || 
                rows.containsKey(StaticFlowEntryPusher.COLUMN_NW_SRC) ||
                rows.containsKey(StaticFlowEntryPusher.COLUMN_NW_PROTO) ||
                rows.containsKey(StaticFlowEntryPusher.COLUMN_NW_TOS)) &&
                (matchEther == false))
            return false;
        
        return true;
    }
}
