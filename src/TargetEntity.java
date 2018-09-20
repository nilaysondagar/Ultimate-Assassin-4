/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class TargetEntity extends Entity {

	private double moveSpeed = 50; // horizontal speed

	private Game game; // the game in which the alien exists

	/*
	 * construct a new alien input: game - the game in which the alien is being
	 * created r - the image representing the alien x, y - initial location of
	 * alien
	 */
	public TargetEntity(Game g, String[] r, int newX, int newY, Map m) {
		super(r, newX, newY, m); // calls the constructor in Entity
		game = g;
		dx = -moveSpeed; // start off moving left
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move alien
	 */
	public void move(long delta) {
		// if we reach left side of screen and are moving left
		// request logic update

		if ((dx < 0) && (x < 10)) {
			game.updateLogic(); // logic deals with moving entities
								// in other direction and down screen
		} // if

		// if we reach right side of screen and are moving right
		// request logic update
		if ((dx > 0) && (x > 750)) {
			game.updateLogic();
		} // if

		if ((dy < 0) && (y < 550)) {
			game.updateLogic();
		} // if

		if ((dy > 0) && (y > 0)) {
			game.updateLogic();
		} // if

		// generate random number
		int turn = (int) (Math.random() * 1001);

		// turn right or left based off of random number
		if (turn == 1) {
			double temp = 0;
			temp = -dx;
			dx = -dy;
			dy = temp;
		} else if (turn == 10) {
			double temp = 0;
			temp = dx;
			dx = -dy;
			dy = temp;
		} else if (turn == 30) {
			double temp = 0;
			temp = -dx;
			dx = dy;
			dy = temp;
		} else if (turn == 50) {
			double temp = 0;
			temp = dx;
			dx = dy;
			dy = temp;
		}

		width = 25;
		
		// proceed with normal move
		super.move(delta);
	} // move

	/*
	 * doLogic Updates the game logic related to the aliens, ie. move it down
	 * the screen and change direction
	 */
	public void doLogic() {

		// swap horizontal direction and move down screen 10 pixels
		if (x < 10) {
			dx *= -1;
			x += 10;
		} else if (y < 0) {
			dy *= -1;
			y += 10;
		} else if (x > 750) {
			dx *= -1;
			x -= 10;
		} else if (y > 550) {
			dy *= -1;
			y -= 10;
		}

	} // doLogic

	/*
	 * collidedWith input: other - the entity with which the alien has collided
	 * purpose: notification that the alien has collided with something
	 */
	public void collidedWith(Entity other) {
		// collisions with aliens are handled in ShotEntity

		if (other instanceof EnemyEntity) {
			dy *= -1;
			dx *= -1;
		} // if

	} // collidedWith

} // AlienEntity class
