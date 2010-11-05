package push.g4;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class G4Player extends Player
{
	int[][] board;
	int[][] oldBoard;
	ArrayList<int[][]> oldBoards;
	TrackPlayer trackPlayer[];
	Direction myCorner;
	int id;
	private ArrayList<Direction> playerPositions;
	private static final Logger log=Logger.getLogger(G4Player.class); 
	
	@Override
	public void updateBoardState(int[][] board)
	{
		if (this.board!=null)
		{
			this.oldBoard=new int[board.length][board[0].length];
			for (int i=0; i<board.length; i++)
				for (int j=0; j<board[0].length; j++)
					oldBoard[i][j]=this.board[i][j];
			oldBoards.add(oldBoard);
		}
		
		this.board=new int[board.length][board[0].length];
		for (int i=0; i<board.length; i++)
			for (int j=0; j<board[0].length; j++)
				this.board[i][j]=board[i][j];
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return "Gambler Player";
	}

	@Override
	public void startNewGame(int id, int m, ArrayList<Direction> playerPositions)
	{
		myCorner=playerPositions.get(id);
		this.id=id;
		this.playerPositions=playerPositions;
		board=null;
		oldBoard=null;
		oldBoards=new ArrayList<int[][]>();
		
		trackPlayer=new TrackPlayer[6];
		
		
		for (int i=0; i<trackPlayer.length; i++)
		{
			int positionPoint;
			if (i==(id+1)%6 || i==(id+5)%6)
				positionPoint=0;
			else if ((i==(id+2)%6 || i==(id+4)%6))
				positionPoint=2;
			else
				positionPoint=1;
			trackPlayer[i]=new TrackPlayer(i, playerPositions.get(i), positionPoint);
		}
	}

	@Override
	public Move makeMove(List<MoveResult> previousMoves)
	{
		updateStatus(previousMoves);
		
		
		Move move=null;
		//if (previousMoves.isEmpty())
		if (oldBoard==null)
		{
			log.debug("first move");
			move=generateFirstMove();
		}
		else
		{
			move=generateFriendlyMove(previousMoves);
		}
		
		if (board[move.getY()][move.getX()]==0)
		{
			log.debug("random move");
			return generateRandomMove(0);
		}
		
		return move;
	}
	
	public void updateStatus(List<MoveResult> previousMoves)
	{
		for (int i=0; i<previousMoves.size(); i++)
		{
			MoveResult moveResult=previousMoves.get(i);
			trackPlayer[i].addMoveResult(moveResult);
//			log.debug("updateStatus: "+trackPlayer[i].id+"["+i+"]"+" scoreTo "+id+" is "+
//				trackPlayer[i].scoreTo(trackPlayer[id], oldBoards));
		}
	}

	public Move generateFirstMove()
	{
		Move move=null;		
		int random2=1-2*GameConfig.random.nextInt(2); //-1 or 1
		Direction friend=myCorner.getRelative(random2);
		Point2D point=new Point2D.Double(friend.getHome().getX()-friend.getDx(), 
			friend.getHome().getY()-friend.getDy());
		move=new Move((int)point.getX(), (int)point.getY(), friend);
		return move;
	}
	
	public Move generateFriendlyMove(List<MoveResult> previousMoves)
	{
		TrackPlayer us=trackPlayer[id];
		ArrayList<TrackPlayer> rankPlayers=new ArrayList<TrackPlayer>();
		
		// sort the players based on diff and position
		for (int i=0; i<trackPlayer.length; i++)
		{
			if (i==id)
				continue;
			TrackPlayer player=trackPlayer[i];
			double scoreToUs=player.scoreTo(us, oldBoards);
			double scoreToPlayer=us.scoreTo(player, oldBoards);
			double lastScoreToUs=player.lastScoreTo(us, oldBoard);
			player.diff=(scoreToUs-scoreToPlayer)*(1-StaticPlayer.alpha)
				+lastScoreToUs*StaticPlayer.alpha;
			rankPlayers.add(player);
		}
		Collections.sort(rankPlayers, new TrackPlayerComparator());
		
		Move bestMove=null;
		for (int i=0; i<rankPlayers.size(); i++)
		{
			TrackPlayer player=rankPlayers.get(i);
			double gain=player.lastScoreTo(us, oldBoard);
			if (gain<=0)
				break;
			ArrayList<TrackMove> trackMoves=benefitFriend(player.playerPosition);
			
			for (int j=0; j<trackMoves.size(); j++)
			{
				log.debug("\t"+id+" can benefit "+player.id+" with "+trackMoves.get(j).gain+" in return to "+gain);
				if (gain>=trackMoves.get(j).gain)
				{
					int randomIndex=j;
					if (trackMoves.get(j).gain<=0)
					{
						if (j==0)
							break;
						else
							bestMove=trackMoves.get(j-1).move;
					}
					else
					{
						if (j!=0)
							randomIndex=j-GameConfig.random.nextInt(2); //50% higher return
						bestMove=trackMoves.get(randomIndex).move;
					}
					
					log.debug(id+" benefit "+player.id+" with "+trackMoves.get(j).gain+" in return to "+gain);
					break;
				}
			}
			if (bestMove!=null)
				continue;
		}
		if (bestMove==null) // if can't help friends, help self
		{
			log.debug(id+" benefit self");
			ArrayList<TrackMove> tempMoves=benefitFriend(myCorner);
			for (int i=0; i<tempMoves.size(); i++)
				log.debug(id+" benefit self "+tempMoves.get(i).gain+" "+tempMoves.get(i).move);
			if (benefitFriend(myCorner).get(0).gain>0)
				bestMove=benefitFriend(myCorner).get(0).move;
		}
		if (bestMove==null) // if can't help self, hurt others.
		{
			log.debug(id+" hurt others");
			bestMove=hurtPlayer();
		}		
				
		return bestMove;
	}
	
	private Move hurtPlayer()
	{
		// TODO Auto-generated method stub
		return generateRandomMove(0);
	}

	public int getGain(Move move, Direction direction)
	{
		return StaticPlayer.getGain(move, direction, oldBoard);
	}
	
	public ArrayList<TrackMove> benefitFriend(Direction friend)
	{
		ArrayList<TrackMove> trackMoves=new ArrayList<TrackMove>();
		for (int row=0; row<StaticPlayer.validRows.length; row++)
		{
			for (int k=0; k<StaticPlayer.validRows[row].length; k++)
			{
				int column=StaticPlayer.validRows[row][k];
				if (board[row][column]==0)
					continue;
				for (int d=-1; d<=1; d++)
				{
					Direction direction=myCorner.getRelative(d);
					if (GameEngine.isInBounds(column+direction.getDx(), row+direction.getDy()))
					{
						Move move=new Move(column, row, direction);
						int gain=getGain(move, friend);
						trackMoves.add(new TrackMove(move, gain));
					}
						
				}
			}
		}
		Collections.sort(trackMoves, new TrackMoveComparator());
		if (trackMoves.size()==0 || trackMoves.get(0).gain<0)
			return null;
		return trackMoves;
	}
	/*
	public Move benefitFriend(Direction friend)
	{
		int bestGain=Integer.MIN_VALUE;
		Move bestMove=null;
				
		for (int row=0; row<StaticPlayer.validRows.length; row++)
		{
			for (int k=0; k<StaticPlayer.validRows[row].length; k++)
			{
				int column=StaticPlayer.validRows[row][k];
				if (board[row][column]==0)
					continue;
				for (int d=-1; d<=1; d++)
				{
					Direction direction=myCorner.getRelative(d);
					if (GameEngine.isInBounds(column+direction.getDx(), row+direction.getDy()))
					{
						Move m=new Move(column, row, direction);
						int gain=getGain(m, friend);
						//log.debug(id+" gain="+gain+" move="+m);
						if (gain>bestGain)
						{
							bestGain=gain;
							bestMove=m;
						}
					}
						
				}
			}
		}
		log.debug(id+" bestGain="+bestGain+" bestMove="+bestMove);
		if (bestGain<0)
			return null;
		return bestMove;
	}*/
	public Move generateRandomMove(int depth)
	{
		if (depth>300)
		{
			return new Move(0, 0, Direction.NE);
		}
		int n2=GameConfig.random.nextInt(9);
		int length=n2;
		if (length>4)
			length=8-length;
		int offset=4-length;
		length+=5;
		int n1=GameConfig.random.nextInt(length);
		n1*=2;
		n1+=offset;
		if (!GameEngine.isInBounds(n1, n2))
			return generateRandomMove(depth+1);

		if (board!=null&&board[n2][n1]==0)
			return generateRandomMove(depth+1);
		Direction d=myCorner.getRelative(GameConfig.random.nextInt(3)-1);
		int tries=0;
		while (!GameEngine.isValidDirectionForCellAndHome(d, myCorner)
			&&tries<10)
		{
			d=myCorner.getRelative(GameConfig.random.nextInt(2)-1);

			tries++;
		}
		if (!GameEngine.isValidDirectionForCellAndHome(d, myCorner))
			return generateRandomMove(depth+1);

		if (!GameEngine.isInBounds(n1+d.getDx(), n2+d.getDy()))
			return generateRandomMove(depth+1);

		Move m=new Move(n1, n2, d);
		return m;
	}
}
