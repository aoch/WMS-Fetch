package com.convergys.wmsfetch;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HtmlUnitTest {

	@Test
	public void homePage() throws Exception {
		final WebClient webClient = new WebClient();
		final HtmlPage page = (HtmlPage) webClient
				.getPage("http://htmlunit.sourceforge.net");
		Assert.assertEquals("HtmlUnit - Welcome to HtmlUnit", page
				.getTitleText());

		final String pageAsXml = page.asXml();
		Assert.assertTrue(pageAsXml.contains("<body class=\"composite\">"));

		final String pageAsText = page.asText();
		Assert.assertTrue(pageAsText
				.contains("Support for the HTTP and HTTPS protocols"));
		// webClient.closeAllWindows();
	}

}
