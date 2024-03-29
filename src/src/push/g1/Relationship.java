package push.g1;
import java.util.ArrayList;

import push.sim.*;

/*Models the relationship between our player and other players. 
 * It keeps track of aggressive and cooperative moves: moves 
 * that directly hurt or help our score. 
 * */ 
public class Relationship {
	
	private PlayerBoard ourBoard;
	private int otherPlayerID;
	private ArrayList<MoveResult> moves; 
	private double cooperationScore = 0;
	
	public Relationship(int otherPlayerID, PlayerBoard ourBoard)
	{
		this.otherPlayerID = otherPlayerID;
		this.ourBoard = ourBoard;
		moves = new ArrayList<MoveResult>();
	}
	
	public ArrayList<MoveResult> getMoves()
	{
		return moves;
	}
	
	public void setMoves(ArrayList<MoveResult> moves)
	{
		this.moves = moves;
	}
	
	public void addMove(MoveResult move)
	{
		moves.add(move);
		findCooperationScore();
	}
	
	public double getCooperationScore()
	{
		return cooperationScore;
	}
	
	public int getPlayerID()
	{
		return otherPlayerID;
	}
	
	public void setPlayerID(int otherPlayerID)
	{
		this.otherPlayerID = otherPlayerID;
	}
	
	private void findCooperationScore()
	{
		Move m; 
		double cooperationScore = 0;
		
		for (int i = 0; i < moves.size(); i++)
		{
			MoveResult mr = moves.get(i);
			
			if (mr.isSuccess())
			{
				m = mr.getMove();
				cooperationScore += findMoveCooperation(m);
			}
		}
		
		/*Normalize the cooperation score, which gives newer moves a higher weight.*/
		cooperationScore /= moves.size();
	}
	
	/*To Do: Take into account the size of the stack of coins that was moved*/
	public int findMoveCooperation(Move m)
	{
		/*If the move did not start or end on one of our hexagons, return 0*/
		
		Hexagon startHex = ourBoard.getHexAtPoint(m.getX(), m.getY());
		Hexagon endHex = ourBoard.getHexAtPoint(m.getNewX(), m.getNewY());
		
		if (!ourBoard.isHexagonOurs(startHex) && !ourBoard.isHexagonOurs(endHex))
		{
			return 0;
		}
		/*If this move moved a piece off of our hexagons, return -1*/ 
		else if (ourBoard.isHexagonOurs(startHex) && !ourBoard.isHexagonOurs(endHex))
		{
			return -1;
		}
		/*If this move moved a piece to a lower valued hexagon from our collection of hexagons, return a negative score*/
		else if (startHex.getMultiplier() > endHex.getMultiplier() && ourBoard.isHexagonOurs(startHex) && ourBoard.isHexagonOurs(endHex))
		{
			return (endHex.getMultiplier() - startHex.getMultiplier());
		}
		/*If this move moved a piece to a higher valued hexagon from our collection of hexagons, return a positive*/
		else if (startHex.getMultiplier() < endHex.getMultiplier() && ourBoard.isHexagonOurs(startHex) && ourBoard.isHexagonOurs(endHex))
		{
			return (endHex.getMultiplier() - startHex.getMultiplier());
		}
		/*If this move moved a coin onto one of our hexagons return a positive score*/
		else if (ourBoard.isHexagonOurs(startHex) && !ourBoard.isHexagonOurs(endHex))
		{
			return 1;
		}

		
		return 0;
	}
}
