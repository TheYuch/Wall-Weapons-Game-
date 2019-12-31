package wallweapons.Weapons;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;
import wallweapons.Weapon;
import wallweapons.Enemies.Jumper;

public class Magnet extends Weapon {
	
	public final static int radius = 3; //out from the shape (attraction radius)
	public final static int alpha = 100; //will be drawn as a magnetic field radius thing
	private Point centerpos;
	
	public static int[][] getShape()
	{
		int[][] shape = {{1, 1}, {1, 1}};
		return shape;
	}
	
	public Magnet(int x, int y) {
		super(getShape(), new Color(200, 85, 220), new Point(x, y), 0, -1);
		centerpos = new Point((pos.x + 1) * GameState.constantx, (pos.y + 1) * GameState.constanty);
	}

	@Override
	public void update(int ticks) {
		for (Enemy enemy : GameState.enemies)
		{
			if (!(enemy instanceof Jumper)) //jumper/demolishers not affected by magnets
			{
				Point2D.Double curpos = new Point2D.Double(enemy.pos.x + (enemy.ENEMY_SIZE / 2), enemy.pos.y + (enemy.ENEMY_SIZE / 2));
				//note that curpos is set to the center coordinates of the enemy
				double distance = Math.sqrt(((curpos.x - centerpos.x) * (curpos.x - centerpos.x)) + ((curpos.y - centerpos.y) * (curpos.y - centerpos.y)));
				if (distance < ((radius + 1) * GameState.constantx) + (enemy.ENEMY_SIZE / 2))
				{
					//magnets cause objects to move faster when closer to the magnet.
					//Add to enemy's velocity depending on distance to magnet.
					int speed = (int)(((radius + 1) * GameState.constantx) + (enemy.ENEMY_SIZE / 2) - distance);
					Point2D.Double attractionvel = Main.getVelocity(speed, centerpos.x, centerpos.y, curpos.x, curpos.y);
					enemy.velocity.x += attractionvel.x / 15;
					enemy.velocity.y += attractionvel.y / 15;
				}
			}
		}
	}

}
