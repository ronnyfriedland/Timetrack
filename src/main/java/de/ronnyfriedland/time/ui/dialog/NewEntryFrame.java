package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
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
        projects.setCellRenderer(new MyListCellThing());
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

    public NewEntryFrame() {
        super(Messages.CREATE_NEW_ENTRY.getMessage(), 320, 300);
        createUI();
        loadProjectListData(false);
    }

    public NewEntryFrame(final Entry entry) {
        this();
        uuid = entry.getUuid();
        date.setText(entry.getDateString());
        description.setText(entry.getDescription());
        duration.setText(entry.getDuration());
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
        // configure
        labelDate.setBounds(10, 10, 100, 24);

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

        labelDescription.setBounds(10, 35, 100, 24);

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

        labelDuration.setBounds(10, 60, 100, 24);

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

        scrollPaneProjects.setViewportView(projects);
        scrollPaneProjects.setBounds(10, 85, 300, 100);

        showDisabledProjects.setBounds(10, 185, 300, 24);

        start.setBounds(265, 62, 20, 20);
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

        stop.setBounds(290, 62, 20, 20);
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
                        entry.setDuration(EntryState.getDuration(entryState.getStart(), entryState.getEnd()));
                        EntityController.getInstance().update(entry);

                        setVisible(false);
                    }
                }
            }
        });

        refresh.setBounds(10, 215, 300, 24);
        refresh.addKeyListener(new TimeTableKeyAdapter());
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                loadProjectListData(showDisabledProjects.isSelected());
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

        delete.setBounds(165, 245, 145, 24);
        delete.addKeyListener(new TimeTableKeyAdapter());
        delete.setEnabled(false);
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

        formatOk(date, description, duration, projects);

        getContentPane().add(labelDate);
        getContentPane().add(date);
        getContentPane().add(labelDescription);
        getContentPane().add(description);
        getContentPane().add(labelDuration);
        getContentPane().add(duration);
        getContentPane().add(scrollPaneProjects);
        getContentPane().add(showDisabledProjects);
        getContentPane().add(start);
        getContentPane().add(stop);
        getContentPane().add(refresh);
        getContentPane().add(save);
        getContentPane().add(delete);
    }

    private void loadProjectListData(boolean includeDisabledProjects) {
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
        try {
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
        }
        try {
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