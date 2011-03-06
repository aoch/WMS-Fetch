package com.convergys.wmsfetch.releasenotes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jxl.Cell;
import jxl.JXLException;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;

import com.convergys.wmsfetch.logic.ExcelUtil;
import com.convergys.wmsfetch.model.WMSItem;

/**
 * I must have been mad or drunk or both when I wrote this
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * 
 */
public class ReleaseNoteUtil {

	public static String TinaClient = "ALUMWL";
	public static String DukeClient = "Duke";
	public static String NCClient = "NUCLEUS CO";

	public static String TinaRelease = "CSM5.2.5";
	public static String DukeRelease = "CSM5.2.1.5";
	public static String NCRelease = "CSM5.2.2";

	public static String TinaSheet = "5.2.2 patches";
	public static String Duke5215Sheet = "5.2.1.2 patches";
	public static String Duke5216Sheet = "5.2.1.6 patches";
	public static String NCSheet = "5.2.2 patches";

	public String targetexcelfilename = "";
	public String newexcelfilename = "";

	/**
	 * 
	 * @return sheetMap
	 */
	public static HashMap<String, String> clientSheetMapbyClient() {
		HashMap<String, String> sheetMap = new HashMap<String, String>();
		sheetMap.put(TinaClient, TinaSheet);
		sheetMap.put(DukeClient, Duke5215Sheet);
		sheetMap.put(NCClient, NCClient);
		return sheetMap;
	}

	/**
	 * 
	 * @param patchName
	 * @param patchNamePattern
	 * @return
	 */
	public String getpatchNumberinPatchName(String patchName,
			String patchNamePattern) {
		int patchNumber = 1;

		if (patchName.matches(patchNamePattern)) {

			String pattern1 = "\\([a-zA-Z0-9\\s]*\\)";
			patchName = patchName.replaceAll(pattern1, "");
			String[] pathversiontemp = patchName.split("\\.");

			for (int i_version = (pathversiontemp.length - 1); i_version >= 0; i_version--) {
				String temp = pathversiontemp[i_version];

				if (temp.contains("v") || temp.contains("V")
						|| temp.contains("zip")) {
					continue;
				}
				return String.valueOf(patchNumber);
			}
		}
		return String.valueOf(patchNumber);
	}

	/**
	 * 
	 * @param trid
	 * @param filename
	 * @param sheetName
	 * @param releaseNumber
	 * @return patchNumber
	 * @throws JXLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String getpatchNumber(String trid, String filename,
			String sheetName, String releaseNumber) throws JXLException,
			FileNotFoundException, IOException {

		String patchNumber = "1";

		ExcelUtil eu = new ExcelUtil();
		WritableSheet ws = eu.getWritableWookbook(filename, sheetName);
		Cell[] tridcolumn = null;
		Cell[] patchcolumn = null;
		try {
			tridcolumn = ws.getColumn(1);
			patchcolumn = ws.getColumn(2);
		} catch (Exception e) {
			System.out.println("....asdf");
		}
		ArrayList countlist = new ArrayList();

		for (int i = 0; i < tridcolumn.length; i++) {
			String temptrid = tridcolumn[i].getContents();

			if (temptrid == null || temptrid.equals("")) {
				continue;
			}

			if (temptrid.length() < 3) {
				continue;
			}

			String patchName = patchcolumn[i].getContents();
			String patchNamePattern = ".*" + releaseNumber + ".*";
			String tempnumber = this.getpatchNumberinPatchName(patchName,
					patchNamePattern);
			System.out.println(".....row=" + i);
			System.out.println("tempnumber=" + tempnumber);
			if (Integer.parseInt(tempnumber) > Integer.parseInt(patchNumber)) {
				patchNumber = tempnumber;
			}

		} // end for
		patchNumber = String.valueOf(Integer.parseInt(patchNumber) + 1);
		return patchNumber;

	}

	/**
	 * 
	 * @param wmsitem
	 * @return patchVersionNo
	 * @throws BiffException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String getPatchVersionNo(WMSItem wmsitem) throws BiffException,
			FileNotFoundException, IOException {
		int number = 0;
		String trid = wmsitem.getId();
		String client = wmsitem.getClient();
		String targetsheetname = (String) ReleaseNoteUtil
				.clientSheetMapbyClient().get(client);

		ExcelUtil eu = new ExcelUtil();
		WritableSheet ws = eu.getWritableWookbook(targetexcelfilename,
				targetsheetname);
		Cell[] tridcolumn = ws.getColumn(1);

		for (int i = 0; i < tridcolumn.length; i++) {
			String existingtrid = String.valueOf(tridcolumn[i].getContents());
			if (existingtrid != null && !existingtrid.equals("")) {
				if (existingtrid.equals(trid)) {
					number++;
				}
			}
		}

		if (number != 0) {
			number++;
		}

		return String.valueOf(number);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getSystemDate() throws Exception {

		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd ");
		String dateString = formatter.format(currentTime);

		return dateString;
	}

	/**
	 * 
	 * @param wmsItem
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> generatePatchExcelContent(WMSItem wmsItem)
			throws Exception {
		ArrayList<String> contextList = new ArrayList<String>();
		String date = ReleaseNoteUtil.getSystemDate();
		String trid = wmsItem.getId();
		String title = wmsItem.getTitle();
		String clientName = wmsItem.getClient();
		String sheetName = (String) ReleaseNoteUtil.clientSheetMapbyClient()
				.get(clientName);
		String releaseNumber = (String) ReleaseNoteUtil.getReleaseNobyClient()
				.get(clientName);
		String patchNumber = this.getpatchNumber(trid,
				this.targetexcelfilename, sheetName, releaseNumber);
		String patchVersion = this.getPatchVersionNo(wmsItem);
		String patchFullName = "";
		String resolution = wmsItem.getResolution();
		String src = "";

		if (patchVersion.equals("0")) {
			patchFullName = ReleaseNoteUtil.getReleaseNobyClient().get(
					clientName)
					+ "." + patchNumber + ".V" + patchVersion + ".zip";
		} else {
			patchFullName = ReleaseNoteUtil.getReleaseNobyClient().get(
					clientName)
					+ "." + patchNumber + ".zip";
		}

		if (resolution.contains("1. Files change list:")
				&& resolution.contains("2. DB script file:")) {
			src = resolution.substring(resolution
					.indexOf("1. Files change list:"), resolution
					.indexOf("2. DB script file:"));
		} else {
			System.out.println("....................tr=" + trid
					+ "src not find");
		}
		contextList.add(date);
		contextList.add(trid);
		contextList.add(patchFullName);
		contextList.add(title);
		contextList.add(src);

		return contextList;
	}

	/**
	 * 
	 * @return nobyClient Who is Noby?
	 * @throws Exception
	 */
	public static HashMap<String, String> getReleaseNobyClient()
			throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TinaClient, TinaRelease);
		map.put(NCClient, NCRelease);
		map.put(DukeClient, DukeRelease);
		return map;
	}

	/**
	 * 
	 * @param wmsitem
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> createpatchRecord(WMSItem wmsitem)
			throws Exception {
		ArrayList<String> contentList = new ArrayList<String>();
		String date = this.getSystemDate();
		String patchName = (String) ReleaseNoteUtil.getReleaseNobyClient().get(
				wmsitem.getClient());
		String tilte = wmsitem.getId();
		String resolution = wmsitem.getResolution();
		String src = "";

		String[] temp = resolution.split("");
		return contentList;
	}

}