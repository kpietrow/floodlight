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
		// If no arguments are given
		if (arguments.length() == 0 || arguments.trim().equalsIgnoreCase("all")) {
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
		// If argument is given
		else {
			CommandHandler commander = CommandHandler.getInstance();
			/* The String builder that hold the resulting string. */
			StringBuilder result = new StringBuilder();
		
			// Find available commands.
			for (ICommand comp : commander.getCommands()) {
				if (comp == null) {
					break;
				}
				// Checks if command same as arguments
				else if (arguments.equals(comp.getCommandString())) {
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
			// If no match, return normal help command
			if (result.length() == 0) {
				return this.execute(console, "");
			}
			// Return match
			else {
				return result.toString();
			}
		}
		
		
	}

}
