package com.convergys.wmsfetch.build.ant;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.convergys.wmsfetch.build.common.WMSFetchBuild;

/**
 * This allows ANT to create Release Notes connecting to WMS.
 * 
 * Please add the following task definition to your build.xml:
 * 
 * <code><taskdef name="releaseNotes" classname="com.convergys.wmsfetch.build.ant.WMSFetchANT"/></code>
 * 
 * In order to call the task use the following example: <code>
<project name="myProject" default="createReleaseNotes" basedir=".">
	<taskdef name="releaseNotes" classname="com.convergys.wmsfetch.build.ant.WMSFetchANT" classpath="." />
	<target name="createReleaseNotes">
		<releaseNotes 
	            wmsUsername="aoch" 
	            wmsPassword="secret" 
	            mailServerUsername="andrew.och@convergys.com" 
	            mailServerPassword="secret" 
	            releaseid="AWCC.1.4.13" 
	            outputDirectory="D:/testing/"
	            outfile="AWCC.1.4.13.txt" 
	            emails="andrew.och@convergys.com" 
	            variables="headerLastLine=AWCC Version: 1.4.9.1;compatibleSoftware=Weblogic-10 Oracle-10g;releaseTitle=Web Self-Care;component=WSC;notes=None;ftpURL=ftp://10.148.130.111/WSC/WSC-1.4.9;dependencyVersions=EBOS-4.4.4.4 OM-4.1.1029.1 AWCCCSM-1.4.14 AWCCOM-1.4.14 PF-4.0.18 AWCCRB-1.4.14 RB-4.4.5 ECA-4.4.5.18 CSM-5.3.235 AWCCECA-1.4.14 AWCCOFM-1.4.13 IM-3.2.1 WSO-1.4.9" />
	</target>
</project>
</code>
 * 
 * The emails field is separated with a semi-colon
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class WMSFetchANT extends Task {
	private static transient final Logger logger = Logger
			.getLogger(WMSFetchANT.class);

	/**
	 * Location of the Release Notes file.
	 */
	private String outputDirectory;

	/**
	 * The username used to log into WMS
	 * 
	 */
	private String wmsUsername;

	/**
	 * The password used to log into WMS
	 * 
	 */
	private String wmsPassword;

	/**
	 * The username used to log into the Mail server
	 * 
	 */
	private String mailServerUsername;

	/**
	 * The password used to log into the Mail server
	 * 
	 */
	private String mailServerPassword;

	/**
	 * Retrieve data on Release Version and create Patch Release Notes e.g.
	 * <code>AWCC-WSC.0.1.125</code>
	 * 
	 */
	private String releaseid;

	/**
	 * The URL used to log into WMS Website e.g. The
	 * <code>IP_address:port</code> of Database e.g.
	 * <code>155.90.47.41:1521</code>
	 * 
	 */
	private String url;

	/**
	 * The emails to send the release notes to
	 * 
	 */
	private String emails;

	/**
	 * The name and path of the file to create the Release Notes
	 * 
	 */
	private String outfile;

	/**
	 * The variables
	 */
	private String variables;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tools.ant.Task#execute()
	 */
	@Override
	public void execute() throws BuildException {

		String[] emailAddresses = new String[] {};
		if (emails.contains(";")) {
			emailAddresses = emails.split(";");
		} else {
			emailAddresses = new String[] { emails };
		}

		HashMap<String, String> variableMap = new HashMap<String, String>();
		if (variables != null && !variables.equalsIgnoreCase("")) {
			String[] variablePairs = null;
			if (variables.contains(";")) {
				variablePairs = variables.split(";");
			} else {
				variablePairs = new String[] { variables };
			}

			for (int i = 0; i < variablePairs.length; i++) {
				String[] keyValue = variablePairs[i].split("=");
				if (keyValue.length == 2) {
					variableMap.put(keyValue[0], keyValue[1]);
				}
			}
		} else {
			String warnMsg = String.format("Variables String is null/empty",
					new Object[] {});
			logger.warn(warnMsg);
		}

		if (outputDirectory != null && !outputDirectory.equalsIgnoreCase("")) {
			File outputDir = new File(outputDirectory);
			if (!outputDir.isDirectory()) {
				String errorMsg = String.format(
						"This is not a directory: [%s]",
						new Object[] { outputDir });
				logger.error(errorMsg);
			}

			boolean success = WMSFetchBuild.execReleaseNotes(outputDir,
					outfile, wmsUsername, wmsPassword, releaseid,
					emailAddresses, mailServerUsername, mailServerPassword,
					url, variableMap);

			if (success) {
				String infoMsg = String.format(
						"ReleaseNotes for: [%s] complete",
						new Object[] { releaseid });
				logger.info(infoMsg);
			} else {
				String warnMsg = String.format("ReleaseNotes for: [%s] failed",
						new Object[] { releaseid });
				logger.warn(warnMsg);
			}
		} else {
			String warnMsg = String.format(
					"ReleaseNotes for: [%s] failed. Output Directory Missing",
					new Object[] { releaseid });
			logger.warn(warnMsg);
		}
	}

	/**
	 * @return the emails
	 */
	public String getEmails() {
		return emails;
	}

	/**
	 * @return the mailServerPassword
	 */
	public String getMailServerPassword() {
		return mailServerPassword;
	}

	/**
	 * @return the mailServerUsername
	 */
	public String getMailServerUsername() {
		return mailServerUsername;
	}

	/**
	 * @return the outfile
	 */
	public String getOutfile() {
		return outfile;
	}

	/**
	 * @return the outputDirectory
	 */
	public String getOutputDirectory() {
		return outputDirectory;
	}

	/**
	 * @return the releaseid
	 */
	public String getReleaseid() {
		return releaseid;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the variables
	 */
	public String getVariables() {
		return variables;
	}

	/**
	 * @return the wmsPassword
	 */
	public String getWmsPassword() {
		return wmsPassword;
	}

	/**
	 * @return the wmsUsername
	 */
	public String getWmsUsername() {
		return wmsUsername;
	}

	/**
	 * @param emails
	 *            the emails to set
	 */
	public void setEmails(String emails) {
		this.emails = emails;
	}

	/**
	 * @param mailServerPassword
	 *            the mailServerPassword to set
	 */
	public void setMailServerPassword(String mailServerPassword) {
		this.mailServerPassword = mailServerPassword;
	}

	/**
	 * @param mailServerUsername
	 *            the mailServerUsername to set
	 */
	public void setMailServerUsername(String mailServerUsername) {
		this.mailServerUsername = mailServerUsername;
	}

	/**
	 * @param outfile
	 *            the outfile to set
	 */
	public void setOutfile(String outfile) {
		this.outfile = outfile;
	}

	/**
	 * @param outputDirectory
	 *            the outputDirectory to set
	 */
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	/**
	 * @param releaseid
	 *            the releaseid to set
	 */
	public void setReleaseid(String releaseid) {
		this.releaseid = releaseid;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param variables
	 *            the variables to set
	 */
	public void setVariables(String variables) {
		this.variables = variables;
	}

	/**
	 * @param wmsPassword
	 *            the wmsPassword to set
	 */
	public void setWmsPassword(String wmsPassword) {
		this.wmsPassword = wmsPassword;
	}

	/**
	 * @param wmsUsername
	 *            the wmsUsername to set
	 */
	public void setWmsUsername(String wmsUsername) {
		this.wmsUsername = wmsUsername;
	}

}
