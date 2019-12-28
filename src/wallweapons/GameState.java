package wallweapons;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;

import wallweapons.Enemies.*;
import wallweapons.Weapons.*;

public class GameState extends KeyAdapter {
	static final int BLOCKS_ALLOWED = 7;
	public static final int GRIDS_X = 25;
	public static final int GRIDS_Y = 15;
	static final int GRID_THICKNESS = 2;
	static final int WALL_HEALTH = 50;
	
	public static final int constantx = Main.WIN_WIDTH / GRIDS_X; //CONSTANTX MUST EQUAL CONSTANTY
	public static final int constanty = Main.WIN_HEIGHT / GRIDS_Y;
	
	static final int CORE_X = Main.WIN_WIDTH / (2 * constantx);
	static final int CORE_Y = Main.WIN_HEIGHT / (2 * constanty);
	
	private static int blockcnt = 0; //used for spawning better enemies
	
	private int cooldown; //used for cool downs between doing damage to enemies (space)
	private int vulnerability = 0; //used for taking damage from enemies. when ticks > vulnerability, enemies touching player do damage.
	
	public static int ticks = 0; // TICK - 30 TICKS/SECOND - used to track time
	
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
	
	//player
	Player player;
	
	//enemies and weapons
	public static ArrayList<Enemy> enemies;
	public static ArrayList<Weapon> weapons;
	
	HashSet<Integer> keyspressed; //stores keycodes
	
	public static int[][] walls; //2D array representing all walls' health

	public GameState() {

		keyspressed = new HashSet<Integer>();
		walls = new int[GRIDS_Y][GRIDS_X]; //Y = I, J = X!!!!!!!!!!!!!!!!!!!!
		walls[CORE_Y][CORE_X] = -1;
		
		player = new Player();
		
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
	
	private void spawnenemies() 
	{
		if (ticks % 210 == 0) //every 3 seconds - change to go faster based on blockCnt 203948039844
		{
			enemies.add(new Regular(player.pos, CORE_X, CORE_Y)); //RANDOM ENEMY GENERATION - BASED ON BLOCKCNT - change later 2398403984
		}
	}
	
	public void update() {
		ticks++;
		spawnenemies();
		player.updateplayer(keyspressed, walls);
		if (ticks >= cooldown)
			Player.drawcolor = Color.BLUE;
		boolean changevulnerability = false;
		for (int i = 0; i < enemies.size(); i ++)
		{
			if (enemies.get(i).update(walls, player.pos, CORE_X, CORE_Y))
			{
				enemies.remove(i);
				i--; 
			}
			//BELOW IS FOR PLAYER TAKING DAMAGE FROM ENEMIES.
			else if (ticks > vulnerability && Math.abs(enemies.get(i).pos.x - player.pos.x) < constantx && Math.abs(enemies.get(i).pos.y - player.pos.y) < constanty)
			{
				player.health -= enemies.get(i).damagetoplayer;
				if (player.health <= 0)
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
		for (int i = 0; i < weapons.size(); i ++) //update weapons
		{
			weapons.get(i).update(ticks);
		}
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
	
	private static void checkwalls(int x, int y) //checks all walls for any weapons, then intializes their classes
	{
		//only check space around placed wall (defined by parameters x and y)
		//when checking, destroy all weapons within range, and enable if it matches.
		
		/*
		 * BUG! WHEN YOU PLACE A BLOCK UNDER/RIGHT OF A WEAPON, IT DELETES IT AND SPAWNS THE WEAPON,
		 * CAUSING IT TO UPDATE (I.E. CONTINUALLY PLACING BLOCK UNDER LASER CAUSES LASER TO ALWAYS BE
		 * ON). EITHER ONLY DELETE WEAPONS TOUCHING BLOCK BEING CHANGED (WHICH IS HARD AND PROB NOT
		 * GOING TO BE DONE) OR ******** KEEP A LIST OF NEXTTIMES OF EACH WEAPON... 239489343094830984
		 */
		
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
					//MAKE IT ONLY CHECK FOR CERTAIN SHAPES BASED ON THEIR SIZES 023984093843094
					//DOESN'T WORK - IF SHIELD IN TOP LEFT CORNER AND PLACE A BLOCK NOT TOUCHING
					//THAT SHIELD, IT DESTROYS IT (IN LOOP ABOVE) AND DOES NOT FIND IT AGAIN. SO
					//MAYBE DO THIS AND ALSO DESTROY WEAPONS BASED ON THEIR SIZES 10923840398409
					//Shield
					if (matchShape(Main.rotateArray(Shield.getShape(), rotation), j, i))
					{
						weapons.add(new Shield(j, i, rotation));
					}
					//Laser
					if (rotation < 180 && matchShape(Main.rotateArray(Laser.getShape(), rotation), j, i))
					{
						weapons.add(new Laser(j, i, rotation));
					}
					//Multigun
					if (matchShape(Main.rotateArray(Multigun.getShape(), rotation), j, i))
					{
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
					weapons.add(new Radiator(j, i));
				}
				//Missile
				if (matchShape(Missile.getShape(), j, i))
				{
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
			addremove(getxonboard(player.pos.x + (Player.PLAYER_SIZE / 2)), getyonboard(player.pos.y + Player.PLAYER_SIZE - 1) + 1);
		}
		else if (c == KeyEvent.VK_UP)
		{
			addremove(getxonboard(player.pos.x + (Player.PLAYER_SIZE / 2)), getyonboard(player.pos.y) - 1);
		}
		else if (c == KeyEvent.VK_RIGHT)
		{
			addremove(getxonboard(player.pos.x + Player.PLAYER_SIZE - 1) + 1, getyonboard(player.pos.y + (Player.PLAYER_SIZE / 2)));
		}
		else if (c == KeyEvent.VK_LEFT)
		{
			addremove(getxonboard(player.pos.x ) - 1, getyonboard(player.pos.y + (Player.PLAYER_SIZE / 2)));
		}
		else if (c == KeyEvent.VK_SPACE)
		{
			if (ticks >= cooldown)
			{
				Player.drawcolor = Color.YELLOW;
				player.damageEnemies();
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