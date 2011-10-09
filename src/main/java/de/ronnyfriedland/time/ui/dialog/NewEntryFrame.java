package de.ronnyfriedland.time.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;

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
        setBounds(0, 0, 300, 250);
        setTitle("Neues Projekt anlegen");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        final JLabel labelDescription = new JLabel("Beschreibung: ");
        labelDescription.setBounds(10, 10, 100, 25);
        final JTextField description = new JTextField();
        description.setBounds(110, 10, 150, 25);

        final JLabel labelDuration = new JLabel("Dauer (in h): ");
        labelDuration.setBounds(10, 35, 100, 25);
        final JTextField duration = new JTextField();
        duration.setBounds(110, 35, 150, 25);

        Collection<Project> projectList = EntityController.getInstance().findAll(Project.class);
        String[] projectNameList = new String[projectList.size()];
        int i = 0;
        for (Project project : projectList) {
            projectNameList[i] = project.getName();
            i++;
        }

        final JList projects = new JList(projectNameList);
        projects.setBounds(10, 70, 150, 50);

        final JButton save = new JButton("Speichern");
        save.setBounds(10, 140, 150, 25);

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Entry entry = new Entry();
                entry.setDescription(description.getText());
                entry.setDuration(Float.valueOf(duration.getText()));
                String projectUuid = (String) projects.getSelectedValue();
                Project selectedProject = EntityController.getInstance().findById(Project.class, projectUuid);
                entry.setProject(selectedProject);
                EntityController.getInstance().create(entry);
                setVisible(false);
            }
        });

        getContentPane().add(labelDescription);
        getContentPane().add(description);
        getContentPane().add(labelDuration);
        getContentPane().add(duration);
        getContentPane().add(projects);
        getContentPane().add(save);
    }

}
