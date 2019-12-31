package wallweapons.Enemies;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;

import wallweapons.Enemy;
import wallweapons.GameState;
import wallweapons.Main;

public class Jumper extends Enemy {

	private static final int size = GameState.constantx;
	private static final int delay = 20; //2/3 seconds
	private static final int jumpttime = 15; //0.5 seconds
	private int ontime = GameState.ticks;
	private int nexttime = -1;
	private Point destination;
	
	public boolean vulnerable = false;
	
	public Jumper() {
		super(getpos(size), Color.GREEN, 40, -1, -1, 15, size, 3);
		//MAYBE MAKE BELOW MORE EFFICIENT?
		int change = 2 * GameState.constantx;
		if (super.pos.equals(new Point2D.Double(0, GameState.CORE_Y * GameState.constanty)))
			super.velocity = new Point2D.Double(change, 0);
		else if (super.pos.equals(new Point2D.Double(4 * GameState.constantx, -size)))
			super.velocity = new Point2D.Double(change, change);
		else if (super.pos.equals(new Point2D.Double(GameState.CORE_X * GameState.constantx, -size)))
			super.velocity = new Point2D.Double(0, change);
		else if (super.pos.equals(new Point2D.Double((GameState.GRIDS_X - 5) * GameState.constantx, -size)))
			super.velocity = new Point2D.Double(-change, change);
		else if (super.pos.equals(new Point2D.Double(Main.WIN_WIDTH - size, GameState.CORE_Y * GameState.constanty)))
			super.velocity = new Point2D.Double(-change, 0);
		else if (super.pos.equals(new Point2D.Double((GameState.GRIDS_X - 5) * GameState.constantx, Main.WIN_HEIGHT)))
			super.velocity = new Point2D.Double(-change, -change);
		else if (super.pos.equals(new Point2D.Double(GameState.CORE_X * GameState.constantx, Main.WIN_HEIGHT)))
			super.velocity = new Point2D.Double(0, -change);
		else
			super.velocity = new Point2D.Double(change, -change);
	}

	@Override
	protected void move(Point2D.Double player, int corex, int corey) {
		if (nexttime != -1 && GameState.ticks >= nexttime) //checking if hit by weapons/player
		{
			pos.x = destination.x;
			pos.y = destination.y;
			nexttime = -1;
			ontime = GameState.ticks + delay;
			vulnerable = true;
			enemyenabled = true;
			drawcolor = Color.GREEN;
		}
		else if (ontime != -1 && GameState.ticks >= ontime) //moving/jumping the enemy
		{
			vulnerable = false;
			enemyenabled = false;
			destination = new Point((int)(pos.x + velocity.x), (int)(pos.y + velocity.y));
			ontime = -1;
			nexttime = GameState.ticks + jumpttime; 
			drawcolor = new Color(195, 250, 0);
		}
		else if (!vulnerable)
		{
			pos.x += (destination.x - pos.x) / 3;
			pos.y += (destination.y - pos.y) / 3;
		}
	}

	@Override
	public boolean update(Point2D.Double player, int corex, int corey) {
		if (health <= 0)
			return true;
		else if (health <= 15) //half of original 30 health
			super.drawcolor = new Color(65, 245, 185, 200);
		if (vulnerable == true && pos.x >= 0 && pos.x < Main.WIN_WIDTH && pos.y >= 0 && pos.y < Main.WIN_HEIGHT)
		{
			if (GameState.walls[GameState.getyonboard(super.pos.y)][GameState.getxonboard(super.pos.x)] > 0) {
				GameState.walls[GameState.getyonboard(super.pos.y)][GameState.getxonboard(super.pos.x)] = 10; return true;
			}
			else if (GameState.walls[GameState.getyonboard(super.pos.y)][GameState.getxonboard(super.pos.x)] == -1)
			{
				try {
					Main.gameover();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		move(player, corex, corey);
		return false;
	}

}
