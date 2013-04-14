package net.floodlightcontroller.cli.commands;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.floodlightcontroller.cli.commands.CommandHandler;
import net.floodlightcontroller.cli.commands.ICommand;
import net.floodlightcontroller.cli.Console;


import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;
import net.floodlightcontroller.cli.IConsole;
import net.floodlightcontroller.core.module.FloodlightModuleContext;

/**
 * This module is for the help command.
 * Its intent is to spit out every possible command when 
 * called upon from the CLI
 * 
 * @author Kevin Pietrow, <kpietrow@gmail.com>
 */
public class ShowHelpCmd implements ICommand {
	/** The command string. */
	private String commandString = "help";
	/** The command's arguments. */
	private String arguments = "[HELP]";
	/** The command's help text. */
	private String help = "Displays all possible commands";
	/** The Jline console reader. */
	private ConsoleReader reader;
	/** The command hander that executes all console commands. */
	private CommandHandler commander;
	

	
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
	
	private void generateCompleters() {
    	for (ICommand cmd : this.commander.getCommands() ){
    		this.addCommand(cmd);
    	}
    }
/*
	private synchronized String getCommands() {
		StringBuilder commands = new StringBuilder();
    	for (ICommand cmd : this.commander.getCommands() ){
    		commands.append(cmd + "\n");
    		System.out.println("yo");
    	}
    	return commands.toString();
    }
	*/
	@Override
	public synchronized String execute(IConsole console, String arguments) {
		return "yo";
	}

}
