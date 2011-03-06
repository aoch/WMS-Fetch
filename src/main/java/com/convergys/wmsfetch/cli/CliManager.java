package com.convergys.wmsfetch.cli;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import com.convergys.wmsfetch.net.db.WMSClientJDBCImpl;
import com.convergys.wmsfetch.releasenotes.ReleaseNoteGeneratorImpl;
import com.convergys.wmsfetch.releasenotes.WMSReleaseNoteMgr;
import com.convergys.wmsfetch.releasenotes.WMSReleaseNoteMgr.Type;

/**
 * The interrogation stage is where the application queries the CommandLine to
 * decide what execution branch to take depending on boolean options and uses
 * the option values to provide the application data. This stage is implemented
 * in the user code. The accessor methods on CommandLine provide the
 * interrogation capability to the user code. The result of the interrogation
 * stage is that the user code is fully informed of all the text that was
 * supplied on the command line and processed according to the parser and
 * Options rules.
 * 
 * All Command Line Options the user selected at run time are accessed from this
 * class
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 */
public class CliManager {
	private static transient final Logger logger = Logger
			.getLogger(CliManager.class);

	/**
	 * Show invalid usage of options and error exit code
	 * 
	 * @param options
	 */
	public static void invalidUsage(Options options) {
		boolean showUsage = true;
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(125);

		// Comparator to sort the args
		// formatter.setOptionComparator(comparator);

		formatter.printHelp(Args.APP_NAME, options, showUsage);
		System.exit(1);
	}

	/**
	 * Show invalid usage of options.
	 * 
	 * @param options
	 * @param msg
	 */
	public static void invalidUsage(Options options, String msg) {
		System.out.println(msg);
		invalidUsage(options);
	}

	/**
	 * Show help and error exit code
	 * 
	 * @param options
	 */
	public static void inValidUsageOfHelp(Options options) {
		boolean showUsage = true;
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(Args.APP_NAME, options, showUsage);
		System.exit(1);
	}

	private CommandLine cmdLine;

	private Options options;

	/**
	 * Default empty constructor
	 */
	public CliManager() {
	}

	/**
	 * 
	 * @param cmdLine
	 * @param options
	 */
	public CliManager(CommandLine cmdLine, Options options) {
		this();
		this.cmdLine = cmdLine;
		this.options = options;
	}

	public CommandLine getCmdLine() {
		return cmdLine;
	}

	/**
	 * 
	 * @param optionValue
	 * @return fileOS
	 */
	public OutputStream getFileOS(String filename) {
		File file = new File(filename);
		File fileDir = file.getParentFile();
		if ((fileDir != null) && (!fileDir.exists())) {
			if (!fileDir.mkdirs()) {
				if (!fileDir.exists()) {
					logger.error("Cannot create directory " + fileDir);
				}
			}
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}

		FileOutputStream fileOS = null;
		try {
			fileOS = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			logger.error(e.getLocalizedMessage());
		}
		return fileOS;
	}

	/**
	 * 
	 * @return the options
	 */
	public Options getOptions() {
		return options;
	}

	/**
	 * The meat of command line arguments. Do what the user wants!
	 */
	public void interrogateArgs() {

		// Version options executes and exits
		if (cmdLine.hasOption(Args.VERSION)) {
			// Just print the version of this tool
			System.out.println(version());
			System.exit(0);
		}

		// TODO move this Invalid usage to parser
		if (cmdLine.hasOption(Args.WMS_ID)
				&& cmdLine.hasOption(Args.RELEASE_ID)) {
			String errorMsg = String.format(
					"Either option: [%s] or option: [%s] not BOTH!",
					new Object[] { Args.WMS_ID, Args.RELEASE_ID });
			logger.error(errorMsg);
			System.out.println(errorMsg);
			invalidUsage(options);
		}

		Type type = null;
		String wmsReleaseID = "";
		if (cmdLine.hasOption(Args.WMS_ID)) {
			wmsReleaseID = cmdLine.getOptionValue(Args.WMS_ID);
			type = Type.PATCH;
		} else if (cmdLine.hasOption(Args.RELEASE_ID)) {
			wmsReleaseID = cmdLine.getOptionValue(Args.RELEASE_ID);
			type = Type.RELEASE;
		} else {
			String errorMsg = String.format(
					"Either option: [%s] or option: [%s] MUST be provided",
					new Object[] { Args.WMS_ID, Args.RELEASE_ID });
			logger.error(errorMsg);
			System.out.println(errorMsg);
			invalidUsage(options);
		}

		String password = "";

		// No password provided collect from user as STDIN
		if (!cmdLine.hasOption(Args.PASSWORD)) {
			Console console = System.console();

			if (console != null) {
				// read the password, without echoing the output
				password = new String(console.readPassword(Args.PASSWORD_ARG
						+ ": "));
			} else {
				logger.error("console is null");
				invalidUsage(options);
			}
		}
		// Password provided as command line option
		else {
			password = cmdLine.getOptionValue(Args.PASSWORD);
		}

		String username = cmdLine.getOptionValue(Args.USERNAME);

		// Set the retrieval engine (Default JDBC) and Release note generator
		WMSReleaseNoteMgr wmsReleaseNoteMgr = new WMSReleaseNoteMgr(
				new WMSClientJDBCImpl(), new ReleaseNoteGeneratorImpl());

		// set UNIX EOL
		if (cmdLine.hasOption(Args.EOL)) {
			wmsReleaseNoteMgr.setEOL("\n");
		} // Default is Windows
		else {
			wmsReleaseNoteMgr.setEOL("\r\n");
		}

		String url = "";

		// set URL for website or database server
		if (cmdLine.hasOption(Args.URL)) {
			url = cmdLine.getOptionValue(Args.URL);
		}

		// Set Output Stream
		if (!cmdLine.hasOption(Args.OUT_FILE)) {
			wmsReleaseNoteMgr.setOutput(System.out);
		} else {
			String filename = cmdLine.getOptionValue(Args.OUT_FILE);
			logger.debug(String.format("Filename is: %s",
					new Object[] { filename }));
			wmsReleaseNoteMgr.setOutput(getFileOS(filename));
		}

		Map<String, String> variablesMap = new HashMap<String, String>();
		if (cmdLine.hasOption(Args.PROPERTY)) {
			variablesMap = new HashMap<String, String>((Map) cmdLine
					.getOptionProperties(Args.PROPERTY));
		}

		// HOOK to Business Logic
		boolean success = wmsReleaseNoteMgr.generateReleaseNotes(type,
				username, password, wmsReleaseID, url, variablesMap);

		if (success) {
			String infoMsg = String.format(
					"Generated Release notes for issue: %s",
					new Object[] { wmsReleaseID });
			logger.info(infoMsg);
			System.out.println(infoMsg);
		} else {
			String errorMsg = String.format(
					"Could not generate Release notes for issue: %s",
					new Object[] { wmsReleaseID });
			logger.error(errorMsg);
			System.out.println(errorMsg);
		}
	}

	/**
	 * Queries the command line entries, checks user args are valid
	 */
	public void process() {
		if (!validHelpOption()) {
			inValidUsageOfHelp(options);
		}
		interrogateArgs();
	}

	/**
	 * 
	 * @param cmdLine
	 *            the cmdLine to set
	 */
	public void setCmdLine(CommandLine cmdLine) {
		this.cmdLine = cmdLine;
	}

	/**
	 * 
	 * @param options
	 *            the options to set
	 */
	public void setOptions(Options options) {
		this.options = options;
	}

	/**
	 * 
	 * @return valid
	 */
	public boolean validHelpOption() {
		Option[] o = cmdLine.getOptions();
		if (cmdLine.hasOption(Args.HELP)) {
			if (o.length > 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Simply return the application name and version
	 * 
	 * @return version
	 */
	public String version() {
		return Args.APP_NAME + " " + Args.VERSION;
	}
}