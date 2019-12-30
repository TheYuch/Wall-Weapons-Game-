package wallweapons.Enemies;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;
import wallweapons.Weapons.Shield;

public class Bomber extends Enemy {

	private boolean dead = false;
	
	public Bomber(Point2D.Double playerpos, int corex, int corey) {
		super(getRandomEdgeSpawn(), new Color(75, 0, 130), 50, 5, 50, 10, GameState.constantx);
		super.setvelocity(playerpos, corex, corey);
	}

	private void die(int radius)
	{
		int xonboard = GameState.getxonboard(pos.x);
		int yonboard = GameState.getyonboard(pos.y);
		for (int i = Math.max(0, yonboard - radius); i < Math.min(GameState.GRIDS_Y, yonboard + radius + 1); i ++)
		{
			for (int j = Math.max(0, xonboard - radius); j < Math.min(GameState.GRIDS_X, xonboard + radius + 1); j ++)
			{
				if (GameState.walls[i][j] > 0)
				{
					GameState.damagewalls(i, j, GameState.walls[i][j]); //sets it to 0. (breaks wall)
				}
			}
		}
		dead = true;
	}
	
	@Override
	protected void move(Double player, int corex, int corey) {
		super.pos.x += super.velocity.x;
		super.pos.y += super.velocity.y;
		
		for (int i = 0; i < GameState.weapons.size(); i ++)
		{
			if (GameState.weapons.get(i) instanceof Shield)
			{
				Shield tmpshield = (Shield)GameState.weapons.get(i);
				if (super.detectCollision(tmpshield.collider))
				{
					die(1);
				}
			}
		}
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
						die(2);
					}
				}
			}
		}
		
		prevpos.x = pos.x;
		prevpos.y = pos.y;
	}

	@Override
	public boolean update(Double player, int corex, int corey) {
		if (health <= 0 || dead)
			return true;
		move(player, corex, corey);
		super.setvelocity(player, corex, corey);
		return false;
	}

}
