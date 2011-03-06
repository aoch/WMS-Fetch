package com.convergys.wmsfetch.net.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.convergys.wmsfetch.console.ProgressReporter;
import com.convergys.wmsfetch.model.WMSItem;
import com.convergys.wmsfetch.net.IWMSClient;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlScript;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;

/**
 * Uses HtmlUnit to retrieve WMS data in head-less fashion
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
@Deprecated
public class WMSClientHtmlUnitImpl implements IWMSClient {

	private static transient final Logger logger = Logger
			.getLogger(WMSClientHtmlUnitImpl.class);

	private ResourceBundle resourceBundle;

	private String url;

	// Page element IDs
	private String descriptionElemID;
	private String titleElemID;
	private String internalReferElemID;
	private String causeElemID;
	private String resolutionElemID;
	private String clientElemID;
	private String clientRefElemID;
	private String productElemID;

	private String username;

	private String password;

	private ProgressReporter progressReporter;

	/**
	 * Constructor read variables from properties file
	 */
	public WMSClientHtmlUnitImpl() {
		progressReporter = new ProgressReporter();
		resourceBundle = ResourceBundle
				.getBundle("com.convergys.wmsfetch.net.wmsConfig");
		initConfigVars();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.convergys.wmsfetch.model.WMSItem#fetch(java.lang.String)
	 */
	// @Override
	public List<WMSItem> fetch(String wmsReleaseID) {
		List<WMSItem> wmsItems = new ArrayList<WMSItem>();
		if (isValidWMSID(wmsReleaseID)) {
			Map<String, String> wmsInfo = retrieveInfoWMS(wmsReleaseID);
			wmsItems.add(new WMSItem(wmsInfo));
		} else {
			String errMsg = String
					.format(
							"WMS ID: [%s] invalid. Web mode does not support full release.",
							new Object[] { wmsReleaseID });
			logger.fatal(errMsg);
		}
		return wmsItems;
	}

	/**
	 * 
	 * @param wmsReleaseID
	 * @return validWMSID
	 */
	public boolean isValidWMSID(String wmsReleaseID) {
		// It cannot contain only numbers if it's null or empty...
		if (wmsReleaseID == null || wmsReleaseID.length() == 0) {
			return false;
		}

		// WMS id are quite long
		if (wmsReleaseID.length() < 7 || wmsReleaseID.length() > 11) {
			return false;
		}

		for (int i = 0; i < wmsReleaseID.length(); i++) {
			// if we find a non-digit character or it not a period return false.
			char c = wmsReleaseID.charAt(i);
			if (!(Character.isDigit(c) || c == '.')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieve value of Input field, with WMS unusual functionality which
	 * requires using XPath to find JavaScript with embedded values
	 * 
	 * @param page
	 * @param elemName
	 * @return value
	 */
	public String getInputFieldValue(HtmlPage page, String elemName) {
		String result = "";
		String type = "script";
		page.getElementByName(elemName);
		String xPath = "//" + type;
		try {
			List<HtmlScript> inputFields = (List<HtmlScript>) page
					.getByXPath(xPath);
			for (HtmlScript html : inputFields) {
				HtmlScript htmlScript = html;
				String content = htmlScript.asXml();
				if (content.contains(elemName)) {
					result = parseInputFieldValue(elemName, content);
				}
			}
		} catch (IndexOutOfBoundsException e) {
			String errorMsg = String.format(
					"Cannot retrieve value from [%s] for element [%s]",
					new Object[] { type, elemName });
			logger.error(errorMsg);
		}
		return result;
	}

	/**
	 * @return the resourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * Retrieve value of Text Area based on attribute name using XPath
	 * 
	 * @param page
	 * @param elemName
	 * @return value
	 */
	public String getTextAreaFieldValue(HtmlPage page, String elemName) {
		String value = "";
		String type = "textarea";
		String xPath = "//" + type + "[@name='" + elemName + "']";
		try {
			HtmlTextArea htmlTextArea = (HtmlTextArea) page.getByXPath(xPath)
					.get(0);
			value = htmlTextArea.getText();
		} catch (IndexOutOfBoundsException e) {
			String errorMsg = String.format(
					"Cannot retrieve value from [%s] for element [%s]",
					new Object[] { type, elemName });
			logger.error(errorMsg);
		}
		return value;
	}

	/**
	 * Initialize variables from properties file. Split out from constructor in
	 * case as resource bundle is replaced at runtime
	 */
	public void initConfigVars() {
		url = resourceBundle.getString("url_website");
		descriptionElemID = resourceBundle.getString("description");
		titleElemID = resourceBundle.getString("title");
		internalReferElemID = resourceBundle.getString("internalRefer");
		causeElemID = resourceBundle.getString("cause");
		resolutionElemID = resourceBundle.getString("resolution");
		clientElemID = resourceBundle.getString("client");
		clientRefElemID = resourceBundle.getString("clientRef");
		productElemID = resourceBundle.getString("product");
	}

	/**
	 * Splitting content using element name to parse for value
	 * 
	 * @param elemName
	 * @param content
	 * @return value
	 */
	public String parseInputFieldValue(String elemName, String content) {
		String value = "";
		String patternString = "edit_layout." + elemName + ".value=\"(.*)\"";
		Pattern pattern = Pattern.compile(patternString);

		Matcher matcher = pattern.matcher(content);

		boolean found = false;
		while (matcher.find()) {
			if (matcher.groupCount() >= 1) {
				value = matcher.group(1);
				logger.debug("Group: " + matcher.group());
			}
			found = true;
			// Found leave loop
			break;
		}
		if (!found) {
			logger.warn(String.format("Value not found for field: [%s]",
					new Object[] { elemName }));
		}
		return value;
	}

	protected Map<String, String> retrieveInfoWMS(String wmsItemID) {
		Map<String, String> wmsInfo = new HashMap<String, String>();

		try {
			// Use HtmlUnit to connect to webpage and retrieve data
			final WebClient webClient = new WebClient(
					BrowserVersion.INTERNET_EXPLORER_8);

			// Suppress JavaScript exceptions (of which there are many in WMS!)
			webClient.setThrowExceptionOnScriptError(false);

			// Set username and password
			final DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient
					.getCredentialsProvider();
			credentialsProvider.addCredentials(username, password);

			logger.info(url + wmsItemID);

			progressReporter.startProgress();
			Thread.yield();

			// Add the wms item id to the url
			HtmlPage page = webClient.getPage(url + wmsItemID);

			// Parse the HTML to get data from WMS
			String description = getTextAreaFieldValue(page, descriptionElemID);
			String title = getInputFieldValue(page, titleElemID);
			String internalRefer = getInputFieldValue(page, internalReferElemID);
			String cause = getTextAreaFieldValue(page, causeElemID);
			String resolution = getTextAreaFieldValue(page, resolutionElemID);
			String client = getInputFieldValue(page, clientElemID);
			String clientRef = getInputFieldValue(page, clientRefElemID);
			String component = getInputFieldValue(page, this.productElemID);

			// Close session
			webClient.closeAllWindows();

			// Finish the progress
			progressReporter.stopProgress();

			wmsInfo.put(WMSItem.wmsItemIDKey, wmsItemID);
			wmsInfo.put(WMSItem.descriptionKey, description);
			wmsInfo.put(WMSItem.titleKey, title);
			wmsInfo.put(WMSItem.internalReferKey, internalRefer);
			wmsInfo.put(WMSItem.causeKey, cause);
			wmsInfo.put(WMSItem.resolutionKey, resolution);
			wmsInfo.put(WMSItem.clientKey, client);
			wmsInfo.put(WMSItem.clientRefKey, clientRef);
			wmsInfo.put(WMSItem.productKey, component);
		} catch (FailingHttpStatusCodeException e) {
			progressReporter.killProgress();
			if (e.getStatusCode() == 401) {
				System.out
						.println("Authorization failed. Incorrect username or password");
			}
			logger.fatal(e.getLocalizedMessage());
		} catch (MalformedURLException e) {
			logger.fatal(e.getLocalizedMessage());
		} catch (IOException e) {
			logger.fatal(e.getLocalizedMessage());
		}

		return wmsInfo;
	}

	// @Override
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Re-initialises all variables
	 * 
	 * @param resourceBundle
	 *            the resourceBundle to set
	 */
	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
		initConfigVars();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see void#setUrl(java.lang.String)
	 */
	// @Override
	public void setUrl(String url) {
		if (url == null || url.equalsIgnoreCase("")) {
			String urlTemp = resourceBundle.getString("url_website");
			String infoMsg = String.format(
					"url is null or empty, setting default value: %s",
					new Object[] { urlTemp });
			logger.info(infoMsg);
			this.url = urlTemp;
		} else {
			String urlSuffix = resourceBundle.getString("url_wms_suffix");
			this.url = url + urlSuffix;
		}
		logger.info("Website full URL: " + this.url);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see void#setUsername(java.lang.String)
	 */
	// @Override
	public void setUsername(String username) {
		this.username = username;
	}

}
