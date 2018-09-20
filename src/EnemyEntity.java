/* EnemyEntity.java
 * Represents one of the enemies
 */
public class EnemyEntity extends Entity {

	private double moveSpeed = 150; // horizontal speed

	private Game game; // the game in which the enemy exists

	/*
	 * construct a new enemy input: game - the game in which the enemy is being
	 * created r - the image representing the enemy x, y - initial location of
	 * enemy
	 */
	public EnemyEntity(Game g, String[] r, int newX, int newY, Map m) {
		super(r, newX, newY, m); // calls the constructor in Entity
		game = g;
		dx = -moveSpeed; // start off moving left
	} // constructor

	// move input: delta - time elapsed since last move (ms) purpose: move enemy
	public void move(long delta) {

		// if we reach any side of screen request logic update
		if ((dx < 0) && (x < 10)) {
			game.updateLogic(); // logic deals with moving entities in other direction and down screen
		} // if

		// if we reach any side of screen request logic update
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

		// turn right, left, up or down based off of random number
		if (turn == 1 || turn == 5) {
			double temp = 0;
			temp = -dx;
			dx = -dy;
			dy = temp;
		} else if (turn == 10 || turn == 20) {
			double temp = 0;
			temp = dx;
			dx = -dy;
			dy = temp;
		} else if (turn == 30 || turn == 80) {
			double temp = 0;
			temp = -dx;
			dx = dy;
			dy = temp;
		} else if (turn == 50 || turn == 100) {
			double temp = 0;
			temp = dx;
			dx = dy;
			dy = temp;
		} // if else

		width = 25;
		
		// proceed with normal move
		super.move(delta);

	} // move

	/*
	 * doLogic Updates the game logic related to the enemies, ie. move it down
	 * the screen and change direction
	 */
	public void doLogic() {

		// swap direction and move down screen 10 pixels
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
		} // if else

	} // doLogic

	/*
	 * collidedWith input: other - the entity with which the enemy has collided
	 * purpose: notification that the enemy has collided with something
	 */
	public void collidedWith(Entity other) {
		// collisions with enemies are handled in ShotEntity and PersonEntity

		if (other instanceof TargetEntity) {
			dy *= -1;
			dx *= -1;
		} // if

		if (other instanceof EnemyEntity) {
			dy *= -1;
			dx *= -1;
		} // if

		if (other instanceof DoorEntity) {
			dy *= -1;
			dx *= -1;
		} // if

	} // collidedWith

} // EnemyEntity class
