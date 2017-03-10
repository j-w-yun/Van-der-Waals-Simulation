package org.yoon_technology.gui;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.yoon_technology.engine.Engine;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Window extends JFrame {

	private static final long serialVersionUID = 2L;
	public volatile boolean lookingUp;
	public volatile boolean lookingDown;
	public volatile boolean lookingLeft;
	public volatile boolean lookingRight;
	public volatile boolean zoomingIn;
	public volatile boolean zoomingOut;
	private static Window instance = null;

	public static Window getInstance() {
		return instance == null ? instance = new Window() : instance;
	}

	public static Window getInstance(String title) {
		return instance == null ? instance = new Window(title) : instance;
	}

	private Window() {
		this("Demonstration");
	}

	// Funnel to here
	private Window(String title) {
		super(title);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
	}

	public static void create() {
		getInstance();
	}

	public static void create(String title) {
		getInstance(title);
	}

	public static void addPanel(JPanel panel, String layoutType) {
		instance.add(panel, layoutType);
	}

	public static void start() {
		instance.setDefaultCloseOperation(EXIT_ON_CLOSE);
		instance.pack();
		instance.setVisible(true);
		instance.initializeMenuBar();
		centerWindow();
	}

	public static void input(Engine engine, double passedTime) {
		if(instance.lookingUp) {		// up
			engine.getCamera().setOrientation(engine.getCamera().getOrientation().add(new Vector3d(-1.00 * passedTime, 0.0, 0.0)));
		}
		if(instance.lookingDown) {		// down
			engine.getCamera().setOrientation(engine.getCamera().getOrientation().add(new Vector3d(1.00 * passedTime, 0.0, 0.0)));
		}
		if(instance.lookingLeft) {		// left
			engine.getCamera().setOrientation(engine.getCamera().getOrientation().add(new Vector3d(0.0, 1.00 * passedTime, 0.0)));
		}
		if(instance.lookingRight) {		// right
			engine.getCamera().setOrientation(engine.getCamera().getOrientation().add(new Vector3d(0.0, -1.00 * passedTime, 0.0)));
		}
	}

	private static void centerWindow() {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - instance.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - instance.getHeight()) / 2);
		instance.setLocation(x, y - 15);
	}


	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {			// up
					lookingUp = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN) {		// down
					lookingDown = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_LEFT) {		// left
					lookingLeft = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT) {		// right
					lookingRight = true;
				}

			} else if (e.getID() == KeyEvent.KEY_RELEASED) {
				if(e.getKeyCode() == KeyEvent.VK_UP) {			// up
					lookingUp = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN) {		// down
					lookingDown = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_LEFT) {		// left
					lookingLeft = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT) {		// right
					lookingRight = false;
				}

			} else if (e.getID() == KeyEvent.KEY_TYPED) {}

			return false;
		}
	}

	private void initializeMenuBar() {
		// Create menu bar
		JMenuBar menubar = new JMenuBar();

		// Tab "File"
		JMenu file = new JMenu("File");

		// Drop-down item
		JMenuItem fMenuItem_1 = new JMenuItem("Exit", null);
		fMenuItem_1.setMnemonic(KeyEvent.VK_E);
		fMenuItem_1.setToolTipText("Close application");
		fMenuItem_1.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		file.add(fMenuItem_1);

		// Tab "About"
		JMenu about = new JMenu("About");

		// Drop-down item
		JMenuItem aMenuItem_1 = new JMenuItem("Dedication", null);
		aMenuItem_1.setMnemonic(KeyEvent.VK_D);
		aMenuItem_1.setToolTipText("Family");
		aMenuItem_1.addActionListener((ActionEvent event) -> {
			JOptionPane.showMessageDialog(
					null,
					"For mother.\n\n"
							+ "Jaewan Yun\n"
							+ "JAY50@pitt.edu",
							"Dedication",
							-1);
		});
		about.add(aMenuItem_1);

		// Add to menu bar
		menubar.add(file);
		menubar.add(about);

		// Add menu bar to JFrame
		setJMenuBar(menubar);

		menubar.setOpaque(true);
		menubar.setBorder(BorderFactory.createEmptyBorder());
	}
}
