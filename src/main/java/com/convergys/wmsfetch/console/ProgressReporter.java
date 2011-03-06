package com.convergys.wmsfetch.console;

import java.util.Random;

/**
 * To enable progress reporting of progress
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 */
public class ProgressReporter implements Runnable {

	private static final int MAX = 100;

	private static final int oneSecond = 1000;

	private String format = ":PercentComplete %d:";

	private String progressIndicator = ".";

	private int progress = 0;

	private boolean percent = false;

	private Thread progressThread;

	private String completeMsg = " Complete\n";

	private String killedMsg = " DANGER Will Robinson DANGER!\n";

	private String startMsg = "Start Request";

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * 
	 * @return progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @return the progressThread
	 */
	public Thread getProgressThread() {
		return progressThread;
	}

	public synchronized void killProgress() {
		if (progressThread.isAlive()) {
			progressThread.interrupt();
		}
		System.out.print(killedMsg);
	}

	/**
	 * Print progress while progress is less than MAX
	 */
	public synchronized void printProgress() {
		if (progress < MAX) {
			if (percent) {
				System.out.println(String.format(format, getProgress()));
			} else {
				System.out.print(progressIndicator);
			}
		}
	}

	public void run() {
		Random random = new Random();
		while (progress < MAX && progressThread.isAlive()) {
			// Sleep for at least one second but not more then 2 seconds.
			int randomNap = oneSecond + random.nextInt(oneSecond);
			try {
				printProgress();
				Thread.sleep(randomNap);
			} catch (InterruptedException ignore) {
				// Reset interrupt
				Thread.interrupted();
			}
		}
		// Make sure we see 100%
		printProgress();
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * 
	 * @param progress
	 */
	public void setProgress(int progress) {
		if (progress > 100) {
			progress = 100;
		}
		this.progress = progress;
	}

	/**
	 * @param progressThread
	 *            the progressThread to set
	 */
	public void setProgressThread(Thread progressThread) {
		this.progressThread = progressThread;
	}

	/**
	 * This will create a new Thread for the progress bar
	 */
	public synchronized void startProgress() {
		System.out.print(startMsg);

		// Make sure we see 0%
		printProgress();
		progressThread = new Thread(this, "Progress Thread");
		progressThread.start();
	}

	/**
	 * This interrupt the progress bar thread and show completion message
	 * 
	 */
	public synchronized void stopProgress() {
		progress = MAX;
		if (progressThread.isAlive()) {
			progressThread.interrupt();
		}
		System.out.print(completeMsg);
	}
}
