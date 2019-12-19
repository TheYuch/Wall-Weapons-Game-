package wallweapons.Enemies;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import wallweapons.Enemy;
import wallweapons.GameState;

public class Regular extends Enemy {
	
	public Regular(Point2D.Double d, int corex, int corey) {
		super(new Point2D.Double());//random spawning on edge of screen 12304983094
		super.health = 25;
		super.speed = 5;
		super.damagetowalls = 5;
		super.damagetoplayer = 5;
		super.ENEMY_SIZE = GameState.constantx - 5;
		super.setvelocity(d, corex, corey);
	}

	@Override
	public void move(int[][] walls) {
		super.pos.x += super.velocity.x;
		super.pos.y += super.velocity.y;
		
		int constantx = GameState.constantx;
		int constanty = GameState.constanty;
		
		tmp = 0;
		for (int i = Math.max(0, (int)((pos.y+velocity.y)/constanty)); i < Math.min((int)((pos.y+ENEMY_SIZE+velocity.y)/constanty) + 1, GameState.GRIDS_Y); i ++)
		{
			for (int j = Math.max(0, (int)((pos.x+velocity.x)/constantx)); j < Math.min((int)((pos.x+ENEMY_SIZE+velocity.x)/constantx) + 1, GameState.GRIDS_X); j ++)
			{
				if (walls[i][j] != 0)
				{
					Rectangle r = new Rectangle(j * constantx, i * constanty, constantx, constanty);
					super.detectCollision(r);
					//change this later to if super.detectCollision(r) then add to shield.23438434234
				}
			}
		}
		
		prevpos.x = pos.x;
		prevpos.y = pos.y;
	}

	@Override
	public void update(int[][] walls, Double player, int corex, int corey) {
		super.setvelocity(player, corex, corey);
		move(walls);
	}

}
