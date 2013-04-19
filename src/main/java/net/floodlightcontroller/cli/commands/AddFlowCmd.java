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

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

/**
 * Adds static flow.
 * 
 * @author Kevin Pietrow <kpietrow@gmail.com>
 */
public class AddFlowCmd implements ICommand {
	/** The command string. */
	private String commandString = "add flow";
	/** The command's arguments. */
	private String arguments = "[JSON]";
	/** The command's help text. */
	private String help = "Adds static flow";

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

	@Override
	public synchronized String execute(IConsole console, String arguments) {
		/* The Restlet client resource, accessed using the REST API. */
		ClientResource cr = new ClientResource("http://localhost:8080/wm/staticflowentrypusher/json");
		
		if (arguments.length() == 0 || arguments.trim().equalsIgnoreCase("all"))
			return "No flow specs entered.";
		
		cr.post(arguments);
		
		
		return "yay";
		
		// If no argument is given
		/*
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
	 * Executes a command without any arguments.
	 * 
	 * @param console The console where the command was initialized.
	 * @param clientResource The client resource retrieved by using the REST API.
	 * @return A string that might be returned by the command execution.
	 */
	public String execute(IConsole console, ClientResource clientResource) {
		/* A List of JSON data objects retrieved by using the REST API. */
        List<Map<String,Object>> jsonData = new ArrayList<Map<String,Object>>();
		/* The resulting string. */
		String result = "";
		
		try {	
			jsonData = this.parseJson(clientResource.get().getText());
		} catch (ResourceException e) {
			System.out.println("Resource not found");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Parses a JSON string and decomposes all JSON arrays and objects. Stores the
	 * resulting strings in a nested Map of string objects.
	 * 
	 * @param jsonString
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String,Object>> parseJson(String jsonString) throws IOException {
		/* The Jackson JSON parser. */
        JsonParser jp;
        /* The Jackson JSON factory. */
        JsonFactory f = new JsonFactory();
        /* The Jackson object mapper. */
        ObjectMapper mapper = new ObjectMapper();
        /* A list of JSON data objects retrieved by using the REST API. */
        List<Map<String,Object>> jsonData = new ArrayList<Map<String,Object>>();
        
        try {
            jp = f.createJsonParser(jsonString);
        } catch (JsonParseException e) {
            throw new IOException(e);
        }
       
        // Move to the first object in the array.
        jp.nextToken();
        if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
        	throw new IOException("Expected START_ARRAY instead of " + jp.getCurrentToken());
        }
        
        // Retrieve the information from JSON
        while (jp.nextToken() == JsonToken.START_OBJECT) {
        	jsonData.add(mapper.readValue(jp, Map.class));
        }
        
        // Close the JSON parser.
        jp.close();
        
        // Return.
        return jsonData;
	}

	/**
	 * Filters the list of JSON data objects to find a specific switch.
	 * 
	 * @param jsonData A list of JSON data objects retrieved by using the REST API.
	 * @param dpid The filter string.
	 * @return A filters list of JSON data objects.
	 */
	private List<Map<String,Object>> filterJsonData(List<Map<String,Object>> jsonData, String dpid) {
		/* A filtered list of JSON data objects. */
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		
		for (Map<String,Object> entry : jsonData) {
			if (dpid.equalsIgnoreCase((String)entry.get("dpid"))) {
				result.add(entry);
			}
		}
		
		return result;
	}

}
