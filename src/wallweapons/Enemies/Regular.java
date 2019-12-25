package wallweapons.Enemies;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Random;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;

import wallweapons.Weapons.Shield;

public class Regular extends Enemy {
	
	private static Point2D.Double getspawnpos()
	{
		Random rand = new Random();
		int size = GameState.constantx - 5;
		double x;
		double y;
		if (rand.nextBoolean())
		{
			if (rand.nextBoolean())
				x = -size;
			else
				x = Main.WIN_WIDTH;
			return new Point2D.Double(x, ((rand.nextInt() & Integer.MAX_VALUE) % (Main.WIN_HEIGHT + size)) - size);
		}
		else
		{
			if (rand.nextBoolean())
				y = -size;
			else
				y = Main.WIN_HEIGHT;
			return new Point2D.Double(((rand.nextInt() & Integer.MAX_VALUE) % (Main.WIN_WIDTH + size)) - size, y);
		}
	}
	
	public Regular(Point2D.Double playerpos, int corex, int corey) {
		super(getspawnpos(), Color.RED);
		super.health = 25;
		super.speed = 2;
		super.damagetowalls = 10;
		super.damagetoplayer = 5;
		super.ENEMY_SIZE = GameState.constantx - 5;
		super.setvelocity(playerpos, corex, corey);
	}

	@Override
	public void move(int[][] walls, Point2D.Double player, int corex, int corey) {
		super.pos.x += super.velocity.x;
		super.pos.y += super.velocity.y;
		
		int constantx = GameState.constantx;
		int constanty = GameState.constanty;
		
		for (int i = 0; i < GameState.weapons.size(); i ++)
		{
			if (GameState.weapons.get(i) instanceof Shield)
			{
				Shield tmpshield = (Shield)GameState.weapons.get(i);
				if (super.detectCollision(tmpshield.collider))
				{
					if (tmpshield.canpass(this.hashCode()))
					{
						super.setvelocity(player, corex, corey);
						super.pos.x += super.velocity.x;
						super.pos.y += super.velocity.y;
					}
				}
			}
		}
		for (int i = Math.max(0, (int)((pos.y+velocity.y)/constanty)); i < Math.min((int)((pos.y+ENEMY_SIZE+velocity.y)/constanty) + 1, GameState.GRIDS_Y); i ++)
		{
			for (int j = Math.max(0, (int)((pos.x+velocity.x)/constantx)); j < Math.min((int)((pos.x+ENEMY_SIZE+velocity.x)/constantx) + 1, GameState.GRIDS_X); j ++)
			{
				if (walls[i][j] == -1)
				{
					//gameover
					try {
						Main.gameover();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (walls[i][j] != 0)
				{
					Rectangle r = new Rectangle(j * constantx, i * constanty, constantx, constanty);
					if (j != GameState.GRIDS_X - 1 && walls[i][j + 1] != 0)
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

	@Override
	public boolean update(int[][] walls, Point2D.Double player, int corex, int corey) {
		if (health <= 0)
			return true;
		super.setvelocity(player, corex, corey);
		move(walls, player, corex, corey);
		return false;
	}

}
