package com.convergys.wmsfetch.releasenotes;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.convergys.wmsfetch.model.WMSItem;

/**
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public interface IReportGenerator {

	/**
	 * 
	 * @param wmsItem
	 * @param output
	 * @return generated
	 */
	public boolean generate(WMSItem wmsItem, OutputStream output);

	/**
	 * 
	 * @param wmsItems
	 * @param output
	 * @return generated
	 */
	public boolean generate(List<WMSItem> wmsItems, OutputStream output);

	/**
	 * Set the End of Line character to use
	 * 
	 * @param eol
	 */
	public void setEOL(String eol);

	/**
	 * 
	 * @return templateFilename
	 */
	public String getTemplateFilename();

	/**
	 * 
	 * @param templateFilename
	 */
	public void setTemplateFilename(String templateFilename);

	/**
	 * 
	 * @param variableMap
	 */
	public void addVariables(Map<String, String> variableMap);
}