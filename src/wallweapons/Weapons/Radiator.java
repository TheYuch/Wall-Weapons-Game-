package wallweapons.Weapons;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Weapon;

public class Radiator extends Weapon {

	private static final int radius = 2;
	private Point centerpos;
	public Rectangle circletodraw; //a rectangle represents an oval's dimensions
	public int alpha = 0;
	private int cnstchange; //used for changing radiating circle
	
	public static int[][] getShape()
	{
		int[][] shape = {{0, 1, 0}, {1, -1, 1}, {0, 1, 0}};
		return shape;
	}
	
	public Radiator(int x, int y) {
		super(getShape(), new Color(65, 245, 195), new Point(x, y), 60); //2 second delay
		centerpos = new Point((int)((x + 1.5) * GameState.constantx), (int)((y + 1.5) * GameState.constanty));
		circletodraw = new Rectangle(pos.x, pos.y, 3 * GameState.constantx, 3 * GameState.constanty);
		cnstchange = radius * GameState.constantx / super.delay;
	}

	@Override
	public void update(int ticks) {
		if (ticks >= super.nexttime)
		{
			alpha = 0;
			for (Enemy enemy : GameState.enemies) //halve the healths of enemies in range
			{
				if (enemy.enemyenabled)
				{
					Point2D.Double curpos = new Point2D.Double(enemy.pos.x + (enemy.ENEMY_SIZE / 2), enemy.pos.y + (enemy.ENEMY_SIZE / 2));
					//note that curpos is set to the center coordinates of the enemy
					double distance = Math.sqrt(((curpos.x - centerpos.x) * (curpos.x - centerpos.x)) + ((curpos.y - centerpos.y) * (curpos.y - centerpos.y)));
					if (distance < ((radius + 1.5) * GameState.constantx) + (enemy.ENEMY_SIZE / 2))
					{
						enemy.health = (int)Math.ceil(enemy.health / 2);
					}
				}
			}
			circletodraw.x = pos.x * GameState.constantx;
			circletodraw.y = pos.y * GameState.constanty; 
			circletodraw.width = 3 * GameState.constantx;
			circletodraw.height = 3 * GameState.constanty;
			super.nexttime = ticks + super.delay;
		}
		else
		{
			circletodraw.x -= cnstchange;
			circletodraw.y -= cnstchange;
			circletodraw.width += 2 * cnstchange;
			circletodraw.height += 2 * cnstchange;
			alpha += 3;
		}
	}

}
