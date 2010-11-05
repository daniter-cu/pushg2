package push.g2;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

import org.apache.log4j.Logger;

public class Util {

	public static Logger log = Logger.getLogger("Util");
	
	public static Move getBestMove(int[][] board, Opponent op, Direction home)
	{
		//find all valid moves for this player
		ArrayList<Moves> moves = new ArrayList<Moves>();
		ArrayList<Direction> dirs = new ArrayList<Direction>();
		dirs.add(home.getLeft().getOpposite());
		dirs.add(home.getOpposite());
		dirs.add(home.getRight().getOpposite());
		Move m;
		
		//iterate through board
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[0].length; j++)
			{
				if(board[i][j] < 1)
					continue;
				//check if move is valid
				for(Direction d : dirs)
				{
					m = new Move(j,i,d);
					if(isValid(m,board,home))
					{
						//log.debug("VALID: " + m.getX() + "," + m.getY() + ": " + m.getDirection());
						moves.add(new Moves(m,worthOfAMove(board,op.oppCorner,m)));
						//if it is, get value, add to list	
					}
					else
					{
						//log.debug("INVALID: " + m.getX() + "," + m.getY() + ": " + m.getDirection());
					}
				}	
			}
		}
		
		//sort list
		log.debug("Play to go after = " + op.oppId);
		return getBest(moves, op.totalValue);
	}
	
	private static Move getBest(ArrayList<Moves> moves, double gold) 
	{
		/*Moves best = null;
		double minDist = Integer.MAX_VALUE;
		for(Moves m : moves)
		{
			//if(m.getVal()*gold < 0)
			//	continue;
			if(Math.abs(m.getVal() - gold) < minDist)
			{
				minDist = Math.abs(m.getVal() - gold);
				best = m;
			}
		}
		
		
		if(best != null)
		{
		//	log.debug("value of best move " + best.getVal());
			return best.getM();
		}
		
		return null;*/
		Moves best = null;
		double minDist = 0;
		if(gold > 0)
			minDist = -1000;
		else
			minDist = 1000;
		for(Moves m : moves)
		{
			log.debug(m.getVal());
			if(gold > 0)
			{
				if(m.getVal() > minDist)
				{
					minDist = m.getVal();
					best = m;
				}
			}
			else
			{
				if(m.getVal() < minDist)
				{
					minDist = m.getVal();
					best = m;
				}
			}
		}
		
		
		if(best != null)
		{
			log.debug("value of best move " + best.getVal());
			return best.getM();
		}
		
		return null;
	}

	//calculates the worth of any move made in the previous round for G2Player
	public static double worthOfAMove(int[][]board, Direction g2Corner, Move m)
    {
        double worth=0.0;
        double oldDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getX(),m.getY()));
        double newDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getNewX(),m.getNewY()));
        double coins= board[m.getNewY()][m.getNewX()];
        if(oldDistance==newDistance)
        {
        	
        }
        if(newDistance == 0)
        	newDistance = 0.1;
        worth = (coins)*((oldDistance/newDistance)-1.0);
        return worth;
    }
	
	private static boolean isValid(Move m, int[][] board, Direction myCorner)
	{
		return isSuccessByBoundsEtc(m, myCorner)
			&& isSuccessByCount(m, board)
			&& GameEngine.isValidDirectionForCellAndHome(m.getDirection(), myCorner);
	}
	
	private static boolean isSuccessByBoundsEtc(Move m, Direction home) {
		// Check that we are in bounds
		if (!GameEngine.isInBounds(m.getNewX(), m.getNewY()))
			return false;
		if (!GameEngine.isInBounds(m.getX(), m.getY()))
			return false;
		// Check that the direction is OK
		if (!m.getDirection().equals(home.getRelative(0))
				&& !m.getDirection()
						.equals(home.getRelative(-1))
				&& !m.getDirection()
						.equals(home.getRelative(1)))
			return false;
		return true;
	}
	
	private static boolean isSuccessByCount(Move m, int[][] board) {
		// Check that there are > 0 in this position
		if (board[m.getY()][m.getX()] == 0)
			return false;
		return true;
	}
	
	private static class Moves implements Comparable<Moves> {
		private Move m;
		private double val;
		
		public Move getM() {
			return m;
		}

		public double getVal() {
			return val;
		}

		
		public Moves(Move _m, double _val)
		{
			m = _m;
			val = _val;
		}

		@Override
		public int compareTo(Moves o) {
			if(Math.abs(val) > Math.abs(o.getVal()))
				return 1;
			if(Math.abs(o.getVal()) > Math.abs(val))
				return -1;
			return 0;
		}
		
		
	}
}








