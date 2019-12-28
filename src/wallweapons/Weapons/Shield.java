package wallweapons.Weapons;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import wallweapons.GameState;
import wallweapons.Weapon;

public class Shield extends Weapon {
	
	//format for shapes: 0 = n/a, 1 = block there, -1 = no block there
	//NOTE THAT THESE SHAPES CAN BE ROTATED.
	//colors for GamePanel
	
	//collider disabled when > 5 enemies 2039840384420981
	public Rectangle collider;
	public boolean colliderenabled = true;
	public static final Color disabledcolor = Color.ORANGE; //when shield is overloaded by enemies
	
	private int enemyCnt = 0;
	private ArrayList<Integer> collidingenemies;
	
	public boolean canpass(int id)
	{
		if (enemyCnt == 3) //CHANGE THIS NUMBER IF NECESSARY
		{
			colliderenabled = false;
			return true;
		}
		if (collidingenemies.contains(id))
			return false; //if enemy is already touching
		else
			collidingenemies.add(id);
		enemyCnt ++;
		return false;
	}
	
	public static int[][] getShape()
	{
		int[][] shape = {{0, 1, 0}, {1, -1, 1}, {0, -1, 0}};
		return shape;
	}
	
	public Shield(int x, int y, int degrees) //constructor
	{
		super(getShape(), Color.RED, new Point(x, y), 0);
		
		collidingenemies = new ArrayList<Integer>();
		
		int rectx = super.pos.x - 1;
		int recty = super.pos.y - 1;
		int rectwidth = 0;
		int rectheight = 0;
		
		if (degrees == 0 || degrees == 180)
		{
			rectwidth = 5;
			rectheight = 4;
		}
		else if (degrees == 90 || degrees == 270)
		{
			rectwidth = 4;
			rectheight = 5;
		}
		if (degrees == 90)
		{
			rectx = super.pos.x;
		}
		else if (degrees == 180)
		{
			recty = super.pos.y;
		}
	
		collider = new Rectangle(rectx * GameState.constantx, recty * GameState.constanty, rectwidth * GameState.constantx, rectheight * GameState.constanty);
	}

	@Override
	public void update(int ticks) {
		
	}
}
