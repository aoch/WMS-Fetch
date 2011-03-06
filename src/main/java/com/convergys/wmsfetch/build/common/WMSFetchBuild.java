package com.convergys.wmsfetch.build.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import org.apache.log4j.Logger;

import com.convergys.wmsfetch.net.db.WMSClientJDBCImpl;
import com.convergys.wmsfetch.net.email.MailSender;
import com.convergys.wmsfetch.releasenotes.ReleaseNoteGeneratorImpl;
import com.convergys.wmsfetch.releasenotes.WMSReleaseNoteMgr;
import com.convergys.wmsfetch.releasenotes.WMSReleaseNoteMgr.Type;

public class WMSFetchBuild {
	private static transient final Logger logger = Logger
			.getLogger(WMSFetchBuild.class);

	public static final String EMAIL_PERSONAL_NAME = "WSC Cruise Control";

	public static final String EMAIL_FROM = "noReply@convergys.com";

	public static final String EMAIL_SUBJECT = "Release Notes";

	public static final String EMAIL_BODY = "Please Check the Release notes";

	/**
	 * Send release notes to email addresses
	 * 
	 * @param emailAddresses
	 * @param mailServerUsername
	 * @param mailServerPassword
	 * @param emailPersonalName
	 * @param emailFrom
	 * @param emailSubject
	 * @param emailBody
	 * @param releaseNotesFile
	 */
	public static void sendEmail(String[] emailAddresses,
			String mailServerUsername, String mailServerPassword,
			String emailPersonalName, String emailFrom, String emailSubject,
			String emailBody, File releaseNotesFile) {
		if (emailAddresses.length <= 0) {
			logger.warn("No email addresses to send email to.");
			return;
		}

		MailSender mailSender = new MailSender();
		mailSender.setUsername(mailServerUsername);
		mailSender.setPassword(mailServerPassword);
		mailSender.setAttachmentFileName(releaseNotesFile.getAbsolutePath());
		mailSender.setMail_fromPersonalName(emailPersonalName);
		mailSender.setMail_from(emailFrom);
		mailSender.setMail_subject(emailSubject);
		mailSender.setMail_body(emailBody);

		// Send email to each address
		for (int i = 0; i < emailAddresses.length; i++) {
			mailSender.setMail_to(emailAddresses[i]);
			try {
				mailSender.send();
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}

	/**
	 * 
	 * @param outputDirectory
	 * @param outfile
	 * @param wmsUsername
	 * @param wmsPassword
	 * @param releaseid
	 * @param emails
	 * @param mailServerUsername
	 * @param mailServerPassword
	 * @param url
	 * @param variablesMap
	 * @return success
	 */
	public static boolean execReleaseNotes(File outputDirectory,
			String outfile, String wmsUsername, String wmsPassword,
			String releaseid, String[] emails, String mailServerUsername,
			String mailServerPassword, String url,
			Map<String, String> variablesMap) {

		WMSReleaseNoteMgr wmsReleaseNoteMgr = new WMSReleaseNoteMgr(
				new WMSClientJDBCImpl(), new ReleaseNoteGeneratorImpl());

		File dir = outputDirectory;
		if (dir != null) {
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} else {
			String warnMsg = String.format("Output Directory is null",
					new Object[] { releaseid });
			logger.warn(warnMsg);
		}

		File releaseNotesFile = new File(dir, outfile);
		try {
			wmsReleaseNoteMgr.setOutput(new FileOutputStream(releaseNotesFile));
		} catch (FileNotFoundException e) {
			logger.error(e.getLocalizedMessage());
		}

		// Hook to Business Logic
		boolean success = wmsReleaseNoteMgr.generateReleaseNotes(Type.RELEASE,
				wmsUsername, wmsPassword, releaseid, url, variablesMap);

		if (success) {
			String infoMsg = String.format(
					"Generated Release notes for issue: %s",
					new Object[] { releaseid });
			logger.info(infoMsg);
			WMSFetchBuild.sendEmail(emails, mailServerUsername,
					mailServerPassword, WMSFetchBuild.EMAIL_PERSONAL_NAME,
					WMSFetchBuild.EMAIL_FROM, WMSFetchBuild.EMAIL_SUBJECT,
					WMSFetchBuild.EMAIL_BODY, releaseNotesFile);
		} else {
			String errorMsg = String.format(
					"Could not generate Release notes for issue: %s",
					new Object[] { releaseid });
			logger.error(errorMsg);
		}
		return success;
	}
}
