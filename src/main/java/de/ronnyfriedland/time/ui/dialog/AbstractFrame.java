package de.ronnyfriedland.time.ui.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/**
 * @author ronnyfriedland
 */
public abstract class AbstractFrame extends JFrame {

    private static final long serialVersionUID = 7680216672898188405L;

    public AbstractFrame(String title, int width, int height) {
        Dimension frameSize = new Dimension(width, height);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int top = (screenSize.height - frameSize.height) / 2;
        int left = (screenSize.width - frameSize.width) / 2;
        setSize(frameSize);
        setLocation(left, top);

        setLayout(null);
        setTitle(title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Frame-spezifische Konfiguration.
     */
    protected abstract void createUI();
}