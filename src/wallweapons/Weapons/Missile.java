package wallweapons.Weapons;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import wallweapons.Bullet;
import wallweapons.GameState;
import wallweapons.Main;
import wallweapons.Weapon;

public class Missile extends Weapon {

	private static final int missilespeed = 8;
	private static final int missilesize = GameState.constantx / 4;
	private static final int missiletime = 60; //2 second lifetime
	
	public ArrayList<Bullet> missiles;
	private ArrayList<Point2D.Double> targets;
	private ArrayList<Integer> enemysizes; //used to calculate middle position of targets
	private ArrayList<Integer> times;
	
	private Point2D.Double missilespawn;
	
	public static int[][] getShape()
	{
		int[][] shape = {{1, -1, 1}, {-1, -1, -1}, {1, -1, 1}};
		return shape;
	}
	
	public Missile(int x, int y) {
		super(getShape(), Color.ORANGE, new Point(x, y), 120); //four second delay
		missiles = new ArrayList<Bullet>();
		targets = new ArrayList<Point2D.Double>();
		enemysizes = new ArrayList<Integer>();
		times = new ArrayList<Integer>();
		missilespawn = new Point2D.Double((pos.x + 1.5) * GameState.constantx - (missilesize / 2), (pos.y + 1.5) * GameState.constanty - (missilesize / 2));
	}
	
	private double getDistance(double x1, double y1, double x2, double y2)
	{
		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2); //no need for square root (speed up runtime)
	}
	
	private void remove(int i)
	{
		missiles.remove(i);
		targets.remove(i);
		enemysizes.remove(i);
		times.remove(i);
	}
	
	@Override
	public void update(int ticks) {
		//missiles lock on to the closest enemy, and always travel towards it.
		//it will travel for certain amount of ticks - no matter if enemy dies. If it dies,
		//it will just keep travelling forward until time's up.
		if (ticks >= super.nexttime)
		{
			//find closest enemy - DEPENDING ON THE CENTERS BETWEEN BULLET AND ENEMY (FOR ACCURACY)
			//missile won't be created if no enemy is present
			if (GameState.enemies.size() != 0)
			{
				double mindistance = getDistance(missilespawn.x + (missilesize / 2), missilespawn.y + (missilesize / 2), GameState.enemies.get(0).pos.x + (GameState.enemies.get(0).ENEMY_SIZE / 2), GameState.enemies.get(0).pos.y + (GameState.enemies.get(0).ENEMY_SIZE / 2));
				Point2D.Double minpos = GameState.enemies.get(0).pos;
				int minsize = GameState.enemies.get(0).ENEMY_SIZE;
				for (int i = 1; i < GameState.enemies.size(); i ++)
				{
					double tmp = getDistance(missilespawn.x + (missilesize / 2), missilespawn.y + (missilesize / 2), GameState.enemies.get(i).pos.x + (GameState.enemies.get(i).ENEMY_SIZE / 2), GameState.enemies.get(i).pos.y + (GameState.enemies.get(i).ENEMY_SIZE / 2));
					if (tmp < mindistance)
					{
						mindistance = tmp;
						minpos = GameState.enemies.get(i).pos;
						minsize = GameState.enemies.get(i).ENEMY_SIZE;
					}
				}
				targets.add(minpos); //WHENEVER TARGETS.GET(I) IS ACCESSED, BE SURE TO ADD ENEMY_SIZE / 2 TO IT (IN LIST) TO FIND CENTER POS
				enemysizes.add(minsize);
				//add missile
				Point2D.Double tmpvel = Main.getVelocity(missilespeed, minpos.x + minsize / 2, minpos.y + minsize / 2, missilespawn.x + missilesize / 2, missilespawn.y + missilesize / 2);
				Point spawnpos = new Point();
				if (tmpvel.x < 0)
					spawnpos.x = pos.x * GameState.constantx - missilesize;
				else
					spawnpos.x = (pos.x + 3) * GameState.constantx;
				if (tmpvel.y < 0)
					spawnpos.y = pos.y * GameState.constanty - missilesize;
				else
					spawnpos.y = (pos.y + 3) * GameState.constanty;
				missiles.add(new Bullet(spawnpos.x, spawnpos.y, tmpvel, missilesize, 20));
				//add lifetime
				times.add(ticks + missiletime);
			}
			super.nexttime = ticks + super.delay;
		}
		for (int i = 0; i < missiles.size(); i ++) //update missiles 
		{//CHECK FOR TIME OF MISSILE (THEN DELETE / REMOVE FROM LIST IF TIME'S UP)
			if (ticks > times.get(i))
			{
				remove(i);
				i--;
			}
			else
			{
				missiles.get(i).velocity = Main.getVelocity(missilespeed, targets.get(i).x + enemysizes.get(i) / 2, targets.get(i).y + enemysizes.get(i) / 2, missiles.get(i).pos.x + missilesize / 2, missiles.get(i).pos.y + missilesize / 2);
				if (missiles.get(i).move() || missiles.get(i).checkenemies() || missiles.get(i).checkwalls())
				{
					remove(i);
					i--;
				}
			}
		}
	}
}