package org.yoon_technology.engine;

import java.awt.Font;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class WorldText extends WorldObject {

	private String text;
	private Font font = new Font("Lucida Sans Typewriter", Font.BOLD, 20);

	public WorldText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
}
