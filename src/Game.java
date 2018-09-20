
/* Game.java
 * Ultimate Assassin 4 Main Program
 * Modified April 14th, 2016
 * By Jen, Ethan and Nilay
 */

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends Canvas {

	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true if game held up until
												// a key is pressed
	private boolean leftPressed = false; // true if left arrow key currently
											// pressed
	private boolean rightPressed = false; // true if right arrow key currently
											// pressed
	private boolean upPressed = false; // true if up arrow key is currently
										// pressed
	private boolean downPressed = false; // true if down arrow key is currently
											// pressed
	private boolean firePressed = false; // true if firing
	private boolean lookingLeft = false; // true if left arrow key was the last
											// pressed key
	private boolean lookingRight = false; // true if right arrow key was the
											// last pressed key
	private boolean lookingUp = false; // true if up arrow key was the last
										// pressed key
	private boolean lookingDown = false; // true if down arrow key was the last
											// pressed key
	private boolean gameRunning = true; // see if the game is running
	private boolean logicRequiredThisLoop = false; // true if logic needs to be
													// applied this loop

	private ArrayList entities = new ArrayList(); // list of entities in game
	private ArrayList removeEntities = new ArrayList(); // list of entities to
														// remove this loop

	private Entity person; // the person

	private double moveSpeed = 300; // speed of the person (px/s)

	private long lastFire = 0; // time when last shot fired
	private long firingInterval = 500; // interval between shots (ms)
	private long lastEnemyShotTime; // time of the last enemy shot

	private int enemyCount; // # of enemies left on screen
	private int targetCount = 0; // # of targets left
	private int shots = 1; // number of shots person has left
	private int levelChange = 1; // changes map
	private int levelNum = 1; // level you are currently on
	private int gameMode = 0; // determines which screen to display (win, lose,
								// splash)
	private int messageY = 0; // y position for the message printed on the
								// screen
	private int pressAnyKeyY = 300; // y position for the "Press any key"
									// message
	private int sound = 0; // determines whether to play win / lose sound

	private String message = ""; // message to display while waiting for a key
									// press
	private String fileName = ""; // file name for sound files

	private Map map; // the map of the game

	private BufferedImage background; // displays the background image
	private BufferedImage shotsLeft; // image of how many shots are left

	String[] personImgArray = { "/sprites/personstillV.gif", "/sprites/person1V.gif", "/sprites/person2V.gif",
			"/sprites/personstillH.gif", "/sprites/person1H.gif", "/sprites/person2H.gif" }; // images for the person
	
	String[] enemyImgArray = { "/sprites/enemystillV.gif", "/sprites/enemy1V.gif", "/sprites/enemy2V.gif",
			"/sprites/enemystillH.gif", "/sprites/enemy1H.gif", "/sprites/enemy2H.gif" }; // images for the enemy

	String[] targetImgArray = { "/sprites/targetstillV.gif", "/sprites/target1V.gif", "/sprites/target2V.gif",
			"/sprites/targetstillH.gif", "/sprites/target1H.gif", "/sprites/target2H.gif" }; // images for the target

	String[] shotImgArray = { "/sprites/shot.png", "/sprites/shot.png", "/sprites/shot.png", "/sprites/shot.png",
			"/sprites/shot.png", "/sprites/shot.png" }; // images for shot
	String[] doorImgArray = { "/sprites/door.gif" }; // image for door
	String[] shotRefillArray = { "/sprites/shotRefill.gif" }; // image for shot refill
	
	// Construct our game and set it running.
	public Game() {

		// create a frame to contain game
		JFrame container = new JFrame("Ultimate Assassin 4");

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the game
		panel.setPreferredSize(new Dimension(800, 600));
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setBounds(0, 0, 800, 600);
		panel.add(this);

		// Tell AWT not to bother repainting canvas since that will
		// be done using graphics acceleration
		setIgnoreRepaint(true);

		// make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// if user closes window, shutdown game and jre
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// add key listener to this canvas
		addKeyListener(new KeyInputHandler());

		// request focus so key events are handled by this canvas
		requestFocus();

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// initialize entities
		map = new Map(levelNum);
		initEntities();

		// start the game
		gameLoop();
	} // constructor

	/*
	 * initEntities purpose: Initialize the starting state of the person, enemy,
	 * target and refill entities. Each entity will be added to the array of
	 * entities in the game.
	 */
	private void initEntities() {

		// create the person and put in the bottom right corner of screen
		person = new PersonEntity(this, personImgArray, 700, 500, map);
		entities.add(person);

		// create enemies
		enemyCount = 0;

		// add one more enemy per level
		for (int i = 0; i < levelNum; i++) {

			// call method to create enemies in random locations
			createEnemy();

			// if on level 2, spawn only one enemy
			if (levelNum == 2 && i == 0) {
				break;
			} // if
			
		} // for

		// create new target in the top right corner of the screen
		Entity target1 = new TargetEntity(this, targetImgArray, 650, 100, map);
		entities.add(target1);
		targetCount++;

		// if on a level higher than 1, add a refill powerup
		if (levelNum > 1) {
			Entity shotRefill1 = new ShotRefillEntity(this, shotRefillArray, 365, 65, map);
			entities.add(shotRefill1);
		} // if

	} // initEntities

	// creates enemies in random locations on the screen
	public void createEnemy() {
		while (true) {

			// choose random locations on the screen
			double x = ((Math.random() * 750) + 50); // random x position
			double y = ((Math.random() * 390) + 50); // random y position

			// check to see if the spawn point is not a wall
			if (!map.blocked(x, y) && !map.blocked(x + 30, y) && !map.blocked(x - 30, y) && !map.blocked(x, y + 30)
					&& !map.blocked(x, y - 30)) {
				Entity enemy = new EnemyEntity(this, enemyImgArray, (int) x, (int) y, map);
				entities.add(enemy);
				enemyCount++;
				return;
			} // if

		} // while

	} // createEnemy

	/*
	 * Notification from a game entity that the logic of the game should be run
	 * at the next opportunity
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	} // updateLogic

	// remove an entity from the game. It will no longer be moved or drawn.
	public void removeEntity(Entity entity) {
		removeEntities.add(entity);
	} // removeEntity

	// notification that the player has died.
	public void notifyDeath() {
		message = "You failed. Try again.";
		waitingForKeyPress = true;
		gameMode = 2;
		fileName = "death.wav";
		playSound(fileName);
		shots = 1;
		levelNum = 1;
		levelChange = 1;
	} // notifyDeath

	// notification that the player has beat the level
	public void notifyWin() {
		levelNum++;
		message = "Good work! Level " + levelNum + " is next.";
		waitingForKeyPress = true;
		gameMode = 1;
		fileName = "win.wav";
		playSound(fileName);
		shots = 1;

		if (levelNum == 16) {
			message = "You have completed the game!";
		} // if

	} // notifyWin

	// plays sound when player wins or loses
	public static void playSound(String audio) {

		new Thread(new Runnable() {

			public void run() {
				try {
					Clip clip = AudioSystem.getClip();
					AudioInputStream inputStream = AudioSystem
							.getAudioInputStream(this.getClass().getResource("/avfiles/" + audio));
					clip.open(inputStream);
					clip.start();
				} catch (Exception e) {
					System.out.println("Play sound error: " + e.getMessage() + " for " + audio);
				} // try catch

			}// run

		}).start();

	}// playSound

	// notification that an enemy has been killed
	public void notifyEnemyKilled() {
		enemyCount--;
	} // notifyEnemyKilled

	// notification that the target has been killed
	public void notifyTargetKilled() {
		targetCount--;
		if (targetCount == 0) {
			makeDoorAppear();
		} // if

	}// notifyTargetKilled

	// attempt to fire.
	public void tryToFire() {

		// check that we've waited long enough to fire
		if ((System.currentTimeMillis() - lastFire) < firingInterval) {
			return;
		} // if

		if (shots == 0) {
			return;
		} // if

		// check for the direction player is facing and fire in that direction
		if (lookingLeft) {
			lastFire = System.currentTimeMillis();
			ShotEntity shot = new ShotEntity(this, shotImgArray, person.getX() - 30, person.getY(), -300, 0, false,
					map);
			entities.add(shot);
			shots--;
		} else if (lookingRight) {
			lastFire = System.currentTimeMillis();
			ShotEntity shot = new ShotEntity(this, shotImgArray, person.getX() + 50, person.getY(), 300, 0, false, map);
			entities.add(shot);
			shots--;
		} else if (lookingDown) {
			lastFire = System.currentTimeMillis();
			ShotEntity shot = new ShotEntity(this, shotImgArray, person.getX() + 10, person.getY() + 50, 0, 300, false,
					map);
			entities.add(shot);
			shots--;
		} else if (lookingUp) {
			lastFire = System.currentTimeMillis();
			ShotEntity shot = new ShotEntity(this, shotImgArray, person.getX() + 10, person.getY() - 30, 0, -300, false,
					map);
			entities.add(shot);
			shots--;
		} else {
			// otherwise add a shot firing upwards
			lastFire = System.currentTimeMillis();
			ShotEntity shot = new ShotEntity(this, shotImgArray, person.getX() + 10, person.getY() - 30, -300, false,
					map);
			entities.add(shot);
			shots--;
		} // if else

	} // tryToFire

	// enemy fire randomly
	public void enemyFire() {

		// check how long it's been since the last enemy shot
		long timeSince = (long) System.currentTimeMillis() - lastEnemyShotTime;

		// as long as enemies still exist, try and fire
		if (enemyCount != 0) {

			// make sure time is greater than firing interval
			if (timeSince >= (1000 / enemyCount)) {

				int enemyX = 0; // enemy x position
				int enemyY = 0; // enemy y position

				int randomIndex; // randomly chooses an enemy to fire from

				while (true) {

					// set a random number
					randomIndex = (int) (Math.random() * entities.size());

					// set entity as random entity
					Entity e = (Entity) entities.get(randomIndex);

					// if the entity chosen is an enemy, shoot
					if (e instanceof EnemyEntity) {

						// get the x and y of the enemy
						enemyX = e.getX();
						enemyY = e.getY();

						// choose random x and y speeds
						int speedX = (int) (Math.random() * 501);
						int speedY = (int) (Math.random() * 501);

						// shoot left
						if (speedX % 5 == 0) {
							speedX *= -1;
						} // if

						// shoot up
						if (speedY % 8 == 0) {
							speedY *= -1;
						} // if

						// create an enemy shot
						ShotEntity enemyShot = new ShotEntity(this, shotImgArray, enemyX + 12, enemyY, speedY, speedX,
								true, map);
						entities.add(enemyShot);

						lastEnemyShotTime = System.currentTimeMillis();

						break;
					} // if

				} // while

			} // if

		} // if

	}// enemyFire

	// method to make doors appear after a certain event happens
	public void makeDoorAppear() {
		DoorEntity exitDoor = new DoorEntity(this, doorImgArray, 50, 50, map);
		entities.add(exitDoor);
	}// makeDoorsAppear

	// get current level
	public int getLevelNum() {
		return levelNum;
	}// getLevelNum

	// set number of shots
	public void setShotNum(int shotNum) {
		shots += shotNum;
	}// setShotNum

	/*
	 * gameLoop input: none output: none purpose: Main game loop. Runs
	 * throughout game play. Responsible for the following activities: -
	 * calculates speed of the game loop to update moves - moves the game
	 * entities - draws the screen contents (entities, text) - updates game
	 * events - checks input
	 */
	public void gameLoop() {

		long lastLoopTime = System.currentTimeMillis();

		// keep loop running until game ends
		while (gameRunning) {

			// calculate time since last update, will be used to calculate
			// entities movement
			long delta = System.currentTimeMillis() - lastLoopTime; // time between this gameloop and previous gameloop
			
			lastLoopTime = System.currentTimeMillis();

			// get graphics context for the accelerated surface
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

			// draw walls for level
			map.paint(g);

			// notify if the player has won
			if (sound == 1) {
				notifyWin();
			} // if

			// move each entity
			if (!waitingForKeyPress) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					entity.move(delta);
					g.setColor(Color.white);
					g.setFont(new Font("Copperplate", Font.PLAIN, 45));
					g.drawString("Shots: ", 550, 585);

					//draw images of how many shots are left
					try {
						shotsLeft = ImageIO.read(this.getClass().getResource("/sprites/shot.png"));
					} catch (IOException e) {
						System.out.println("Failed to find /sprites/shot.png");
					} // try catch

					if (shots > 0) {
						g.drawImage(shotsLeft, 705, 568, null);
					} // if

					if (shots > 1) {
						g.drawImage(shotsLeft, 725, 568, null);
					} // if

					if (shots == 3) {
						g.drawImage(shotsLeft, 745, 568, null);
					} // if

					g.drawString("Level: " + levelNum, 20, 585);
				} // for

			} // if

			// draw all entities
			for (int i = 0; i < entities.size(); i++) {
				Entity entity = (Entity) entities.get(i);
				entity.draw(g);
			} // for

			// brute force collisions, compare every entity
			// against every other entity. If any collisions
			// are detected notify both entities that it has
			// occurred
			for (int i = 0; i < entities.size(); i++) {
				for (int j = i + 1; j < entities.size(); j++) {
					Entity me = (Entity) entities.get(i);
					Entity him = (Entity) entities.get(j);

					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					} // if

				} // inner for

			} // outer for

			// remove dead entities
			entities.removeAll(removeEntities);
			removeEntities.clear();

			// run logic if required
			if (logicRequiredThisLoop) {
				for (int i = 0; i < entities.size(); i++) {
					Entity entity = (Entity) entities.get(i);
					entity.doLogic();
				} // for

				logicRequiredThisLoop = false;

			} // if

			// if waiting for "any key press", draw message
			if (waitingForKeyPress) {

				if (gameMode == 0) {
					pressAnyKeyY = 560;

					try {
						background = ImageIO.read(this.getClass().getResource("/avfiles/splashBackground.png"));
					} catch (IOException e) {
						System.out.println("Failed to find splashBackground.png");
					} // try catch

					g.drawImage(background, 0, 0, null);
				} // if

				// print win screen
				if (gameMode == 1) {
					messageY = 70;
					pressAnyKeyY = 525;

					try {
						background = ImageIO.read(this.getClass().getResource("/avfiles/winBackground.jpg"));
					} catch (IOException e) {
						System.out.println("Failed to find winBackground.jpg");
					} // try catch

					g.drawImage(background, 0, 0, null);

					if (levelNum == 16) {
						System.exit(0);
					}
				} // if

				// print lose screen
				if (gameMode == 2) {
					messageY = 300;
					pressAnyKeyY = 350;

					try {
						background = ImageIO.read(this.getClass().getResource("/avfiles/loseBackground.jpg"));
					} catch (IOException e) {
						System.out.println("Failed to find loseBackground.jpg");
					} // try catch

					g.drawImage(background, 0, 0, null);
				} // if

				// set graphic values and draw message
				g.setColor(Color.white);
				g.setFont(new Font("Copperplate", Font.PLAIN, 45));
				g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, messageY);
				g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key")) / 2,
						pressAnyKeyY);
			} // if

			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			// person should not move without user input
			person.setHorizontalMovement(0); // if key is released, person stops moving
			person.setVerticalMovement(0);

			// respond to user moving person
			if ((leftPressed) && (!rightPressed)) {
				person.setHorizontalMovement(-moveSpeed);
			} else if ((rightPressed) && (!leftPressed)) {
				person.setHorizontalMovement(moveSpeed);
			} else if ((upPressed) && (!downPressed)) {
				person.setVerticalMovement(-moveSpeed);
			} else if ((downPressed) && (!upPressed)) {
				person.setVerticalMovement(moveSpeed);
			} // else if

			// if spacebar pressed, try to fire
			if (firePressed) {
				tryToFire();
			} // if

			// while game is running, make enemy fire
			if (!waitingForKeyPress) {
				enemyFire();
			} // if

			// pause so game isn't too jittery
			try {
				Thread.sleep(10);
			} catch (Exception e) {

			} // try catch

		} // while

	} // gameLoop

	 // startGame input: none output: none purpose: start a fresh game, clear old data
	private void startGame() {

		// clear out any existing entities and initialize a new set
		entities.clear();

		// changes map every few levels
		if (levelNum % 3 == 1) {
			levelChange++;
		} // if

		map = new Map(levelChange);
		initEntities();

		// blank out any keyboard settings that might exist
		leftPressed = false;
		rightPressed = false;
		upPressed = false;
		downPressed = false;
		firePressed = false;
		lookingLeft = false;
		lookingRight = false;
		lookingUp = false;
		lookingDown = false;
	} // startGame

	// inner class KeyInputHandler handles keyboard input from the user
	private class KeyInputHandler extends KeyAdapter {

		private int pressCount = 1; // the number of key presses since
									// waiting for 'any' key press

		/*
		 * The following methods are required for any class that extends the
		 * abstract class KeyAdapter. They handle keyPressed, keyReleased and
		 * keyTyped events.
		 */
		public void keyPressed(KeyEvent e) {

			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			// respond to move left, right or fire
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = true;
				lookingLeft = true;
				lookingRight = false;
				lookingUp = false;
				lookingDown = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = true;
				lookingRight = true;
				lookingLeft = false;
				lookingUp = false;
				lookingDown = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = true;
				lookingUp = true;
				lookingRight = false;
				lookingLeft = false;
				lookingDown = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				downPressed = true;
				lookingDown = true;
				lookingRight = false;
				lookingUp = false;
				lookingLeft = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = true;
			} // if

		} // keyPressed

		public void keyReleased(KeyEvent e) {
			// if waiting for keypress to start game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			// respond to move left, right, up, down or fire
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				leftPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				rightPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				upPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				downPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				firePressed = false;
			} // if

		} // keyReleased

		public void keyTyped(KeyEvent e) {

			// if waiting for key press to start game
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					waitingForKeyPress = false;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				} // else

			} // if waitingForKeyPress

			// if escape is pressed, end game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			} // if escape pressed

		} // keyTyped

	} // class KeyInputHandler

	// Main Program
	public static void main(String[] args) {
		
		// instantiate this object
		new Game();
	} // main
	
} // Game