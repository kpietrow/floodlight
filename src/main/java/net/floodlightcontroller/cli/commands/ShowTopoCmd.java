package net.floodlightcontroller.cli.commands;

import java.io.IOException;
import java.util.Collection;

import jline.console.completer.Completer;
import net.floodlightcontroller.cli.IConsole;
import net.floodlightcontroller.core.module.FloodlightModuleContext;

/**
 * This command will return the current topology of the
 * network when called.
 * 
 * @author Kevin Pietrow, <kpietrow@gmail.com>
 */
public class ShowTopoCmd implements ICommand, IConsole {
	/** The command string. */
	private String commandString = "show topology";
	/** The command's arguments. */
	private String arguments = null;
	/** The command's help text. */
	private String help = "Displays topology of current network";

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
	public void setPrompt(String prompt) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPrompt() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Completer> getCompleters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(String string) throws IOException {
		// TODO Auto-generated method stub

	}


	@Override
	public String execute(IConsole console, String arguments) {
		String temp = "poop";
		return temp;
	}

}
