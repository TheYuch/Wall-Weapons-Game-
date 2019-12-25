package wallweapons.Weapons;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;
import wallweapons.Weapon;

public class Laser extends Weapon {
	
	public Rectangle laser1;
	public Rectangle laser2;
	public boolean laserenabled = false;
	
	static final int damage = 35;
	static final int laserdif = 10; //dif from laser rectangle to edge of grid block
	
	private boolean vertical;
	private int ontime = -1;

	public static int[][] getShape()
	{
		int[][] shape = {{1, 1, 1, 1}};
		return shape;
	}
	
	public Laser(int x, int y, int degrees) {
		super(getShape(), Color.CYAN, new Point(x, y), 45);
		if (degrees == 90 || degrees == 270)
		{
			vertical = true;
			//define rectangles of lasers (for GamePanel)
			laser1 = new Rectangle((pos.x * GameState.constantx) + laserdif, 0, GameState.constantx - (laserdif * 2), pos.y * GameState.constanty);
			laser2 = new Rectangle((pos.x * GameState.constantx) + laserdif, GameState.constanty * (pos.y + 4), GameState.constantx - (laserdif * 2), (Main.WIN_HEIGHT) - ((pos.y + 4) * GameState.constanty));
		}
		else
		{
			vertical = false;
			//define rectangles of lasers (for GamePanel)
			laser1 = new Rectangle(0, pos.y * GameState.constanty + laserdif, pos.x * GameState.constantx, GameState.constanty - (laserdif * 2));
			laser2 = new Rectangle((pos.x + 4) * GameState.constantx, pos.y * GameState.constantx + laserdif, Main.WIN_WIDTH - ((pos.x + 4) * GameState.constantx), GameState.constanty - (laserdif * 2));
		}
	}

	public void damageWalls()
	{
		//called after checking for enemy damage - called only once for ONLY LASER GUN AND MULTI-GUN
		if (laserenabled)
		{
			if (vertical)
			{
				for (int i = 0; i < pos.y; i ++) //REMEMBER, I = Y AND J = X!
				{
					if (GameState.walls[i][pos.x] > 0)
						GameState.damagewalls(i, pos.x, damage);
				}
				for (int i = pos.y + 4; i < GameState.GRIDS_Y; i ++)
				{
					if (GameState.walls[i][pos.x] > 0)
						GameState.damagewalls(i, pos.x, damage);
				}
			}
			else
			{
				for (int i = 0; i < pos.x; i ++)
				{
					if (GameState.walls[pos.y][i] > 0)
						GameState.damagewalls(pos.y, i, damage);
				}
				for (int i = pos.x + 4; i < GameState.GRIDS_X; i ++)
				{
					if (GameState.walls[pos.y][i] > 0)
						GameState.damagewalls(pos.y, i, damage);
				}
			}
		}
	}
	
	public void damageEnemies()
	{
		if (laserenabled)
		{
			//check enemies/walls in range and do damage
			for (int i = 0; i < GameState.enemies.size(); i ++)
			{
				if (vertical)
				{
					if (Math.abs((pos.x * GameState.constantx) - GameState.enemies.get(i).pos.x) < GameState.constantx - (laserdif * 2))
						GameState.enemies.get(i).health -= damage;
				}
				else
				{
					if (Math.abs((pos.y * GameState.constanty) - GameState.enemies.get(i).pos.y) < GameState.constanty - (laserdif * 2))
						GameState.enemies.get(i).health -= damage;
				}
			}
		}
	}
	
	@Override
	public void update(int ticks) {
		if (super.nexttime != -1 && ticks >= super.nexttime)
		{
			laserenabled = true;
			
			ontime = ticks + (30 / 4); //0.25 seconds
			super.nexttime = -1;
		}
		else if (ontime != -1 && ticks >= ontime)
		{
			laserenabled = false;
			super.nexttime = ticks + super.delay;
			ontime = -1;
		}
	}
}
