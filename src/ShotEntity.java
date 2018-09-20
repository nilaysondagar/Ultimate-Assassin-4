/* ShotEntity.java
 * Represents a bullet / shot
 */
public class ShotEntity extends Entity {

	private boolean isEnemy; // true is the shot came from an enemy
	private boolean used = false; // true if shot hits something

	private Game game; // the game in which the person exists

	/*
	 * construct the shot input: game - the game in which the shot is being
	 * created ref - a string with the name of the image associated to the
	 * sprite for the shot x, y - initial location of shot
	 */
	public ShotEntity(Game g, String[] r, int newX, int newY, double moveSpeed, boolean isEnemyg, Map m) {
		super(r, newX, newY, m); // calls the constructor in Entity
		game = g;
		dy = moveSpeed;
		isEnemy = isEnemyg;
	} // constructor

	//constructor for alien's shot, so it shoots randomly
	public ShotEntity(Game g, String[] r, int newX, int newY, double moveX, int moveY, boolean isEnemyg, Map m) {
		super(r, newX, newY, m); // calls the constructor in Entity
		game = g;
		dy = moveY;
		dx = moveX;
		isEnemy = isEnemyg;
	}// constructor

	// move input: delta - time elapsed since last move (ms) purpose: move shot
	public void move(long delta) {

		width = 15;
		
		super.move(delta); // calls the move method in Entity

		// check if shot hits a wall
		if (this.validLocation(nx, ny, width) == false) {
			game.removeEntity(this);
		} // if

		// if shot moves off of the screen, remove it from entity list
		if (y < -100) {
			game.removeEntity(this);
		} // if

		if (y > 700) {
			game.removeEntity(this);
		} // if

	} // move

	/*
	 * collidedWith input: other - the entity with which the shot has collided
	 * purpose: notification that the shot has collided with something
	 */
	public void collidedWith(Entity other) {

		// prevents double kills
		if (used) {
			return;
		} // if

		if (!isEnemy) {

			// if it has hit an enemy, kill it!
			if (other instanceof EnemyEntity) {
				// remove affect entities from the Entity list
				game.removeEntity(this);
				game.removeEntity(other);

				game.notifyEnemyKilled();

				used = true;
			} // if

			//if shit hits target, kill the target
			if (other instanceof TargetEntity) {

				game.removeEntity(this);
				game.removeEntity(other);
				game.notifyTargetKilled();
				game.makeDoorAppear();

				if (game.getLevelNum() == 2) {
					game.createEnemy();
				} // if

				used = true;
			} // if

			// if shot hits person, kill person
			if (other instanceof PersonEntity) {
				game.removeEntity(other);
			} // if

		} else {

			// if shot hits person, show lose screen
			if (other instanceof PersonEntity) {
				sound += 1;
			} // if

			//remove shot if target is hit
			if (other instanceof TargetEntity) {
				game.removeEntity(this);
			} // if

		} // if else

		// notify death if player is hit with shot
		if (sound == 1) {
			game.notifyDeath();
		} // if

	} // collidedWith

} // ShotEntity class
