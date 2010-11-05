package push.g1;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import push.sim.GameController;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.Player;
import push.sim.Player.Direction;

public class MoveFinder{
	private int numPlayers = 6;
	private Direction myCorner;
	private int id;
	private ArrayList<Relationship> relationshipList;
	private GameEngine gameEngine;
	private PlayerBoard ourBoard;
	private Logger log;

	
	public MoveFinder(PlayerBoard ourBoard)
	{
		this.ourBoard = ourBoard;
		
		relationshipList = new ArrayList<Relationship>();
		
		/*Initialize relationships with other players*/
		for (int playerID = 0; playerID < numPlayers; playerID++)
		{
			/*Add all the players except ourselves to the relationship list*/
			if (playerID!=id)
			{
				relationshipList.add(new Relationship(playerID, ourBoard));
			}
		}
		
		gameEngine = new GameEngine("push.xml");
		log = Logger.getLogger(GameController.class);

	}
	
	public Move findCooperativeMove()
	{
		log.trace("In find cooperative move");
		
		/*Find the most cooperative player using historical data*/
		Collections.sort(relationshipList, new RCmp());
		
		for (Relationship mostCooperative : relationshipList)
		{	
			/*Get that player's hexagons, so we can figure out which hexagons we want to move towards
			 * to cooperate more with this player. */
			ArrayList<Hexagon> playerHexagons = ourBoard.getPlayerHexagons(mostCooperative.getPlayerID());
			
			/*Sort the other player's hexagons in order of their multipliers*/
			Collections.sort(playerHexagons, new HexComparator());
			
			for (Hexagon endHex : playerHexagons)
			{
				for (Direction dir : Player.Direction.values())
				{	
					Move m = new Move (endHex.getX() + dir.getDx(), endHex.getY() + dir.getDy(), dir.getOpposite());
					log.trace("move: " + m + "\n");
					
					if (ourBoard.validMove(m))
					{
						log.trace("that move was considered valid");
						return m;
					}
					else
					{
						log.trace("that move was considered invalid");
					}
				}
			}
		}
		
		log.trace("Returning a default move.");
		
		//Return a default move
		Move m = new Move (1,1,Player.Direction.NE);
		return m;
		
	}
}

