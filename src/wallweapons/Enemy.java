package wallweapons;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public abstract class Enemy { //enemy will go to whichever is nearest - player or center
	
	public int health;
	protected int speed;
	public Point2D.Double pos;
	protected Point2D.Double prevpos; //used to calculate collision
	protected Point2D.Double velocity; //calculated using trigonometry depending on speed variable
	public int damagetowalls;
	public int damagetoplayer; //maybe change to protected later, add function - damageplayer()20938409384
	public int ENEMY_SIZE; //MUST BE SMALLER THAN THE SIZE OF A SINGLE WALL
	public int tmp; //used to make drawing smooth
	
	protected Enemy(Point2D.Double pos)
	{
		this.pos = pos;
		this.prevpos = pos;
		this.tmp = 0;
	}
	
	protected boolean detectCollision(Rectangle r) //called in move abstract method
	{
		if (pos.x < r.x + r.width && pos.x + ENEMY_SIZE > r.x && pos.y < r.y + r.height && pos.y + ENEMY_SIZE > r.y)
		{
			if (prevpos.y <= r.y - ENEMY_SIZE) //floor collision
			{
				pos.y = r.y - ENEMY_SIZE;
				velocity.y = 0;
			}
			else if (prevpos.y >= r.y + r.height) //ceiling collision
			{
				pos.y = r.y + r.height;
				velocity.y = 0;
			}
			else if (prevpos.x <= r.x - ENEMY_SIZE) //left-side collision
			{
				tmp = 1;
				pos.x = r.x - ENEMY_SIZE - 1;
				velocity.x = 0;
			}
			else if (prevpos.x >= r.x + r.width) //right-side collision
			{
				tmp = -1;
				pos.x = r.x + r.width + 1;
				velocity.x = 0;
			}
			return true;
		}
		return false;
	}
	
	abstract protected void move(int[][] walls); //use walls to detect collision
	
	protected void setvelocity(Point2D.Double player, int corex, int corey) //corepos relative to screen
	{
		//trigonometry based on speed and player/core position
		double targetx;
		double targety;
		//calculate distance to player/core (based on their centers) - note square roots cancel so aren't necessary
		double distancetoplayer = (pos.x - player.x)*(pos.x - player.x) + (pos.y - player.y)*(pos.y - player.y);
		double distancetocore = (pos.x - corex)*(pos.x - corex) + (pos.y - corey)*(pos.y - corey);
		if (distancetoplayer >= distancetocore)
		{
			targetx = player.x + (GameState.PLAYER_SIZE / 2);
			targety = player.y + (GameState.PLAYER_SIZE / 2);
		}
		else
		{
			targetx = corex * GameState.constantx + (GameState.constantx / 2);
			targety = corey * GameState.constanty + (GameState.constanty / 2);
		}
		//set velocity
		//find angle of pos relative to target
		double angle = Math.atan2((targetx - pos.x), (targety - pos.y));
		if (Math.toDegrees(angle) == 90)
		{
			velocity.x = speed;
			velocity.y = 0;
		}
		else if (Math.toDegrees(angle) == -90)
		{
			velocity.x = -speed;
			velocity.y = 0;
		}
		velocity.x = Math.sin(angle) * speed;
		velocity.y = Math.cos(angle) * speed;
	}
	
	abstract public void update(int[][] walls, Point2D.Double player, int corex, int corey); //called by GameState
	/*
	 * Call in update method:
	 * move()
	 *     call setvelocity method inside first
	 *     jumper (green enemy) has different move
	 *     green jumper and blue demolisher spawn only in 8 square corner directions.
	 * for all obstacles (for scout, it passes through shields, so don't check shields)
	 *     checkCollision()
	 * perform special ability
	 *     green jumper: if land on player/walls, do damage/die
	 *     blue demomilsher: shoot bullet every 2 seconds
	 *     purple bomber: if collision break walls in 1/2 radius, do damage to player if in range
	 */
	/*
	 * Note:
	 * For every weapon, go through each enemy and check their position for shooting
	 * and magnets and radiators and missile shooters. Check class of enemies - if enemy
	 * is a green jumper or blue demolisher, they are unaffected by magnets.
	 */
}
	