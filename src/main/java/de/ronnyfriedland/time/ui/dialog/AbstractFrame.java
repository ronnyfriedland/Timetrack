package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author Ronny Friedland
 */
public abstract class AbstractFrame extends JFrame {

    private static final long serialVersionUID = 7680216672898188405L;

    /**
     * Creates a new {@link AbstractFrame} instance.
     *
     * @param title Titel des Frames
     * @param width Breite des Frames
     * @param height Höhe des Frames
     * @param resizeable Flag, ob Grösse änderbar
     */
    public AbstractFrame(final String title, final int width, final int height, final boolean resizeable) {
        Dimension frameSize = new Dimension(width + 5, height + 5);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int top = (screenSize.height - frameSize.height) / 2;
        int left = (screenSize.width - frameSize.width) / 2;
        setMinimumSize(frameSize);
        setLocation(left, top);
        setTitle(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(resizeable);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    /**
     * Komponentenformatierung im Fehlerfall
     *
     * @param component the component
     */
    protected void formatError(final JComponent component) {
        component.setBorder(BorderFactory.createLineBorder(Color.RED));
    }

    /**
     * Komponentenformatierung im OK-Fall
     *
     * @param components list of components
     */
    protected void formatOk(final JComponent... components) {
        for (JComponent component : components) {
            component.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
    }

    /**
     * Frame-spezifische Konfiguration.
     */
    protected abstract void createUI();
}
