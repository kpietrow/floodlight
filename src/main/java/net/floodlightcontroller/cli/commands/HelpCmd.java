package net.floodlightcontroller.cli.commands;


import net.floodlightcontroller.cli.commands.CommandHandler;
import net.floodlightcontroller.cli.commands.ICommand;

import net.floodlightcontroller.cli.IConsole;

/**
 * This module is for the help command.
 * Its intent is to spit out every possible command when 
 * called upon from the CLI
 * 
 * @author Kevin Pietrow, <kpietrow@gmail.com>
 */
public class HelpCmd implements ICommand {
	/** The command string. */
	private String commandString = "help";
	/** The command's arguments. */
	private String arguments = null;
	/** The command's help text. */
	private String help = "Displays all possible commands";
	
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

	public synchronized String execute(IConsole console, String arguments) {
		CommandHandler commander = CommandHandler.getInstance();
		/* The String builder that hold the resulting string. */
		StringBuilder result = new StringBuilder();
		
		// Find available commands.
		for (ICommand comp : commander.getCommands()) {
			if (comp == null) {
				break;
			}
			else {
				String temp = comp.getCommandString();
				// Append command 
				result.append("\n" + temp);
				// Check length. Add on tabs according to length.
				if (temp.length() < 8) {
					result.append("\t\t\t" + comp.getHelpText());
				}
				else {
					result.append("\t\t" + comp.getHelpText());
				}
			}
		}
		 
	
		// Return.
		return result.toString();
	}

}
