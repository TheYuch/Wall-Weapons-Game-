package wallweapons.Weapons;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import wallweapons.GameState;

import java.awt.Color;

public class Shield {
	
	public static final int[][] shape = {{0, 1, 0}, {1, -1, 1}, {0, -1, 0}};
	//format: 0 = n/a, 1 = block there, -1 = no block there
	//NOTE THAT THESE SHAPES CAN BE ROTATED.
	static final int width = 3; //size of the shape
	static final int height = 2;
	public static final Color drawcolor = Color.RED; //for GamePanel
	public static final Color disabledcolor = Color.YELLOW; //when shield is overloaded by enemies
	
	public Rectangle collider;
	public boolean colliderenabled = true; //disabled when > 5 enemies 2039840384420981
	
	private int degrees;
	
	private int enemyCnt = 0; //DO AFTER MAKING ENEMY CLASSES 10280492384293843
	//store the classes of enemies that can pass through shield? 1290837829320938
	
	public Point pos; //x,y of top left corner of shield.
	
	public Shield(int x, int y, int degrees) //constructor
	{
		pos = new Point(x, y);
		this.degrees = degrees;
		int rectx = pos.x - 1;
		int recty = pos.y - 1;
		int rectwidth = 0;
		int rectheight = 0;
		if (degrees == 0 || degrees == 180)
		{
			rectwidth = width + 2;
			rectheight = height + 2;
		}
		else if (degrees == 90 || degrees == 270)
		{
			rectwidth = height + 2;
			rectheight = width + 2;
		}
		if (degrees == 90)
		{
			rectx = pos.x;
		}
		else if (degrees == 180)
		{
			recty = pos.y;
		}
	
		collider = new Rectangle(rectx * GameState.constantx, recty * GameState.constanty, rectwidth * GameState.constantx, rectheight * GameState.constanty);
	}
}
