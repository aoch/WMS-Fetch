package com.convergys.wmsfetch.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * The parsing stage is where the text passed into the application via the
 * command line is processed. The text is processed according to the rules
 * defined by the parser implementation. The parse method defined on
 * CommandLineParser takes an Options instance and a String[] of arguments and
 * returns a CommandLine Object. The result of the parsing stage is a
 * CommandLine instance.
 * 
 * The Command Line is parsed for user selected options.
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 */
public class ArgumentParser {

	private static Logger logger = Logger.getLogger(ArgumentParser.class);

	// Create the command line parser
	private CommandLineParser parser;

	// Create the Argument Definitions
	private ArgumentDefinitions argumentDefinitions;

	// Wrap the command line in an object
	private CommandLine cmdLine;

	// List Command line options
	private Options options;

	// Command Line Argument Manager
	private CliManager cliManager;

	/**
	 * 
	 * @param args
	 */
	public ArgumentParser(String[] args) {
		parser = new GnuParser();
		argumentDefinitions = new ArgumentDefinitions();
		options = argumentDefinitions.getOptions();

		// Add the option to the command line
		cliManager = new CliManager();
		cliManager.setOptions(options);
		try {
			// parse the command line arguments
			cmdLine = parser.parse(options, args);
		} catch (ParseException pe) {
			String invalidUsage = pe.getLocalizedMessage();
			logger.error(invalidUsage);
			CliManager.invalidUsage(options, invalidUsage + "\n");
		}
		cliManager.setCmdLine(cmdLine);

		// Start INTERROGATION phase
		cliManager.process();

	}

	/**
	 * @return the cliManager
	 */
	public CliManager getCliManager() {
		return cliManager;
	}

	/**
	 * @param cliManager
	 *            the cliManager to set
	 */
	public void setCliManager(CliManager cliManager) {
		this.cliManager = cliManager;
	}
}