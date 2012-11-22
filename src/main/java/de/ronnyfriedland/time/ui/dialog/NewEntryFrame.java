package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;

import de.ronnyfriedland.time.config.Const;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.EntryState;
import de.ronnyfriedland.time.entity.EntryState.State;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.sort.SortParam;
import de.ronnyfriedland.time.sort.SortParam.SortOrder;
import de.ronnyfriedland.time.ui.adapter.TimeTableKeyAdapter;

/**
 * @author Ronny Friedland
 */
public class NewEntryFrame extends AbstractFrame {
    private static final Logger LOG = Logger.getLogger(NewEntryFrame.class.getName());

    private static final long serialVersionUID = -8738367859388084898L;

    private final JLabel labelDate = new JLabel(Messages.DATE.getMessage());
    private final JTextField date = new JTextField();
    private final JLabel labelDescription = new JLabel(Messages.DESCRIPTION.getMessage());
    private final JTextField description = new JTextField();
    private final JLabel labelDuration = new JLabel(Messages.DURATION.getMessage());
    private final JTextField duration = new JTextField();
    private final JScrollPane scrollPaneProjects = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    private final JCheckBox showDisabledProjects = new JCheckBox(Messages.SHOW_DISABLED_PROJECT.getMessage());
    private final JButton start = new JButton(new ImageIcon(Thread.currentThread().getContextClassLoader()
            .getResource("images/start.png")));
    private final JButton stop = new JButton(new ImageIcon(Thread.currentThread().getContextClassLoader()
            .getResource("images/stop.png")));
    private final JButton refresh = new JButton(Messages.REFRESH_PROJECT.getMessage());
    private final JButton save = new JButton(Messages.SAVE.getMessage());
    private final JButton delete = new JButton(Messages.DELETE.getMessage());

    private class MyListCellThing extends JLabel implements ListCellRenderer {
        private static final long serialVersionUID = 1L;

        public MyListCellThing() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index,
                final boolean isSelected, final boolean cellHasFocus) {
            ProjectData data = (ProjectData) value;
            if (data.enabled) {
                setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
                setForeground(Color.DARK_GRAY);
            } else {
                setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
                setForeground(Color.GRAY);
            }
            if (isSelected) {
                setBackground(new Color(184, 207, 229));
            } else {
                setBackground(Color.WHITE);
            }
            setText(data.projectName + " (" + data.entryCount + ")");
            setToolTipText(data.projectName + " (" + data.description + ")");
            return this;
        }
    }

    private final JList projects = new JList();
    {
    }

    private class ProjectData {
        private final String projectName;
        private final String description;
        private final Integer entryCount;
        private final Boolean enabled;

        public ProjectData(final String projectName, final String description, final boolean enabled,
                final Integer entryCount) {
            this.projectName = projectName;
            this.enabled = enabled;
            this.entryCount = entryCount;
            if (!StringUtils.isBlank(description)) {
                this.description = description;
            } else {
                this.description = StringUtils.EMPTY;
            }
        }
    }

    private String uuid = null;
    private final JPanel panel = new JPanel();

    public NewEntryFrame() {
        super(Messages.CREATE_NEW_ENTRY.getMessage(), 570, 350, false);
        getContentPane().setBackground(Const.COLOR_BACKGROUND);
        createUI();
        loadProjectListData(false);
    }

    public NewEntryFrame(final Entry entry) {
        this();
        uuid = entry.getUuid();
        date.setText(entry.getDateString());
        description.setText(entry.getDescription());
        duration.setText(entry.getCalculatedDuration(new Date()));
        switch (entry.getState().getState()) {
        case OK:
        case WARN:
            duration.setEditable(false);
            start.setEnabled(false);
            break;
        case FIXED:
        case STOPPED:
        default:
            start.setEnabled(false);
            stop.setEnabled(false);
            break;
        }
        ListModel model = projects.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ProjectData item = (ProjectData) model.getElementAt(i);
            if (entry.getProject().getName().equals(item.projectName)) {
                projects.setSelectedIndex(i);
            }
        }
        delete.setEnabled(true);
    }

    /**
     * (non-Javadoc)
     * 
     * @see de.ronnyfriedland.time.ui.dialog.AbstractFrame#createUI()
     */
    @Override
    protected void createUI() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 250, 30, 30, 35, 80, 0 };
        gridBagLayout.rowHeights = new int[] { 22, 22, 22, 100, 22, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0 };
        getContentPane().setLayout(gridBagLayout);
        // configure
        labelDate.setBounds(10, 10, 100, 24);
        GridBagConstraints gbc_labelDate = new GridBagConstraints();
        gbc_labelDate.fill = GridBagConstraints.BOTH;
        gbc_labelDate.insets = new Insets(25, 25, 5, 5);
        gbc_labelDate.gridx = 0;
        gbc_labelDate.gridy = 0;
        getContentPane().add(labelDate, gbc_labelDate);

        date.setName("date");
        date.setBounds(110, 10, 200, 24);
        date.setText(new SimpleDateFormat(Entry.DATESTRINGFORMAT).format(new Date()));
        date.addKeyListener(new TimeTableKeyAdapter());
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
                Set<ConstraintViolation<Entry>> violations = validator.validateValue(Entry.class, "dateString",
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
        GridBagConstraints gbc_date = new GridBagConstraints();
        gbc_date.fill = GridBagConstraints.BOTH;
        gbc_date.insets = new Insets(25, 10, 5, 10);
        gbc_date.gridwidth = 3;
        gbc_date.gridx = 1;
        gbc_date.gridy = 0;
        getContentPane().add(date, gbc_date);

        labelDescription.setBounds(10, 35, 100, 24);
        GridBagConstraints gbc_labelDescription = new GridBagConstraints();
        gbc_labelDescription.fill = GridBagConstraints.BOTH;
        gbc_labelDescription.insets = new Insets(10, 25, 5, 5);
        gbc_labelDescription.gridx = 0;
        gbc_labelDescription.gridy = 1;
        getContentPane().add(labelDescription, gbc_labelDescription);

        description.setName("description");
        description.setBounds(110, 35, 200, 24);
        description.addKeyListener(new TimeTableKeyAdapter());
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
                    formatOk(arg0);
                } else {
                    formatError(arg0);
                }
                return valid;
            }
        });
        GridBagConstraints gbc_description = new GridBagConstraints();
        gbc_description.fill = GridBagConstraints.BOTH;
        gbc_description.insets = new Insets(10, 10, 5, 10);
        gbc_description.gridwidth = 3;
        gbc_description.gridx = 1;
        gbc_description.gridy = 1;
        getContentPane().add(description, gbc_description);

        labelDuration.setBounds(10, 60, 100, 24);
        GridBagConstraints gbc_labelDuration = new GridBagConstraints();
        gbc_labelDuration.fill = GridBagConstraints.BOTH;
        gbc_labelDuration.insets = new Insets(10, 25, 5, 5);
        gbc_labelDuration.gridx = 0;
        gbc_labelDuration.gridy = 2;
        getContentPane().add(labelDuration, gbc_labelDuration);

        duration.setName("duration");
        duration.setBounds(110, 60, 150, 24);
        duration.setText("0");
        duration.addKeyListener(new TimeTableKeyAdapter());
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
                    formatOk(arg0);
                } else {
                    formatError(arg0);
                }
                return valid;
            }
        });
        GridBagConstraints gbc_duration = new GridBagConstraints();
        gbc_duration.fill = GridBagConstraints.BOTH;
        gbc_duration.insets = new Insets(10, 10, 5, 5);
        gbc_duration.gridwidth = 2;
        gbc_duration.gridx = 1;
        gbc_duration.gridy = 2;
        getContentPane().add(duration, gbc_duration);
        scrollPaneProjects.setBounds(10, 85, 300, 100);
        scrollPaneProjects.setViewportView(projects);
        projects.setCellRenderer(new MyListCellThing());

        projects.setName("projects");
        projects.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        projects.addKeyListener(new TimeTableKeyAdapter());
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
                    formatOk(arg0);
                } else {
                    formatError(arg0);
                }
                return valid;
            }
        });
        projects.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if ((e.getClickCount() == 2) && (null != projects.getSelectedValue())) {
                    Map<String, Object> parameters = new HashMap<String, Object>();
                    parameters.put(Project.PARAM_NAME, ((ProjectData) projects.getSelectedValue()).projectName);
                    Project project = EntityController.getInstance().findSingleResultByParameter(Project.class,
                            Project.QUERY_FINDBYNAME, parameters);

                    new NewProjectFrame(project).setVisible(true);
                    if (StringUtils.isBlank(date.getText()) || StringUtils.isBlank(description.getText())
                            || StringUtils.isBlank(duration.getText())) {
                        setVisible(false);
                    }
                }
            }
        });
        panel.setBackground(Const.COLOR_BACKGROUND);
        panel.setLayout(null);

        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(10, 0, 5, 10);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 3;
        gbc_panel.gridy = 2;
        getContentPane().add(panel, gbc_panel);
        panel.add(start);

        start.setBounds(191, 0, 16, 16);
        panel.add(stop);

        stop.setBounds(220, 0, 16, 16);
        stop.addKeyListener(new TimeTableKeyAdapter());
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                Entry entry = null;
                if (null != uuid) {
                    entry = EntityController.getInstance().findById(Entry.class, uuid);
                    EntryState entryState = entry.getState();
                    if (!State.STOPPED.equals(entryState.getState()) && !State.FIXED.equals(entryState.getState())) {
                        // update entrystate
                        entryState.setEnd(Calendar.getInstance().getTime());
                        entryState.setState(State.STOPPED);
                        // update entry
                        entry.setDuration(EntryState.getDuration(entryState.getStart(), entryState.getEnd(),
                                entry.getDuration()));
                        EntityController.getInstance().update(entry);

                        setVisible(false);
                    }
                }
            }
        });
        start.addKeyListener(new TimeTableKeyAdapter());
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                Entry entry;
                if (null == uuid) {
                    entry = createEntry(date.getText(), description.getText(), duration.getText(), State.OK,
                            (ProjectData) projects.getSelectedValue());
                    if (null != entry) {
                        setVisible(false);
                    }
                }
            }
        });

        formatOk(date, description, duration, projects);
        GridBagConstraints gbc_scrollPaneProjects = new GridBagConstraints();
        gbc_scrollPaneProjects.gridheight = 2;
        gbc_scrollPaneProjects.fill = GridBagConstraints.BOTH;
        gbc_scrollPaneProjects.insets = new Insets(10, 25, 5, 10);
        gbc_scrollPaneProjects.gridwidth = 4;
        gbc_scrollPaneProjects.gridx = 0;
        gbc_scrollPaneProjects.gridy = 3;
        getContentPane().add(scrollPaneProjects, gbc_scrollPaneProjects);
        delete.setEnabled(false);

        delete.setBounds(165, 245, 145, 24);
        delete.addKeyListener(new TimeTableKeyAdapter());
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (null != uuid) {
                    try {
                        Entry entry = EntityController.getInstance().findById(Entry.class, uuid);
                        Project project = entry.getProject();
                        project.getEntries().remove(entry);
                        EntityController.getInstance().delete(entry);
                        EntityController.getInstance().update(project);
                        setVisible(false);
                    } catch (PersistenceException ex) {
                        LOG.log(Level.SEVERE, "Error removing project", ex);
                        formatError(delete);
                    }
                }
            }
        });

        save.setBounds(10, 245, 145, 24);
        save.addKeyListener(new TimeTableKeyAdapter());
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Entry entry = null;
                if (null == uuid) {
                    entry = createEntry(date.getText(), description.getText(), duration.getText(), State.FIXED,
                            (ProjectData) projects.getSelectedValue());
                } else {
                    entry = EntityController.getInstance().findById(Entry.class, uuid);
                    entry = updateEntry(entry, date.getText(), description.getText(), duration.getText(),
                            (ProjectData) projects.getSelectedValue());
                }
                if (null != entry) {
                    setVisible(false);
                }
            }
        });
        GridBagConstraints gbc_save = new GridBagConstraints();
        gbc_save.anchor = GridBagConstraints.NORTH;
        gbc_save.fill = GridBagConstraints.HORIZONTAL;
        gbc_save.insets = new Insets(10, 25, 5, 10);
        gbc_save.gridx = 0;
        gbc_save.gridy = 5;
        getContentPane().add(save, gbc_save);
        showDisabledProjects.setBackground(Const.COLOR_BACKGROUND);

        showDisabledProjects.setBounds(10, 185, 300, 24);
        GridBagConstraints gbc_showDisabledProjects = new GridBagConstraints();
        gbc_showDisabledProjects.insets = new Insets(10, 25, 5, 5);
        gbc_showDisabledProjects.fill = GridBagConstraints.HORIZONTAL;
        gbc_showDisabledProjects.gridx = 3;
        gbc_showDisabledProjects.gridy = 5;
        getContentPane().add(showDisabledProjects, gbc_showDisabledProjects);
        GridBagConstraints gbc_delete = new GridBagConstraints();
        gbc_delete.insets = new Insets(10, 25, 5, 10);
        gbc_delete.anchor = GridBagConstraints.NORTH;
        gbc_delete.fill = GridBagConstraints.HORIZONTAL;
        gbc_delete.gridx = 0;
        gbc_delete.gridy = 6;
        getContentPane().add(delete, gbc_delete);

        refresh.setBounds(10, 215, 300, 24);
        refresh.addKeyListener(new TimeTableKeyAdapter());
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                loadProjectListData(showDisabledProjects.isSelected());
            }
        });
        GridBagConstraints gbc_refresh = new GridBagConstraints();
        gbc_refresh.anchor = GridBagConstraints.NORTH;
        gbc_refresh.insets = new Insets(10, 25, 5, 5);
        gbc_refresh.fill = GridBagConstraints.HORIZONTAL;
        gbc_refresh.gridx = 3;
        gbc_refresh.gridy = 6;
        getContentPane().add(refresh, gbc_refresh);
    }

    private void loadProjectListData(final boolean includeDisabledProjects) {
        Collection<Project> projectList = EntityController.getInstance().findAll(Project.class,
                new SortParam(Project.PARAM_NAME, SortOrder.ASC), includeDisabledProjects);
        ProjectData[] projectNameList = new ProjectData[projectList.size()];
        int i = 0;
        for (Project project : projectList) {
            projectNameList[i] = new ProjectData(project.getName(), project.getDescription(), project.getEnabled(),
                    project.getEntries().size());
            i++;
        }
        projects.setListData(projectNameList);
    }

    private Entry createEntry(final String date, final String description, final String duration, final State state,
            final ProjectData projectData) {
        Entry result = null;
        try {
            Entry entry = new Entry();
            if (!StringUtils.isBlank(date)) {
                entry.setDateString(date);
            }
            if (!StringUtils.isBlank(description)) {
                entry.setDescription(description);
            }
            if (!StringUtils.isBlank(duration)) {
                entry.setDuration(duration);
            }
            if (null != projectData) {
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put(Project.PARAM_NAME, projectData.projectName);
                Project selectedProject = EntityController.getInstance().findSingleResultByParameter(Project.class,
                        Project.QUERY_FINDBYNAME, parameters);
                selectedProject.addEntry(entry);
                entry.setProject(selectedProject);
                entry.setState(new EntryState(Calendar.getInstance().getTime(), state));
            }
            EntityController.getInstance().create(entry);
            result = entry;
        } catch (PersistenceException ex) {
            LOG.log(Level.SEVERE, "Error creating new entry", ex);
            formatError(getRootPane());
        } catch (ConstraintViolationException ex) {
            LOG.log(Level.SEVERE, "Error creating new entry", ex);
            formatError(getRootPane());
        }
        return result;
    }

    private Entry updateEntry(final Entry entry, final String date, final String description, final String duration,
            final ProjectData projectData) {
        Entry result = null;
        try {
            if (!StringUtils.isBlank(date)) {
                entry.setDateString(date);
            }
            if (!StringUtils.isBlank(description)) {
                entry.setDescription(description);
            }
            if (!StringUtils.isBlank(duration)) {
                entry.setDuration(duration);
            }
            if (null != projectData) {
                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put(Project.PARAM_NAME, projectData.projectName);
                Project selectedProject = EntityController.getInstance().findSingleResultByParameter(Project.class,
                        Project.QUERY_FINDBYNAME, parameters);
                if (!entry.getProject().equals(selectedProject)) {
                    entry.getProject().getEntries().remove(entry);
                    selectedProject.addEntry(entry);
                    entry.setProject(selectedProject);
                    EntityController.getInstance().update(entry.getProject());
                }
            }
            EntityController.getInstance().update(entry);
            result = entry;
        } catch (PersistenceException ex) {
            LOG.log(Level.SEVERE, "Error updating entry", ex);
            formatError(getRootPane());
        } catch (ConstraintViolationException ex) {
            LOG.log(Level.SEVERE, "Error updating entry", ex);
            formatError(getRootPane());
        }
        return result;
    }

    public static void main(final String[] args) {
        new NewEntryFrame().setVisible(true);
    }
}