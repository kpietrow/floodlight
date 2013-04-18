package net.floodlightcontroller.cli.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.floodlightcontroller.cli.IConsole;

import jline.console.completer.Completer;

/**
 * The add command is used to present the completer of
 * "add", i.e. all commands that start with a "add" string.
 * 
 * @author Kevin Pietrow <kpietrow@gmail.com>
 */
public class AddCmd implements ICommand {
	/** The command string. */
	private String commandString = "add";
	/** The command's arguments. */
	private String arguments = null;
	/** The command's help text. */
	private String help = "Autocompletes Add commands";

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
		/* The String builder that hold the resulting string. */
		StringBuilder result = new StringBuilder();
		/* A list of command completion candidates. */
		List<CharSequence> candidates = new ArrayList<CharSequence>();
		/* Since Completer.complete needs a blank at the end of the command, we need to add one. */
		String line = this.commandString + " ";
		
		// Find possible command completions.
		for (Completer comp : console.getCompleters()) {
			if (comp.complete(line, line.length(), candidates) == -1) {
				break;
			}
		}
		
		// Make sure the list is unique.
		candidates = new ArrayList<CharSequence>(new HashSet<CharSequence>(candidates)); 
		
		// Create the result string.
		result.append("Command not found. Use:");
		for (CharSequence candidate : candidates) {
			result.append("\n  add " + candidate);
		}
	
		// Return.
		return result.toString();
	}

}
