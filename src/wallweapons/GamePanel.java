package wallweapons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import wallweapons.Weapons.Shield;

public class GamePanel extends JPanel {
	/*
	 * Moved the update method into GameState.java
	 */

	@Override
	public void paintComponent(Graphics g) {
		/*
		 * Clear the screen
		 */
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		GameState currState = Main.state;
		
		/*
		 * Draw the grid
		 */
		g.setColor(Color.LIGHT_GRAY);
		final int constantx = Main.WIN_WIDTH / currState.GRIDS_X;
		for (int i = 1; i < currState.GRIDS_X; i++)
		{
			g.fillRect((i * constantx) - (currState.GRID_THICKNESS / 2), 0, currState.GRID_THICKNESS, Main.WIN_HEIGHT); 
			//rectangles act as thicker lines
		}
		final int constanty = Main.WIN_HEIGHT / currState.GRIDS_Y;
		for (int i = 1; i < currState.GRIDS_Y; i ++)
		{
			g.fillRect(0, (i * constanty) - (currState.GRID_THICKNESS / 2), Main.WIN_WIDTH, currState.GRID_THICKNESS);
		}
		
		/*
		 * draw obstacles
		 */
		for (int i = 0; i < currState.GRIDS_Y; i ++)
		{
			for (int j = 0; j < currState.GRIDS_X; j ++)
			{
				if (currState.walls[i][j] > 0)
				{
					g.setColor(Color.GRAY);
					g.fillRect(j * constantx, i * constanty, constantx, constanty);
				}
				else if (currState.walls[i][j] == -1)
				{ //draw core
					g.setColor(Color.BLACK);
					g.fillRect(j * constantx, i * constanty, constantx, constanty);
				}
			}
		}
		
		/*
		 * Draw the player
		 */
		g.setColor(Color.BLUE);
		Point2D.Double player = currState.player;
		g.fillRect((int)player.x + currState.tmp, (int)player.y, GameState.PLAYER_SIZE, GameState.PLAYER_SIZE);
		//g.fillOval((int)player.x, (int)player.y, GameState.PLAYER_SIZE, GameState.PLAYER_SIZE);
		
		//draw shields
		for (int i = 0; i < currState.shields.size(); i ++)
		{
			if (currState.shields.get(i).colliderenabled) 
				g.setColor(Shield.drawcolor);
			else
				g.setColor(Shield.disabledcolor);
			Rectangle r = currState.shields.get(i).collider;
			g.drawRect(r.x, r.y, r.width, r.height);
		}
	}
}