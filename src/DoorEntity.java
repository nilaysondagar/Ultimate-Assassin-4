/* DoorEntity.java
 * Represents the door
 */
public class DoorEntity extends Entity {

  private Game game; // the game in which the ship exists

  /* construct the player's ship
   * input: game - the game in which the ship is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the ship
   *        x, y - initial location of ship
   */
  public DoorEntity(Game g, String[] r, int newX, int newY, Map m) {
    super(r, newX, newY, m);  // calls the constructor in the parent (Entity.java), must be called first
    game = g;
  } // constructor  
  
  /* collidedWith
   * input: other - the entity with which the ship has collided
   * purpose: notification that the player's ship has collided
   *          with something
   */
   public void collidedWith(Entity other) {
	   
     if (other instanceof PersonEntity) {  
    	 sound += 1;
     } // if
     
     if(sound == 1) {
    	 game.notifyWin();
     } // if
     
   } // collidedWith    

} // ShipEntity class