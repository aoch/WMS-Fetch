package com.convergys.wmsfetch.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import com.convergys.wmsfetch.cli.Args;

public class Form {
	private static transient final Logger logger = Logger.getLogger(Form.class);

	private HashMap<String, JTextField> fields;

	private JScrollPane pane;

	private JPanel optionsPanel;

	private JPanel masterPanel;

	private JPanel titlePanel;

	private JPanel buttonPanel;

	private static final long serialVersionUID = -5661327897154228929L;

	private Options options;

	private JButton submit;

	private JButton reset;

	public Form(Options options) {
		super();
		this.options = options;
		int cols = 2;
		int rows = options.getOptions().size() / 2;
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(rows, cols));

		fields = new HashMap<String, JTextField>();

		for (Option option : (Collection<Option>) options.getOptions()) {
			if (option.hasArg()) {
				JPanel subPanel = new JPanel();
				subPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				JTextField field = new JTextField(15);
				JLabel label = new JLabel(option.getArgName(), JLabel.RIGHT);
				label.setLabelFor(field);
				subPanel.add(label);
				subPanel.add(field);
				subPanel.setToolTipText(option.getDescription());
				optionsPanel.add(subPanel);
				fields.put(option.getOpt(), field);
			}

			masterPanel = new JPanel();
			masterPanel.setLayout(new GridLayout(3, 1));

			titlePanel = new JPanel();
			JLabel titleLabel = new JLabel(Args.APP_NAME, JLabel.CENTER);
			titlePanel.add(titleLabel);

			masterPanel.add(titlePanel);
			masterPanel.add(optionsPanel);

			masterPanel.add(createButtonPanel());

			pane = new JScrollPane(masterPanel);
		}

	}

	private JPanel createButtonPanel() {
		buttonPanel = new JPanel();

		submit = new JButton();

		// Add rollover icon
		Icon rolloverIcon = new ImageIcon("submitOver.png");
		submit.setRolloverIcon(rolloverIcon);

		// Add pressed icon
		Icon pressedIcon = new ImageIcon("submitPressed.png");
		submit.setPressedIcon(pressedIcon);

		// To remove rollover icon, set to null
		submit.setRolloverIcon(null);

		// To remove pressed icon, set to null
		submit.setPressedIcon(null);

		// Retrieve the icon
		Icon icon = new ImageIcon("submit.png");

		// Create an action with an icon
		Action submitAction = new AbstractAction("Submit", icon) {
			public void actionPerformed(ActionEvent event) {
				HashMap<String, String> arguments = new HashMap<String, String>();
				for (String key : fields.keySet()) {
					arguments.put(key, fields.get(key).getText());
				}
				logger.info(arguments.toString());
				new GUIParser(arguments);
			}
		};
		submit.setAction(submitAction);

		buttonPanel.add(submit);

		reset = new JButton();
		reset.setRolloverIcon(new ImageIcon("resetOver.png"));
		reset.setPressedIcon(new ImageIcon("submitPressed.png"));

		// Create an action with an icon
		Action resetAction = new AbstractAction("Reset", new ImageIcon(
				"reset.png")) {
			public void actionPerformed(ActionEvent event) {
				for (String key : fields.keySet()) {
					fields.get(key).setText("");
				}
			}
		};
		reset.setAction(resetAction);

		buttonPanel.add(reset);

		return buttonPanel;
	}

	/**
	 * @return the options
	 */
	public Options getOptions() {
		return options;
	}

	/**
	 * @return the pane
	 */
	public JScrollPane getPane() {
		return pane;
	}

	/**
	 * @return the reset
	 */
	public JButton getReset() {
		return reset;
	}

	/**
	 * @return the submit
	 */
	public JButton getSubmit() {
		return submit;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(Options options) {
		this.options = options;
	}

	/**
	 * @param pane
	 *            the pane to set
	 */
	public void setPane(JScrollPane pane) {
		this.pane = pane;
	}

	/**
	 * @param reset
	 *            the reset to set
	 */
	public void setReset(JButton reset) {
		this.reset = reset;
	}

	/**
	 * @param submit
	 *            the submit to set
	 */
	public void setSubmit(JButton submit) {
		this.submit = submit;
	}
}
