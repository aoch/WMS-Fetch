package com.convergys.wmsfetch;

import java.util.Properties;

import com.convergys.wmsfetch.cli.ArgumentParser;
import com.convergys.wmsfetch.gui.GuiManager;

/**
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		checkJavaVersion();
		if (args.length > 0) {
			new ArgumentParser(args);
		} else {
			new GuiManager();
		}
	}

	/**
	 * Check the correct version of java is being used.
	 */
	public static void checkJavaVersion() {
		Properties properties = System.getProperties();
		String javaVersion = (String) properties.get("java.version");
		if (javaVersion.startsWith("1 .6") || javaVersion.startsWith("1.7")
				|| javaVersion.startsWith("1.8")) {
			System.out
					.println(String
							.format(
									"This requires Java Version 6 or higher but your version is [%s] ",
									new Object[] { javaVersion }));
			System.exit(-1);
		}
	}

}
