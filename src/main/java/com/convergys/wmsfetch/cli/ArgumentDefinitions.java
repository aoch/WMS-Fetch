package com.convergys.wmsfetch.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

/**
 * Each command line must define the set of options that will be used to define
 * the interface to the application. CLI uses the Options class, as a container
 * for Option instances.
 * 
 * All Command Line Options should be defined in this Class file.
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 */
public class ArgumentDefinitions {

	private Options options;

	private Option help;

	private Option version;

	private Option wmsid;

	private Option releaseid;

	private Option web;

	private Option eol;

	private Option username;

	private Option password;

	private Option url;

	private Option outfile;

	private Option property;

	public ArgumentDefinitions() {
		defineBooleanOptions();
		defineArgumentOptions();
		createOptions();
	}

	private void createOptions() {
		options = new Options();

		// Add each option
		options.addOption(help);
		options.addOption(version);
		options.addOption(web);
		options.addOption(eol);
		options.addOption(wmsid);
		options.addOption(releaseid);
		options.addOption(username);
		options.addOption(password);
		options.addOption(url);
		options.addOption(outfile);
		options.addOption(property);
	}

	// Define are argument type options here
	private void defineArgumentOptions() {
		OptionBuilder.hasArg();
		OptionBuilder.withArgName(Args.USERNAME_ARG);
		OptionBuilder.withDescription(Args.USERNAME_DESC);
		OptionBuilder.isRequired();
		username = OptionBuilder.create(Args.USERNAME);

		OptionBuilder.hasArg();
		OptionBuilder.withArgName(Args.PASSWORD_ARG);
		OptionBuilder.withDescription(Args.PASSWORD_DESC);
		password = OptionBuilder.create(Args.PASSWORD);

		OptionBuilder.hasArg();
		OptionBuilder.withArgName(Args.WMS_ID_ARG);
		OptionBuilder.withDescription(Args.WMS_ID_DESC);
		wmsid = OptionBuilder.create(Args.WMS_ID);

		OptionBuilder.hasArg();
		OptionBuilder.withArgName(Args.RELEASE_ID_ARG);
		OptionBuilder.withDescription(Args.RELEASE_ID_DESC);
		releaseid = OptionBuilder.create(Args.RELEASE_ID);

		OptionBuilder.hasArg();
		OptionBuilder.withArgName(Args.URL_ARG);
		OptionBuilder.withDescription(Args.URL_DESC);
		url = OptionBuilder.create(Args.URL);

		OptionBuilder.hasArg();
		OptionBuilder.withArgName(Args.OUT_FILE_ARG);
		OptionBuilder.withDescription(Args.OUT_FILE_DESC);
		outfile = OptionBuilder.create(Args.OUT_FILE);

		OptionBuilder.hasArgs(2);
		OptionBuilder.withArgName(Args.PROPERTY_ARG);
		OptionBuilder.withDescription(Args.PROPERTY_DESC);
		OptionBuilder.withValueSeparator();
		property = OptionBuilder.create(Args.PROPERTY);
	}

	// Define are flag type options here
	private void defineBooleanOptions() {
		help = new Option(Args.HELP, Args.HELP_DESC);
		web = new Option(Args.WEB, Args.WEB_DESC);
		eol = new Option(Args.EOL, Args.EOL_DESC);
		version = new Option(Args.VERSION_ARG, Args.VERSION_DESC);
	}

	/**
	 * Returns all available Command Line Options
	 * 
	 * @return the options
	 */
	public Options getOptions() {
		return options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(Options options) {
		this.options = options;
	}
}