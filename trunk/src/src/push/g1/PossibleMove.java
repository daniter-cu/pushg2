package push.g1;

import org.apache.log4j.Logger;

import push.sim.GameController;
import push.sim.Move;
import push.sim.Player;
import push.sim.Player.Direction;

public class PossibleMove extends Move {

	public PossibleMove(int x, int y, Direction direction, PlayerBoard b) {
		super(x, y, direction);
		board = b;
		log = Logger.getLogger(GameController.class);
	}

	/** Initialize a possible move by giving it two hexagons */
	public PossibleMove(Hexagon a, Hexagon b, PlayerBoard bd) {

		super(a.getX(), a.getY(), findDirection(a, b));
		board = bd;
	}

	public PossibleMove(Move p, PlayerBoard b) {

		super(p.getX(), p.getY(), p.getDirection());
		board = b;
	}

	/** Find the move direction for two hexagons */
	public static Direction findDirection(Hexagon a, Hexagon b) {

		// ne
		if (a.getX() + 1 == b.getX() && a.getY() - 1 == b.getY())
			return Player.Direction.NE;

		// e
		if (a.getX() + 2 == b.getX() && a.getY() == b.getY())
			return Player.Direction.E;

		// se
		if (a.getX() + 1 == b.getX() && a.getY() + 1 == b.getY())
			return Player.Direction.SE;

		// sw
		if (a.getX() - 1 == b.getX() && a.getY() + 1 == b.getY())
			return Player.Direction.SW;

		// w
		if (a.getX() - 2 == b.getX() && a.getY() == b.getY())
			return Player.Direction.NE;

		// ne
		if (a.getX() + 1 == b.getX() && a.getY() - 1 == b.getY())
			return Player.Direction.NE;

		return null;
	}

	/** Gives the points gained in the move **/
	public int getPointsGained() {
		if (!isValid()) {
			// log.trace("invalid: cannot return points");
			return -1000;
		}

		return board.getOurPoints(this);
		// return 10;
	}

	public String toString() {
		return "Possible move from: " + this.getX() + ", " + this.getY()
				+ " to: " + this.getNewX() + ", " + this.getNewY() + " in: "
				+ this.getDirection();
	}

	public Boolean isValid() {

		if (this.getNewX() > 16 || this.getNewX() < 0 || this.getNewY() < 0
				|| this.getNewY() > 8) // OOB
			return false;

		if (this.getX() > 16 || this.getX() < 0 || this.getY() < 0
				|| this.getY() > 8) // OOB
			return false;

		// no coins
		if (board.getHexagon(this.getX(), this.getY()).getNumCoins() == 0)
			return false;

		// Going onto an uninitialized hexagon
		if (board.getHexagon(this.getNewX(), this.getNewY()).owner == 1000)
			return false;

		if (board.getHexagon(this.getX(), this.getY()).owner == 1000)
			return false;

		if (this.getDirection() == board.getOurDirection())
			return false;

		if (this.getDirection() == board.getOurDirection()
				|| this.getDirection() == board.getOurDirection().getRight()
				|| this.getDirection() == board.getOurDirection().getLeft())
			return false;

		return true;
	}

	private PlayerBoard board;
	private Logger log;

}
