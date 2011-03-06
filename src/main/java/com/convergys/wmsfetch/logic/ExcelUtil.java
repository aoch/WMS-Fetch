package com.convergys.wmsfetch.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * 
 * 
 * @author Jingang
 * 
 */
public class ExcelUtil {
	private static transient final Logger logger = Logger
			.getLogger(ExcelUtil.class);

	public String newfilename = "newPatch";

	/**
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws BiffException
	 * @see
	 */
	public int getmaxrownumber(String filename, String sheetname)
			throws BiffException, FileNotFoundException, IOException {
		int rownumber = 0;
		Workbook wb = Workbook.getWorkbook(new FileInputStream(filename));
		Sheet sheet = wb.getSheet(sheetname);
		rownumber = sheet.getRows();

		return rownumber;
	}

	/**
	 * 
	 * @param filename
	 * @param targetsheetname
	 * @return
	 * @throws BiffException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public WritableSheet getWritableWookbook(String filename,
			String targetsheetname) throws BiffException,
			FileNotFoundException, IOException {

		Workbook wb = Workbook.getWorkbook(new FileInputStream(filename));
		WritableWorkbook wwb = Workbook.createWorkbook(new File(newfilename),
				wb);
		WritableSheet ws = null;

		for (int numSheets = 0; numSheets < wwb.getNumberOfSheets(); numSheets++) {
			ws = wwb.getSheet(numSheets);
			String sheetName = ws.getName();
			if (sheetName == null || sheetName == "") {
				break;
			}
			System.out.println("sheetName=" + sheetName);
			System.out.println("targetsheetname=" + targetsheetname);
			if (sheetName.equals(targetsheetname)) {
				System.out.println(".........ws=" + ws);
				return ws;

			}
		}
		return null;
	}

	/**
	 * 
	 * @param wb
	 * @param wwb
	 * @throws WriteException
	 * @throws IOException
	 */
	public static void closeallsteam(Workbook wb, WritableWorkbook wwb)
			throws WriteException, IOException {
		if (wb != null)
			wb.close();
		if (wwb != null)
			wwb.close();
	}

	/**
	 * 
	 * @param sheet
	 * @param numrow
	 * @return
	 */
	public static ArrayList readrow(WritableSheet sheet, int numrow) {
		ArrayList contentlist = new ArrayList();
		for (int numcol = 0; numcol < sheet.getColumns(); numcol++) {
			Cell cell = sheet.getCell(numcol, numrow);
			String content = cell.getContents();
			contentlist.add(content);
		}
		return contentlist;
	}

	/**
	 * @see read excel by row,read sheet
	 * @param sheet
	 * @param numrow
	 * @return
	 */
	public static ArrayList readrow(Sheet sheet, int numrow) {
		ArrayList contentlist = new ArrayList();
		for (int numcol = 0; numcol < sheet.getColumns(); numcol++) {
			Cell cell = sheet.getCell(numcol, numrow);
			String content = cell.getContents();
			contentlist.add(content);
		}
		return contentlist;
	}

	/**
	 * 
	 * @param ws
	 * @param numrow
	 * @param contentList
	 * @return
	 */
	public static boolean writebyrow(WritableSheet ws, int numrow,
			ArrayList contentList) {

		try {
			for (int i = 0; i < contentList.size(); i++) {
				System.out.println(".....ws=" + ws.getName());
				String cellcontent = (String) contentList.get(i);
				System.out.println(".....cellcontent=" + cellcontent);
				jxl.write.Label labelC = new jxl.write.Label(i, numrow,
						cellcontent);
				ws.addCell(labelC);
			}

		} catch (Exception e) {
			System.out.println("Exception is " + e);
		}
		return true;
	}
}
