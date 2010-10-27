package push.g2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Queue;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class G2Player extends Player{
	
	int[][] board;
	Direction myCorner;
	Direction myOp;
	int id;
	private LinkedList<Point> queue;
	int opId;
	ArrayList<Direction> moves = new ArrayList<Direction>();
	
	public void updateBoardState(int[][] board)
	{
		this.board= board;
	}
	
	public String getName()
	{
		return "G2Player";
	}
	
	public void startNewGame(int id, int m, ArrayList<Direction> playerPositions) 
	{
		myCorner=playerPositions.get(id);
		myOp=myCorner.getOpposite();
		this.id=id;
		queue = new LinkedList<Point>();
		
		moves.add(myOp.getLeft());
		moves.add(myOp.getRight());
		moves.add(myOp.getOpposite());
	}

	public Move makeMove(List<MoveResult> previousMoves)
	{
		//return generateRandomMove(0);
		queue.addLast(getStartPoint());
		Move m = depthSearch();
		queue.clear();
		return m;
	}
	
	
	
	public Move depthSearch()
	{
		if(queue.isEmpty())
			return(new Move(0,0, myCorner.getOpposite()));
		
		Point start = queue.removeFirst();

		Move m;
		for(int i = 0; i< 3; i++)
		{
			m = new Move(start.x, start.y, moves.get(i));
			if(isValid(m))
			{
				return m;
			}
		}
		
		for(int i = 0; i < 3; i++)
		{
			queue.addLast(new Point(start.x+moves.get(i).getDx(), start.y+moves.get(i).getDy()));
		}
		
		return depthSearch();
	}
	
	public boolean isValid(Move m)
	{
		if(m.getY()<0 || m.getY()>8 || m.getX()<0 || m.getX()>16)
			return false;

		System.err.println("board top: " + board.length);
		System.err.println("board left: " + board[0].length);
		
		System.err.println("left move: " + m.getY());
		System.err.println("top  move: " + m.getX());
		System.err.println("dir: " + m.getDirection().name());
		
		return board[m.getY()][m.getX()]>0 && 
			GameEngine.isValidDirectionForCellAndHome(m.getDirection(), myCorner) &&
			GameEngine.isInBounds(m.getX()+m.getDirection().getDx(), m.getY()+m.getDirection().getDy());
	}
	
	public Point getStartPoint()
	{
		return myOp.getHome();
	}
	
	public Move generateRandomMove(int depth)
	{
		if(depth > 300)
		{
			return new Move(0,0,Direction.NE);
		}
		int n2 = GameConfig.random.nextInt(9);
		int length = n2;
		if(length > 4)
			length=8-length;
		int offset = 4-length;
		length+=5;
		int n1 = GameConfig.random.nextInt(length);
		n1*=2;
		n1 += offset;
		if(!GameEngine.isInBounds(n1, n2))
			return generateRandomMove(depth+1);
		
		if(board != null&& board[n2][n1] == 0)
			return generateRandomMove(depth+1);
		Direction d = myCorner.getRelative(GameConfig.random.nextInt(3)-1);
		int tries = 0;
		while(!GameEngine.isValidDirectionForCellAndHome(d, myCorner) && tries < 10)
		{
			d = myCorner.getRelative(GameConfig.random.nextInt(2)-1);
			
			tries++;
		}
		if(!GameEngine.isValidDirectionForCellAndHome(d, myCorner))
			return generateRandomMove(depth+1);
		
		if(!GameEngine.isInBounds(n1+d.getDx(), n2+d.getDy()))
			return generateRandomMove(depth+1);
		
		Move m = new Move(n1, n2,d);
		return m;
	}
}
