package push.g6.strategy;

import java.util.List;

import push.g6.AbstractPlayer;
import push.g6.TitTatPlayer;
import push.sim.Move;
import push.sim.MoveResult;

/**
 * A general strategy class
 *
 */
public abstract class Strategy {

    protected TitTatPlayer player = null;
    public abstract Move getMove(int round, List<MoveResult> previousMoves);
}
