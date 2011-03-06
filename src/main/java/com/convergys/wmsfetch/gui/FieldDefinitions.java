package com.convergys.wmsfetch.gui;

import java.io.File;
import java.util.HashMap;

/**
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 *
 */
public class FieldDefinitions {

	private String wmsid;

	private String releaseid;

	private Boolean web;

	private String wmsUsername;

	private String wmsPassword;

	private String url;

	private File outfile;

	private HashMap<String, String> variableMap;

	/**
	 * @return the wmsid
	 */
	public String getWmsid() {
		return wmsid;
	}

	/**
	 * @param wmsid the wmsid to set
	 */
	public void setWmsid(String wmsid) {
		this.wmsid = wmsid;
	}

	/**
	 * @return the releaseid
	 */
	public String getReleaseid() {
		return releaseid;
	}

	/**
	 * @param releaseid the releaseid to set
	 */
	public void setReleaseid(String releaseid) {
		this.releaseid = releaseid;
	}

	/**
	 * @return the web
	 */
	public Boolean getWeb() {
		return web;
	}

	/**
	 * @param web the web to set
	 */
	public void setWeb(Boolean web) {
		this.web = web;
	}

	/**
	 * @return the wmsUsername
	 */
	public String getWmsUsername() {
		return wmsUsername;
	}

	/**
	 * @param wmsUsername the wmsUsername to set
	 */
	public void setWmsUsername(String wmsUsername) {
		this.wmsUsername = wmsUsername;
	}

	/**
	 * @return the wmsPassword
	 */
	public String getWmsPassword() {
		return wmsPassword;
	}

	/**
	 * @param wmsPassword the wmsPassword to set
	 */
	public void setWmsPassword(String wmsPassword) {
		this.wmsPassword = wmsPassword;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the outfile
	 */
	public File getOutfile() {
		return outfile;
	}

	/**
	 * @param outfile the outfile to set
	 */
	public void setOutfile(File outfile) {
		this.outfile = outfile;
	}

	/**
	 * @return the variableMap
	 */
	public HashMap<String, String> getVariableMap() {
		return variableMap;
	}

	/**
	 * @param variableMap the variableMap to set
	 */
	public void setVariableMap(HashMap<String, String> variableMap) {
		this.variableMap = variableMap;
	}
}
