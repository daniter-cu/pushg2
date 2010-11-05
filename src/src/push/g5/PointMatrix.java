package push.g5;

import java.util.ArrayList;
import java.util.List;

import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;

public class PointMatrix {
	private final int NUM_PLAYERS = 6;
	private int[][] pointMatrix = new int[NUM_PLAYERS][NUM_PLAYERS];
	private int[][] netMoves = new int[NUM_PLAYERS][NUM_PLAYERS];
	private HelpRatio[][] helpMatrix = new HelpRatio[NUM_PLAYERS][NUM_PLAYERS];
	private int[][] oldBoard = new int[9][17];
	private g5player player;
	
	public static ArrayList<Slot> invalidSlots = new ArrayList<Slot>();
	
	static
	{
		invalidSlots.add(new Slot(2, 0));
		invalidSlots.add(new Slot(1, 1));
		invalidSlots.add(new Slot(0, 2));
		invalidSlots.add(new Slot(14, 0));
		invalidSlots.add(new Slot(15, 1));
		invalidSlots.add(new Slot(16, 2));
		invalidSlots.add(new Slot(16, 6));
		invalidSlots.add(new Slot(15, 7));
		invalidSlots.add(new Slot(14, 8));
		invalidSlots.add(new Slot(0, 6));
		invalidSlots.add(new Slot(1, 7));
		invalidSlots.add(new Slot(2, 8));
	}
	
	public PointMatrix(g5player player)
	{
		this.player = player;
		for(int i=0; i<pointMatrix.length; i++)
		{
			for(int j=0; j<pointMatrix[0].length; j++)
			{
				pointMatrix[i][j] = 0;
				netMoves[i][j] = 0;
			}
		}
		
		for(int i=0; i<oldBoard.length; i++)
		{
			for(int j=0; j<oldBoard[0].length; j++)
			{
				oldBoard[i][j] = 1;
			}
		}
		
		for(int i=0; i<helpMatrix.length; i++)
		{
			for(int j=0; j<helpMatrix[0].length; j++)
			{
				helpMatrix[i][j] = new HelpRatio();
			}
		}
	}
	
	public int[][] getPointMatrix()
	{
		return pointMatrix;
	}
	
	public int[][] getNetMovesMatrix()
	{
		return netMoves;
	}
	
	public HelpRatio[][] getHelpMatrix()
	{
		return helpMatrix;
	}
	
	private void addRoundToHelpMatrix(int affectorIndex, int affectedIndex, int pointChange)
	{
		Move bestMove = player.findMostHelpfulMove(player.getDirection(affectorIndex), player.getDirection(affectedIndex));
		NetMove bestNetMove = calcNetMove(bestMove, oldBoard);
		
		Move worstMove = player.findMostHurtfulMove(player.getDirection(affectorIndex), player.getDirection(affectedIndex));
		NetMove worstNetMove = calcNetMove(worstMove, oldBoard);
		
		int bestPointChangePossible;
		if( bestNetMove.playerIncreaseIndex == affectedIndex )
			bestPointChangePossible = bestNetMove.pointIncrease;
		else
			bestPointChangePossible = bestNetMove.pointDecrease;
		
		int worstPointChangePossible;
		if( worstNetMove.playerIncreaseIndex == affectedIndex )
			worstPointChangePossible = worstNetMove.pointIncrease;
		else
			worstPointChangePossible = worstNetMove.pointDecrease;
		
		helpMatrix[affectorIndex][affectedIndex].addRound(pointChange, bestPointChangePossible, worstPointChangePossible);
		
	}
	
	public void addRound(List<MoveResult> currentMoves, int[][]currentBoard)
	{		
		for(MoveResult m : currentMoves)
		{
			NetMove n = new NetMove();
			if(m.isSuccess() != false)
			{
				n = calcNetMove(m.getMove(), oldBoard);
		
				if(n.playerIncreaseIndex != -1)
				{
					pointMatrix[m.getPlayerId()][n.playerIncreaseIndex] += n.pointIncrease;
					netMoves[m.getPlayerId()][n.playerIncreaseIndex]++;
					addRoundToHelpMatrix(m.getPlayerId(), n.playerIncreaseIndex, n.pointIncrease);
				}
				
				if(n.playerDecreaseIndex != -1)
				{
					pointMatrix[m.getPlayerId()][n.playerDecreaseIndex] += n.pointDecrease;
					netMoves[m.getPlayerId()][n.playerDecreaseIndex]++;
					addRoundToHelpMatrix(m.getPlayerId(), n.playerDecreaseIndex, n.pointDecrease);
				}
			}
			
			for(int i=0; i<NUM_PLAYERS; i++)
			{
				if( i != n.playerIncreaseIndex && i != n.playerDecreaseIndex )
				{
					addRoundToHelpMatrix(m.getPlayerId(), i, 0);
				}
			}
		}
		
		for(int i=0; i<oldBoard.length; i++)
		{
			for(int j=0; j<oldBoard[0].length; j++)
			{
				oldBoard[i][j] = currentBoard[i][j];
			}
		}
	}
	
	
	
	public NetMove calcNetMove(Move m, int[][]oldBoard) {		
		NetMove net = new NetMove();
		
		Slot begin = new Slot(m.getX(), m.getY());
		Slot end = new Slot(m.getNewX(), m.getNewY());
		
		if( !GameEngine.isInBounds(begin.getX(), begin.getY()) || !GameEngine.isInBounds(end.getX(), end.getY()) )
			return net;
		
		int oldOwner = getOwner(begin);
		int newOwner = getOwner(end);
		
		int oldMultiplier;
		if(oldOwner != -1)
			oldMultiplier = player.getBonusFactor(oldOwner, begin);
		else
			oldMultiplier = 0;
		
		int newMultiplier;
		if(newOwner != -1)
			newMultiplier = player.getBonusFactor(newOwner, end);
		else
			newMultiplier = 0;
		
		int stackSize = oldBoard[begin.getY()][begin.getX()];
		
		if(oldOwner == newOwner)
		{
			if(oldMultiplier < newMultiplier)
			{
				net.playerIncreaseIndex = oldOwner;
				net.pointIncrease = newMultiplier * stackSize - oldMultiplier * stackSize;
			}
			else
			{
				net.playerDecreaseIndex = oldOwner;
				net.pointDecrease = newMultiplier * stackSize - oldMultiplier * stackSize;
			}
		}
		else
		{
			net.playerDecreaseIndex = oldOwner;
			net.pointDecrease = -1 * oldMultiplier * stackSize;
			
			net.playerIncreaseIndex = newOwner;
			net.pointIncrease = newMultiplier * stackSize;
		}
		
		return net;
	}

//	public static boolean isValidSlot(Slot s) {
//		if( s.getX() < 0 || s.getY() < 0 || s.getX() > 16 || s.getY() > 8 )
//			return false;
//		
//		if( invalidSlots.contains(s) )
//			return false;
//			
//		return true;
//	}

	public static int getOwner(Slot s)
	{
		if( ( s.getX() <= 7  &&  s.getX() >= 3  && s.getY() <= 2 && s.getY() >= 0 ) || ( s.getX() == 7  && s.getY() == 3 ) )
			return 0;  //green
		if( ( s.getX() <= 13 &&  s.getX() >= 9  && s.getY() <= 2 && s.getY() >= 0 ) || ( s.getX() == 9  && s.getY() == 3 ) )
			return 1;  //yellow 
		if( ( s.getX() <= 16 &&  s.getX() >= 12 && s.getY() <= 5 && s.getY() >= 3 ) || ( s.getX() == 10 && s.getY() == 4 ) )
			return 2;  //orange
		if( ( s.getX() <= 13 &&  s.getX() >= 9  && s.getY() <= 8 && s.getY() >= 6 ) || ( s.getX() == 9  && s.getY() == 5 ) )
			return 3;  //red
		if( ( s.getX() <= 7  &&  s.getX() >= 3  && s.getY() <= 8 && s.getY() >= 6 ) || ( s.getX() == 7  && s.getY() == 5 ) )
			return 4;  //purple
		if( ( s.getX() <= 4  &&  s.getX() >= 0  && s.getY() <= 5 && s.getY() >= 3 ) || ( s.getX() == 6  && s.getY() == 4 ) )
			return 5;  //blue
		return -1;  //white
	}
}
