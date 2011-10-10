package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;

import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;

/**
 * @author ronnyfriedland
 */
public class NewProjectFrame extends JFrame {

    private static final long serialVersionUID = -8738367859388084898L;

    public NewProjectFrame() {
        createUI();
    }

    private void createUI() {
        setLayout(null);
        setBounds(0, 0, 280, 150);
        setTitle("Neues Projekt anlegen");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // init
        final JLabel labelName = new JLabel("Projektname: ");
        final JTextField name = new JTextField();
        final JLabel labelDescription = new JLabel("Beschreibung: ");
        final JTextField description = new JTextField();
        final JButton save = new JButton("Speichern");

        // configure
        labelName.setBounds(10, 10, 100, 24);

        name.setBounds(110, 10, 150, 24);
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

        description.setBounds(110, 35, 150, 24);
        description.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        save.setBounds(10, 70, 250, 24);

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
