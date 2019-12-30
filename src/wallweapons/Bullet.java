package wallweapons;

import java.awt.Point;
import java.awt.geom.Point2D;

public class Bullet {

	/*
	 * USED BY DEMOLISHER ENEMY AND MULTIGUN
	 */
	
	public Point pos; //will be represented by a rounded rectangle. pos defines top-left corner
	public Point2D.Double velocity;
	private int damage;
	public int size;
	
	public Bullet(int xpos, int ypos, Point2D.Double velocity, int size, int damage) { //NOTE - VELOCITY DOES NOT CHANGE.
		this.pos = new Point(xpos, ypos);
		this.velocity = velocity;
		this.size = size;
		this.damage = damage;
	}
	
	public boolean checkenemies()
	{
		for (Enemy enemy : GameState.enemies)
		{
			if (enemy.enemyenabled && pos.x + size >= enemy.pos.x && pos.x <= enemy.pos.x + enemy.ENEMY_SIZE && pos.y + size >= enemy.pos.y && pos.y < enemy.pos.y + enemy.ENEMY_SIZE)
			{
				enemy.health -= damage;
				return true;
			}
		}
		return false;
	}
	
	public boolean checkwalls()
	{
		int x = GameState.getxonboard(pos.x);
		int y = GameState.getyonboard(pos.y);
		if (GameState.walls[y][x] > 0)
		{
			GameState.damagewalls(y, x, damage);
			return true;
		}
		return false;
	}
	
	public boolean checkplayer(int playerdamage)
	{
		if (pos.x < Player.pos.x + Player.PLAYER_SIZE && pos.x + size > Player.pos.x && pos.y < Player.pos.y + Player.PLAYER_SIZE && pos.y + size > Player.pos.y)
		{
			Player.health -= playerdamage;
			return true;
		}
		return false;
	}
	
	public boolean move() //returns whether it should get deleteds
	{
		pos.x += velocity.x;
		pos.y += velocity.y;
		if (pos.x < 0 || pos.x + size > Main.WIN_WIDTH || pos.y < 0 || pos.y + size > Main.WIN_HEIGHT)
			return true;
		return false;
	}

}
