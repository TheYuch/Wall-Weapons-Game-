package wallweapons;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Main {

	static GameState state = new GameState();
	static final int WIN_WIDTH = 1000; //must be multiple of 100 for grids to work
	static final int WIN_HEIGHT = 600;

	public static void main(String args[]) throws InterruptedException {
		JFrame frame = new JFrame("Wall Weapons");
		frame.addKeyListener(state);

		GamePanel game = new GamePanel();
		frame.add(game);

		frame.setPreferredSize(new Dimension(WIN_WIDTH + 6, WIN_HEIGHT + 29));
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
	
	public static int[][] rotateArray(int[][] arr, int degrees)
	{
		int width = arr.length;
		int height = arr[0].length;
		
		int[][] ret;
		if (degrees == 180)
			ret = new int[width][height];
		else
			ret = new int[height][width];
		
		if (degrees == 0)
			return arr;
		else if (degrees == 90)
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
}