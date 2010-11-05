package push.g6.strategy;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import push.g6.AbstractPlayer;
import push.g6.NeutralMover;
import push.g6.TitTatPlayer;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player.Direction;

public class GeneralStrategy extends Strategy {

    private LinkedList<Point> neighbourhood = null;
    private Direction playerToHelp = null;

    public GeneralStrategy(TitTatPlayer p) {
        this.player = p;

        neighbourhood = new LinkedList<Point>();
        System.out.println("homex " + this.player.getCorner().getHome().getX()
                + " homey " + this.player.getCorner().getHome().getY());
        neighbourhood.add(new Point((int) this.player.getCorner().getHome()
                .getX(), (int) this.player.getCorner().getHome().getY()));

        for (int i = 0; i < 3; i++) {

            int x = (int) (this.player.getCorner().getRelative(1 - i).getDx() + this.player
                    .getCorner().getHome().getX());
            int y = (int) (this.player.getCorner().getRelative(1 - i).getDy() + this.player
                    .getCorner().getHome().getY());

            neighbourhood.add(new Point(x, y));

            for (int j = 0; j < 3; j++) {
                neighbourhood.add(new Point(x
                        + this.player.getCorner().getRelative(1 - j).getDx(), y
                        + this.player.getCorner().getRelative(1 - j).getDy()));
            }
        }
        
        for (Point po : neighbourhood) {
            this.player.getLogger().debug(
                    "Player " + player.getID() + " is close too " + po.getX()
                            + " " + po.getY());

        }
    }

    public void evaluateMoves(List<MoveResult> previousMoves) {

        if ((previousMoves == null) || previousMoves.isEmpty())
            return;
        LinkedList<Integer> players = new LinkedList<Integer>();
        for (MoveResult mr : previousMoves) {
            if (mr.getPlayerId() == this.player.getID())
                continue;

            // Evaluate whether the moves of the other players have helped or
            // not

            if (!GameEngine.isInBounds(mr.getMove().getNewY(), mr.getMove()
                    .getNewX())
                    || !GameEngine.isValidDirectionForCellAndHome(
                            AbstractPlayer.getHomeofID(mr.getPlayerId()), mr
                                    .getMove().getDirection())) {
                continue;
            }

            if ((mr.getMove().getDirection().equals(this.player.getCorner())))
                players.add(mr.getPlayerId());

        }

        if (players.size() == 0)
            this.playerToHelp = null;
        else {
            Random r = new Random();
            int pId = r.nextInt(players.size());
            playerToHelp = this.player.getHomeofID(pId);
        }

    }

    @Override
    public Move getMove(int round, List<MoveResult> previousMoves) {

        if (previousMoves == null || previousMoves.isEmpty()) {
            return this.player.makeNeutralMove();
        }
        this.evaluateMoves(previousMoves);

        if (playerToHelp == null) {
            return this.player.makeNeutralMove();

        } else {

            for (int i = 0; i < this.player.getBoard().length; i++) {
                for (int j = 0; j < this.player.getBoard()[i].length; j++) {
                    Point p = new Point(i, j);
                    if (this.neighbourhood.contains(p)){
                        this.player.getLogger().debug("x: "+p.getX()+" y: "+p.getY());
                        continue;
                    }
                    if (!GameEngine.isInBounds(i, j)
                            || !GameEngine.isValidDirectionForCellAndHome(
                                    this.player.getCorner(), this.playerToHelp))
                        continue;
                    if (this.player.getBoard()[j][i] != 0) {

                        Move m = new Move(i, j, playerToHelp);
                        this.player.getLogger().debug(
                                "player id:" + this.player.getID() + " x: " + m.getX()
                                        + " y:" + m.getY() + " newX"+ m.getNewX()+
                                        "newY "+ m.getNewY()
                                        +"DirectionTo "+m.getDirection());
                        if(GameEngine.isInBounds(m.getNewX(), m.getNewY()))
                            return m;
                    }

                }
            }
            this.player.getLogger().debug("Player "+this.player.getID()+" neutral move");

            return this.player.makeNeutralMove();
        }

    }

}
