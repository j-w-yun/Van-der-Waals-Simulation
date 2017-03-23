package org.yoon_technology.engine.objects;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class WorldObjectProperty {

	public static final int LINES = 1;
	public static final int END_LINES = 2;

	private int drawMode;

	public WorldObjectProperty() {}

	public void setDrawMode(int drawMode) {
		this.drawMode = drawMode;
	}

	public int getDrawMode() {
		return drawMode;
	}
}
