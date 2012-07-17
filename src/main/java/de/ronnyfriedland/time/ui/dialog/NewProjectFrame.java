package de.ronnyfriedland.time.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;

import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.ui.adapter.TimeTableKeyAdapter;

/**
 * @author Ronny Friedland
 */
public class NewProjectFrame extends AbstractFrame {
	private static final Logger LOG = Logger.getLogger(NewProjectFrame.class.getName());

	private static final long serialVersionUID = -8738367859388084898L;

	private final JLabel labelName = new JLabel(Messages.PROJECT_NAME.getMessage());
	private final JTextField name = new JTextField();
	private final JLabel labelDescription = new JLabel(Messages.DESCRIPTION.getMessage());
	private final JTextField description = new JTextField();
	private final JLabel labelDisable = new JLabel(Messages.DISABLE.getMessage());
	private final JCheckBox disableProject = new JCheckBox();
	private final JButton save = new JButton(Messages.SAVE.getMessage());

	private String uuid = null;

	public NewProjectFrame() {
		super(Messages.CREATE_NEW_PROJECT.getMessage(), 320, 150);
		createUI();
	}

	public NewProjectFrame(final Project project) {
		this();
		uuid = project.getUuid();
		name.setText(project.getName());
		description.setText(project.getDescription());
		disableProject.setSelected(!project.getEnabled());
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see de.ronnyfriedland.time.ui.dialog.AbstractFrame#createUI()
	 */
	@Override
	protected void createUI() {
		// configure
		labelName.setBounds(10, 10, 100, 24);

		name.setName("name");
		name.setBounds(110, 10, 200, 24);
		name.addKeyListener(new TimeTableKeyAdapter());
		name.setInputVerifier(new InputVerifier() {
			/**
			 * (non-Javadoc)
			 * 
			 * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
			 */
			@Override
			public boolean verify(final JComponent arg0) {
				ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
				Validator validator = factory.getValidator();
				Set<ConstraintViolation<Project>> violations = validator.validateValue(Project.class, "name",
				        ((JTextField) arg0).getText());
				boolean valid = violations.isEmpty();
				if (valid) {
					formatOk(arg0);
				} else {
					formatError(arg0);
				}
				return valid;
			}
		});

		labelDescription.setBounds(10, 35, 100, 24);

		description.setName("description");
		description.setBounds(110, 35, 200, 24);
		description.addKeyListener(new TimeTableKeyAdapter());

		labelDisable.setBounds(10, 65, 100, 24);

		disableProject.setBounds(110, 65, 200, 24);

		save.setBounds(10, 90, 300, 24);
		save.addKeyListener(new TimeTableKeyAdapter());
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Project project;
				if (null == uuid) {
					project = new Project();
				} else {
					project = EntityController.getInstance().findById(Project.class, uuid);
				}
				if (!StringUtils.isBlank(name.getText())) {
					project.setName(name.getText());
				}
				if (!StringUtils.isBlank(description.getText())) {
					project.setDescription(description.getText());
				}
				project.setEnabled(!disableProject.isSelected());
				try {
					if (null != uuid) {
						EntityController.getInstance().update(project);
					} else {
						EntityController.getInstance().create(project);
					}
					setVisible(false);
				} catch (PersistenceException ex) {
					LOG.log(Level.SEVERE, "Error saving new project", ex);
					formatError(getRootPane());
				} catch (ConstraintViolationException ex) {
					LOG.log(Level.SEVERE, "Error saving new project", ex);
					formatError(getRootPane());
				}
			}
		});

		formatOk(name, description);

		getContentPane().add(labelName);
		getContentPane().add(name);
		getContentPane().add(labelDescription);
		getContentPane().add(description);
		getContentPane().add(labelDisable);
		getContentPane().add(disableProject);
		getContentPane().add(save);
	}

	public static void main(final String[] args) {
		new NewProjectFrame().setVisible(true);
	}
}
