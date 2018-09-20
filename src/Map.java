
/* Map.java
 * April 12, 2016
 * Map of the level
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Map {

	private static final int CLEAR = 0; // walkable tile
	private static final int BLOCKED = 1; // non-walkable tile
	public static final int TILE_SIZE = 50;

	private static final int WIDTH = 16;
	private static final int HEIGHT = 12;

	private int[][] data = new int[WIDTH][HEIGHT]; // layout of map

	// constructor
	/*****
	 * note when making the level have it so that the name of the file is the
	 * same as the level number (ex: 1.txt, 2.txt, etc.)
	 */
	public Map(int level) {
		String file = level + ".txt";

		try {
			BufferedReader b = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/avfiles/" + file)));
			String delimiters = "";

			for (int row = 0; row < data[row].length; row++) {
				String line = b.readLine();
				String[] values = line.split(delimiters);

				for (int col = 0; col < data.length; col++) {
					data[col][row] = Integer.parseInt(values[col]);
				} // for
			} // for

		} catch (Exception e) {
			e.printStackTrace();
		} // try/catch

	} // Map (constructor)

	// use data array to render map
	public void paint(Graphics2D g) {

		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				g.setColor(Color.darkGray);

				if (data[x][y] == BLOCKED) {
					g.setColor(Color.gray);
				} // if

				// draw the actual tile
				g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
				g.setColor(g.getColor().darker());
				g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
			} // for
		} // for

	} // paint

	// check if location is blocked or clear
	public boolean blocked(double x, double y) {
		return (data[(int) x / TILE_SIZE][(int) y / TILE_SIZE] == BLOCKED);
	} // blocked

} // Map (class)
