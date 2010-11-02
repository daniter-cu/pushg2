package push.g2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.Player.Direction;

public class Util {

	public static Move getBestMove(int[][] board, Opponent op, Direction home)
	{
		//find all valid moves for this player
		ArrayList<Moves> moves = new ArrayList<Moves>();
		ArrayList<Direction> dirs = new ArrayList<Direction>();
		dirs.add(home.getLeft());
		dirs.add(home.getOpposite());
		dirs.add(home.getRight());
		Move m;
		//iterate through board
		for(int i = 0; i < board.length; i++)
		{
			for(int j = 0; j < board[0].length; i++)
			{
				if(board[i][j] == 0)
					continue;
				//check if move is valid
				for(Direction d : dirs)
				{
					m = new Move(i,j,d);
					if(isValid(m,board,home))
					{
						moves.add(new Moves(m,worthOfAMove(board,op.oppCorner,m)));
						//if it is, get value, add to list	
					}
				}	
			}
		}
		
		//sort list
		return getBest(moves, op.totalValue);
	}
	
	private static Move getBest(ArrayList<Moves> moves, double gold) 
	{
		Moves best = null;
		double minDist = Integer.MAX_VALUE;
		for(Moves m : moves)
		{
			if(m.getVal()*gold < 0)
				continue;
			if(Math.abs(m.getVal() - gold) < minDist)
			{
				minDist = Math.abs(m.getVal() - gold);
				best = m;
			}
		}
		return best.getM();
	}

	//calculates the worth of any move made in the previous round for G2Player
	public static double worthOfAMove(int[][]board, Direction g2Corner, Move m)
    {
        double worth=0.0;
        double oldDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getX(),m.getY()));
        int newDistance=GameEngine.getDistance(g2Corner.getHome(), new Point(m.getNewX(),m.getNewY()));
        int coins= board[m.getNewY()][m.getNewX()];
        worth = (coins)*((oldDistance-newDistance)-1);
        return worth;
    }
	
	private static boolean isValid(Move m, int[][] board, Direction myCorner)
	{
		if(m.getY()<0 || m.getY()>8 || m.getX()<0 || m.getX()>16)
			return false;

		//System.err.println("board top: " + board.length);
		//System.err.println("board left: " + board[0].length);
		
		//System.err.println("left move: " + m.getY());
		//System.err.println("top  move: " + m.getX());
		//System.err.println("dir: " + m.getDirection().name());
		
		return board[m.getY()][m.getX()]>0 && 
			GameEngine.isValidDirectionForCellAndHome(m.getDirection(), myCorner) &&
			GameEngine.isInBounds(m.getX()+m.getDirection().getDx(), m.getY()+m.getDirection().getDy());
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








