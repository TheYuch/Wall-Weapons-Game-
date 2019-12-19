package wallweapons;

import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;

import wallweapons.Enemies.Regular;
import wallweapons.Weapons.Shield;

public class GameState extends KeyAdapter {
	static final int BLOCKS_ALLOWED = 7;
	public static final int GRIDS_X = 25;
	public static final int GRIDS_Y = 15;
	static final int GRID_THICKNESS = 2;
	static final int PLAYER_SIZE = Main.WIN_WIDTH / GRIDS_X - 5;
	static final int WALL_HEALTH = 50;
	
	public static final int constantx = Main.WIN_WIDTH / GRIDS_X;
	public static final int constanty = Main.WIN_HEIGHT / GRIDS_Y;
	
	static final int CORE_X = Main.WIN_WIDTH / (2 * constantx);
	static final int CORE_Y = Main.WIN_HEIGHT / (2 * constanty);

	public Point2D.Double player;
	private Point2D.Double prevplayer;
	private Point2D.Double velocity;
	private double FRICTION = 0.8;
	
	private int blockcnt = 0; //used for spawning better enemies
	
	public int tmp = 0;
	
	//MAKE LISTS FOR EACH CLASS OF WEAPONS, THEN IMPLEMENT IN CHECKWALLS() FUNCTION
	public ArrayList<Enemy> enemies;
	public ArrayList<Shield> shields;
	
	HashSet<Integer> keyspressed; //stores keycodes
	
	public int[][] walls; //2D array representing all walls' health

	public GameState() {

		keyspressed = new HashSet<Integer>();
		player = new Point2D.Double(Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2), Main.WIN_HEIGHT / 2 - ((PLAYER_SIZE * 2) + constantx - PLAYER_SIZE + 1));
		prevplayer = new Point2D.Double(Main.WIN_WIDTH / 2 - (PLAYER_SIZE / 2), Main.WIN_HEIGHT / 2 - (PLAYER_SIZE / 2));
		velocity = new Point2D.Double(0, 0);
		walls = new int[GRIDS_Y][GRIDS_X]; //Y = I, J = X!!!!!!!!!!!!!!!!!!!!
		walls[CORE_Y][CORE_X] = -1;
		
		shields = new ArrayList<Shield>();
		enemies = new ArrayList<Enemy>();
	}

	private void addremove(int[][] obstacles, int x, int y)
	{
		if (x >= 0 && x < GRIDS_X && y >= 0 && y < GRIDS_Y)
		{
			if (obstacles[y][x] > 0)
			{
				obstacles[y][x] = 0;
				blockcnt--;

				checkwalls(x, y);
			}
			else if (obstacles[y][x] == 0)
			{
				obstacles[y][x] = WALL_HEALTH;
				blockcnt++;

				checkwalls(x, y);
			}
		}
	}
	
	private void detectCollision(Rectangle r)
	{
		if (player.x < r.x + r.width && player.x + PLAYER_SIZE > r.x && player.y < r.y + r.height && player.y + PLAYER_SIZE > r.y)
		{
			if (prevplayer.y <= r.y - PLAYER_SIZE) //floor collision
			{
				player.y = r.y - PLAYER_SIZE;
				velocity.y = 0;
			}
			else if (prevplayer.y >= r.y + r.height) //ceiling collision
			{
				player.y = r.y + r.height;
				velocity.y = 0;
			}
			else if (prevplayer.x <= r.x - PLAYER_SIZE) //left-side collision
			{
				tmp = 1;
				player.x = r.x - PLAYER_SIZE - 1;
				velocity.x = 0;
			}
			else if (prevplayer.x >= r.x + r.width) //right-side collision
			{
				tmp = -1;
				player.x = r.x + r.width + 1;
				velocity.x = 0;
			}
		}
	}
	
	private void updateplayer()
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
		
		player.y += velocity.y;
		player.x += velocity.x;
		
		//collision detection
		tmp = 0;
		for (int i = Math.max(0, (int)((player.y+velocity.y)/constanty)); i < Math.min((int)((player.y+PLAYER_SIZE+velocity.y)/constanty) + 1, GRIDS_Y); i ++)
		{
			for (int j = Math.max(0, (int)((player.x+velocity.x)/constantx)); j < Math.min((int)((player.x+PLAYER_SIZE+velocity.x)/constantx) + 1, GRIDS_X); j ++)
			{
				if (walls[i][j] != 0)
				{
					Rectangle r = new Rectangle(j * constantx, i * constanty, constantx, constanty);
					detectCollision(r);
				}
			}
		}
		
		velocity.x *= FRICTION;
		velocity.y *= FRICTION;
		
		//keep track of prev position of player for collision logic
		prevplayer.x = player.x;
		prevplayer.y = player.y;
	}
	
	public void update() {
		updateplayer();
	}
	
	public int getxonboard (double x) //gets x on board using actual x coordinate
	{
		return (int)(x / constantx);
	}
	
	public int getyonboard (double y)
	{
		return (int)(y / constanty);
	}
	
	private boolean matchShape(int[][] shape, int[][] walls, int x, int y)
	{
		if (y + shape.length >= walls.length || x + shape[0].length >= walls[0].length) //if not fit
			return false;
		for (int i = 0; i < shape.length; i ++)
		{
			for (int j = 0; j < shape[i].length; j ++)
			{
				if (shape[i][j] == 0)
					continue;
				else if ((shape[i][j] == -1 && walls[i + y][j + x] != 0) || (shape[i][j] == 1 && walls[i + y][j + x] <= 0))
					return false;
			}
		}
		return true;
	}
	
	private void checkwalls(int x, int y) //checks all walls for any weapons, then intializes their classes
	{
		//only check space around placed wall (defined by parameters x and y)
		//when checking, destroy all weapons within range, and enable if it matches.
		//shields
		int ystart = Math.max(0, y - 3);
		int yend = Math.min(GRIDS_Y, y + 1);
		int xstart = Math.max(0, x - 3);
		int xend = Math.min(GRIDS_X - 1, x + 1);
		for (int i = 0; i < shields.size(); i ++)
		{
			int curx = shields.get(i).pos.x;
			int cury = shields.get(i).pos.y;
			if (cury >= ystart && cury < yend && curx >= xstart && curx < xend)
			{
				shields.remove(i);
				i--;
			}
		}
		for (int i = ystart; i < yend; i ++)
		{
			for (int j = xstart; j < xend; j ++)
			{
				for (int rotation = 0; rotation < 360; rotation += 90)
				{
					//shield
					if (matchShape(Main.rotateArray(Shield.shape, rotation), walls, j, i))
					{
						Shield toadd = new Shield(j, i, rotation);
						if (!shields.contains(toadd))
							shields.add(toadd);
					}
				}
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode();
		if (c == KeyEvent.VK_DOWN) //placing block mechanism
		{
			addremove(walls, getxonboard(player.x + velocity.x + (PLAYER_SIZE / 2)), getyonboard(player.y + velocity.y + PLAYER_SIZE - 1) + 1);
		}
		else if (c == KeyEvent.VK_UP)
		{
			addremove(walls, getxonboard(player.x + velocity.x + (PLAYER_SIZE / 2)), getyonboard(player.y + velocity.y) - 1);
		}
		else if (c == KeyEvent.VK_RIGHT)
		{
			addremove(walls, getxonboard(player.x + velocity.x + PLAYER_SIZE - 1) + 1, getyonboard(player.y + velocity.y + (PLAYER_SIZE / 2)));
		}
		else if (c == KeyEvent.VK_LEFT)
		{
			addremove(walls, getxonboard(player.x + velocity.x) - 1, getyonboard(player.y + velocity.y + (PLAYER_SIZE / 2)));
		}
		else if (!keyspressed.contains(c))
			keyspressed.add(c);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		keyspressed.remove(e.getKeyCode());
	}
}