package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
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

/**
 * @author ronnyfriedland
 */
public class NewProjectFrame extends AbstractFrame {
    private static final Logger LOG = Logger.getLogger(NewProjectFrame.class.getName());

    private static final long serialVersionUID = -8738367859388084898L;

    private final JLabel labelName = new JLabel(Messages.PROJECT_NAME.getText());
    private final JTextField name = new JTextField();
    private final JLabel labelDescription = new JLabel(Messages.DESCRIPTION.getText());
    private final JTextField description = new JTextField();
    private final JButton save = new JButton(Messages.SAVE.getText());

    public NewProjectFrame() {
        super(Messages.CREATE_NEW_PROJECT.getText(), 320, 150);
        createUI();
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

        name.setBounds(110, 10, 200, 24);
        name.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
                    arg0.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                } else {
                    arg0.setBorder(BorderFactory.createLineBorder(Color.RED));
                }
                return valid;
            }
        });

        labelDescription.setBounds(10, 35, 100, 24);

        description.setBounds(110, 35, 200, 24);
        description.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        save.setBounds(10, 70, 300, 24);

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project project = new Project();
                if (!StringUtils.isBlank(name.getText())) {
                    project.setName(name.getText());
                }
                if (!StringUtils.isBlank(description.getText())) {
                    project.setDescription(description.getText());
                }
                try {
                    EntityController.getInstance().create(project);
                    setVisible(false);
                } catch (ConstraintViolationException ex) {
                    LOG.log(Level.SEVERE, "Error saving new project", ex);
                    save.setBorder(BorderFactory.createLineBorder(Color.RED));
                }
            }
        });

        getContentPane().add(labelName);
        getContentPane().add(name);
        getContentPane().add(labelDescription);
        getContentPane().add(description);
        getContentPane().add(save);
    }
}
