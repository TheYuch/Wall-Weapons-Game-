package wallweapons;

import java.awt.Color;
import java.awt.Point;

public abstract class Weapon {
	
	public int[][] shape;
	public Color drawcolor;
	public Point pos;
	protected int delay;
	protected int nexttime;
	
	public Weapon(int[][] shape, Color drawcolor, Point pos, int delay) {
		this.shape = shape;
		this.drawcolor = drawcolor;
		this.pos = new Point();
		this.pos.x = pos.x;
		this.pos.y = pos.y;
		this.delay = delay;
		this.nexttime = delay;
		//is delay and ticks are both 0, then that means that it is constantly updating.
	}
	
	public abstract void update(int ticks); //ALWAYS SET NEXT TIME TO += DELAY.

}
