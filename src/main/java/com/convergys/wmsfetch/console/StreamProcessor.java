package com.convergys.wmsfetch.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

/**
 * Class to deal with STDOUT and STDERR
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 */
public class StreamProcessor implements Runnable {
	private static transient final Logger logger = Logger
			.getLogger(StreamProcessor.class);

	private InputStream is;

	// either STDOUT or STDERR
	private String type;

	public InputStream getIs() {
		return is;
	}

	/**
	 * 
	 * @param is
	 */
	public void setIs(InputStream is) {
		this.is = is;
	}

	/**
	 * STDOUT or STDERR
	 * 
	 * @return type
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(OutputStream os) {
		this.os = os;
	}

	private OutputStream os;

	/**
	 * 
	 * @param type
	 * @param is
	 */
	public StreamProcessor() {
		this(null, null, null);
	}

	/**
	 * 
	 * @param type
	 * @param is
	 */
	public StreamProcessor(String type, InputStream is) {
		this(type, is, null);
	}

	/**
	 * 
	 * @param type
	 * @param is
	 * @param os
	 */
	public StreamProcessor(String type, InputStream is, OutputStream os) {
		this.is = is;
		this.type = type;
		this.os = os;
	}

	/**
	 * Captures an output stream, either STDOUT or STDERR. Captured output is
	 * read by LINE not CHAR and flushed after each LINE. This is a good balance
	 * between performance and flexibility
	 */
	public void run() {
		PrintWriter printWriter = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line = null;

			if (os != null) {
				printWriter = new PrintWriter(os);
			} else {
				logger.warn("reporting output stream is null");
			}

			// Using readLine rather then read means its a
			while ((line = br.readLine()) != null) {
				if (os != null) {
					printWriter.append(line + "\n");
					/*
					 * Don't forget to flush the toilet! Once we have a line
					 * flush the buffer else the output of STDERR and STDOUT
					 * cannot be read in real time.
					 */
					printWriter.flush();
				}
			}
		} catch (IOException ioe) {
			logger.error("exception occurred: " + ioe.toString());
		} finally {
			// Finished reading inputStream so flush outputStream
			if (printWriter != null) {
				printWriter.flush();
				// If this is System.out or System.err DON'T Close it
				// printWriter.close();
			}

			// close inputStream
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("exception occurred: " + e.toString());
				}
			}

			// close buffer reader
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					logger.error("exception occurred: " + e.toString());
				}
			}
		}
	}
}
