package com.goregoblin.effects;


import java.util.Random;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {

	private DoubleProperty x = new SimpleDoubleProperty();
	private DoubleProperty y = new SimpleDoubleProperty();
	private Color color;
	private double life = 1.0; // for alpha value
	private boolean active = false;
	private Point2D velocity = Point2D.ZERO;
	
	public Particle(int x, int y, Color color) {
		this.x.set(x);
		this.y.set(y);
		this.color = color;
	}
	
	public DoubleProperty xProperty() {
		return x;
	}
	
	public DoubleProperty yProperty() {
		return y;
	}
	
	
	public double getX() {
		return x.get();
	}
	
	public double getY() {
		return y.get();
	}
	
	
	public boolean isDead() {
		return life == 0;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void activate(Point2D velocity) {
		active = true;
		this.velocity = velocity;
	}
		
	public void update() {
		if (!active) {
			return;
		}
		
		life-= 0.017*0.75;
		if (life < 0.0) {
			life = 0.0;
		}
		this.x.set(getX()+ velocity.getX());
		this.y.set(getY()+ velocity.getY());
	}
	
	//  hier mal an Particle rumbasteln; Effekt verändert sich stark durch Parameter hier 
	public void draw(GraphicsContext g) {
		int width = getRandomNumberInRange(getRandomNumberInRange(0, 5), getRandomNumberInRange(6, 10));
		int height = getRandomNumberInRange(getRandomNumberInRange(0, 5), getRandomNumberInRange(6, 10));
		g.setFill(color);
		g.setGlobalAlpha(life);
		g.fillOval(getX(), getY(), width, height);	
	}

	
	// to generate random Integer
	private int getRandomNumberInRange(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
}
