
/* Entity.java
 * An entity is any object that appears in the game.
 * It is responsible for resolving collisions and movement.
 */

import java.awt.*;

public abstract class Entity {

	// Java Note: the visibility modifier "protected"
	// allows the variable to be seen by this class,
	// any classes in the same package, and any subclasses
	// "private" - this class only
	// "public" - any class can see it

	protected double x; // current x location
	protected int lastX; // last x location
	protected double y; // current y location
	protected int lastY; // last y location

	protected Sprite sprite[] = null; // this entity's sprite

	protected int sound = 0; // determines whether to play sound or not
	protected int sizeOfR = 0;
	protected int sizeOfSprite = 0; // size of sprite array
	protected int currentImg = 0; // determines current sprite image
	protected int levelUp; // changes levels
	protected int width; // size of entity

	protected double dx; // horizontal speed (px/s) + -> right
	protected double dy; // vertical speed (px/s) + -> down
	protected double sizeWidth; // width of Entity
	protected double sizeHeight; // height of Entity 
	protected double nx = 0; // new x position
	protected double ny = 0; // new y position

	protected long lastImgChange = 0; // time since last image of sprite was changed
	protected long imgChangeInterval = 100; // how often the sprite image should change

	protected Map map; // map of level

	private double speed = 0.6; // speed

	private Rectangle me = new Rectangle(); // bounding rectangle of this entity
	private Rectangle him = new Rectangle(); // bounding rectangle of other entities

	/*
	 * Constructor input: reference to the image for this entity, initial x and y location to be drawn at
	 */
	public Entity(String[] r, int newX, int newY, Map m) {
		x = newX;
		y = newY;

		sprite = new Sprite[r.length];

		// assign each sprite element with the string array
		for (int i = 0; i < r.length; i++) {
			sprite[i] = (SpriteStore.get()).getSprite(r[i]);
		} // for

		sizeWidth = 25; // me.getWidth();
		sizeHeight = 25; // me.getHeight();

		map = m;

	} // constructor

	/*
	 * move input: delta - the amount of time passed in ms output: none purpose:
	 * after a certain amount of time has passed, update the location
	 */
	public void move(long delta) {

		nx = 0.0;
		ny = 0.0;

		// update location of entity based on move speeds
		nx = x + (delta * dx * speed) / 1000;
		ny = y + (delta * dy * speed) / 1000;
		if (validLocation(nx, ny, width)) {
			x += (delta * dx * speed) / 1000;
			y += (delta * dy * speed) / 1000;
			int random = (int) (Math.random() * 10);
			if (random == 4) {
				nx = x + (delta * -dx * speed) / 1000;
				ny = y + (delta * -dy * speed) / 1000;
				if (validLocation(nx, ny, width)) {
					x += (delta * -dx * speed) / 1000;
					y += (delta * -dy * speed) / 1000;
				} // if

			} // if

		} // if

	} // move

	public boolean validLocation(double nx, double ny, int width) {
		
		// here we're going to check some points at the corners of
		// the player to see whether we're at an invalid location
		// if any of them are blocked then the location specified
		// isn't valid

		if (map.blocked(nx + width, ny)) {
			dy *= -1;
			dx *= -1;
			return false;
		} // if
		if (map.blocked(nx, ny + width)) {
			dy *= -1;
			dx *= -1;
			return false;
		} // if
		if (map.blocked(nx + width, ny + width)) {
			dy *= -1;
			dx *= -1;
			return false;
		} // if
		if (map.blocked(nx, ny)) {
			dy *= -1;
			dx *= -1;
			return false;
		} // if

		// if all the points checked are unblocked then we're ok location

		return true;

	} // move

	// set speed of entity
	public void setSpeed(double d) {
		speed = d;
	} // setSpeed

	// check if person is moving left and right
	public boolean ifMovingHorizontally() {
		boolean ifMoving;
		double currentX = getX();

		if (currentX != lastX) {
			ifMoving = true;
		} else {
			ifMoving = false;
		} // else

		return ifMoving;

	}// ifMovingHorizontally

	// check if person is moving up and down
	public boolean ifMovingVertically() {
		boolean ifMoving;
		double currentY = getY();

		if (currentY != lastY) {
			ifMoving = true;
		} else {
			ifMoving = false;
		} // else

		return ifMoving;

	}// ifMovingVertically

	// set horizontal velocity
	public void setHorizontalMovement(double newDX) {
		dx = newDX;
	} // setHorizontalMovement

	// set vertical velocity
	public void setVerticalMovement(double newDY) {
		dy = newDY;
	} // setVerticalMovement

	// get horizontal velocity 
	public double getHorizontalMovement() {
		return dx;
	} // getHorizontalMovement

	// get vertical velocity
	public double getVerticalMovement() {
		return dy;
	} // getVerticalMovement

	// get x position
	public int getX() {
		return (int) x;
	} // getX

	// get y position
	public int getY() {
		return (int) y;
	} // getY

	// Draw this entity to the graphics object provided at (x,y)
	public void draw(Graphics g) {

		if (ifMovingVertically() == true) {
			sprite[currentImg].draw(g, (int) x, (int) y);

			if ((System.currentTimeMillis() - lastImgChange) < imgChangeInterval) {
				return;
			} // if

			currentImg++;
			lastImgChange = System.currentTimeMillis();
			lastX = getX();
			lastY = getY();
			currentImg %= 3;
			currentImg %= sprite.length;

		} else if (ifMovingVertically() == false && ifMovingHorizontally() == false) {
			sprite[0].draw(g, (int) x, (int) y);

		} else if (sprite.length > 3) {

			if (ifMovingHorizontally() == true) {

				if (currentImg == 0) {
					currentImg = 3;
				} // if

				sprite[currentImg].draw(g, (int) x, (int) y);

				if ((System.currentTimeMillis() - lastImgChange) < imgChangeInterval) {
					return;
				} // if

				currentImg++;
				lastImgChange = System.currentTimeMillis();
				lastX = getX();
				lastY = getY();
				currentImg %= 6;
				currentImg %= sprite.length;
			} // if

		} // if

	} // draw

	/*
	 * Do the logic associated with this entity. This method will be called
	 * periodically based on game events.
	 */
	public void doLogic() {
	}// doLogic

	/*
	 * collidesWith input: the other entity to check collision against output:
	 * true if entities collide purpose: check if this entity collides with the
	 * other.
	 */
	public boolean collidesWith(Entity other) {
		me.setBounds((int) x, (int) y, sprite[currentImg].getWidth(), sprite[currentImg].getHeight());
		him.setBounds(other.getX(), other.getY(), other.sprite[0].getWidth(), other.sprite[0].getHeight());
		return me.intersects(him);
	} // collidesWith

	/*
	 * collidedWith input: the entity with which this has collided purpose:
	 * notification that this entity collided with another Note: abstract
	 * methods must be implemented by any class that extends this class
	 */
	public abstract void collidedWith(Entity other);

} // Entity class