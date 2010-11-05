package push.g6.strategy;

import java.util.List;

import push.g6.AbstractPlayer;
import push.g6.TitTatPlayer;
import push.sim.Move;
import push.sim.MoveResult;


/**
 * Strategy used for our last move
 */

public class LastMoveStrategy extends Strategy{

    
    public LastMoveStrategy(TitTatPlayer p) {

        this.player = p;
    }
    
    @Override
    public Move getMove(int round,List<MoveResult> previousMoves) {
       
        //Decide whether to harm the stronger or to help the weakest
        
        int firstId=this.player.getHighestScorer();
        
        return null;
    }


}
