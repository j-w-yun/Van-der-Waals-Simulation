package org.yoon_technology.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Comparator;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.yoon_technology.engine.Engine;
import org.yoon_technology.engine.objects.World;
import org.yoon_technology.engine.objects.WorldObject;
import org.yoon_technology.engine.objects.WorldObjectProperty;
import org.yoon_technology.engine.objects.WorldText;
import org.yoon_technology.math.Function;
import org.yoon_technology.math.Vector3d;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Display extends JPanel implements ComponentListener {

	private static final long serialVersionUID = 3L;
	private Engine engine;
	private World world;
	private String displayInformation;
	private double scale;
	private volatile boolean beautify = true;

	public Display(int width, int height, Engine engine) {
		super(new FlowLayout(SwingConstants.CENTER));
		this.engine = engine;
		engine.addDisplay(this);
		setFocusable(true);
		setPreferredSize(new Dimension(width, height));

		addComponentListener(this);

		addMouseListener(new MouseListener() {
			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocus();
			}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
	}

	public synchronized void setBeautify(boolean beautify) {
		this.beautify = beautify;
	}

	public synchronized void setWorld(World world) {
		this.world = world;
	}

	public synchronized void setDisplayInformation(String displayInformation) {
		this.displayInformation = displayInformation;
	}

	public synchronized void setScale(double scale) {
		this.scale = scale;
	}

	public synchronized double getScale() {
		return scale;
	}

	public synchronized void render() {
		revalidate();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// Antialiasing
		Graphics2D g2 = (Graphics2D)g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		g2.setRenderingHints(rh);

		double maxZ = 0.0000001;
		double minZ = -0.0000001;
		synchronized(world) {
			if(beautify) {
				// Depth buffer equivalent
				try {
					world.getPoints().sort(new Comparator<WorldObject>() {
						@Override
						public int compare(WorldObject o1, WorldObject o2) {
							Vector3d o1_ = engine.getCamera().project(o1.getPosition(), 1.0, 1.0);
							Vector3d o2_ = engine.getCamera().project(o2.getPosition(), 1.0, 1.0);
							return (int)(o1_.getZ() - o2_.getZ());
						}
					});
				} catch (IllegalArgumentException iae) {} // Non transient?

				// "Fog" scale
				if(world.getPoints().size() != 0) {
					Vector3d maxZPos;
					maxZPos = world.getPoints().get(world.getPoints().size()-1).getPosition();
					maxZ = engine.getCamera().project(
							maxZPos,
							(int)((double)getWidth()),
							(int)((double)getHeight())).getZ();

					Vector3d minZPos;
					minZPos = world.getPoints().get(0).getPosition();
					minZ = engine.getCamera().project(
							minZPos,
							(int)((double)getWidth()),
							(int)((double)getHeight())).getZ();
				}
			}

			// Particles
			for(WorldObject object : world.getPoints()) {
				// Get projection vector
				Vector3d projectionVector = engine.getCamera().project(
						object.getPosition().mul(scale),
						(int)((double)getWidth()),			// offset x
						(int)((double)getHeight()));		// offset y

				// "Fog"
				if(beautify) {
					Color objectColor = object.getColor();
					double factor = Function.clamp((projectionVector.getZ()+maxZ)/(maxZ-minZ), 0.15, 1.0);
					int red = (int) Function.clamp(factor * objectColor.getRed()*1.7, 30.0, 255.0);
					int green = (int) Function.clamp(factor * objectColor.getGreen()*1.7, 30.0, 255.0);
					int blue = (int) Function.clamp(factor * objectColor.getBlue()*1.7, 30.0, 255.0);
					g.setColor(new Color(red, green, blue));
				} else {
					g.setColor(object.getColor());
				}

				double radius = object.getRadius() * scale;
				if(radius < 1)
					radius = 1.0;

				g.fillOval((int)(projectionVector.getX() - radius),
						(int)(projectionVector.getY() - radius),
						(int)(radius*2.0),
						(int)(radius*2.0));
			}

			// Draw lines
			WorldObject lastObject = null; // For lines
			for(WorldObject line : world.getLines()) {
				// Get projection vector
				Vector3d projectionVector = engine.getCamera().project(
						line.getPosition().mul(scale),
						(int)((double)getWidth()),			// offset x
						(int)((double)getHeight()));		// offset y
				g.setColor(line.getColor());

				if(line.getProperties().getDrawMode() == WorldObjectProperty.LINES) {
					if(lastObject != null) {
						Vector3d projectionVector2 = engine.getCamera().project(
								lastObject.getPosition().mul(scale),
								(double)getWidth(),			// offset x to center at 0, 0
								(double)getHeight());		// offset y

						g.drawLine((int) projectionVector.getX(),
								(int) projectionVector.getY(),
								(int) projectionVector2.getX(),
								(int) projectionVector2.getY());
					}
					lastObject = line;

				} else if(line.getProperties().getDrawMode() == WorldObjectProperty.END_LINES) {
					if(lastObject == null) {
						g.drawOval((int)(projectionVector.getX() - 10),
								(int)(projectionVector.getY() - 10),
								(int)(20),
								(int)(20));
					} else {
						Vector3d projectionVector2 = engine.getCamera().project(
								lastObject.getPosition().mul(scale),
								(double)getWidth(),			// offset x to center at 0, 0
								(double)getHeight());		// offset y

						g.drawLine((int) projectionVector.getX(),
								(int) projectionVector.getY(),
								(int) projectionVector2.getX(),
								(int) projectionVector2.getY());
					}
					lastObject = null;
				}
			}

			// Draw rectangle
			int index = 0;
			int[] xPoints = new int[4];
			int[] yPoints = new int[4];
			for(WorldObject rectangle : world.getRectangles()) {
				Vector3d projectionVector = engine.getCamera().project(
						rectangle.getPosition().mul(scale),
						(double)getWidth(),			// offset x to center at 0, 0
						(double)getHeight());		// offset y;
				xPoints[index] = (int) projectionVector.getX();
				yPoints[index] = (int) projectionVector.getY();

				index++;
				if(index == 4) {
					g.setColor(rectangle.getColor());
					g.drawPolygon(xPoints, yPoints, 4);
					index = 0;
				}
			}

			// Draw text
			for(WorldText text : world.getTexts()) {
				Vector3d projectionVector = engine.getCamera().project(
						text.getPosition().mul(scale),
						(double)getWidth(),
						(double)getHeight());
				g.setColor(text.getColor());
				g.setFont(text.getFont());
				g.drawString(text.getText(), (int) projectionVector.getX(), (int) projectionVector.getY());
			}
		}

		if(displayInformation != null) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 10));
			g.drawString(displayInformation, 10, 15);
		}
	}

	@Override
	public synchronized void componentResized(ComponentEvent e) {
		for(World world : engine.getWorld()) {
			this.scale = world.resized(getWidth(), getHeight());
		}
		render();
	}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}
}
