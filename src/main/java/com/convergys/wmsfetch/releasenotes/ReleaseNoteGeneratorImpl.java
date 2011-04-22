package com.convergys.wmsfetch.releasenotes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.convergys.wmsfetch.model.WMSItem;

/**
 * @author Andrew Och
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class ReleaseNoteGeneratorImpl implements IReportGenerator {
	private static transient final Logger logger = Logger
			.getLogger(ReleaseNoteGeneratorImpl.class);

	public static final String PATCH_TEMPLATE = "PatchTemplate.vm";

	public static final String RELEASE_TEMPLATE = "ReleaseNotesTemplate.vm";

	private Map<String, String> variableMap;

	private String templateFilename;

	private VelocityEngine engine;

	private String eol = "\r\n";

	public void addVariables(Map<String, String> variableMap) {
		if (this.variableMap == null) {
			this.variableMap = new HashMap<String, String>();
		}
		
		if(variableMap != null) {
			this.variableMap.putAll(variableMap);
		}
	}

	private boolean generate(HashMap<String, String> variables,
			OutputStream output) {
		boolean generated = false;

		if (output != null) {

			// Merge user defined variables with WMS variables (override
			// defaults)
			if (variableMap != null) {
				variables.putAll(variableMap);
			}

			// Write the report
			String releaseNotes = populateTemplate(variables);
			releaseNotes = releaseNotes.replaceAll("\r", "");
			releaseNotes = releaseNotes.replaceAll("\n", eol);

			try {
				output.write(releaseNotes.getBytes("UTF8"));

				// Only close output stream if it is a file handle
				if (output instanceof FileOutputStream) {
					try {
						((FileOutputStream) output).close();
					} catch (IOException e) {
						logger.error(e.getLocalizedMessage());
					}
				} else {
					// Always flush the toilet after use
					output.flush();
				}
				generated = true;
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.error("Output Stream is null");
		}
		return generated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.convergys.wmsfetch.logic.IReleaseNoteGenerator#generate(java.util
	 * .List wmsItems, java.io.OutputStream output)
	 */
	// @Override
	public boolean generate(List<WMSItem> wmsItems, OutputStream output) {
		templateFilename = RELEASE_TEMPLATE;
		HashMap<String, String> variables = getWMSItemsVariables(wmsItems);
		return generate(variables, output);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.convergys.wmsfetch.logic.IReleaseNoteGenerator#generate(com.convergys
	 * .wmsfetch.model.WMSItem wmsItems, java.io.OutputStream output)
	 */
	// @Override
	public boolean generate(WMSItem wmsItem, OutputStream output) {
		templateFilename = PATCH_TEMPLATE;
		return generate(wmsItem.getVariableMap(), output);
	}

	/**
	 * 
	 * @param velocityEngine
	 * @return template
	 */
	public Template getTemplate(VelocityEngine velocityEngine) {
		Template template = null;
		File file = new File(this.getClass().getResource(templateFilename)
				.getFile());

		try {
			template = velocityEngine.getTemplate(file.getAbsolutePath(),
					"UTF-8");
		} catch (Exception e) {
			logger.fatal(e.getLocalizedMessage());
		}
		return template;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.convergys.wmsfetch.logic.IReleaseNoteGenerator#getTemplateFilename()
	 */
	// @Override
	public String getTemplateFilename() {
		return templateFilename;
	}

	private String getTemplateFromJar() {
		// Reading the file contents from the JAR
		InputStream inStream = this.getClass().getResourceAsStream(
				templateFilename);
		StringBuilder stringBuilder = new StringBuilder();
		InputStreamReader streamReader = new InputStreamReader(inStream);
		BufferedReader bufferedReader = new BufferedReader(streamReader);
		String line = "";

		try {
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + eol);
			}
		} catch (IOException e) {
			logger.fatal(e.getLocalizedMessage());
		}
		return stringBuilder.toString();
	}

	private VelocityEngine getVelocityEngine(VelocityEngine engine)
			throws Exception {
		Properties properties = new Properties();
		properties.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
				"org.apache.velocity.runtime.log.Log4JLogChute");
		properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "string");
		properties
				.setProperty("string.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.StringResourceLoader");

		engine = new VelocityEngine();
		engine.init(properties);

		return (engine);
	}

	/**
	 * 
	 * @param wmsItems
	 * @return variables
	 */
	public HashMap<String, String> getWMSItemsVariables(List<WMSItem> wmsItems) {
		HashMap<String, String> variables = new HashMap<String, String>();

		StringBuffer fixedTRs = new StringBuffer();
		StringBuffer cancelledTRs = new StringBuffer();

		if (wmsItems != null) {
			for (WMSItem wmsItem : wmsItems) {
				String infoLine = String.format(
						" %s\tSeverity: [%s]\t%s\t%s\n", new Object[] {
								wmsItem.getId(), wmsItem.getSeverity(),
								wmsItem.getClientRef(), wmsItem.getTitle() });
				if (wmsItem.getStatus().equalsIgnoreCase("Cancelled")) {
					cancelledTRs.append(infoLine);
				} else {
					fixedTRs.append(infoLine);
				}
			}

			// If no data put defaults
			if (fixedTRs.toString().equalsIgnoreCase("")) {
				variables.put("fixedTRs", "    None");
			} else {
				variables.put("fixedTRs", fixedTRs.toString());
			}

			if (cancelledTRs.toString().equalsIgnoreCase("")) {
				variables.put("cancelledTRs", "    None");
			} else {
				variables.put("cancelledTRs", cancelledTRs.toString());
			}

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			variables.put("date", formatter.format(new Date()));
			variables.put("headerLastLine", "");
			variables.put("knownIssues", "None");
			variables.put("patches", "None");
			variables.put("notes", "None");
			variables.put("compatibleSoftware", "None");
		} else {
			logger.warn("wmsItems is NULL");
		}
		return variables;
	}

	/**
	 * 
	 * @param context
	 * @param variables
	 */
	public void populateContext(VelocityContext context,
			HashMap<String, String> variables) {
		if (variables != null) {
			for (String key : variables.keySet()) {
				String value = variables.get(key);
				context.put(key, value);
			}
		}
	}

	/**
	 * 
	 * @param variables
	 * @return mergedTemplate
	 */
	public String populateTemplate(HashMap<String, String> variables) {
		String result = "";

		try {
			// Getting Velocity Engine
			engine = getVelocityEngine(engine);

			// Reading Template Body from the template file(.vm file) in the jar
			String templateBody = getTemplateFromJar();

			// Setting the template body in string repository with a template
			// name. Here the template name is used as a key for future mapping.
			StringResourceRepository repository = StringResourceLoader
					.getRepository();
			repository.putStringResource(templateFilename, templateBody);

			VelocityContext context = new VelocityContext();

			// Getting the context with placeholder values
			populateContext(context, variables);

			// Fetch Template to a StringWriter and write to result
			Template template = engine.getTemplate(templateFilename);
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			result = writer.toString();
		} catch (Exception e) {
			logger.fatal(e.getLocalizedMessage());
		}
		return result;
	}

	public void setEOL(String eol) {
		this.eol = eol;
	}

	public void setTemplateFilename(String templateFilename) {
		this.templateFilename = templateFilename;
	}

}
