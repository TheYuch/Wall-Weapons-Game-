package wallweapons.Enemies;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import wallweapons.Bullet;
import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;
import wallweapons.Weapons.Shield;

public class Demolisher extends Enemy {

	public ArrayList<Bullet> bullets;
	private int nexttime = GameState.ticks;
	private static final int delay = 20; //2/3 seconds
	public static final Color bulletcolor = Color.DARK_GRAY;
	
	public Demolisher(Point2D.Double playerpos, int corex, int corey) {
		super(getRandomEdgeSpawn(), Color.BLUE, 40, 2, 0, 0, GameState.constantx - 5, 4);
		super.setvelocity(playerpos, corex, corey);
		bullets = new ArrayList<Bullet>();
	}

	@Override
	protected void move(Point2D.Double player, int corex, int corey) {
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
					super.detectCollision(r);
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
		else if (health <= 20) //half of original 40 health
			super.drawcolor = new Color(80, 65, 245, 200);
		
		if (GameState.ticks > nexttime)
		{
			bullets.add(new Bullet((int)(pos.x + ENEMY_SIZE / 2), (int)(pos.y + ENEMY_SIZE / 2), new Point2D.Double(velocity.x * 5, velocity.y * 5), GameState.constantx / 4, 15));
			nexttime = GameState.ticks + delay;
		}
		for (int i = 0; i < bullets.size(); i ++)
		{
			if (bullets.get(i).move() || bullets.get(i).checkwalls() || bullets.get(i).checkplayer(super.damagetoplayer))
			{
				bullets.remove(i);
				i--;
			}
		}
		
		move(player, corex, corey);
		super.setvelocity(player, corex, corey);
		return false;
	}

}