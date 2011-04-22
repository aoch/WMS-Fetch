package com.convergys.wmsfetch.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulate a WMS Item and all its information. Well at least what is
 * pertinant to release notes
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class WMSItem implements Serializable {
	private static final long serialVersionUID = 8479413501845185543L;

	// Default Empty string value
	public static final String EMPTY = "\t";

	// Hash Map keys for DB to populate Velocity template variables
	public static final String wmsItemIDKey = "item_id";
	public static final String typeKey = "item_type";
	public static final String descriptionKey = "DESCRIPTION";
	public static final String titleKey = "title";
	public static final String internalReferKey = "internalRefer";
	public static final String causeKey = "rootCause";
	public static final String resolutionKey = "resolutionSummary";
	public static final String clientKey = "clientName";
	public static final String clientRefKey = "CLIENT_REFERENCE";
	public static final String productKey = "component";
	public static final String dateKey = "date";
	public static final String releaseKey = "external_reference";
	public static final String severityKey = "CLIENT_PRIORITY";
	public static final String statusKey = "STATUS_NAME";
	public static final String developerKey = "RESPONSIBLE_ANALYST";

	/**
	 * 
	 * @param release
	 * @return version
	 */
	public static String getVersion(String release) {
		String[] versionDirty = release.split("\\.");
		StringBuffer version = new StringBuffer("");
		for (int i = 0; i < versionDirty.length; i++) {
			if (i > 0) {
				version.append(versionDirty[i] + ".");
			}

			if (i == versionDirty.length - 1) {
				version.deleteCharAt(version.length() - 1);
			}
		}
		return version.toString();
	}

	private String id;

	private String title;

	private String description;

	private String client;

	private String clientRef;

	private String resolution;

	private String rootcause;

	private String patchNumber;

	private String patchVersion;

	private String product;

	private String release;

	private String severity;

	private String status;

	private String developer;

	/**
	 * @return the developer
	 */
	public String getDeveloper() {
		return developer;
	}

	/**
	 * @param developer
	 *            the developer to set
	 */
	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	/**
	 * Empty constructor
	 */
	public WMSItem() {
	}

	/**
	 * Build constructor from a Map
	 * 
	 * @param itemList
	 */
	public WMSItem(Map<String, String> itemList) {
		setId(itemList.get(WMSItem.wmsItemIDKey));
		setDescription(itemList.get(WMSItem.descriptionKey));
		setTitle(itemList.get(WMSItem.titleKey));
		setResolution(itemList.get(WMSItem.resolutionKey));
		setClient(itemList.get(WMSItem.clientKey));
		setClientRef(itemList.get(WMSItem.clientRefKey));
		setRootcause(itemList.get(WMSItem.causeKey));
		setProduct(itemList.get(WMSItem.productKey));
		setRelease(itemList.get(WMSItem.releaseKey));
		setStatus(itemList.get(WMSItem.statusKey));
		setDeveloper(itemList.get(WMSItem.developerKey));
	}

	public String getClient() {
		return client;
	}

	/**
	 * @return the clientRef
	 */
	public String getClientRef() {
		return clientRef;
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}

	public String getPatchNumber() {
		return patchNumber;
	}

	public String getPatchVersion() {
		return patchVersion;
	}

	/**
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}

	/**
	 * @return the release
	 */
	public String getRelease() {
		return release;
	}

	public String getResolution() {
		return resolution;
	}

	/**
	 * @return the rootcause
	 */
	public String getRootcause() {
		return rootcause;
	}

	/**
	 * @return the severity
	 */
	public String getSeverity() {
		return severity;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 
	 * @return variableMap
	 */
	public HashMap<String, String> getVariableMap() {
		HashMap<String, String> variables = new HashMap<String, String>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String date = formatter.format(new Date());
		variables.put(dateKey, date);
		variables.put(wmsItemIDKey, this.id);
		variables.put(titleKey, this.title);
		variables.put(descriptionKey, this.description);
		variables.put(causeKey, this.rootcause);
		variables.put(resolutionKey, this.resolution);
		variables.put(clientKey, this.client);
		variables.put(clientRefKey, this.clientRef);
		variables.put(productKey, this.product);
		variables.put(releaseKey, this.release);
		return variables;
	}

	public void setClient(String client) {
		this.client = client;
	}

	/**
	 * If set to null, replaced with <code>WMSItem.EMPTY</code> otherwise value
	 * enclosed in square brackets
	 * 
	 * @param clientRef
	 *            the clientRef to set
	 */
	public void setClientRef(String clientRef) {
		if (clientRef == null) {
			this.clientRef = WMSItem.EMPTY;
		} else {
			this.clientRef = "[" + clientRef + "]";
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPatchNumber(String patchNumber) {
		this.patchNumber = patchNumber;
	}

	public void setPatchVersion(String patchVersion) {
		this.patchVersion = patchVersion;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
	}

	/**
	 * @param release
	 *            the release to set
	 */
	public void setRelease(String release) {
		this.release = release;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	/**
	 * @param rootcause
	 *            the rootcause to set
	 */
	public void setRootcause(String rootcause) {
		this.rootcause = rootcause;
	}

	/**
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(String severity) {
		this.severity = severity;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Id: " + id + "\n");
		sb.append("Title: " + title + "\n");
		sb.append("Description: " + description + "\n");
		sb.append("Client: " + client + "\n");
		sb.append("Client Ref: " + clientRef + "\n");
		sb.append("Resolution: " + resolution + "\n");
		sb.append("Rootcause: " + rootcause + "\n");
		sb.append("Severity: " + severity + "\n");
		sb.append("Developer: " + developer + "\n");
		sb.append("Release: " + release + "\n");
		return sb.toString();
	}

}