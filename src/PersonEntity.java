/* PersonEntity.java 
 * Represents the player
 */
public class PersonEntity extends Entity {

	private Game game; // the game in which the ship exists

	/*
	 * construct the player's ship input: game - the game in which the person is
	 * being created ref - a string with the name of the image associated to the
	 * sprite for the ship x, y - initial location of person
	 */
	public PersonEntity(Game g, String[] r, int newX, int newY, Map m) {
		super(r, newX, newY, m); // calls the constructor in Entity
		game = g;
	} // constructor

	// move input: delta - time elapsed since last move (ms) purpose: move ship
	public void move(long delta) {
		// stop at left side of screen
		if ((dx < 0) && (x < 10)) {
			return;
		} // if
			// stop at right side of screen
		if ((dx > 0) && (x > 770)) {
			return;
		} // if

		// stop at top side of screen
		if ((dy < 0) && (y < 10)) {
			return;
		} // if
			// stop at bottom side of screen
		if ((dy > 0) && (y > 570)) {
			return;
		} // if

		width = 25;
		
		super.move(delta); // calls the move method in Entity
	} // move

	/*
	 * collidedWith input: other - the entity with which the ship has collided
	 * purpose: notification that the player's ship has collided with something
	 */
	public void collidedWith(Entity other) {
		if (other instanceof EnemyEntity) { // only if it is an alien entity you are going to die
			sound += 1;
		} // if

		if (other instanceof ShotEntity) {
			sound += 1;
		} // if
		
		if(other instanceof TargetEntity) {
			game.removeEntity(other);
			game.notifyTargetKilled();
			game.makeDoorAppear();
			
			if(game.getLevelNum() == 2) {
				game.createEnemy();
			} // if
		} // if

		// notify player has died
		if (sound == 1) {
			game.notifyDeath();
		} // if

	} // collidedWith

} // ShipEntity class