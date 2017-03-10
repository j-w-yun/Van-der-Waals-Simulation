package org.yoon_technology.math;

import java.io.Serializable;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class Vector2d implements Serializable {

	private static final long serialVersionUID = 10L;

	protected double X;
	protected double Y;

	public Vector2d(double X, double Y) {
		this.X = X;
		this.Y = Y;
	}

	public double length() {
		return (double) Math.sqrt(this.X * this.X + this.Y * this.Y);
	}

	public double max() {
		return Math.max(this.X, this.Y);
	}

	public double dot(Vector2d r) {
		return this.X * r.getX() + this.Y * r.getY();
	}

	public Vector2d normalized() {
		double length = length();

		if(length == 0) {
			System.out.println("Vector length is zero");
			length = 0.00001f;
		}

		return new Vector2d(this.X / length, this.Y / length);
	}

	public double cross(Vector2d r) {
		return this.X * r.getY() - this.Y * r.getX();
	}

	public Vector2d rotate(double angle) {
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);

		return new Vector2d((double) (this.X * cos - this.Y * sin), (double) (this.X * sin + this.Y * cos));
	}

	public Vector2d add(Vector2d r) {
		return new Vector2d(this.X + r.getX(), this.Y + r.getY());
	}

	public Vector2d add(double r) {
		return new Vector2d(this.X + r, this.Y + r);
	}

	public Vector2d sub(Vector2d r) {
		return new Vector2d(this.X - r.getX(), this.Y - r.getY());
	}

	public Vector2d sub(double r) {
		return new Vector2d(this.X - r, this.Y - r);
	}

	public Vector2d mul(Vector2d r) {
		return new Vector2d(this.X * r.getX(), this.Y * r.getY());
	}

	public Vector2d mul(double r) {
		return new Vector2d(this.X * r, this.Y * r);
	}

	public Vector2d div(Vector2d r) {
		return new Vector2d(this.X / r.getX(), this.Y / r.getY());
	}

	public Vector2d div(double r) {
		return new Vector2d(this.X / r, this.Y / r);
	}

	public Vector2d abs() {
		return new Vector2d(Math.abs(this.X), Math.abs(this.Y));
	}

	@Override
	public String toString() {
		return "(" + this.X + " " + this.Y + ")";
	}

	public Vector2d set(double x, double y) {
		this.X = x;
		this.Y = y;
		return this;
	}

	public Vector2d set(Vector2d r) {
		set(r.getX(), r.getY());
		return this;
	}

	public double getX() {
		return this.X;
	}

	public void setX(double x) {
		this.X = x;
	}

	public double getY() {
		return this.Y;
	}

	public void setY(double y) {
		this.Y = y;
	}

	public boolean equals(Vector2d r) {
		return this.X == r.getX() && this.Y == r.getY();
	}
}
