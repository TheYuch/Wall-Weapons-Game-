package wallweapons.Weapons;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import wallweapons.Bullet;
import wallweapons.GameState;
import wallweapons.Weapon;

public class Multigun extends Weapon {

	private Point shootingpos1;
	private Point shootingpos2;
	private Point shootingpos3;
	public ArrayList<Bullet> bullets;
	
	private static final int bulletspeed = 10;
	private static final int bulletsize = GameState.constantx / 5;
	
	private Point2D.Double shoot1;
	private Point2D.Double shoot2;
	private Point2D.Double shoot3; //shooting locations of 3 bullets
	
	public static int[][] getShape()
	{
		int[][] shape = {{0, 0, 0, 1}, {0, 0, 1, 0}, {1, 1, 0, 0}, {0, 1, 0, 0}};
		return shape;
	}
	
	public Multigun(int x, int y, int degrees) {
		super(getShape(), Color.DARK_GRAY, new Point(x, y), 30, degrees);
		//CURRENT SHAPE = 0 DEGREE ROTATION (shoots in top RIGHT corner)
		bullets = new ArrayList<Bullet>();
		
		if (degrees == 0)
		{
			shootingpos1 = new Point((int)((x + 3.5) * GameState.constantx - (bulletsize / 2)), y * GameState.constanty - bulletsize);
			shootingpos2 = new Point((x + 4) * GameState.constantx, y * GameState.constanty - bulletsize);
			shootingpos3 = new Point((x + 4) * GameState.constantx, (int)((y + 0.5) * GameState.constanty - (bulletsize / 2)));
			shoot1 = new Point2D.Double(0, -bulletspeed);
			shoot2 = new Point2D.Double((int)(bulletspeed / Math.sqrt(2)), -(int)(bulletspeed / Math.sqrt(2)));
			shoot3 = new Point2D.Double(bulletspeed, 0);
		}
		else if (degrees == 90)
		{
			shootingpos1 = new Point((x + 4) * GameState.constantx, (int)((y + 3.5) * GameState.constanty - (bulletsize / 2)));
			shootingpos2 = new Point((x + 4) * GameState.constantx, (y + 4) * GameState.constanty);
			shootingpos3 = new Point((int)((x + 3.5) * GameState.constantx - (bulletsize / 2)), (y + 4) * GameState.constanty);
			shoot1 = new Point2D.Double(bulletspeed, 0);
			shoot2 = new Point2D.Double((int)(bulletspeed / Math.sqrt(2)), (int)(bulletspeed / Math.sqrt(2)));
			shoot3 = new Point2D.Double(0, bulletspeed);
		}
		else if (degrees == 180)
		{
			shootingpos1 = new Point((int)((x + 0.5) * GameState.constantx - (bulletsize / 2)), (y + 4) * GameState.constanty);
			shootingpos2 = new Point(x * GameState.constantx - bulletsize, (y + 4) * GameState.constanty);
			shootingpos3 = new Point(x * GameState.constantx - bulletsize, (int)((y + 3.5) * GameState.constanty - (bulletsize / 2)));
			shoot1 = new Point2D.Double(0, bulletspeed);
			shoot2 = new Point2D.Double(-(int)(bulletspeed / Math.sqrt(2)), (int)(bulletspeed / Math.sqrt(2)));
			shoot3 = new Point2D.Double(-bulletspeed, 0);
		}
		else if (degrees == 270)
		{
			shootingpos1 = new Point(x * GameState.constantx - bulletsize, (int)((y + 0.5) * GameState.constanty - (bulletsize / 2)));
			shootingpos2 = new Point(x * GameState.constantx - bulletsize, y * GameState.constanty - bulletsize);
			shootingpos3 = new Point((int)((x + 0.5) * GameState.constantx - (bulletsize / 2)), y * GameState.constanty - bulletsize);
			shoot1 = new Point2D.Double(-bulletspeed, 0);
			shoot2 = new Point2D.Double(-(int)(bulletspeed / Math.sqrt(2)), -(int)(bulletspeed / Math.sqrt(2)));
			shoot3 = new Point2D.Double(0, -bulletspeed);
		}
	}

	@Override
	public void update(int ticks) {
		for (int i = 0; i < bullets.size(); i ++)
		{
			if (bullets.get(i).move() || bullets.get(i).checkwalls() ||	bullets.get(i).checkenemies())
			{
				bullets.remove(i);
				i --;
			}
		}
		if (ticks >= super.nexttime)
		{
			bullets.add(new Bullet(shootingpos1.x, shootingpos1.y, shoot1, bulletsize, 20));
			bullets.add(new Bullet(shootingpos2.x, shootingpos2.y, shoot2, bulletsize, 20));
			bullets.add(new Bullet(shootingpos3.x, shootingpos3.y, shoot3, bulletsize, 20));
			super.nexttime = ticks + delay;
		}
	}

}
