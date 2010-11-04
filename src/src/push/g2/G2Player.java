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
	}

	public Move makeMove(List<MoveResult> previousMoves)
	{
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
				log.debug(o.oppId + " : " + o.totalValue);
			}
			
			//return the best move
			for(Opponent o : opponents)
			{
				Move ourMove = Util.getBestMove(board, o, myCorner); 
				if(ourMove != null)
				{
					//log.debug(ourMove.getX() + "," + ourMove.getY() + ": " + ourMove.getDirection());
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
			log.debug(sw.toString());
		}
		log.debug("Printing default move");
		//no move is possible (or it's the first turn)
		return new Move(0,0, myCorner.getOpposite());
	}
	
	public Move generateRandomMove(int depth)
	{
		log.debug("generating a rando");
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
