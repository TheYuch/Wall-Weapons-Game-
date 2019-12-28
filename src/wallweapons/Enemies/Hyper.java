package wallweapons.Enemies;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import wallweapons.Enemy;

public class Hyper extends Enemy {

	public Hyper(Point2D.Double playerpos, int corex, int corey) {
		super();
		super.setvelocity(playerpos, corex, corey);
	}

	@Override
	protected void move(int[][] walls, Double player, int corex, int corey) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean update(int[][] walls, Double player, int corex, int corey) {
		// TODO Auto-generated method stub
		return false;
	}

}
