package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;
import javax.swing.DefaultListSelectionModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;

import de.ronnyfriedland.time.config.Const;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.entity.Protocol;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ProtocolController;
import de.ronnyfriedland.time.ui.adapter.TimeTrackKeyAdapter;
import de.ronnyfriedland.time.ui.to.EntryData;

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
    private final JButton delete = new JButton(Messages.DELETE.getMessage());
    private final JScrollPane scrollPaneEntries = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    private final JList entries = new JList();

    private String uuid = null;

    private class TimetrackEntryViewCellRenderer extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        public TimetrackEntryViewCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index,
                final boolean isSelected, final boolean cellHasFocus) {
            EntryData data = (EntryData) value;
            setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
            setBackground(Color.WHITE);
            setText(data.entryDescription + " (" + data.entryDate + ")");
            return this;
        }
    }

    /**
     * Erzeugt eine neue {@link NewProjectFrame} Instanz.
     */
    public NewProjectFrame() {
        super(Messages.CREATE_NEW_PROJECT.getMessage(), 585, 300, false);
        getContentPane().setBackground(Const.COLOR_BACKGROUND);
        createUI();
    }

    public NewProjectFrame(final Project project) {
        this();
        uuid = project.getUuid();
        name.setText(project.getName());
        description.setText(project.getDescription());
        disableProject.setSelected(!project.getEnabled());
        if (project.getEntries().isEmpty()) {
            delete.setEnabled(true);
        }
        Collection<Entry> entryList = project.getEntries();
        EntryData[] entryNameList = new EntryData[entryList.size()];
        int i = 0;
        for (Entry e : entryList) {
            entryNameList[i] = new EntryData(e.getUuid(), e.getDescription(), e.getDateString());
            i++;
        }
        entries.setListData(entryNameList);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.ronnyfriedland.time.ui.dialog.AbstractFrame#createUI()
     */
    @Override
    protected void createUI() {
        getContentPane().addKeyListener(new TimeTrackKeyAdapter());

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 100, 202, 0 };
        gridBagLayout.rowHeights = new int[] { 28, 28, 28, 28, 0, 10, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
        getContentPane().setLayout(gridBagLayout);
        GridBagConstraints gbc_labelName = new GridBagConstraints();
        gbc_labelName.fill = GridBagConstraints.HORIZONTAL;
        gbc_labelName.insets = new Insets(25, 25, 5, 25);
        gbc_labelName.gridx = 0;
        gbc_labelName.gridy = 0;
        getContentPane().add(labelName, gbc_labelName);

        name.setName("name");
        name.addKeyListener(new TimeTrackKeyAdapter());
        name.setInputVerifier(new InputVerifier() {
            /**
             * {@inheritDoc}
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
        gbc_labelDescription.insets = new Insets(10, 25, 5, 25);
        gbc_labelDescription.gridx = 0;
        gbc_labelDescription.gridy = 1;
        getContentPane().add(labelDescription, gbc_labelDescription);

        description.setName("description");
        description.addKeyListener(new TimeTrackKeyAdapter());

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
        gbc_labelDisable.insets = new Insets(10, 25, 5, 25);
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

        scrollPaneEntries.setViewportView(entries);
        entries.setCellRenderer(new TimetrackEntryViewCellRenderer());

        entries.setName("entries");
        entries.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        entries.addKeyListener(new TimeTrackKeyAdapter());
        entries.setEnabled(false);

        GridBagConstraints gbc_entries = new GridBagConstraints();
        gbc_entries.gridheight = 3;
        gbc_entries.gridwidth = 2;
        gbc_entries.insets = new Insets(5, 25, 5, 25);
        gbc_entries.fill = GridBagConstraints.BOTH;
        gbc_entries.gridx = 0;
        gbc_entries.gridy = 3;
        getContentPane().add(scrollPaneEntries, gbc_entries);
        delete.setHorizontalAlignment(SwingConstants.LEADING);
        delete.setEnabled(false);
        delete.addKeyListener(new TimeTrackKeyAdapter());
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project project;
                if (null != uuid) {
                    project = EntityController.getInstance().findById(Project.class, uuid);
                    EntityController.getInstance().delete(project);
                    ProtocolController.getInstance()
                            .writeProtocol(new Protocol(Protocol.ProtocolValue.PROJECT_DELETED));

                    setVisible(false);
                }
            }
        });
        save.setHorizontalAlignment(SwingConstants.LEADING);
        save.addKeyListener(new TimeTrackKeyAdapter());
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
                        ProtocolController.getInstance().writeProtocol(
                                new Protocol(Protocol.ProtocolValue.PROJECT_UPDATED));
                    } else {
                        EntityController.getInstance().create(project);
                        ProtocolController.getInstance().writeProtocol(
                                new Protocol(Protocol.ProtocolValue.PROJECT_CREATED));
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
        GridBagConstraints gbc_save = new GridBagConstraints();
        gbc_save.insets = new Insets(10, 25, 10, 25);
        gbc_save.anchor = GridBagConstraints.EAST;
        gbc_save.gridx = 0;
        gbc_save.gridy = 6;
        getContentPane().add(save, gbc_save);

        GridBagConstraints gbc_delete = new GridBagConstraints();
        gbc_delete.anchor = GridBagConstraints.EAST;
        gbc_delete.insets = new Insets(10, 10, 10, 25);
        gbc_delete.gridx = 1;
        gbc_delete.gridy = 6;
        getContentPane().add(delete, gbc_delete);
    }

    public static void main(final String[] args) {
        new NewProjectFrame().setVisible(true);
    }
}
