package com.convergys.wmsfetch.net;

import java.util.List;

import com.convergys.wmsfetch.model.WMSItem;

/**
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public interface IWMSClient {

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url);

	/**
	 * 
	 * @param username
	 */
	public void setUsername(String username);

	/**
	 * 
	 * @param password
	 */
	public void setPassword(String password);

	/**
	 * 
	 * @param wmsReleaseID
	 * @return wmsItems
	 */
	public List<WMSItem> fetch(String wmsReleaseID);
}
