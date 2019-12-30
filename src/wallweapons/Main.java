package wallweapons;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

public class Main {

	static GameState state = new GameState();
	public static final int WIN_WIDTH = 1000; //must be multiple of 100 for grids to work
	public static final int WIN_HEIGHT = 600;

	public static void main(String args[]) throws InterruptedException {
		JFrame frame = new JFrame("Wall Weapons");
		frame.addKeyListener(state);

		GamePanel game = new GamePanel();
		frame.add(game);

		frame.setPreferredSize(new Dimension(WIN_WIDTH + 6, WIN_HEIGHT + 28));
		frame.pack();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		frame.setVisible(true);

		while (true) {
			long startTime = System.currentTimeMillis();
			
			Main.state.update();
			frame.repaint();

			long elapsedTime = System.currentTimeMillis() - startTime;

			Thread.sleep(Math.max(0, 1000 / 30 - elapsedTime));
		}
	}
	
	public static Point2D.Double getVelocity(int speed, double pos1x, double pos1y, double pos2x, double pos2y)
	{
		double angle = Math.atan2((pos1x - pos2x), (pos1y - pos2y));
		if (Math.toDegrees(angle) == 90)
		{
			return new Point2D.Double(speed, 0);
		}
		else if (Math.toDegrees(angle) == -90)
		{
			return new Point2D.Double(-speed, 0);
		}
		else
		{
			return new Point2D.Double(Math.sin(angle) * speed, Math.cos(angle) * speed);
		}
	}
	
	public static int[][] rotateArray(int[][] arr, int degrees)
	{
		if (degrees == 0)
			return arr;
		
		int width = arr[0].length;
		int height = arr.length;
		
		int[][] ret;
		if (degrees == 180)
			ret = new int[height][width];
		else
			ret = new int[width][height];
		
		if (degrees == 90)
		{
			for (int i = 0; i < width; i ++) //i here is the x value
			{
				for (int j = height - 1; j >= 0; j --) //from bottom to up, y value
					ret[i][height - j - 1] = arr[j][i];
			}
		}
		else if (degrees == 180)
		{
			for (int i = height - 1; i >= 0; i --) //i is y value
			{
				for (int j = width - 1; j >= 0; j --)
					ret[height - i - 1][width - j - 1] = arr[i][j];
			}
		}
		else if (degrees == 270)
		{
			//i = y value, j = x value
			for (int j = width - 1; j >= 0; j --)
			{
				for (int i = 0; i < height; i ++)
					ret[width - j - 1][i] = arr[i][j];
			}
		}
		
		return ret;
	}
	
	public static void gameover() throws InterruptedException
	{
		Thread.sleep(1000);
		System.exit(0);
	}
}