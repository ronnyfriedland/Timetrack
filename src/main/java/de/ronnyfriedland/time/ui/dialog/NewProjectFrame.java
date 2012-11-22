package de.ronnyfriedland.time.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.SwingConstants;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;

import de.ronnyfriedland.time.config.Const;
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
        super(Messages.CREATE_NEW_PROJECT.getMessage(), 315, 205, false);
        getContentPane().setBackground(Const.COLOR_BACKGROUND);
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
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 100, 202, 0 };
        gridBagLayout.rowHeights = new int[] { 28, 28, 23, 29, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        getContentPane().setLayout(gridBagLayout);
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
        GridBagConstraints gbc_labelName = new GridBagConstraints();
        gbc_labelName.fill = GridBagConstraints.HORIZONTAL;
        gbc_labelName.insets = new Insets(25, 25, 5, 5);
        gbc_labelName.gridx = 0;
        gbc_labelName.gridy = 0;
        getContentPane().add(labelName, gbc_labelName);

        name.setName("name");
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
        GridBagConstraints gbc_name = new GridBagConstraints();
        gbc_name.anchor = GridBagConstraints.NORTH;
        gbc_name.fill = GridBagConstraints.HORIZONTAL;
        gbc_name.insets = new Insets(25, 10, 5, 25);
        gbc_name.gridx = 1;
        gbc_name.gridy = 0;
        getContentPane().add(name, gbc_name);
        GridBagConstraints gbc_labelDescription = new GridBagConstraints();
        gbc_labelDescription.fill = GridBagConstraints.HORIZONTAL;
        gbc_labelDescription.insets = new Insets(10, 25, 5, 5);
        gbc_labelDescription.gridx = 0;
        gbc_labelDescription.gridy = 1;
        labelDescription.setHorizontalAlignment(SwingConstants.RIGHT);
        getContentPane().add(labelDescription, gbc_labelDescription);

        description.setName("description");
        description.addKeyListener(new TimeTableKeyAdapter());

        formatOk(name, description);
        GridBagConstraints gbc_description = new GridBagConstraints();
        gbc_description.anchor = GridBagConstraints.NORTH;
        gbc_description.fill = GridBagConstraints.HORIZONTAL;
        gbc_description.insets = new Insets(10, 10, 5, 25);
        gbc_description.gridx = 1;
        gbc_description.gridy = 1;
        getContentPane().add(description, gbc_description);
        GridBagConstraints gbc_labelDisable = new GridBagConstraints();
        gbc_labelDisable.fill = GridBagConstraints.HORIZONTAL;
        gbc_labelDisable.insets = new Insets(10, 25, 5, 5);
        gbc_labelDisable.gridx = 0;
        gbc_labelDisable.gridy = 2;
        getContentPane().add(labelDisable, gbc_labelDisable);
        disableProject.setBackground(Const.COLOR_BACKGROUND);
        GridBagConstraints gbc_disableProject = new GridBagConstraints();
        gbc_disableProject.anchor = GridBagConstraints.NORTH;
        gbc_disableProject.fill = GridBagConstraints.HORIZONTAL;
        gbc_disableProject.insets = new Insets(10, 5, 5, 0);
        gbc_disableProject.gridx = 1;
        gbc_disableProject.gridy = 2;
        getContentPane().add(disableProject, gbc_disableProject);
        GridBagConstraints gbc_save = new GridBagConstraints();
        gbc_save.insets = new Insets(10, 25, 0, 25);
        gbc_save.anchor = GridBagConstraints.NORTH;
        gbc_save.fill = GridBagConstraints.HORIZONTAL;
        gbc_save.gridwidth = 2;
        gbc_save.gridx = 0;
        gbc_save.gridy = 3;
        getContentPane().add(save, gbc_save);
    }

    public static void main(final String[] args) {
        new NewProjectFrame().setVisible(true);
    }
}
