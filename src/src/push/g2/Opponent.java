package push.g2;

import java.util.*;

public class Opponent
{
	public static final int HISTORY_MEMORY = 10;
	
	public int oppId = 0;
	public int[][] board;
	
	public LinkedList<Double> history = new LinkedList<Double>();
	
	// negative value = defect opponent
	// 0 = neutral opponent
	// positive value = cooperative opponent
	public double totalValue = 0;
	
	public Opponent(int idNum)
	{
		oppId = idNum;
		history = new LinkedList<Double>();
	}
	
	// adds a move to the value
	public void addToHistory(double val)
	{
		if(history.size() > HISTORY_MEMORY)
		{
			history.remove();
		}
		
		history.addLast(val);
	}
	
	//updates the player's total score
	public void updateValue(int oldDistance, int newDistance, int numCoins)
	{
		
	}
}
