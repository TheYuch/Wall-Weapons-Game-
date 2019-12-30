package wallweapons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import wallweapons.Enemies.Demolisher;
import wallweapons.Weapons.*;

public class GamePanel extends JPanel {
	/*
	 * Moved the update method into GameState.java
	 */
	
	@Override
	public void paintComponent(Graphics g) {

		//Graphics2D g2 = (Graphics2D) g;
		//g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		/*
		 * 
		 * MAKE IT SO THAT YOU PUT A TRANSPARENT COLOR OVER EVERY WEAPON? NEW COLOR(R, G, B, ALPHA)
		 * 2341234234
		 */
		
		/*
		 * Clear the screen
		 */
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		/*
		 * Draw the grid
		 
		
		g.setColor(Color.LIGHT_GRAY);
		for (int i = 1; i < GameState.GRIDS_X; i++)
		{
			g.fillRect((i * GameState.constantx) - (GameState.GRID_THICKNESS / 2), 0, GameState.GRID_THICKNESS, Main.WIN_HEIGHT); 
			//rectangles act as thicker lines
		}
		for (int i = 1; i < GameState.GRIDS_Y; i ++)
		{
			g.fillRect(0, (i * GameState.constanty) - (GameState.GRID_THICKNESS / 2), Main.WIN_WIDTH, GameState.GRID_THICKNESS);
		}*/
		
		/*
		 * draw walls
		 */
		for (int i = 0; i < GameState.GRIDS_Y; i ++)
		{
			for (int j = 0; j < GameState.GRIDS_X; j ++)
			{
				if (GameState.walls[i][j] > 0)
				{
					g.setColor(Color.LIGHT_GRAY);
					if (GameState.walls[i][j] >= 25)
						g.setColor(Color.GRAY);
					g.fillRect(j * GameState.constantx, i * GameState.constanty, GameState.constantx, GameState.constanty);
				}
				else if (GameState.walls[i][j] == -1)
				{ //draw core
					g.setColor(Color.BLACK);
					g.fillRect(j * GameState.constantx, i * GameState.constanty, GameState.constantx, GameState.constanty);
				}
			}
		}
		
		//draw enemies
		for (int i = 0; i < GameState.enemies.size(); i ++)
		{
			Enemy curenemy = GameState.enemies.get(i);
			
			if (curenemy instanceof Demolisher)
			{
				Demolisher cur = (Demolisher)curenemy;
				g.setColor(Demolisher.bulletcolor);
				for (Bullet bullet : cur.bullets)
				{
					g.drawRect(bullet.pos.x, bullet.pos.y, bullet.size, bullet.size);
				}
			}
			
			g.setColor(curenemy.drawcolor);
			//LATER: MAKE IT SO ENEMIES WITH REALTIVELY LOWER HEALTH (HAVE BOOLEAN LOWERHEALTH)
			//WILL BE DRAWN WITH BRIGHER COLORS (curenemy.drawcolor.brigher())
			
			//using g2 for smooth ovals (and uncomment first 2 lines of paintComponent)
			g.fillOval((int)(curenemy.pos.x), (int)(curenemy.pos.y), curenemy.ENEMY_SIZE, curenemy.ENEMY_SIZE);
		}
		
		/*
		 * Draw the player
		 */
		if (Player.health <= 25)
			g.setColor(Player.lowhealthcolor);
		else
			g.setColor(Player.drawcolor);
		Point2D.Double player = Player.pos;
		g.fillRect((int)player.x, (int)player.y, Player.PLAYER_SIZE, Player.PLAYER_SIZE);
		//g.fillOval((int)player.x, (int)player.y, GameState.PLAYER_SIZE, GameState.PLAYER_SIZE);
		
		//draw weapons
		for (int i = 0; i < GameState.weapons.size(); i ++)
		{
			Weapon curweapon = GameState.weapons.get(i);
			if (curweapon instanceof Magnet)
			{
				Magnet cur = (Magnet)curweapon;
				g.setColor(new Color(cur.drawcolor.getRed(), cur.drawcolor.getGreen(), cur.drawcolor.getBlue(), Magnet.alpha));
				g.fillOval((cur.pos.x - Magnet.radius) * GameState.constantx, (cur.pos.y - Magnet.radius) * GameState.constanty, (Magnet.radius * 2 + 2) * GameState.constantx, (Magnet.radius * 2 + 2) * GameState.constanty);
			}
			else if (curweapon instanceof Shield)
			{
				Shield cur = (Shield)curweapon;
				if (cur.colliderenabled) 
					g.setColor(cur.drawcolor);
				else
					g.setColor(Shield.disabledcolor);
				Rectangle r = cur.collider;
				g.drawRect(r.x, r.y, r.width, r.height);
			}
			else if (curweapon instanceof Laser)
			{
				Laser cur = (Laser)curweapon;
				if (cur.laserenabled)
				{
					g.setColor(cur.drawcolor);
					g.fillRect(cur.laser1.x, cur.laser1.y, cur.laser1.width, cur.laser1.height);
					g.fillRect(cur.laser2.x, cur.laser2.y, cur.laser2.width, cur.laser2.height);
				}
			}
			else if (curweapon instanceof Multigun)
			{
				Multigun cur = (Multigun)curweapon;
				g.setColor(cur.drawcolor);
				for (Bullet curbullet : cur.bullets) //CODE NOTE: ALWAYS USE FOR EACH LOOP WHEN RENDERING BULLETS.
				{
					g.fillRect(curbullet.pos.x, curbullet.pos.y, curbullet.size, curbullet.size);
				}
			}
			else if (curweapon instanceof Radiator)
			{
				Radiator cur = (Radiator)curweapon;
				g.setColor(new Color(cur.drawcolor.getRed(), cur.drawcolor.getGreen(), cur.drawcolor.getBlue(), cur.alpha));
				g.fillOval(cur.circletodraw.x, cur.circletodraw.y, cur.circletodraw.width, cur.circletodraw.height);
			}
			else
			{
				Missile cur = (Missile)curweapon;
				g.setColor(cur.drawcolor);
				for (Bullet missile : cur.missiles)
				{
					g.fillRect(missile.pos.x, missile.pos.y, missile.size, missile.size);
				}
			}
		}
	}
}