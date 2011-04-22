package com.convergys.wmsfetch.releasenotes;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.convergys.wmsfetch.model.WMSItem;
import com.convergys.wmsfetch.net.IWMSClient;

/**
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class WMSReleaseNoteMgr {

	public enum Type {
		PATCH, RELEASE
	}

	private static transient final Logger logger = Logger
			.getLogger(WMSReleaseNoteMgr.class);

	private IWMSClient wmsClient;

	private IReportGenerator releaseNoteGenerator;

	private OutputStream output;

	/**
	 * 
	 * @param wmsClient
	 * @param releaseNoteGenerator
	 */
	public WMSReleaseNoteMgr(IWMSClient wmsClient,
			IReportGenerator releaseNoteGenerator) {
		// TODO Use Google Guice to Inject this dependency!
		this.releaseNoteGenerator = releaseNoteGenerator;
		this.wmsClient = wmsClient;
	}

	/**
	 * @see com.convergys.wmsfetch.releasenotes#WMSReleaseNoteMgr(String
	 *      username, String password, String wmsID, String url)
	 * @param username
	 * @param password
	 * @param wmsReleaseID
	 * @return success
	 */
	@Deprecated
	public boolean generateReleaseNotes(String username, String password,
			String wmsReleaseID) {
		return generateReleaseNotes(username, password, wmsReleaseID, null);
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @param wmsReleaseID
	 * @param url
	 * @return success
	 */
	@Deprecated
	public boolean generateReleaseNotes(String username, String password,
			String wmsReleaseID, String url) {
		return generateReleaseNotes(Type.PATCH, username, password,
				wmsReleaseID, null, null);
	}

	/**
	 * 
	 * @param type
	 * @param username
	 * @param password
	 * @param wmsReleaseID
	 * @param url
	 * @param variablesMap
	 * @return success
	 */
	public boolean generateReleaseNotes(Type type, String username,
			String password, String wmsReleaseID, String url,
			Map<String, String> variablesMap) {
		boolean success = false;

		List<WMSItem> wmsItems = null;
		if (releaseNoteGenerator != null && wmsClient != null) {
			wmsClient.setUsername(username);
			wmsClient.setPassword(password);
			wmsClient.setUrl(url);
			wmsItems = wmsClient.fetch(wmsReleaseID);
		} else {
			logger.fatal(String.format(
					"releaseNoteGenerator is null: %s wmsClient is null: %s",
					new Object[] { releaseNoteGenerator == null,
							wmsClient == null }));
		}

		if (wmsItems != null) {
			releaseNoteGenerator.addVariables(variablesMap);
			if (type == Type.RELEASE) {
				Map<String, String> variables = new HashMap<String, String>();
				String version = WMSItem.getVersion(wmsReleaseID);
				variables.put("version", version);
				variables.put("release", wmsReleaseID);
				releaseNoteGenerator.addVariables(variables);
				success = releaseNoteGenerator.generate(wmsItems, getOutput());
			} else {
				if (wmsItems.size() == 1 && type == Type.PATCH) {
					success = releaseNoteGenerator.generate(wmsItems.get(0),
							getOutput());
				} else {
					String errMsg = String.format(
							"WMS Item: [%s] could not be found.",
							new Object[] { wmsReleaseID });
					logger.error(errMsg);
				}
			}
		} else {
			logger.fatal("wmsItems is null");
		}
		return success;
	}

	/**
	 * @return the output
	 */
	public OutputStream getOutput() {
		return output;
	}

	/**
	 * @return the releaseNoteGenerator
	 */
	public IReportGenerator getReleaseNoteGenerator() {
		return releaseNoteGenerator;
	}

	/**
	 * @return the wmsClient
	 */
	public IWMSClient getWmsClient() {
		return wmsClient;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(OutputStream output) {
		this.output = output;
	}

	/**
	 * @param releaseNoteGenerator
	 *            the releaseNoteGenerator to set
	 */
	public void setReleaseNoteGenerator(IReportGenerator releaseNoteGenerator) {
		this.releaseNoteGenerator = releaseNoteGenerator;
	}

	/**
	 * @param wmsClient
	 *            the wmsClient to set
	 */
	public void setWmsClient(IWMSClient wmsClient) {
		this.wmsClient = wmsClient;
	}

	/**
	 * 
	 * @param eol
	 *            the end-of-line character
	 */
	public void setEOL(String eol) {
		releaseNoteGenerator.setEOL(eol);
	}

}
