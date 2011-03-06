package com.convergys.wmsfetch.releasenotes;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.convergys.wmsfetch.model.WMSItem;
import com.convergys.wmsfetch.releasenotes.ReleaseNoteGeneratorImpl;

/**
 * 
 * @author Andrew Och
 * 
 */
public class ReleaseNoteGeneratorImplTest {
	ReleaseNoteGeneratorImpl releaseNoteGenerator;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		releaseNoteGenerator = new ReleaseNoteGeneratorImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.convergys.wmsfetch.releasenotes.ReleaseNoteGeneratorImpl#generate(com.convergys.wmsfetch.model.WMSItem, java.io.OutputStream)}
	 * .
	 */
	@Ignore
	@Test
	public void testGenerate() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link com.convergys.wmsfetch.releasenotes.ReleaseNoteGeneratorImpl#populateTemplate(java.util.HashMap)}
	 * .
	 */
	@Test
	public void testPopulateTemplate() {
		HashMap<String, String> variables = new HashMap<String, String>();
		variables.put("clientName", "zxcv");
		variables.put("clientRef", "fghj");
		variables.put("component", "sdfg");
		variables.put("releaseNumber", "wert");
		variables.put("patchNumber", "1234");

		String expected = "Client Name:        zxcv\n"
				+ "Client Reference:   fghj\n" + "Component:          sdfg\n"
				+ "Release Number:     wert\n" + "Patch Number:       1234\n"
				+ "Patch Dependencies: all patches before are supplied.\n"
				+ "Date:               $date\n";
		releaseNoteGenerator.setTemplateFilename("TestTemplate.vm");
		String actual = releaseNoteGenerator.populateTemplate(variables);
		// Get rid of tricky Windows eol
		actual = actual.replaceAll("\r", "");

		Assert.assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link com.convergys.wmsfetch.releasenotes.ReleaseNoteGeneratorImpl#generate(WMSItem wmsItem, OutputStream output)}
	 * .
	 */
	@Test
	public void testGenReleaseNotes() {
		WMSItem wmsItem = new WMSItem();
		wmsItem.setClient("CLIENT");
		wmsItem.setId("1234");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File("test.txt"));
		} catch (FileNotFoundException e) {
			Assert.fail(e.getLocalizedMessage());
		}

		boolean expected = true;
		boolean actual = releaseNoteGenerator.generate(wmsItem, fos);

		Assert.assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link com.convergys.wmsfetch.releasenotes.ReleaseNoteGeneratorImpl#getVersion(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetVersion() {
		String release = "AWCC.0.0.2.129";
		String expected = "0.0.2.129";
		String actual = releaseNoteGenerator.getVersion(release);
		Assert.assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link com.convergys.wmsfetch.releasenotes.ReleaseNoteGeneratorImpl#populateContext(org.apache.velocity.VelocityContext, java.util.HashMap)}
	 * .
	 */
	@Ignore
	@Test
	public void testPopulateContext() {
		fail("Not yet implemented"); // TODO
	}

}
