package push.g2;

import java.util.LinkedList;
import java.awt.Point;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;
import push.sim.Player.Direction;

public class Opponent
{
	public static final int HISTORY_MEMORY = 10;
	
	public int oppId = 0;
	public int[][] board;
	public Direction g2Corner;
	public Direction oppCorner;
	
	public LinkedList<Double> valHistory;
	
	// negative value = defect opponent
	// 0 = neutral opponent
	// positive value = cooperative opponent
	public double totalValue = 0.0;
	
	public Opponent(int idNum, Direction myCorner, Direction opposingCorner)
	{
		g2Corner = myCorner;
		oppId = idNum;
		oppCorner = opposingCorner;
		
		valHistory = new LinkedList<Double>();
	}
	
	// adds the player's most recent move to the historical stack
	public void addToHistory(double val)
	{
		if(valHistory.size() > HISTORY_MEMORY)
		{
			valHistory.remove();
		}
		
		valHistory.addLast(val);
	}
	
	// gets the opponent's total average "value"
	public double getHistoricalValAverage()
	{
		double avgVal = 0.0;
		double count = 0.0; 
		
		for(Double val : valHistory)
		{
			avgVal += val;
			count++;
		}
		
		return avgVal/count;
	}
}







