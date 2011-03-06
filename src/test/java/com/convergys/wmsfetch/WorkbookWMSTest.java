package com.convergys.wmsfetch;

import java.io.File;
import java.io.FileInputStream;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.convergys.wmsfetch.logic.ExcelUtil;
import com.convergys.wmsfetch.releasenotes.ReleaseNoteUtil;

public class WorkbookWMSTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void readWorkbook() {
		ReleaseNoteUtil releasenote = new ReleaseNoteUtil();
		releasenote.targetexcelfilename = "D:/abc.xls";
		releasenote.newexcelfilename = "new.xls";

		ExcelUtil eu = new ExcelUtil();
		String client = ReleaseNoteUtil.TinaClient;
		String sheetname = (String) ReleaseNoteUtil.clientSheetMapbyClient()
				.get(client);
		WritableSheet ws = null;
		try {
			Workbook wb = Workbook.getWorkbook(new FileInputStream(
					releasenote.targetexcelfilename));
			WritableWorkbook wwb = Workbook.createWorkbook(new File(
					releasenote.newexcelfilename), wb);

			wb.close();
			wwb.close();
			System.out.println("..........write over");
			String resolution = "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
