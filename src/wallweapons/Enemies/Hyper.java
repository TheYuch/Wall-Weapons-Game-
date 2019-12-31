package wallweapons.Enemies;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;
import wallweapons.Weapons.Shield;

public class Hyper extends Enemy {

	public Hyper(Point2D.Double playerpos, int corex, int corey) {
		super(getRandomEdgeSpawn(), Color.ORANGE, 20, 7, 7, 3, GameState.constantx - 8, 2);
		super.setvelocity(playerpos, corex, corey);
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
	public boolean update(Double player, int corex, int corey) {
		if (health <= 0)
		{
			return true;
		}
		else if (health <= 10) //half of original 20 health
			super.drawcolor = new Color(245, 185, 65, 200);
		move(player, corex, corey);
		super.setvelocity(player, corex, corey);
		return false;
	}

}
