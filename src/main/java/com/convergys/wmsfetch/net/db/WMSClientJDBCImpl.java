/**
 * 
 */
package com.convergys.wmsfetch.net.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.convergys.wmsfetch.console.ProgressReporter;
import com.convergys.wmsfetch.model.WMSItem;
import com.convergys.wmsfetch.net.IWMSClient;

/**
 * Uses JDBC to retrieve WMS data from database back end
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class WMSClientJDBCImpl implements IWMSClient {

	private static transient final Logger logger = Logger
			.getLogger(WMSClientJDBCImpl.class);

	public static final String THIN_CLIENT = "jdbc:oracle:thin:@";

	public static final String SERVICE_NAME = "(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=%s) (PORT=%s)))(CONNECT_DATA=(SERVICE_NAME=WMS.WORLD)))";

	private String username;

	private String password;

	private String url;

	private Connection connection = null;

	private Statement statement = null;

	private ResourceBundle resourceBundle;

	public WMSClientJDBCImpl() {
		resourceBundle = ResourceBundle
				.getBundle("com.convergys.wmsfetch.net.wmsConfig");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.convergys.wmsfetch.net.IWMSClient#fetch(java.lang.String)
	 */
	public List<WMSItem> fetch(String wmsReleaseID) {
		List<WMSItem> wmsItems = new ArrayList<WMSItem>();
		ProgressReporter progressReporter = new ProgressReporter();

		// Start Progress bar
		progressReporter.startProgress();
		Thread.yield();

		try {
			// WMS Item ID
			if (isValidWMSID(wmsReleaseID)) {
				wmsItems = queryWMSItem(wmsReleaseID);
			}
			// Probably a release ID
			else {
				wmsItems = queryRelease(wmsReleaseID);
			}
		} catch (Exception e) {
			progressReporter.killProgress();
			logger.fatal(e.getLocalizedMessage());
		}

		// Finish the progress bar
		progressReporter.stopProgress();
		return wmsItems;
	}

	/**
	 * 
	 * @param wmsReleaseID
	 * @return valid
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
	 * This does NOT close the Statement
	 * 
	 * @param url
	 * @param username
	 * @param password
	 * @param sql
	 * @return resultSet
	 */
	public ResultSet query(String url, String username, String password,
			String sql) {
		ResultSet resultSet = null;
		try {
			if (connection == null && statement == null) {
				Class.forName(resourceBundle.getString("db_driver"));
				connection = DriverManager.getConnection(url, username,
						password);
				statement = connection.createStatement();
			}
			resultSet = statement.executeQuery(sql);
		} catch (Exception e) {
			logger.fatal(e.getLocalizedMessage());
		}
		return resultSet;
	}

	private List<WMSItem> queryRelease(String wmsReleaseID) throws Exception {

		String queryStr = String
				.format(
						"SELECT %s,item_type,%s,%s,%s,%s,INTERNAL_PRIORITY,%s,REQUESTED_DELIVERY_DATE,COMMITMENT_DATE,%s,%s "
								+ "FROM item_rpt "
								+ "WHERE %s = '%s' AND item_type = 'TR' ORDER BY CLIENT_PRIORITY",
						new Object[] { WMSItem.wmsItemIDKey,
								WMSItem.clientRefKey, WMSItem.statusKey,
								WMSItem.titleKey, WMSItem.severityKey,
								WMSItem.developerKey, WMSItem.releaseKey,
								WMSItem.descriptionKey, WMSItem.releaseKey,
								wmsReleaseID });
		logger.info(queryStr);
		List<WMSItem> wmsItems = buildWMSItems(query(url, username, password,
				queryStr));
		return wmsItems;
	}

	private List<WMSItem> queryWMSItem(String wmsItemID) throws Exception {

		String queryStr = String
				.format(
						"SELECT %s,$s,%s,%s,%s,%s,INTERNAL_PRIORITY,%s,REQUESTED_DELIVERY_DATE,COMMITMENT_DATE,%s,%s "
								+ "FROM item_rpt "
								+ "WHERE  %s = 'TR' "
								+ "AND %s = '%s'", new Object[] {
								WMSItem.wmsItemIDKey, WMSItem.typeKey,
								WMSItem.clientRefKey, WMSItem.statusKey,
								WMSItem.titleKey, WMSItem.severityKey,
								WMSItem.developerKey, WMSItem.releaseKey,
								WMSItem.descriptionKey, WMSItem.typeKey,
								WMSItem.wmsItemIDKey, wmsItemID });
		logger.debug(queryStr);
		List<WMSItem> wmsItems = buildWMSItems(query(url, username, password,
				queryStr));
		return wmsItems;
	}

	private List<WMSItem> buildWMSItems(ResultSet resultSet) throws Exception {
		List<WMSItem> wmsItems = new ArrayList<WMSItem>();
		while (resultSet.next()) {
			WMSItem wmsItem = new WMSItem();
			wmsItem.setRelease(resultSet.getString(WMSItem.releaseKey));
			wmsItem.setTitle(resultSet.getString(WMSItem.titleKey));
			wmsItem.setId(resultSet.getString(WMSItem.wmsItemIDKey));
			wmsItem.setDescription(resultSet.getString(WMSItem.descriptionKey));
			wmsItem.setProduct(resultSet.getString(WMSItem.releaseKey));
			wmsItem.setSeverity(resultSet.getString("CLIENT_PRIORITY"));
			wmsItem.setStatus(resultSet.getString("STATUS_NAME"));
			wmsItem.setDeveloper(resultSet.getString(WMSItem.developerKey));
			wmsItem.setClientRef(resultSet.getString(WMSItem.clientRefKey));
			wmsItems.add(wmsItem);
		}
		return wmsItems;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.convergys.wmsfetch.net.IWMSClient#setPassword(java.lang.String)
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.convergys.wmsfetch.net.IWMSClient#setUrl(java.lang.String)
	 */
	public void setUrl(String url) {
		String ip = "";
		String port = "";
		String ipPort = "";

		// User defined
		if (url != null && !url.equalsIgnoreCase("") && url.contains(":")) {
			ipPort = url;

		}
		// Default
		else {
			ipPort = resourceBundle.getString("url_database");
		}

		String[] ipPortArr = ipPort.split(":");
		ip = ipPortArr[0];
		port = ipPortArr[1];
		this.url = THIN_CLIENT
				+ String.format(SERVICE_NAME, new Object[] { ip, port });
		String infoMsg = String.format(
				"url is null or empty, setting default value: %s",
				new Object[] { this.url });
		logger.info(infoMsg);
	}

	public String getUrl() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.convergys.wmsfetch.net.IWMSClient#setUsername(java.lang.String)
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public static void main(String[] args) {
		WMSClientJDBCImpl wmsClient = new WMSClientJDBCImpl();
		wmsClient.setUrl(null);
		String url = wmsClient.getUrl();
		String username = "aoch";
		String password = "ern8UwzE";
		String sql = "SELECT item_id, item_type, STATUS_NAME, title, CLIENT_PRIORITY,INTERNAL_PRIORITY,RESPONSIBLE_ANALYST,REQUESTED_DELIVERY_DATE,COMMITMENT_DATE,external_reference,DESCRIPTION "
				+ "FROM item_rpt "
				+ "WHERE external_reference like '%WSC%' "
				+ "AND item_type = 'TR' " + "ORDER BY CLIENT_PRIORITY";

		ResultSet rs = wmsClient.query(url, username, password, sql);
		System.out.println("Started");
		try {
			while (rs.next()) {
				System.out.println(rs.getString("item_id"));
			}
		} catch (SQLException e) {
			logger.error(e.getLocalizedMessage());
		}
		System.out.println("Complete");
	}

	/**
	 * Print out all column names for a result set
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	public static void getColumnNames(ResultSet resultSet) throws SQLException {
		if (resultSet == null) {
			return;
		}

		// get result set meta data
		ResultSetMetaData rsMetaData = resultSet.getMetaData();
		int numberOfColumns = rsMetaData.getColumnCount();

		// get the column names; column indexes start from 1
		for (int i = 1; i < numberOfColumns + 1; i++) {
			String columnName = rsMetaData.getColumnName(i);
			// Get the name of the column's table name
			String tableName = rsMetaData.getTableName(i);
			System.out.println("column name=" + columnName + " table="
					+ tableName + "");
		}
	}
}
