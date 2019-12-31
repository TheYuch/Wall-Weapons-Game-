package wallweapons.Enemies;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Random;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;
import wallweapons.Weapons.Laser;
import wallweapons.Weapons.Multigun;

public class Scout extends Enemy {

	private static int delay = 150;
	private int nexttime = GameState.ticks + delay;
	private int[][][] weaponshapes;
	
	public Scout(Point2D.Double playerpos, int corex, int corey) {
		super(getRandomEdgeSpawn(), Color.YELLOW, 25, 2, 10, 5, GameState.constantx - 10, 3);
		super.setvelocity(playerpos, corex, corey);
		weaponshapes = new int[6][][];
		for (int i = 0; i < 2; i ++)
			weaponshapes[i] = Main.rotateArray(Laser.getShape(), i * 90);
		for (int i = 0; i < 4; i ++)
			weaponshapes[i + 2] = Main.rotateArray(Multigun.getShape(), i * 90);
	}

	@Override
	protected void move(Point2D.Double player, int corex, int corey) {
		super.pos.x += super.velocity.x;
		super.pos.y += super.velocity.y;
		
		for (int i = Math.max(0, (int)((pos.y+velocity.y)/GameState.constanty)); i < Math.min((int)((pos.y+ENEMY_SIZE+velocity.y)/GameState.constanty) + 1, GameState.GRIDS_Y); i ++)
		{
			for (int j = Math.max(0, (int)((pos.x+velocity.x)/GameState.constantx)); j < Math.min((int)((pos.x+ENEMY_SIZE+velocity.x)/GameState.constantx) + 1, GameState.GRIDS_X); j ++)
			{
				if (GameState.walls[i][j] == -1)
				{
					//gameover
					try {
						Main.gameover();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (GameState.walls[i][j] != 0)
				{
					Rectangle r = new Rectangle(j * GameState.constantx, i * GameState.constanty, GameState.constantx, GameState.constanty);
					if (j != GameState.GRIDS_X - 1 && GameState.walls[i][j + 1] != 0)
						r.width *= 2;
					if (super.detectCollision(r));
					{
						if (GameState.ticks % 30 == 0) //make it not uniform?!??!?!12309830293
						{
							GameState.damagewalls(i, j, super.damagetowalls);
						}
					}
				}
			}
		}
		
		prevpos.x = pos.x;
		prevpos.y = pos.y;
	}

	private void spawnweapon(int xonboard, int yonboard, int[][] weaponshape)
	{
		for (int i = 0; i < weaponshape.length; i ++)
		{
			for (int j = 0; j < weaponshape[0].length; j ++)
			{
				if (GameState.walls[i + yonboard][j + xonboard] == -1)
					return;
				if (weaponshape[i][j] == -1)
					GameState.walls[i + yonboard][j + xonboard] = 0;
				else if (weaponshape[i][j] == 1)
					GameState.walls[i + yonboard][j + xonboard] = 50;
			}
		}
		GameState.checkwalls(xonboard, yonboard);
	}
	
	@Override
	public boolean update(Point2D.Double player, int corex, int corey) {
		if (health <= 0)
			return true;
		else if (health <= 13) //half of original 25 health
			super.drawcolor = new Color(210, 245, 65, 200);
		move(player, corex, corey);
		super.setvelocity(player, corex, corey);
		
		int xonboard = GameState.getxonboard(pos.x);
		int yonboard = GameState.getyonboard(pos.y);
		Random rand = new Random();
		if (rand.nextBoolean() && GameState.ticks >= nexttime) //50% chance it will spawn something
		{
			int spawnx;
			int spawny;
			int choice = rand.nextInt(4);
			if (choice == 0) //up
			{
				spawnx = xonboard - 2;
				spawny = yonboard - 4;
			}
			else if (choice == 1) //right
			{
				spawnx = xonboard + 1;
				spawny = yonboard - 2;
			}
			else if (choice == 2) //down
			{
				spawnx = xonboard - 2;
				spawny = yonboard + 1;
			}
			else //left
			{
				spawnx = xonboard - 4;
				spawny = yonboard - 2;
			}
			if (spawnx < 0)
				spawnx = 0;
			else if (spawnx > GameState.GRIDS_X - 4)
				spawnx = GameState.GRIDS_X - 4;
			if (spawny < 0)
				spawny = 0;
			else if (spawny > GameState.GRIDS_Y - 4)
				spawny = GameState.GRIDS_Y - 4;
			spawnweapon(spawnx, spawny, weaponshapes[rand.nextInt(weaponshapes.length)]);
			nexttime = GameState.ticks + delay;
		}
		
		return false;
	}

}
