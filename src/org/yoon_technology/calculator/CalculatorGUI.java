//package org.yoon_technology.calculator;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.Font;
//import java.awt.GridLayout;
//import java.awt.Insets;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JSlider;
//import javax.swing.JTextField;
//import javax.swing.JTextPane;
//import javax.swing.ScrollPaneConstants;
//import javax.swing.SwingConstants;
//import javax.swing.SwingUtilities;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.text.BadLocationException;
//import javax.swing.text.SimpleAttributeSet;
//import javax.swing.text.StyleConstants;
//import javax.swing.text.StyledDocument;
//
//import org.yoon_technology.engine.Camera;
//import org.yoon_technology.engine.Engine;
//import org.yoon_technology.gui.Display;
//import org.yoon_technology.gui.Window;
//import org.yoon_technology.math.Vector3d;
//
///**
// * Refer to LICENSE
// *
// * @author Jaewan Yun (jay50@pitt.edu)
// */
//
//public class CalculatorGUI extends JPanel implements ActionListener {
//
//	private static final long serialVersionUID = 5L;
//	private Calculator calculator;
//	private Engine engine;
//	private Display display;
//
//	// North Panel
//
//	private JPanel northPanel;
//
//	private JPanel upperPanel1;
//	private JPanel upperPanel2;
//
//	private JPanel xPanel;
//	private JPanel yPanel;
//	private JPanel zPanel;
//
//	private JPanel xSliderPanel;
//	private JPanel ySliderPanel;
//	private JPanel zSliderPanel;
//
//	private JPanel refreshPanel;
//
//	private JLabel varXLabel;
//	private JLabel varYLabel;
//	private JLabel densityLabel;
//
//	private JLabel xMinLabel;
//	private JLabel xMaxLabel;
//	private JLabel yMinLabel;
//	private JLabel yMaxLabel;
//	private JLabel zMinLabel;
//	private JLabel zMaxLabel;
//
//	private JLabel xStepLabel;
//	private JLabel yStepLabel;
//	private JLabel zStepLabel;
//
//	private JLabel xRotateLabel;
//	private JLabel yRotateLabel;
//	private JLabel zRotateLabel;
//
//	private MyTextField varXminField;
//	private MyTextField varXmaxField;
//	private MyTextField varYminField;
//	private MyTextField varYmaxField;
//
//	private MyTextField xMinField;
//	private MyTextField xMaxField;
//	private MyTextField yMinField;
//	private MyTextField yMaxField;
//	private MyTextField zMinField;
//	private MyTextField zMaxField;
//
//	private MyTextField xStepField;
//	private MyTextField yStepField;
//	private MyTextField zStepField;
//
//	private JSlider densitySlider;
//
//	private JSlider xRotateSlider;
//	private JSlider yRotateSlider;
//	private JSlider zRotateSlider;
//
//	private JSlider convinientSlider;
//
//	private JLabel xTranslateLabel;
//	private JLabel yTranslateLabel;
//	private JLabel zTranslateLabel;
//
//	private MyTextField xTranslateField;
//	private MyTextField yTranslateField;
//	private MyTextField zTranslateField;
//
//	private JButton refreshButton;
//	private boolean updateAll;
//
//
//	// Bottom Panel
//
//	private JPanel bottomPanel;
//
//	private JPanel bottomPanel_displayPanel;
//	private JPanel bottomPanel_buttonPanel;
//
//	private JPanel bottomPanel_buttonPanel1;
//	private JPanel bottomPanel_buttonPanel2;
//
//	private JButton[] panelOneButtons;
//	private JButton[] panelTwoButtons;
//
//
//	// Display Panel
//
//	private JSlider zoomSlider;
//	private JLabel zoomLabel1;
//	private JLabel zoomLabel2;
//	private JButton toggleIdleRotation;
//
//
//	// Console Panel
//
//	private JTextPane textDisplay;
//	private JTextField input;
//
//
//
//	// Restore GUI settings
//	public void restoreGUIdefault() {
//		calculator.restoreWorldDefault();
//
//		// Reset sliders
//		engine.getCamera().reset();
//		xRotateSlider.setValue(0);
//		yRotateSlider.setValue(0);
//		zRotateSlider.setValue(0);
//		zoomSlider.setValue(0);
//		convinientSlider.setValue(0);
//
//		// Reset all fields
//		xMinField.setText(Double.toString(calculator.xMin));
//		xMaxField.setText(Double.toString(calculator.xMax));
//		xStepField.setText(Double.toString(calculator.xStep));
//		yMinField.setText(Double.toString(calculator.yMin));
//		yMaxField.setText(Double.toString(calculator.yMax));
//		yStepField.setText(Double.toString(calculator.yStep));
//		zMinField.setText(Double.toString(calculator.zMin));
//		zMaxField.setText(Double.toString(calculator.zMax));
//		zStepField.setText(Double.toString(calculator.zStep));
//
//		xTranslateField.setText("0.0");
//		yTranslateField.setText("0.0");
//		zTranslateField.setText("0.0");
//
//		calculator.calculate(engine.getWorld().get(0));
//	}
//
//	public static void startGUI() {
//
//		Calculator calculator = new Calculator();
//
//		Engine engine = new Engine();
//		Display display = new Display(500, 500, engine);
//		display.setBackground(Color.BLACK);
//		display.setLayout(new FlowLayout(SwingConstants.RIGHT));
//
//		engine.addEmptyWorld();
//		engine.addCamera(calculator.getOrigin(), Camera.ORTHOGRAPHIC_PROJECTION);
//		engine.getCamera().setOrientation(new Vector3d(-0.05, -0.05, 0.0));
//		engine.addDisplay(display);
//
//		display.setWorld(engine.getWorld().get(0));
//
//		CalculatorGUI gui = new CalculatorGUI(calculator, engine, display);
//
//		try {
//			SwingUtilities.invokeAndWait(new Runnable() {
//				@Override
//				public void run() {
//
//					// Generate world
//					Window.create("Yun, Jaewan");
//					Window.addPanel(gui, BorderLayout.EAST);
//					Window.addPanel(display, BorderLayout.CENTER);
//					Window.start();
//				}
//			});
//		} catch (Exception e) {
//			System.err.println("Failed to create GUI.");
//			e.printStackTrace();
//		}
//
//		calculator.calculate(engine.getWorld().get(0));
//		engine.start();
//	}
//
//	public CalculatorGUI(Calculator calculator, Engine engine, Display display) {
//		super(new BorderLayout());
//		this.calculator = calculator;
//		this.engine = engine;
//		this.display = display;
//
//		// Display
//
//		toggleIdleRotation = new JButton("Stop Spin");
//		formatComponents(toggleIdleRotation);
//		display.add(toggleIdleRotation);
//		toggleIdleRotation.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if(toggleIdleRotation.getText().equals("Idle Spin")) {
//					engine.rotateOn(true);
//					toggleIdleRotation.setText("Stop Spin");
//				} else {
//					engine.rotateOn(false);
//					toggleIdleRotation.setText("Idle Spin");
//				}
//			}
//		});
//
//		zoomLabel1 = new JLabel("-");
//		zoomLabel1.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 20));
//		zoomLabel1.setForeground(Color.WHITE);
//		zoomLabel2 = new JLabel("+");
//		zoomLabel2.setFont(new Font("Lucida Sans Typewriter", Font.BOLD, 20));
//		zoomLabel2.setForeground(Color.WHITE);
//
//		zoomSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
//		zoomSlider.setBackground(Color.GRAY);
//		display.add(zoomLabel1);
//		display.add(zoomSlider);
//		display.add(zoomLabel2);
//		zoomSlider.setPreferredSize(new Dimension(200, 20));
//		zoomSlider.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				calculator.axialLength = (zoomSlider.getValue() * zoomSlider.getValue()) * 10;
//				calculator.calculate(engine.getWorld().get(0));
//				display.requestFocus();
//			}
//		});
//
//
//		// TODO RESET VIEW. ALL SLIDERS
//		JButton resetCameraViewButton = new JButton("Reset View");
//		formatComponents(resetCameraViewButton);
//		display.add(resetCameraViewButton);
//		resetCameraViewButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				restoreGUIdefault();
//				calculator.restoreWorldDefault();
//			}
//		});
//
//
//		// North Panel
//
//		northPanel = new JPanel(new BorderLayout());
//
//		upperPanel1 = new JPanel(new GridLayout(0, 1));
//		upperPanel2 = new JPanel(new FlowLayout());//new GridLayout(0, 1));
//
//		xPanel = new JPanel(new FlowLayout());
//		xPanel.setPreferredSize(new Dimension(200, 150));
//		xPanel.setBackground(Color.LIGHT_GRAY);
//		yPanel = new JPanel(new FlowLayout());
//		yPanel.setBackground(Color.LIGHT_GRAY);
//		yPanel.setPreferredSize(new Dimension(200, 150));
//		zPanel = new JPanel(new FlowLayout());
//		zPanel.setPreferredSize(new Dimension(200, 150));
//		zPanel.setBackground(Color.LIGHT_GRAY);
//
//		xSliderPanel = new JPanel(new FlowLayout());
//		ySliderPanel = new JPanel(new FlowLayout());
//		zSliderPanel = new JPanel(new FlowLayout());
//
//		refreshPanel = new JPanel(new GridLayout(0, 1));
//
//		xMinField = new MyTextField(4);
//		xMinField.setText("-10");
//		xMinField.addActionListener(this);
//		xMaxField = new MyTextField(4);
//		xMaxField.setText("10");
//		xMaxField.addActionListener(this);
//		xStepField = new MyTextField(4);
//		xStepField.setText("2");
//		xStepField.addActionListener(this);
//
//		yMinField = new MyTextField(4);
//		yMinField.setText("-10");
//		yMinField.addActionListener(this);
//		yMaxField = new MyTextField(4);
//		yMaxField.setText("10");
//		yMaxField.addActionListener(this);
//		yStepField = new MyTextField(4);
//		yStepField.setText("2");
//		yStepField.addActionListener(this);
//
//		zMinField = new MyTextField(4);
//		zMinField.setText("-10");
//		zMinField.addActionListener(this);
//		zMaxField = new MyTextField(4);
//		zMaxField.setText("10");
//		zMaxField.addActionListener(this);
//		zStepField = new MyTextField(4);
//		zStepField.setText("2");
//		zStepField.addActionListener(this);
//
//		xMinLabel = new JLabel("X Min");
//		xMaxLabel = new JLabel("X Max");
//		xStepLabel = new JLabel("Step X");
//
//		yMinLabel = new JLabel("Y Min");
//		yMaxLabel = new JLabel("Y Max");
//		yStepLabel = new JLabel("Step Y");
//
//		zMinLabel = new JLabel("Z Min");
//		zMaxLabel = new JLabel("Z Max");
//		zStepLabel = new JLabel("Step Z");
//
//		xRotateLabel = new JLabel("Rotate X");
//		yRotateLabel = new JLabel("Rotate Y");
//		zRotateLabel = new JLabel("Rotate Z");
//
//		xTranslateField = new MyTextField(3);
//		xTranslateField.setText("0");
//		xTranslateField.addActionListener(this);
//		yTranslateField = new MyTextField(3);
//		yTranslateField.setText("0");
//		yTranslateField.addActionListener(this);
//		zTranslateField = new MyTextField(3);
//		zTranslateField.setText("0");
//		zTranslateField.addActionListener(this);
//
//		xTranslateLabel = new JLabel("Translate X");
//		yTranslateLabel = new JLabel("Translate Y");
//		zTranslateLabel = new JLabel("Translate Z");
//
//
//
//		// Sliders for rotation
//
//		xRotateSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
//		xRotateSlider.setPreferredSize(new Dimension(110, 20));
//		xRotateSlider.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				engine.getCamera().getOrientation().setX(
//						xRotateSlider.getValue() / -15.915);
//			}
//		});
//		yRotateSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
//		yRotateSlider.setPreferredSize(new Dimension(110, 20));
//		yRotateSlider.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				engine.getCamera().getOrientation().setY(
//						yRotateSlider.getValue() / -15.915);
//			}
//		});
//		zRotateSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
//		zRotateSlider.setPreferredSize(new Dimension(110, 20));
//		zRotateSlider.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				engine.getCamera().getOrientation().setZ(
//						zRotateSlider.getValue() / -15.915);
//			}
//		});
//
//		convinientSlider = new JSlider(SwingConstants.HORIZONTAL, 5, 500000000, 500000);
//		convinientSlider.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent arg0) {
//				double deltaValue = Math.log((double)convinientSlider.getValue());
//				if(deltaValue == 0)
//					return;
//
//				String negVal = Double.toString(-deltaValue);
//				if(negVal.length() > 7)
//					negVal = negVal.substring(0, 7);
//				String posVal = Double.toString(deltaValue);
//				if(posVal.length() > 6)
//					posVal = posVal.substring(0, 6);
//
//				xMinField.setText(negVal);
//				xMaxField.setText(posVal);
//
//				yMinField.setText(negVal);
//				yMaxField.setText(posVal);
//
//				zMinField.setText(negVal);
//				zMaxField.setText(posVal);
//
//				double stepSize = (deltaValue / 2.0);
//
//				String stepVal = Double.toString(stepSize);
//				if(stepVal.length() > 6)
//					stepVal = stepVal.substring(0, 6);
//
//				xStepField.setText(stepVal);
//				yStepField.setText(stepVal);
//				zStepField.setText(stepVal);
//			}
//		});
//
//		xPanel.add(xMinLabel);
//		xPanel.add(xMinField);
//		xPanel.add(xMaxLabel);
//		xPanel.add(xMaxField);
//		xPanel.add(xStepLabel);
//		xPanel.add(xStepField);
//
//		xSliderPanel.add(xTranslateLabel);
//		xSliderPanel.add(xTranslateField);
//		xSliderPanel.add(xRotateLabel);
//		xSliderPanel.add(xRotateSlider);
//
//		yPanel.add(yMinLabel);
//		yPanel.add(yMinField);
//		yPanel.add(yMaxLabel);
//		yPanel.add(yMaxField);
//		yPanel.add(yStepLabel);
//		yPanel.add(yStepField);
//
//		ySliderPanel.add(yTranslateLabel);
//		ySliderPanel.add(yTranslateField);
//		ySliderPanel.add(yRotateLabel);
//		ySliderPanel.add(yRotateSlider);
//
//		zPanel.add(zMinLabel);
//		zPanel.add(zMinField);
//		zPanel.add(zMaxLabel);
//		zPanel.add(zMaxField);
//		zPanel.add(zStepLabel);
//		zPanel.add(zStepField);
//
//		zSliderPanel.add(zTranslateLabel);
//		zSliderPanel.add(zTranslateField);
//		zSliderPanel.add(zRotateLabel);
//		zSliderPanel.add(zRotateSlider);
//
//		upperPanel1.add(xPanel);
//		upperPanel1.add(xSliderPanel);
//		upperPanel1.add(yPanel);
//		upperPanel1.add(ySliderPanel);
//		upperPanel1.add(zPanel);
//		upperPanel1.add(zSliderPanel);
//
//		refreshButton = new JButton("Update Settings");
//		refreshButton.addActionListener(this);
//
//		refreshPanel.add(convinientSlider);
//		refreshPanel.add(refreshButton);
//
//		upperPanel1.add(refreshPanel);
//		upperPanel1.setPreferredSize(new Dimension(280, 250));
//
//
//		varXLabel = new JLabel("Var X Range");
//		varYLabel = new JLabel("Var Y Range");
//
//		varXminField = new MyTextField(4);
//		varXminField.setText("MIN");
//		varXminField.addActionListener(this);
//		varXminField.addFocusListener(new FocusListener() {
//			@Override
//			public void focusLost(FocusEvent e) {}
//			@Override
//			public void focusGained(FocusEvent e) {
//				varXminField.setText("");
//			}
//		});
//		varXmaxField = new MyTextField(4);
//		varXmaxField.setText("MAX");
//		varXmaxField.addActionListener(this);
//		varXmaxField.addFocusListener(new FocusListener() {
//			@Override
//			public void focusLost(FocusEvent e) {}
//			@Override
//			public void focusGained(FocusEvent e) {
//				varXmaxField.setText("");
//			}
//		});
//		varYminField = new MyTextField(4);
//		varYminField.setText("MIN");
//		varYminField.addActionListener(this);
//		varYminField.addFocusListener(new FocusListener() {
//			@Override
//			public void focusLost(FocusEvent e) {}
//			@Override
//			public void focusGained(FocusEvent e) {
//				varYminField.setText("");
//			}
//		});
//		varYmaxField = new MyTextField(4);
//		varYmaxField.setText("MAX");
//		varYmaxField.addActionListener(this);
//		varYmaxField.addFocusListener(new FocusListener() {
//			@Override
//			public void focusLost(FocusEvent e) {}
//			@Override
//			public void focusGained(FocusEvent e) {
//				varYmaxField.setText("");
//			}
//		});
//
//		densityLabel = new JLabel("<HTML>Scale<br>Factor</HTML>");
//		densitySlider = new JSlider(SwingConstants.VERTICAL, 1, 100, 50);
//		densitySlider.setPreferredSize(new Dimension(50, 130));
//		densitySlider.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				calculator.scaleFactor = densitySlider.getValue();
//				densityLabel.setText("<HTML>Scale<br>Factor<br>" + Double.toString(densitySlider.getValue()) + "</HTML>");
//				revalidate();
//				repaint();
//			}
//		});
//
//		upperPanel2.add(varXLabel);
//		upperPanel2.add(varXminField);
//		upperPanel2.add(varXmaxField);
//		upperPanel2.add(varYLabel);
//		upperPanel2.add(varYminField);
//		upperPanel2.add(varYmaxField);
//		upperPanel2.add(densityLabel);
//		upperPanel2.add(densitySlider);
//		upperPanel2.setPreferredSize(new Dimension(110, 200));
//
//		northPanel.add(upperPanel2, BorderLayout.WEST);
//		northPanel.add(upperPanel1, BorderLayout.CENTER);
//
//		add(northPanel, BorderLayout.NORTH);
//
//		/////////////////////////////////////////////
//		// Bottom Panel
//
//		// Text display
//
//		bottomPanel = new JPanel(new BorderLayout());
//
//		bottomPanel_displayPanel = new JPanel(new BorderLayout());
//
//		textDisplay = new JTextPane();
//		textDisplay.setEditable(true);
//		textDisplay.setMargin(new Insets(0, 0, 0, 0));
//		textDisplay.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 10));
//		textDisplay.setBackground(Color.BLACK);
//		JScrollPane scrollPane = new JScrollPane(textDisplay,
//				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		scrollPane.setPreferredSize(new Dimension(200, 200));
//
//		CalculatorConsole.setOutput(textDisplay);
//
//
//		SimpleAttributeSet right = new SimpleAttributeSet();
//		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
//		StyleConstants.setForeground(right, Color.WHITE);
//
//		input = new JTextField();
//		input.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				StyledDocument doc = textDisplay.getStyledDocument();
//				try {
//					doc.setParagraphAttributes(doc.getLength(), 1, right, false);
//					doc.insertString(doc.getLength(), input.getText() + "\n", right );
//
//					CalculatorConsole.getCommand(input.getText(), calculator, engine);
//					input.setText("");
//
//					textDisplay.setCaretPosition(textDisplay.getDocument().getLength());
//				} catch (BadLocationException e1) {
//					e1.printStackTrace();
//				}
//			}
//		});
//
//		add(scrollPane, BorderLayout.CENTER);
//		add(input, BorderLayout.SOUTH);
//
//		//		bottomPanel_displayPanel.add(scrollPane);
//
//		// Init buttons and button panels
//
//		//		bottomPanel_buttonPanel = new JPanel(new GridLayout(0, 2));
//		//
//		//		bottomPanel_buttonPanel1 = new JPanel(new GridLayout(4, 4));
//		//		bottomPanel_buttonPanel1.setPreferredSize(new Dimension(200, 200));
//		//		bottomPanel_buttonPanel2 = new JPanel(new GridLayout(4, 4));
//		//		bottomPanel_buttonPanel2.setPreferredSize(new Dimension(200, 200));
//		//
//		//		panelOneButtons = new JButton[16];
//		//		panelTwoButtons = new JButton[16];
//		//
//		//		for(int j = 0; j < 10; j++) {
//		//			panelOneButtons[j] = new JButton(Integer.toString(j));
//		//		}
//		//		panelOneButtons[10] = new JButton(".");
//		//		panelOneButtons[11] = new JButton("=");
//		//		panelOneButtons[12] = new JButton("+");
//		//		panelOneButtons[13] = new JButton("-");
//		//		panelOneButtons[14] = new JButton("x");
//		//		panelOneButtons[15] = new JButton("/");
//		//
//		//		for(int j = 0; j < 16; j++) {
//		//			panelTwoButtons[j] = new JButton();
//		//		}
//		//
//		//		// Button set no. 1
//		//
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[7]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[8]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[9]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[15]); // "/"
//		//
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[4]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[5]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[6]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[14]); // "x"
//		//
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[1]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[2]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[3]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[13]); // "-"
//		//
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[0]);
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[10]); // "."
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[11]); // "="
//		//		bottomPanel_buttonPanel1.add(panelOneButtons[12]); // "+"
//		//
//		//
//		//		// Button set no. 2
//		//
//		//		for(int j = 0; j < 16; j++) {
//		//			bottomPanel_buttonPanel2.add(panelTwoButtons[j]);
//		//		}
//		//
//		//
//		//
//		//		bottomPanel_buttonPanel.add(bottomPanel_buttonPanel1);
//		//		bottomPanel_buttonPanel.add(bottomPanel_buttonPanel2);
//		//
//		//		bottomPanel.add(bottomPanel_buttonPanel, BorderLayout.SOUTH);
//		//		bottomPanel.add(bottomPanel_displayPanel, BorderLayout.CENTER);
//
//		//		add(bottomPanel, BorderLayout.CENTER);
//
//
//		restoreGUIdefault();
//	}
//
//	private class MyTextField extends JTextField {
//		private static final long serialVersionUID = 20L;
//		public MyTextField(int width) {
//			super(width);
//		}
//	}
//
//	public Calculator getCalculator() {
//		return calculator;
//	}
//
//	public Display getDisplay() {
//		return display;
//	}
//
//	@Override
//	public void actionPerformed(ActionEvent e) {
//		if(e.getSource() == refreshButton) {
//			updateAll = true;
//		}
//
//		try {
//			if(updateAll || e.getSource() == xMinField)
//				calculator.xMin = Double.parseDouble(xMinField.getText());
//			if(updateAll || e.getSource() == xMaxField)
//				try {
//					calculator.xMax = Double.parseDouble(xMaxField.getText());
//				} catch (NumberFormatException nfe) {}
//			if(updateAll || e.getSource() == xStepField)
//				try {
//					calculator.xStep = Double.parseDouble(xStepField.getText());
//				} catch (NumberFormatException nfe) {}
//
//
//			if(updateAll || e.getSource() == yMinField)
//				try {
//					calculator.yMin = Double.parseDouble(yMinField.getText());
//				} catch (NumberFormatException nfe) {}
//			if(updateAll || e.getSource() == yMaxField)
//				try {
//					calculator.yMax = Double.parseDouble(yMaxField.getText());
//				} catch (NumberFormatException nfe) {}
//			if(updateAll || e.getSource() == yStepField)
//				try {
//					calculator.yStep = Double.parseDouble(yStepField.getText());
//				} catch (NumberFormatException nfe) {}
//
//
//			if(updateAll || e.getSource() == zMinField)
//				try {
//					calculator.zMin = Double.parseDouble(zMinField.getText());
//				} catch (NumberFormatException nfe) {}
//			if(updateAll || e.getSource() == zMaxField)
//				try {
//					calculator.zMax = Double.parseDouble(zMaxField.getText());
//				} catch (NumberFormatException nfe) {}
//			if(updateAll || e.getSource() == zStepField)
//				try {
//					calculator.zStep = Double.parseDouble(zStepField.getText());
//				} catch (NumberFormatException nfe) {}
//
//
//			if(updateAll || e.getSource() == xTranslateField)
//				engine.getCamera().getPosition().setX(Double.parseDouble(xTranslateField.getText()));
//			if(updateAll || e.getSource() == yTranslateField)
//				engine.getCamera().getPosition().setY(Double.parseDouble(yTranslateField.getText()));
//			if(updateAll || e.getSource() == zTranslateField)
//				engine.getCamera().getPosition().setZ(Double.parseDouble(zTranslateField.getText()));
//
//			if(e.getSource() == varXminField) {
//				calculator.varXminRange = Double.parseDouble(varXminField.getText());
//			} else if(updateAll || e.getSource() == varXmaxField) {
//				calculator.varXmaxRange = Double.parseDouble(varXmaxField.getText());
//			} else if(updateAll || e.getSource() == varYminField) {
//				calculator.varYminRange = Double.parseDouble(varYminField.getText());
//			} else if(updateAll || e.getSource() == varYmaxField) {
//				calculator.varYmaxRange = Double.parseDouble(varYmaxField.getText());
//			}
//		} catch (NumberFormatException nfe) {}
//
//		calculator.calculate(engine.getWorld().get(0));
//		updateAll = false;
//	}
//
//	public static void formatComponents(JComponent component) {
//		// Buttons
//		if(component instanceof JButton) {
//			JButton temp = (JButton)component;
//			temp.setMargin(new Insets(0, 0, 0, 0));
//			temp.setFocusPainted(false);
//		}
//	}
//}
