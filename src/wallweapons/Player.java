package wallweapons;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;

public class Player {

	static final int PLAYER_SIZE = Main.WIN_WIDTH / GameState.GRIDS_X - 5;
	static final int DAMAGE_TO_ENEMIES = 15;
	
	public static Color drawcolor = Color.BLUE;
	
	public int health = 100;
	
	public Point2D.Double pos;
	private Point2D.Double prevplayer;
	public Point2D.Double velocity;
	private double FRICTION = 0.8;
	
	//public int tmp = 0; //try to get rid of this later
	
	public Player() {
		pos = new Point2D.Double(Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2), Main.WIN_HEIGHT / 2 - ((PLAYER_SIZE * 2) + GameState.constantx - PLAYER_SIZE + 1));
		prevplayer = new Point2D.Double(Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2), Main.WIN_HEIGHT / 2 - (PLAYER_SIZE / 2));
		velocity = new Point2D.Double(0, 0);
	}
	
	public void damageEnemies()
	{
		for (int i = 0; i < GameState.enemies.size(); i ++)
		{
			Point2D.Double enemypos = GameState.enemies.get(i).pos;
			if (Math.abs(enemypos.x - pos.x) <= GameState.constantx && Math.abs(enemypos.y - pos.y) <= GameState.constanty)
			{
				GameState.enemies.get(i).health -= DAMAGE_TO_ENEMIES;
				//EITHER DO IT EVERY SECOND - ADDING VAR TO EACH ENEMY - TO DO DAMAGE TO PLAYER,
				//OR ONLY FOR CERTAIN CLASSES (AND E.G. BULLETS/BOMBS THEY SHOOT) AFFECT THE ENEMY
				//BUT ONLY ONCE IDK
			}
		}
	}
	
	private void detectCollision(Rectangle r)
	{
		if (pos.x < r.x + r.width && pos.x + PLAYER_SIZE > r.x && pos.y < r.y + r.height && pos.y + PLAYER_SIZE > r.y)
		{
			if (prevplayer.y <= r.y - PLAYER_SIZE) //floor collision
			{
				pos.y = r.y - PLAYER_SIZE;
				velocity.y = 0;
			}
			else if (prevplayer.x <= r.x - PLAYER_SIZE) //left-side collision
			{
				//tmp = 1;
				pos.x = r.x - PLAYER_SIZE;
				velocity.x = 0;
			}
			else if (prevplayer.x >= r.x + r.width) //right-side collision
			{
				//tmp = -1;
				pos.x = r.x + r.width;
				velocity.x = 0;
			}
			else if (prevplayer.y >= r.y + r.height) //ceiling collision
			{
				pos.y = r.y + r.height;
				velocity.y = 0;
			}
		}
	}
	
	public void updateplayer(HashSet<Integer> keyspressed, int[][] walls)
	{
		for (Integer code : keyspressed) //keys pressed
		{
			if (code == KeyEvent.VK_W)
			{
				velocity.y -= 1;
			}
			if (code == KeyEvent.VK_S)
			{
				velocity.y += 1;
			}
			else if (code == KeyEvent.VK_A)
			{
				velocity.x -= 1;
			}
			else if (code == KeyEvent.VK_D)
			{
				velocity.x += 1;
			}
		}
		
		pos.y += velocity.y;
		pos.x += velocity.x;
		
		//collision detection
		//tmp = 0;
		for (int i = Math.max(0, (int)((pos.y+velocity.y)/GameState.constanty)); i < Math.min((int)((pos.y+PLAYER_SIZE+velocity.y)/GameState.constanty) + 1, GameState.GRIDS_Y); i ++)
		{
			for (int j = Math.max(0, (int)((pos.x+velocity.x)/GameState.constantx)); j < Math.min((int)((pos.x+PLAYER_SIZE+velocity.x)/GameState.constantx) + 1, GameState.GRIDS_X); j ++)
			{
				if (walls[i][j] != 0)
				{
					Rectangle r = new Rectangle(j * GameState.constantx, i * GameState.constanty, GameState.constantx, GameState.constanty);
					if (j != GameState.GRIDS_X - 1 && walls[i][j + 1] != 0) //necessary for collision robustness
						r.width *= 2;
					detectCollision(r);
				}
			}
		}
		
		velocity.x *= FRICTION;
		velocity.y *= FRICTION;
		
		//keep track of prev position of player for collision logic
		prevplayer.x = pos.x;
		prevplayer.y = pos.y;
	}

}
