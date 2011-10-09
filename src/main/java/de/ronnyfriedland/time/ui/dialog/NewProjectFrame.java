package de.ronnyfriedland.time.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

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
        setBounds(0, 0, 300, 150);
        setTitle("Neues Projekt anlegen");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        final JLabel labelName = new JLabel("Projektname: ");
        labelName.setBounds(10, 10, 100, 25);
        final JTextField name = new JTextField();
        name.setBounds(110, 10, 150, 25);

        final JLabel labelDescription = new JLabel("Beschreibung: ");
        labelDescription.setBounds(10, 35, 100, 25);
        final JTextField description = new JTextField();
        description.setBounds(110, 35, 150, 25);

        final JButton save = new JButton("Speichern");
        save.setBounds(10, 70, 150, 25);

        final JLabel error = new JLabel();
        error.setBounds(160, 70, 50, 25);

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Project project = new Project();
                project.setName(name.getText());
                project.setDescription(description.getText());
                try {
                    EntityController.getInstance().create(project);
                    setVisible(false);
                } catch (Exception e1) {
                    error.setText("" + e.getSource());
                }
            }
        });

        getContentPane().add(labelName);
        getContentPane().add(name);
        getContentPane().add(labelDescription);
        getContentPane().add(description);
        getContentPane().add(save);
        getContentPane().add(error);
    }

}
