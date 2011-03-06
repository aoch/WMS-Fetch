package com.convergys.wmsfetch.build.maven;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.convergys.wmsfetch.build.common.WMSFetchBuild;

/**
 * Goal which connects to WMS and creates release notes
 * 
 * <code>
<plugin>
	<groupId>com.convergys</groupId>
	<artifactId>wmsfetch</artifactId>
	<version>1.0.4</version>
	<executions>
		<execution>
			<phase>package</phase>
			<goals>
				<goal>releaseNotes</goal>
			</goals>
		</execution>
	</executions>
	<configuration>
		<wmsUsername>aoch</wmsUsername>
		<wmsPassword>T6y7u8i9</wmsPassword>
		<mailServerUsername>andrew.och@converygs.com</mailServerUsername>
		<mailServerPassword>secret</mailServerPassword>
		<releaseid>AWCC.0.0.2.126</releaseid>
		<outfile>AWCC.0.0.2.126.txt</outfile>
		<emails>
			<param>andrew.och@convergys.com</param>
			<param>charlie.xiongya.you@convergys.com</param>
		</emails>
		<variableMap>
			<dependencyVersions>EBOS-4.4.3</dependencyVersions>
			<test>value</test>
		</variableMap>
	</configuration>
</plugin>
</code>
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 * @goal releaseNotes
 * @phase package
 */
public class WMSFetchMojo extends AbstractMojo {
	private static transient final Logger logger = Logger
			.getLogger(WMSFetchMojo.class);

	/**
	 * Location of the Release Notes file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * The username used to log into WMS
	 * 
	 * @parameter expression="${releaseNotes.wmsUsername}"
	 *            default-value="Unknown"
	 */
	private String wmsUsername;

	/**
	 * The password used to log into WMS
	 * 
	 * @parameter expression="${releaseNotes.wmsPassword}"
	 *            default-value="secret"
	 */
	private String wmsPassword;

	/**
	 * The username used to log into the Mail server
	 * 
	 * @parameter expression="${releaseNotes.mailServerUsername}"
	 *            default-value="Unknown"
	 */
	private String mailServerUsername;

	/**
	 * The password used to log into the Mail server
	 * 
	 * @parameter expression="${releaseNotes.mailServerPassword}"
	 *            default-value="secret"
	 */
	private String mailServerPassword;

	/**
	 * Retrieve data on Release Version and create Patch Release Notes e.g.
	 * <code>AWCC-WSC.0.1.125</code>
	 * 
	 * @parameter expression="${releaseNotes.releaseid}"
	 *            default-value="AWCC.0.0.2.125"
	 */
	private String releaseid;

	/**
	 * The URL used to log into WMS Website e.g. The
	 * <code>IP_address:port</code> of Database e.g.
	 * <code>155.90.47.41:1521</code>
	 * 
	 * @parameter expression="${releaseNotes.url}"
	 *            default-value="155.90.47.41:1521"
	 */
	private String url;

	/**
	 * The emails to send the release notes to
	 * 
	 * @parameter
	 */
	private String[] emails;

	/**
	 * The name and path of the file to create the Release Notes
	 * 
	 * @parameter expression="${releaseNotes.outfile}"
	 *            default-value="AWCC.0.0.2.125.txt"
	 */
	private String outfile;

	/**
	 * Variable Map
	 * 
	 * @parameter
	 */
	private Map<String, String> variableMap;

	/**
	 * The method called by maven when goal is executed
	 */
	public void execute() throws MojoExecutionException {
		boolean success = WMSFetchBuild.execReleaseNotes(outputDirectory,
				outfile, wmsUsername, wmsPassword, releaseid, emails,
				mailServerUsername, mailServerPassword, url, variableMap);

		if (success) {
			String infoMsg = String.format("ReleaseNotes for: [%s] complete",
					new Object[] { releaseid });
			logger.info(infoMsg);
		} else {
			String warnMsg = String.format("ReleaseNotes for: [%s] failed",
					new Object[] { releaseid });
			logger.warn(warnMsg);
		}
	}
}
