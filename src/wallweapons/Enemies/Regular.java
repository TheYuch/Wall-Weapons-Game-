package wallweapons.Enemies;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;

import wallweapons.Weapons.Shield;

public class Regular extends Enemy {
	
	public Regular(Point2D.Double playerpos, int corex, int corey) {
		super(getRandomEdgeSpawn(), Color.RED, 25, 2, 10, 5, GameState.constantx - 5);
		super.setvelocity(playerpos, corex, corey);
	}

	@Override
	public void move(Point2D.Double player, int corex, int corey) {
		super.pos.x += super.velocity.x;
		super.pos.y += super.velocity.y;
		
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

	@Override
	public boolean update(Point2D.Double player, int corex, int corey) {
		if (health <= 0)
			return true;
		move(player, corex, corey);
		super.setvelocity(player, corex, corey);
		return false;
	}

}
