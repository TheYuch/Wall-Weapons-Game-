package wallweapons;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import wallweapons.Enemies.*;
import wallweapons.Weapons.*;

public class GameState extends KeyAdapter {
	static final int BLOCKS_ALLOWED = 7;
	public static final int GRIDS_X = 25;
	public static final int GRIDS_Y = 15;
	static final int GRID_THICKNESS = 2;
	static final int WALL_HEALTH = 50;
	
	public static final int constantx = Main.WIN_WIDTH / GRIDS_X; //CONSTANTX MUST EQUAL CONSTANTY
	public static final int constanty = Main.WIN_HEIGHT / GRIDS_Y; //represents grid dimensions
	
	public static final int CORE_X = Main.WIN_WIDTH / (2 * constantx);
	public static final int CORE_Y = Main.WIN_HEIGHT / (2 * constanty);
	
	private static int blockcnt = 0; //used for spawning better enemies
	
	private int cooldown; //used for cool downs between doing damage to enemies (space)
	private int vulnerability = 0; //used for taking damage from enemies. when ticks > vulnerability, enemies touching player do damage.
	
	public static int ticks = 0; // TICK - 30 TICKS/SECOND - used to track time
	
	public static int score = 0;
	
	/*
	 * 
	 * 
	 * MAKE COLORS MORE ELEGANT, USING NEW COLOR(R, G, B) AND IMPORT IMAGES FOR BETTER GRAPHICS!!!
	 * 
	 * MAKE IT SO YOU HAVE TO KILL ENEMIES TO GAIN MATERIALS TO BUILD WALLS?!!?!??!?!?!!!?
	 * 203984039849384
	 * 
	 * 
	 */
	
	//player is already static. Reference it by doing Player.soijdfoi
	
	//enemies and weapons
	public static ArrayList<Enemy> enemies;
	public static ArrayList<Weapon> weapons;
	
	HashSet<Integer> keyspressed; //stores keycodes
	
	public static int[][] walls; //2D array representing all walls' health

	public GameState() {

		keyspressed = new HashSet<Integer>();
		walls = new int[GRIDS_Y][GRIDS_X]; //Y = I, J = X!!!!!!!!!!!!!!!!!!!!
		walls[CORE_Y][CORE_X] = -1;
		
		weapons = new ArrayList<Weapon>();
		enemies = new ArrayList<Enemy>();
	}

	public static void damagewalls(int i, int j, int damage)
	{
		walls[i][j] -= damage;
		if (walls[i][j] <= 0)
		{
			blockcnt -= 1;
			walls[i][j] = 0;
			checkwalls(j, i);
		}
	}
	
	private void addremove(int x, int y)
	{
		if (x >= 0 && x < GRIDS_X && y >= 0 && y < GRIDS_Y)
		{
			if (walls[y][x] > 0)
			{
				walls[y][x] = 0;
				blockcnt--;

				checkwalls(x, y);
			}
			else if (walls[y][x] == 0)
			{
				walls[y][x] = WALL_HEALTH;
				blockcnt++;

				checkwalls(x, y);
			}
		}
	}
	
	private void spawnenemies() //TICKS determines which enemies to spawn. BLOCKCNT determines speed of spawning.
	{
		if (ticks % (4000 / (blockcnt + 20)) == 0)
		{
			Random random = new Random();
			int enemy;
			if (ticks >= 2700) //1 minute 30 secs (2700)
				enemy = random.nextInt(6);
			else if (ticks >= 1800) //1 minute (1800)
				enemy = random.nextInt(5);
			else if (ticks >= 1350) //45 seconds (1350)
				enemy = random.nextInt(4);
			else if (ticks >= 900) //30 seconds (900)
				enemy = random.nextInt(3);
			else if (ticks >= 450) //15 seconds (450)
				enemy = random.nextInt(2);
			else
				enemy = random.nextInt(1);
			switch (enemy)
			{
			case 0:
				enemies.add(new Regular(Player.pos, CORE_X, CORE_Y));
				break;
			case 1:
				enemies.add(new Hyper(Player.pos, CORE_X, CORE_Y));
				break;
			case 2:
				enemies.add(new Scout(Player.pos, CORE_X, CORE_Y));
				break;
			case 3:
				enemies.add(new Jumper());
				break;
			case 4:
				enemies.add(new Demolisher(Player.pos, CORE_X, CORE_Y));
				break;
			case 5:
				enemies.add(new Bomber(Player.pos, CORE_X, CORE_Y));
				break;
			}
		}
	}
	
	public void update() {
		ticks++;
		spawnenemies();
		Player.updateplayer(keyspressed, walls);
		if (ticks >= cooldown)
			Player.drawcolor = Color.BLUE;
		for (int i = 0; i < weapons.size(); i ++) //update weapons
		{
			weapons.get(i).update(ticks);
		}
		boolean changevulnerability = false;
		for (int i = 0; i < enemies.size(); i ++)
		{
			if (enemies.get(i).update(Player.pos, CORE_X, CORE_Y))
			{
				score += enemies.get(i).scoreval;
				enemies.remove(i);
				i--; 
			}
			//BELOW IS FOR PLAYER TAKING DAMAGE FROM ENEMIES.
			else if (ticks > vulnerability && Math.abs(enemies.get(i).pos.x - Player.pos.x) < constantx && Math.abs(enemies.get(i).pos.y - Player.pos.y) < constanty)
			{
				Player.health -= enemies.get(i).damagetoplayer;
				if (Player.health <= 0)
				{
					try {
						Main.gameover();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				else
				{
					changevulnerability = true; //THIS WAY MULTIPLE ENEMIES CAN DEAL DAMAGE SIMULTANEOUSLY
				}
			}
		}
		if (changevulnerability)
			vulnerability = ticks + 30;
	}
	
	public static int getxonboard (double x) //gets x on board using actual x coordinate
	{
		return (int)(x / constantx);
	}
	
	public static int getyonboard (double y)
	{
		return (int)(y / constanty);
	}
	
	private static boolean matchShape(int[][] shape, int x, int y)
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
	
	public static void checkwalls(int x, int y) //checks all walls for any weapons, then intializes their classes
	{
		//only check space around placed wall (defined by parameters x and y)
		//when checking, destroy all weapons within range, and enable if it matches.
		
		ArrayList<Weapon> tmpweapons = new ArrayList<Weapon>();
		
		int ystart = Math.max(0, y - 3);
		int yend = Math.min(GRIDS_Y, y + 1);
		int xstart = Math.max(0, x - 3);
		int xend = Math.min(GRIDS_X - 1, x + 1);
		for (int i = 0; i < weapons.size(); i ++)
		{
			int curx = weapons.get(i).pos.x;
			int cury = weapons.get(i).pos.y;
			if (cury >= ystart && cury < yend && curx >= xstart && curx < xend)
			{
				if (!(weapons.get(i) instanceof Shield) && !(weapons.get(i) instanceof Magnet))
					tmpweapons.add(weapons.get(i));
				weapons.remove(i);
				i--;
			}
		}
		for (int i = ystart; i < yend; i ++)
		{
			for (int j = xstart; j < xend; j ++)
			{
				for (int rotation = 0; rotation < 360; rotation += 90)
				{
					//Shield
					if (matchShape(Main.rotateArray(Shield.getShape(), rotation), j, i))
					{
						weapons.add(new Shield(j, i, rotation));
					}
					//Laser
					if (rotation < 180 && matchShape(Main.rotateArray(Laser.getShape(), rotation), j, i))
					{
						boolean added = false;
						for (Weapon weapon: tmpweapons)
						{
							if (weapon.pos.x == j && weapon.pos.y == i && weapon.degrees == rotation)
							{
								weapons.add(weapon);
								added = true;
							}
						}
						if (!added)
							weapons.add(new Laser(j, i, rotation));
					}
					//Multigun
					if (matchShape(Main.rotateArray(Multigun.getShape(), rotation), j, i))
					{
						boolean added = false;
						for (Weapon weapon: tmpweapons)
						{
							if (weapon.pos.x == j && weapon.pos.y == i && weapon.degrees == rotation)
							{
								weapons.add(weapon);
								added = true;
							}
						}
						if (!added)
							weapons.add(new Multigun(j, i, rotation));
					}
				}
				//NO NEED FOR ROTATION (THEY ARE SYMMETRICAL)
				//Magnet
				if (matchShape(Magnet.getShape(), j, i))
				{
					weapons.add(new Magnet(j, i));
				}
				//Radiator
				if (matchShape(Radiator.getShape(), j, i))
				{
					boolean added = false;
					for (Weapon weapon: tmpweapons)
					{
						if (weapon.pos.x == j && weapon.pos.y == i)
						{
							weapons.add(weapon);
							added = true;
						}
					}
					if (!added)
						weapons.add(new Radiator(j, i));
				}
				//Missile
				if (matchShape(Missile.getShape(), j, i))
				{
					boolean added = false;
					for (Weapon weapon: tmpweapons)
					{
						if (weapon.pos.x == j && weapon.pos.y == i)
						{
							weapons.add(weapon);
							added = true;
						}
					}
					if (!added)
						weapons.add(new Missile(j, i));
				}
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode();
		if (c == KeyEvent.VK_DOWN) //placing block mechanism
		{
			addremove(getxonboard(Player.pos.x + (Player.PLAYER_SIZE / 2)), getyonboard(Player.pos.y + Player.PLAYER_SIZE - 1) + 1);
		}
		else if (c == KeyEvent.VK_UP)
		{
			addremove(getxonboard(Player.pos.x + (Player.PLAYER_SIZE / 2)), getyonboard(Player.pos.y) - 1);
		}
		else if (c == KeyEvent.VK_RIGHT)
		{
			addremove(getxonboard(Player.pos.x + Player.PLAYER_SIZE - 1) + 1, getyonboard(Player.pos.y + (Player.PLAYER_SIZE / 2)));
		}
		else if (c == KeyEvent.VK_LEFT)
		{
			addremove(getxonboard(Player.pos.x ) - 1, getyonboard(Player.pos.y + (Player.PLAYER_SIZE / 2)));
		}
		else if (c == KeyEvent.VK_SPACE)
		{
			if (ticks >= cooldown)
			{
				Player.drawcolor = Color.YELLOW;
				Player.damageEnemies();
				cooldown = ticks + 30;
			}
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