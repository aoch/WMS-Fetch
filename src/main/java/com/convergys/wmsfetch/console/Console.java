package com.convergys.wmsfetch.console;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

/**
 * This Class is used to execute Shell and DOS commands, wrapping Input and
 * Output Streams into a more manageable methods. Provide the wrapped process in
 * Static and Stateless fashion.
 * 
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 */
public class Console {

	/*
	 * Used to kill the process if it has not completed within the specified
	 * time
	 */
	private class InterruptScheduler extends TimerTask {
		Thread target = null;

		public InterruptScheduler(Thread target) {
			this.target = target;
		}

		@Override
		public void run() {
			timeoutReached = true;
			target.interrupt();
		}
	}

	private static transient final Logger logger = Logger
			.getLogger(Console.class);

	public static final String STDOUT = "STDOUT";

	public static final String STDERR = "STDERR";

	/** Default MAX process execution time in milliseconds is 100 hours */
	public static final long TIMEOUT = 360000000;

	/**
	 * Execute a shell or DOS command.
	 * 
	 * The <code>command</code> is simply a String to be executed. The
	 * <code>workDir</code> is the working directory of the process of the
	 * execution, think change to this directory prior to executing command. if
	 * null default is current working directory. The <code>environment</code>
	 * is Map of key value pairs which correspond to environment variable key
	 * and its value. It can be null. Finally to ensure this method provide
	 * completely Static and Stateless functionality, StreamProcessors are
	 * required to handle both STDOUT and STDERR. If null, default
	 * StreamProcessors are used, outputs results to <code>System.out</code>.
	 * The Process MUST complete before the timeout <code>Console.TIMEOUT</code>
	 * 
	 * Returns the exit code of the process.
	 * 
	 * @param command
	 * @param workDir
	 * @param environment
	 * @param stdOut
	 * @param stdErr
	 * 
	 * @return exitCode
	 * @throws TimeoutException
	 */
	public static int execute(List<String> command, String workDir,
			Map<String, String> environment, OutputStream stdOut,
			OutputStream stdErr) throws TimeoutException {
		return execute(command, workDir, environment, stdOut, stdErr,
				Console.TIMEOUT);
	}

	/**
	 * Execute a shell or DOS command.
	 * 
	 * The <code>command</code> is simply a String to be executed. The
	 * <code>workDir</code> is the working directory of the process of the
	 * execution, think change to this directory prior to executing command. if
	 * null default is current working directory. The <code>environment</code>
	 * is Map of key value pairs which correspond to environment variable key
	 * and its value. It can be null. Finally to ensure this method provide
	 * completely Static and Stateless functionality, StreamProcessors are
	 * required to handle both STDOUT and STDERR. If null, default
	 * StreamProcessors are used, outputing results to <code>System.out</code>.
	 * The Process MUST complete before the timeout <code>timeout</code>
	 * 
	 * Returns the exit code of the process.
	 * 
	 * @param command
	 * @param workDir
	 * @param environment
	 * @param stdOut
	 * @param stdErr
	 * @param timeout
	 * @return exitCode
	 * @throws TimeoutException
	 */
	public static int execute(List<String> command, String workDir,
			Map<String, String> environment, OutputStream stdOut,
			OutputStream stdErr, long timeout) throws TimeoutException {
		Console console = new Console();
		return console.executor(command, workDir, environment, stdOut, stdErr,
				timeout);
	}

	public static int execute(String command, String workDir)
			throws TimeoutException {
		return execute(command, workDir, null, null, null);
	}

	/**
	 * Execute a shell or DOS command.
	 * 
	 * The <code>command</code> is simply a String to be executed. If Arguments
	 * are added then they are simply split on space. The <code>workDir</code>
	 * is the working directory of the process of the execution, think change to
	 * this directory prior to executing command. if null default is current
	 * working directory. The <code>environment</code> is Map of key value pairs
	 * which correspond to environment variable key and its value. It can be
	 * null. Finally to ensure this method provide completely Static and
	 * Stateless functionality, StreamProcessors are required to handle both
	 * STDOUT and STDERR. If null, default StreamProcessors are used, outputing
	 * results to <code>System.out</code>. The Process MUST complete before the
	 * timeout <code>Console.TIMEOUT</code>
	 * 
	 * Returns the exit code of the process.
	 * 
	 * @param command
	 * @param workDir
	 * @param environment
	 * @param stdOut
	 * @param stdErr
	 * 
	 * @return exitCode
	 * @throws TimeoutException
	 */
	public static int execute(String command, String workDir,
			Map<String, String> environment, OutputStream stdOut,
			OutputStream stdErr) throws TimeoutException {
		ArrayList<String> cmd = new ArrayList<String>();

		// Simply split the User Arguments by spaces
		ArrayList<String> commandArgs = new ArrayList<String>();
		commandArgs.addAll(Arrays.asList(command.split(" ")));

		if (commandArgs.size() > 1) {
			cmd.addAll(commandArgs);
		} else {
			cmd.add(command);
		}

		return execute(cmd, workDir, environment, stdOut, stdErr);
	}

	private boolean timeoutReached;

	/**
	 * 
	 * Execute a shell or DOS command.
	 * 
	 * This is needed because the InterruptScheduler cannot work in a Static
	 * Context
	 * 
	 * @param command
	 * @param workDir
	 * @param environment
	 * @param stdOut
	 * @param stdErr
	 * @param timeout
	 * @return exitCode
	 * @throws TimeoutException
	 */
	private int executor(List<String> command, String workDir,
			Map<String, String> environment, OutputStream stdOut,
			OutputStream stdErr, long timeout) throws TimeoutException {

		int exitCode = -1;
		ProcessBuilder processBuilder = new ProcessBuilder(command);

		// If the user has provided a set of variables, add to process env
		Map<String, String> processEnv = processBuilder.environment();
		if (environment != null) {
			Set<String> keys = environment.keySet();
			for (String key : keys) {
				processEnv.put(key, environment.get(key));
			}
		}

		// If the user has provided a working directory, set to process
		if (workDir != null) {
			File workingDirectory = new File(workDir);
			if (workingDirectory.isDirectory()) {
				processBuilder.directory(workingDirectory);
				logger.debug("Working Directory is: "
						+ processBuilder.directory().toString());
			}
		}

		logger.debug("Executing: " + command.toString());

		// Execute command
		Process childProcess;
		try {
			childProcess = processBuilder.start();
		} catch (IOException ioe) {
			logger.error("exception occurred: " + ioe.toString());
			return exitCode;
		}

		// Set a timer to interrupt the process if it does not return within the
		// timeout period
		Timer timer = new Timer();
		timer.schedule(new InterruptScheduler(Thread.currentThread()),
				(long) timeout);
		timeoutReached = false;

		// Deal with STDOUT
		StreamProcessor stdoutProcessor = new StreamProcessor(Console.STDOUT,
				childProcess.getInputStream(), stdOut);

		// Deal with STDERR
		StreamProcessor stderrProcessor = new StreamProcessor(Console.STDERR,
				childProcess.getErrorStream(), stdErr);

		// Start the STDERR & STDOUT threads
		Thread stdOutThread = new Thread(stdoutProcessor, Console.STDOUT
				+ " capture thread");
		Thread stdErrThread = new Thread(stderrProcessor, Console.STDERR
				+ " capture thread");
		stdOutThread.start();
		stdErrThread.start();

		// Thread sleep time
		int nap = 100;

		// Wait for process threads to complete and log any errors
		try {
			// Collect Exit code of process
			exitCode = childProcess.waitFor();

			while (stdOutThread.isAlive() || stdErrThread.isAlive()) {
				logger.debug("STDOUT & STDERR not complete, sleep for: " + nap
						+ "ms");
				Thread.sleep(nap);
			}
		} catch (InterruptedException ie) {
			// RESTORE THE INTERRUPTED STATUS INSTEAD
			Thread.interrupted();

			if (timeoutReached) {
				// Stop the process from running
				childProcess.destroy();
				throw new TimeoutException(command.toString()
						+ " did not return after " + timeout + " milliseconds.");
			} else {
				logger.fatal("Something really bad happened", ie);
			}
		} finally {
			// Stop the timer
			timer.cancel();
			logger.debug("Process ExitValue: " + exitCode);
		}
		return exitCode;
	}
}
