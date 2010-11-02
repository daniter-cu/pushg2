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
	
	public LinkedList<Double> history = new LinkedList<Double>();
	
	// negative value = defect opponent
	// 0 = neutral opponent
	// positive value = cooperative opponent
	public double totalValue = 0;
	
	public Opponent(int idNum, Direction myCorner)
	{
		g2Corner = myCorner;
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
	
	//calculates the worth of any move made in the previous round for G2Player
	public double worthOfAMove(Move m)
    {
        double worth=0.0;
        double oldDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getX(),m.getY()));
        double newDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getNewX(),m.getNewY()));
        double coins= board[m.getNewY()][m.getNewX()];
        worth = (coins)*((oldDistance-newDistance)-1.0);
        return worth;
    }
}
