package push.g5;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class g5player extends Player{
	int[][] board;
	private Logger log = Logger.getLogger(this.getClass());
	private ArrayList<Direction> playerPositions;
	private int round = 0;
	private PointMatrix pointMatrix1;
	private PointMatrix pointMatrix2;
	private PointMatrix pointMatrix3;
	private PointMatrix currentMatrix;
	private PointMatrix pointMatrixWholeGame;
	
	private static ArrayList<Slot> slotListNorthWest;
	private static ArrayList<Slot> slotListNorthEast;
	private static ArrayList<Slot> slotListEast;
	private static ArrayList<Slot> slotListSouthEast;
	private static ArrayList<Slot> slotListSouthWest;
	private static ArrayList<Slot> slotListWest;
	static{
		slotListWest = new ArrayList<Slot>();
		slotListWest.add(new Slot(1,3));
		slotListWest.add(new Slot(2,4));
		slotListWest.add(new Slot(1,5));
		slotListWest.add(new Slot(2,2));
		slotListWest.add(new Slot(3,3));
		slotListWest.add(new Slot(4,4));
		slotListWest.add(new Slot(3,5));
		slotListWest.add(new Slot(2,6));
		slotListWest.add(new Slot(4,2));
		slotListWest.add(new Slot(5,3));
		slotListWest.add(new Slot(6,4));
		slotListWest.add(new Slot(5,5));
		slotListWest.add(new Slot(4,6));
		slotListWest.add(new Slot(7,3));
		slotListWest.add(new Slot(8,4));
		slotListWest.add(new Slot(7,5));
		
		slotListSouthWest = new ArrayList<Slot>();
		slotListSouthWest.add(new Slot(3,7));
		slotListSouthWest.add(new Slot(5,7));
		slotListSouthWest.add(new Slot(6,8));
		slotListSouthWest.add(new Slot(2,6));
		slotListSouthWest.add(new Slot(4,6));
		slotListSouthWest.add(new Slot(6,6));
		slotListSouthWest.add(new Slot(7,7));
		slotListSouthWest.add(new Slot(8,8));
		slotListSouthWest.add(new Slot(3,5));
		slotListSouthWest.add(new Slot(5,5));
		slotListSouthWest.add(new Slot(7,5));
		slotListSouthWest.add(new Slot(8,6));
		slotListSouthWest.add(new Slot(9,7));
		slotListSouthWest.add(new Slot(6,4));
		slotListSouthWest.add(new Slot(8,4));
		slotListSouthWest.add(new Slot(9,5));
		
		slotListSouthEast = new ArrayList<Slot>();
		slotListSouthEast.add(new Slot(10,8));
		slotListSouthEast.add(new Slot(11,7));
		slotListSouthEast.add(new Slot(13,7));
		slotListSouthEast.add(new Slot(8,8));
		slotListSouthEast.add(new Slot(9,7));
		slotListSouthEast.add(new Slot(10,6));
		slotListSouthEast.add(new Slot(12,6));
		slotListSouthEast.add(new Slot(14,6));
		slotListSouthEast.add(new Slot(7,7));
		slotListSouthEast.add(new Slot(8,6));
		slotListSouthEast.add(new Slot(9,5));
		slotListSouthEast.add(new Slot(11,5));
		slotListSouthEast.add(new Slot(13,5));
		slotListSouthEast.add(new Slot(7,5));
		slotListSouthEast.add(new Slot(8,4));
		slotListSouthEast.add(new Slot(10,4));
		
		slotListEast = new ArrayList<Slot>();
		slotListEast.add(new Slot(15,5));
		slotListEast.add(new Slot(14,4));
		slotListEast.add(new Slot(15,3));
		slotListEast.add(new Slot(14,6));
		slotListEast.add(new Slot(13,5));
		slotListEast.add(new Slot(12,4));
		slotListEast.add(new Slot(13,3));
		slotListEast.add(new Slot(14,2));
		slotListEast.add(new Slot(12,6));
		slotListEast.add(new Slot(11,5));
		slotListEast.add(new Slot(10,4));
		slotListEast.add(new Slot(11,3));
		slotListEast.add(new Slot(12,2));
		slotListEast.add(new Slot(9,5));
		slotListEast.add(new Slot(8,4));
		slotListEast.add(new Slot(9,3));
		
		slotListNorthEast = new ArrayList<Slot>();
		slotListNorthEast.add(new Slot(10,0));
		slotListNorthEast.add(new Slot(11,1));
		slotListNorthEast.add(new Slot(13,1));
		slotListNorthEast.add(new Slot(8,0));
		slotListNorthEast.add(new Slot(9,1));
		slotListNorthEast.add(new Slot(10,2));
		slotListNorthEast.add(new Slot(12,2));
		slotListNorthEast.add(new Slot(14,2));
		slotListNorthEast.add(new Slot(7,1));
		slotListNorthEast.add(new Slot(8,2));
		slotListNorthEast.add(new Slot(9,3));
		slotListNorthEast.add(new Slot(11,3));
		slotListNorthEast.add(new Slot(13,3));
		slotListNorthEast.add(new Slot(7,3));
		slotListNorthEast.add(new Slot(8,4));
		slotListNorthEast.add(new Slot(10,4));
		
		slotListNorthWest = new ArrayList<Slot>();
		slotListNorthWest.add(new Slot(3,1));
		slotListNorthWest.add(new Slot(5,1));
		slotListNorthWest.add(new Slot(6,0));
		slotListNorthWest.add(new Slot(2,2));
		slotListNorthWest.add(new Slot(4,2));
		slotListNorthWest.add(new Slot(6,2));
		slotListNorthWest.add(new Slot(7,1));
		slotListNorthWest.add(new Slot(8,0));
		slotListNorthWest.add(new Slot(3,3));
		slotListNorthWest.add(new Slot(5,3));
		slotListNorthWest.add(new Slot(7,3));
		slotListNorthWest.add(new Slot(8,2));
		slotListNorthWest.add(new Slot(9,1));
		slotListNorthWest.add(new Slot(6,4));
		slotListNorthWest.add(new Slot(8,4));
		slotListNorthWest.add(new Slot(9,3));
	}

	@Override
	public void updateBoardState(int[][] board) {
		this.board= board;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Prisoner 0";
	}
	Direction myCorner;
	boolean cooperationEstablished;
	int cooperatorRelative = -1;
	int numTriesToInitiateCooperation = 0;
	Direction playerToHelp;
	int id;
	@Override
	public void startNewGame(int id, int m,
			ArrayList<Direction> playerPositions) {
		myCorner=playerPositions.get(id);
		this.playerPositions = playerPositions;
		this.id=id;
		cooperationEstablished = false;
		pointMatrix1 = new PointMatrix(this);
		pointMatrix2 = new PointMatrix(this);
		pointMatrix3 = new PointMatrix(this);
		pointMatrixWholeGame = new PointMatrix(this);
	} 

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		log.info(getPlayerIndex(myCorner) + "round: " + round);
		
		pointMatrix1.addRound(previousMoves, board);
		pointMatrix2.addRound(previousMoves, board);
		pointMatrix3.addRound(previousMoves, board);
		pointMatrixWholeGame.addRound(previousMoves, board);
		
		if(round % 3 == 0)
		{
			currentMatrix = pointMatrix1;
			pointMatrix1 = new PointMatrix(this);
		}
		else if(round % 3 == 1)
		{
			currentMatrix = pointMatrix2;
			pointMatrix2 = new PointMatrix(this);
		}
		else if(round % 3 == 2)
		{
			currentMatrix = pointMatrix3;
			pointMatrix3 = new PointMatrix(this);
		}
		
//		int[][] currentPointMatrix = pointMatrix.getPointMatrix();
//		System.err.println("Point Matrix:");
//		for(int i=0; i<currentPointMatrix.length; i++)
//		{
//			for(int j=0; j<currentPointMatrix[0].length; j++)
//			{
//				System.err.print(currentPointMatrix[i][j] + " ");
//			}
//			System.err.println();
//		}
//		
//		System.err.println("\nNet moves matrix:");
//		int[][] currentNetMoveMatrix = pointMatrix.getNetMovesMatrix();
//		for(int i=0; i<currentNetMoveMatrix.length; i++)
//		{
//			for(int j=0; j<currentNetMoveMatrix[0].length; j++)
//			{
//				System.err.print(currentNetMoveMatrix[i][j] + " ");
//			}
//			System.err.println();
//		}
		
		log.info(null);
		log.info(getPlayerIndex(myCorner) + "Help ratio matrix from the last three rounds:");
		HelpRatio[][] currentHelpMatrix = currentMatrix.getHelpMatrix();
		printMatrix(currentHelpMatrix);
		

		
		double highestRatio = -1.0;
		int cooperatingPlayer = 0;
		for(int i = 0; i < 5; i++)
		{
			if(i != getPlayerIndex(myCorner))
			{
				double thisRatio = currentHelpMatrix[i][getPlayerIndex(myCorner)].getHelpRatio();
				if(thisRatio >= highestRatio)
				{
					highestRatio = thisRatio;
					cooperatingPlayer = i;
				}
			}
		}

		
		log.info(getPlayerIndex(myCorner) + "Found that the player most trying to help me is player " + cooperatingPlayer + " with a ratio of " + highestRatio); 
		
		if(highestRatio <= .7)
		{
			playerToHelp = myCorner.getRelative(cooperatorRelative);
			log.info(getPlayerIndex(myCorner) + "Help ratio from affector player " + getPlayerIndex(playerToHelp) + " to affected player " + getPlayerIndex(myCorner) + " is: " + currentHelpMatrix[getPlayerIndex(playerToHelp)][getPlayerIndex(myCorner)].getHelpRatio());
			
			if(numTriesToInitiateCooperation >= 4 && currentHelpMatrix[getPlayerIndex(playerToHelp)][getPlayerIndex(myCorner)].getHelpRatio() <= .4) 
			{
				if(cooperatorRelative <= 0)
				{
					log.info("He's not cooperating with us. Let's try someone else!");
					cooperatorRelative++;
					playerToHelp = myCorner.getRelative(cooperatorRelative);
					numTriesToInitiateCooperation = 0;
				}
				else
				{
					log.info("Couldn't find any cooperators, so make a random move");
					return generateRandomMove(0);
				}
			}
		}
		else
		{
			log.info(getPlayerIndex(myCorner) + "This ratio is > .7, so found someone who might be cooperating with us: Player " + cooperatingPlayer);
			playerToHelp = getDirection(cooperatingPlayer);
			numTriesToInitiateCooperation = 0;
		}
		
		

		
		log.info(getPlayerIndex(myCorner) + "direction we are trying to cooperate with is: " + playerToHelp.name());
		log.info(getPlayerIndex(myCorner) + "index of this direction is: " + getPlayerIndex(playerToHelp));
		
		Move moveToMake = findMostHelpfulMove(myCorner, playerToHelp);
		round++;
		numTriesToInitiateCooperation++;
		log.info(getPlayerIndex(myCorner) + "Move is: " + moveToMake.getX() + ", " + moveToMake.getY() + " to " + moveToMake.getNewX() + ", " + moveToMake.getNewY());
		log.info(getPlayerIndex(myCorner) + "");
		return moveToMake;
		
	}
	
	public void printMatrix(HelpRatio[][] matrix)
	{
		if( matrix == null || matrix.length == 0 )
			log.info("tried to print an empty matrix");
		
		String output = "\n";
		for(int i=0; i<matrix.length; i++)
		{
			for(int j=0; j<matrix[0].length; j++)
			{
				output += String.format("%9.3f ", matrix[i][j].getHelpRatio());
			}
			output += "\n";
		}
		log.info(output + "\n");
	}
	
	/* Returns the most helpful move that player "helper" can do for player "receiverOfHelp".
	 * The most helpful move is the one that increases receiverOfHelp's score by the most points
	 * (or decreases it by the least points if no helpful moves are possible). 
	 */
	public Move findMostHelpfulMove(Direction helper, Direction receiverOfHelp)
	{
		int maxPointChange = -122;//this is the minimum possible change in points, so we initialize maxPointChange to this. 
		Move moveToMake = null;
		ArrayList<Slot> potentialSlots = getPotentiallyHelpfulSlots(receiverOfHelp);
		for(int i = 0; i < potentialSlots.size(); i++)
		{
			Slot mySlot = potentialSlots.get(i);
			//System.out.println("Trying position " + mySlot.getX() + ", " + mySlot.getY());
			if(board[mySlot.getY()][mySlot.getX()] != 0)
			{
				for(int j = -1; j <= 1; j++)
				{
					Move m = new Move(mySlot.getX(), mySlot.getY(), helper.getRelative(j));
					NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, board);
					int pointIncrease = myNetMove.pointIncrease;
					if(myNetMove.playerIncreaseIndex == getPlayerIndex(receiverOfHelp) && GameEngine.isInBounds(m.getNewX(), m.getNewY()))
					{
						if(pointIncrease > maxPointChange)
						{
							maxPointChange = pointIncrease;
							moveToMake = m;
						}
					} 
				}
			}
		}
		
		if(moveToMake == null)
		{
			moveToMake = new Move(8,4,myCorner.getOpposite()); //initialize moveToMake to a random move in case it finds that no moves are possible. 
			//If it reaches here, then it couldn't find a move that increased receiverOfHelp's score. 
			//So here it tries to find a netural move - i.e. one that does not change receiverOfHelp's score.
			//If this is not possible, it finds the least hurtful move. 
			boolean foundBest = false;
			potentialSlots = getAllSlots();
			for(int i = 0; i < potentialSlots.size(); i++)
			{
				if(foundBest == false) //this is here to prevent unnecessary iterations of the loop from occurring if a neutral move is found (because we would then know's it's definitely best because we already know the best we can do is a neutral move).  
				{
					Slot mySlot = potentialSlots.get(i);
					if(board[mySlot.getY()][mySlot.getX()] != 0)
					{
						for(int j = -1; j <= 1; j++)
						{
							Move m = new Move(mySlot.getX(), mySlot.getY(), helper.getRelative(j));			
							NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, board);
							//int pointIncrease = myNetMove.pointIncrease;
							if((myNetMove.playerIncreaseIndex != -1 || myNetMove.playerDecreaseIndex != -1) && myNetMove.playerDecreaseIndex != getPlayerIndex(receiverOfHelp) && GameEngine.isInBounds(m.getNewX(), m.getNewY()))
							{
								maxPointChange = 0; //playerIncreaseIndex could not have possibly been receiverOfHelp if we've made it here, and so if it gets here then the best move is one that doesn't change receiverOfHelp's score. 
								moveToMake = m;
								foundBest = true;
							} 
							else
							{
								int pointChange = -1*myNetMove.pointDecrease;
								if(myNetMove.playerDecreaseIndex == getPlayerIndex(receiverOfHelp) && GameEngine.isInBounds(m.getNewX(), m.getNewY()))
								{
									if(pointChange > maxPointChange)
									{
										maxPointChange = pointChange;
										moveToMake = m;
									}
								}
							}
						}
					}
				}
			}
		}
		return moveToMake;
	}
	
	/* Returns the most hurtful move that player "hurter" can do for player "receiverOfHurtfulMove".
	 * The most hurtful move is the one that decreases receiverOfHelp's score by the most points
	 * (or increases it by the least points if no hurtful moves are possible). 
	 */
	public Move findMostHurtfulMove(Direction hurter, Direction receiverOfHurtfulMove)
	{
		int minPointChange = 122;
		Move moveToMake = new Move(8,4,myCorner.getOpposite()); //initialize moveToMake to a random move in case it finds that no moves are possible. 
		ArrayList<Slot>potentialSlots = getAllSlots();
		for(int i = 0; i < potentialSlots.size(); i++)
		{
			Slot mySlot = potentialSlots.get(i);
			if(board[mySlot.getY()][mySlot.getX()] != 0)
			{
				for(int j = -1; j <= 1; j++)
				{
					Move m = new Move(mySlot.getX(), mySlot.getY(), hurter.getRelative(j));			
					NetMove myNetMove = pointMatrixWholeGame.calcNetMove(m, board);
					if(myNetMove.playerDecreaseIndex == getPlayerIndex(receiverOfHurtfulMove))
					{
						int pointChange = -1*myNetMove.pointDecrease;
						if(pointChange < minPointChange)
						{
							minPointChange = pointChange;
							moveToMake = m;
						}
					} 
					else if((myNetMove.playerDecreaseIndex != -1 || myNetMove.playerIncreaseIndex != -1) && myNetMove.playerIncreaseIndex != getPlayerIndex(receiverOfHurtfulMove))
					{
						if(0 < minPointChange)
						{
							minPointChange = 0;
							moveToMake = m;
						}
					}
					else
					{
						if(myNetMove.playerIncreaseIndex == getPlayerIndex(receiverOfHurtfulMove))
						{
							int pointChange = myNetMove.pointIncrease;
							if(pointChange < minPointChange)
							{
								minPointChange = pointChange;
								moveToMake = m;
							}
						}
					}
				}
			}
		}
		return moveToMake;
	}
	
	
	public Move generateRandomDirectionMove(Direction dir, int depth)
	{
		if(depth > 300)
		{
			log.info("couldn't make a move");
			return new Move(0,0,Direction.NE);
		}
		log.info("    direction to return favor: " + dir.name());
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
			return generateRandomDirectionMove(dir, depth+1);
		
		if(board != null&& board[n2][n1] == 0)
			return generateRandomDirectionMove(dir, depth+1);
		if(!GameEngine.isValidDirectionForCellAndHome(dir, myCorner))
			return generateRandomDirectionMove(dir, depth+1);
		
		if(!GameEngine.isInBounds(n1+dir.getDx(), n2+dir.getDy()))
			return generateRandomDirectionMove(dir, depth+1);
		
		Move m = new Move(n1, n2,dir);
		return m;
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
	
	/*Takes a player location and returns a list of the slots that pushing coins from could increase that person's score. 
	 *So this would be all of the slots of that persons' color minus the 4 pt one, plus all slots ONE away from a slot of their color. 
	 */  
	public ArrayList<Slot> getPotentiallyHelpfulSlots(Direction playerLocation)
	{
		if(playerLocation.name().equals("NW"))
			return slotListNorthWest;
		else if(playerLocation.name().equals("NE"))
			return slotListNorthEast;
		else if(playerLocation.name().equals("E"))
			return slotListEast;
		else if(playerLocation.name().equals("SE"))
			return slotListSouthEast;
		else if(playerLocation.name().equals("SW"))
			return slotListSouthWest;
		return slotListWest; //W
	}
	
	public ArrayList<Slot> getAllSlots()
	{
		ArrayList<Slot> slotListAll = new ArrayList<Slot>();
		for(int y = 0; y < 9; y++)
		{
			for(int x = 0; x < 17; x++)
			{
				if(GameEngine.isInBounds(x, y))
				{
					slotListAll.add(new Slot(x,y));
				}
			}
		}
		
		return slotListAll;
	}
	
	public int getPlayerIndex(Direction d)
	{
		if(d.name().equals("NW"))
			return 0;
		else if(d.name().equals("NE"))
			return 1;
		else if(d.name().equals("E"))
			return 2;
		else if(d.name().equals("SE"))
			return 3;
		else if(d.name().equals("SW"))
			return 4;
		return 5; //W
	}
	
	public Direction getDirection(int d)
	{
		if( d == 0 )
			return Direction.NW;
		if( d == 1 )
			return Direction.NE;
		if( d == 2 )
			return Direction.E;
		if( d == 3 )
			return Direction.SE;
		if( d == 4 )
			return Direction.SW;
		
		return Direction.W;
	}
	
	/*Takes a player's index and returns the coordinates of his home slot */
	public Point getHomePoint(int playerIndex)
	{
		Point homePoint = new Point(4,0);
		if(playerIndex == 0)
			homePoint = new Point(4,0); //NW
		else if(playerIndex == 1)
			homePoint = new Point(12,0); //NE
		else if(playerIndex == 2)
			homePoint = new Point(16,4);//E
		else if(playerIndex == 3)
			homePoint = new Point(12,8);//SE
		else if(playerIndex == 4)
			homePoint = new Point(4,8);//SW
		else if(playerIndex == 5)
			homePoint = new Point(0,4);//W
		
		return homePoint;
	}
	
	/*Takes a player location and a slot and returns how many points that slot is worth for that player.
	 */ 
	public int getBonusFactor(int playerIndex, Slot mySlot)
	{	
		Point homePoint = getHomePoint(playerIndex);
		Point myPoint = new Point(mySlot.getX(), mySlot.getY());
		int distance = GameEngine.getDistance(homePoint, myPoint); //this is "d"
		
		int closestNeighborDistance = 20;
		for(int i = 0; i < 6; i++)
		{
			if(i != playerIndex)
			{
				Point neighbor = getHomePoint(i);
				int neighborDistance = GameEngine.getDistance(neighbor, myPoint);
				if(neighborDistance < closestNeighborDistance)
				{
					closestNeighborDistance = neighborDistance; //this is "e"
				}
			}
		}
		return Math.max(0, closestNeighborDistance - distance); // returns e - d
	}
	
	
}
