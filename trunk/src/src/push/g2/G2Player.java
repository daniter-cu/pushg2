package push.g2;

import java.awt.Point;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.Queue;

import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class G2Player extends Player{
	
	private Logger log = Logger.getLogger(this.getClass());
	
	ArrayList<Opponent> opponents;
	
	int[][] board;
	Direction myCorner;
	int id;
	
	//Direction myOp;
	//private LinkedList<Point> queue;
	//int opId;
	//ArrayList<Direction> moves = new ArrayList<Direction>();
	
	public String getName()
	{
		return "G2Player";
	}
	
	// changes the current board state for G2 and the opponent objects
	public void updateBoardState(int[][] board)
	{
		this.board= board;
		
		for(Opponent o : opponents)
		{
			o.board = board;
		}
	}
	
	// initializes the new board game settings
	public void startNewGame(int id, int m, ArrayList<Direction> playerPositions) 
	{
		//create the list of opponents
		opponents = new ArrayList<Opponent>();
		for(int oppCount=0; oppCount<6; oppCount++)
		{
			if(oppCount != id)
			{
				opponents.add(new Opponent(oppCount, 
						playerPositions.get(oppCount), 
						playerPositions.get(oppCount)));
			}
		}
		this.id=id;
		myCorner=playerPositions.get(id);
		
//		myOp=myCorner.getOpposite();
//		queue = new LinkedList<Point>();
//		moves.add(myOp.getLeft());
//		moves.add(myOp.getRight());
//		moves.add(myOp.getOpposite());
	}

	public Move makeMove(List<MoveResult> previousMoves)
	{
//		return generateRandomMove(0);
//		queue.addLast(getStartPoint());
//		Move m = depthSearch();
//		queue.clear();
//		return m;
		
		try
		{
			// add every opponent's move to their respective history
			if(previousMoves.isEmpty())
			{
				return new Move(8, 4, myCorner.getOpposite());
			}
			
			// it's not the first move
			for(MoveResult mr : previousMoves)
			{
				for(int x=0; x<opponents.size(); x++)
				{
					if(mr.getPlayerId() == opponents.get(x).oppId)
					{
						Move m = mr.getMove();
						Opponent op = opponents.get(x);
						op.addToHistory(Util.worthOfAMove(board, myCorner, m));
						break;
					}
				}
			}
			
			Collections.sort(opponents);
			Collections.reverse(opponents);
			
			for(Opponent o : opponents)
			{
				log.error(o.oppId + " : " + o.totalValue);
			}
			
			//return the best move
			for(Opponent o : opponents)
			{
				Move ourMove = Util.getBestMove(board, o, myCorner); 
				if(ourMove != null)
				{
					//log.error(ourMove.getX() + "," + ourMove.getY() + ": " + ourMove.getDirection());
					return ourMove;
				}
			}
		}
		catch(Exception e)
		{
			java.io.StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
			log.error(sw.toString());
		}
		log.error("Printing default move");
		//no move is possible (or it's the first turn)
		return new Move(0,0, myCorner.getOpposite());
	}
	
	/*public Point getStartPoint()
	{
		return myOp.getHome();
	}*/
	
	/*public Move depthSearch()
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
	}*/
	
	/*public boolean isValid(Move m)
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
	}*/
	
	public Move generateRandomMove(int depth)
	{
		log.error("generating a rando");
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
