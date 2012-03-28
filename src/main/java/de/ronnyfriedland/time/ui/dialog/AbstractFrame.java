package de.ronnyfriedland.time.ui.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
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
		setAlwaysOnTop(true);
	}

	protected void formatError(JComponent component) {
		component.setBorder(BorderFactory.createLineBorder(new Color(255, 127, 80)));
	}

	protected void formatOk(JComponent... components) {
		for (JComponent component : components) {
			component.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150)));
		}
	}

	/**
	 * Frame-spezifische Konfiguration.
	 */
	protected abstract void createUI();
}
