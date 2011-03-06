package com.convergys.wmsfetch.cli;

import java.util.ResourceBundle;

/**
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class Args {
	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle("com.convergys.wmsfetch.cli.cliConfig");

	public static final String APP_NAME = resourceBundle.getString("app_name");

	public static final String HELP = resourceBundle.getString("help");

	public static final String HELP_DESC = resourceBundle
			.getString("help_desc");

	public static final String WEB = resourceBundle.getString("web");

	public static final String WEB_DESC = resourceBundle.getString("web_desc");

	public static final String EOL = resourceBundle.getString("eol");

	public static final String EOL_DESC = resourceBundle.getString("eol_desc");

	public static final String VERSION = resourceBundle.getString("version");

	public static final String VERSION_ARG = resourceBundle
			.getString("version_arg");

	public static final String VERSION_DESC = resourceBundle
			.getString("version_desc");

	public static final String USERNAME = resourceBundle.getString("username");

	public static final String USERNAME_DESC = resourceBundle
			.getString("username_desc");

	public static final String USERNAME_ARG = resourceBundle
			.getString("username_arg");

	public static final String PASSWORD = resourceBundle.getString("password");

	public static final String PASSWORD_DESC = resourceBundle
			.getString("password_desc");

	public static final String PASSWORD_ARG = resourceBundle
			.getString("password_arg");

	public static final String URL = resourceBundle.getString("url");

	public static final String URL_DESC = resourceBundle.getString("url_desc");

	public static final String URL_ARG = resourceBundle.getString("url_arg");

	public static final String WMS_ID = resourceBundle.getString("wms_id");

	public static final String WMS_ID_DESC = resourceBundle
			.getString("wms_id_desc");

	public static final String WMS_ID_ARG = resourceBundle
			.getString("wms_id_arg");

	public static final String RELEASE_ID = resourceBundle
			.getString("release_id");

	public static final String RELEASE_ID_DESC = resourceBundle
			.getString("release_id_desc");

	public static final String RELEASE_ID_ARG = resourceBundle
			.getString("release_id_arg");

	public static final String PROPERTY = resourceBundle.getString("property");

	public static final String PROPERTY_DESC = resourceBundle
			.getString("property_desc");

	public static final String PROPERTY_ARG = resourceBundle
			.getString("property_arg");

	public static final String OUT_FILE = resourceBundle.getString("outfile");

	public static final String OUT_FILE_DESC = resourceBundle
			.getString("outfile_desc");

	public static final String OUT_FILE_ARG = resourceBundle
			.getString("outfile_arg");

}