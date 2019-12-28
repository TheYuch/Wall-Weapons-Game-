package wallweapons;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public abstract class Enemy { //enemy will go to whichever is nearest - player or center
	
	public int health;
	protected int speed;
	public Point2D.Double pos;
	protected Point2D.Double prevpos; //used to calculate collision
	public Point2D.Double velocity; //calculated using trigonometry depending on speed variable
	public int damagetowalls;
	public int damagetoplayer; //MAYBE GET RID OF THIS - SEE PLAYER LINE 40.. 21342341432
	public int ENEMY_SIZE; //MUST BE SMALLER THAN THE SIZE OF A SINGLE WALL
	
	public Color drawcolor;
	
	protected Enemy(Point2D.Double pos, Color color, int health, int speed, int walldamage, int playerdamage, int size)
	{
		this.pos = new Point2D.Double();
		this.prevpos = new Point2D.Double();
		this.velocity = new Point2D.Double();
		this.pos = pos; //NOTE THAT POS top-left corner of the enemy.
		this.prevpos.x = pos.x;
		this.prevpos.y = pos.y;
		this.drawcolor = color;
		this.health = health;
		this.speed = speed;
		this.damagetoplayer = playerdamage;
		this.damagetowalls = walldamage;
		this.ENEMY_SIZE = size;
	}
	
	protected boolean detectCollision(Rectangle r) //called in move abstract method
	{
		if (pos.x < r.x + r.width && pos.x + ENEMY_SIZE > r.x && pos.y < r.y + r.height && pos.y + ENEMY_SIZE > r.y)
		{
			if (prevpos.y <= r.y - ENEMY_SIZE) //floor collision
			{
				pos.y = r.y - ENEMY_SIZE;
				velocity.y = 0;
				return true;
			}
			else if (prevpos.x <= r.x - ENEMY_SIZE) //left-side collision
			{
				pos.x = r.x - ENEMY_SIZE;
				velocity.x = 0;
				return true;
			}
			else if (prevpos.x >= r.x + r.width) //right-side collision
			{
				pos.x = r.x + r.width;
				velocity.x = 0;
				return true;
			}
			else if (prevpos.y >= r.y + r.height) //ceiling collision
			{
				pos.y = r.y + r.height;
				velocity.y = 0;
				return true;
			}
			//return true; //UNCOMMENT THIS IF YOU WANT ENEMY TO DOUBLE SPEED INSIDE SHIELD BOUNDARY
			//2O348093840938409384039284
		}
		return false;
	}
	
	abstract protected void move(int[][] walls, Point2D.Double player, int corex, int corey);//use walls to detect collision
	
	protected void setvelocity(Point2D.Double player, int corex, int corey) //corepos relative to screen
	{
		//trigonometry based on speed and player/core position
		double targetx;
		double targety;
		Point2D.Double tmppos = new Point2D.Double(pos.x + ENEMY_SIZE / 2, pos.y + ENEMY_SIZE / 2);
		//calculate distance to player/core (based on their centers) - note square roots cancel so aren't necessary
		double distancetoplayer = (tmppos.x - player.x)*(tmppos.x - player.x) + (tmppos.y - player.y)*(tmppos.y - player.y);
		double distancetocore = (tmppos.x - (corex * GameState.constantx))*(tmppos.x - (corex * GameState.constantx)) + (tmppos.y - (corey * GameState.constanty))*(tmppos.y - (corey * GameState.constanty));
		if (distancetoplayer <= distancetocore)
		{
			targetx = player.x + (Player.PLAYER_SIZE / 2);
			targety = player.y + (Player.PLAYER_SIZE / 2);
		}
		else
		{
			targetx = corex * GameState.constantx + (GameState.constantx / 2);
			targety = corey * GameState.constanty + (GameState.constanty / 2);
		}
		//set velocity
		//find angle of pos relative to target
		Point2D.Double tmp = Main.getVelocity(speed, targetx, targety, tmppos.x, tmppos.y);
		velocity.x = tmp.x;
		velocity.y = tmp.y;
	}
	
	abstract public boolean update(int[][] walls, Point2D.Double player, int corex, int corey); //called by GameState
	//NOTE THAT THIS RETURNS WHETHER THE ENEMY HAS DIED OR NOT.
	
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
	