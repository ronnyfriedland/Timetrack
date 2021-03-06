package de.ronnyfriedland.time.ui.dialog;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.ui.adapter.TimeTrackKeyAdapter;

/**
 * @author Ronny Friedland
 */
public class ShowHelpFrame extends AbstractFrame implements HyperlinkListener {

    private static final Logger LOG = Logger.getLogger(ShowHelpFrame.class.getName());
    private static final String README_FILE = "timetrack_readme.html";
    private static final long serialVersionUID = -3667564200048966812L;
    private final JEditorPane editorPane = new JEditorPane();

    /**
     * Erzeugt eine neue {@link ShowHelpFrame} Instanz.
     */
    public ShowHelpFrame() {
        super(Messages.HELP.getMessage(), 655, 455, true);
        createUI();
    }

    /**
     * {@inheritDoc}
     *
     * @see de.ronnyfriedland.time.ui.dialog.AbstractFrame#createUI()
     */
    @Override
    protected void createUI() {
        editorPane.setContentType("text/html");
        try {
            editorPane.setPage(Thread.currentThread().getContextClassLoader().getResource(README_FILE));
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Error loading help file", e);
        }
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(this);
        editorPane.addKeyListener(new TimeTrackKeyAdapter());
        getContentPane().setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane(editorPane);
        getContentPane().add(scrollPane);
    }

    /**
     * {@inheritDoc}
     *
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    @Override
    public void hyperlinkUpdate(final HyperlinkEvent event) {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                editorPane.setPage(event.getURL());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * The main method
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        new ShowHelpFrame().setVisible(true);
    }

}
