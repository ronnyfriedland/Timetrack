package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;

import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;

/**
 * @author ronnyfriedland
 */
public class NewEntryFrame extends JFrame {
    private static final long serialVersionUID = -8738367859388084898L;

    public NewEntryFrame() {
        createUI();
    }

    private void createUI() {
        setLayout(null);
        setBounds(0, 0, 280, 250);
        setTitle("Neues Projekt anlegen");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // init
        final JLabel labelDate = new JLabel("Datum: ");
        final JTextField date = new JTextField();
        final JLabel labelDescription = new JLabel("Beschreibung: ");
        final JTextField description = new JTextField();
        final JLabel labelDuration = new JLabel("Dauer (in h): ");
        final JTextField duration = new JTextField();
        final JScrollPane scrollPaneProjects = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        final JList projects = new JList();
        final JButton save = new JButton("Speichern");

        // configure
        labelDate.setBounds(10, 10, 100, 24);

        date.setBounds(110, 10, 150, 24);
        date.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        date.setInputVerifier(new InputVerifier() {
            /**
             * (non-Jsdoc)
             * 
             * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
             */
            @Override
            public boolean verify(final JComponent arg0) {
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<Entry>> violations = validator.validateValue(Entry.class, "date",
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
        description.setInputVerifier(new InputVerifier() {
            /**
             * (non-Javadoc)
             * 
             * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
             */
            @Override
            public boolean verify(final JComponent arg0) {
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<Entry>> violations = validator.validateValue(Entry.class, "description",
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

        labelDuration.setBounds(10, 60, 100, 24);

        duration.setBounds(110, 60, 150, 24);
        duration.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        duration.setInputVerifier(new InputVerifier() {
            /**
             * (non-Javadoc)
             * 
             * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
             */
            @Override
            public boolean verify(final JComponent arg0) {
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<Entry>> violations = validator.validateValue(Entry.class, "duration",
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

        Collection<Project> projectList = EntityController.getInstance().findAll(Project.class);
        String[] projectNameList = new String[projectList.size()];
        int i = 0;
        for (Project project : projectList) {
            projectNameList[i] = project.getName();
            i++;
        }

        projects.setListData(projectNameList);
        projects.setInputVerifier(new InputVerifier() {
            /**
             * (non-Javadoc)
             * 
             * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
             */
            @Override
            public boolean verify(final JComponent arg0) {
                ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                Validator validator = factory.getValidator();
                Set<ConstraintViolation<Entry>> violations = validator.validateValue(Entry.class, "project",
                        ((JList) arg0).getSelectedValue());
                boolean valid = violations.isEmpty();
                if (valid) {
                    arg0.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                } else {
                    arg0.setBorder(BorderFactory.createLineBorder(Color.RED));
                }
                return valid;
            }
        });

        scrollPaneProjects.setViewportView(projects);
        scrollPaneProjects.setBounds(10, 85, 250, 100);
        scrollPaneProjects.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        save.setBounds(10, 190, 250, 24);

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Entry entry = new Entry();
                if (!StringUtils.isBlank(date.getText())) {
                    entry.setDate(date.getText());
                }
                if (!StringUtils.isBlank(description.getText())) {
                    entry.setDescription(description.getText());
                }
                if (!StringUtils.isBlank(duration.getText())) {
                    entry.setDuration(duration.getText());
                }
                String projectName = (String) projects.getSelectedValue();
                if (!StringUtils.isBlank(projectName)) {
                    Map<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put(Project.PARAM_NAME, projectName);
                    Project selectedProject = EntityController.getInstance().findSingleResultByParameter(Project.class,
                            Project.QUERY_FINDBYNAME, parameters);
                    entry.setProject(selectedProject);
                }
                try {
                    EntityController.getInstance().create(entry);
                    setVisible(false);
                } catch (ConstraintViolationException ex) {
                    save.setBorder(BorderFactory.createLineBorder(Color.RED));
                }

            }
        });

        getContentPane().add(labelDate);
        getContentPane().add(date);
        getContentPane().add(labelDescription);
        getContentPane().add(description);
        getContentPane().add(labelDuration);
        getContentPane().add(duration);
        getContentPane().add(scrollPaneProjects);
        getContentPane().add(save);
    }
}