package push.g2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import push.sim.GameConfig;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;
public class G2Player extends Player {
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Dumb Player2";
	}
	Direction myCorner;
	@Override
	public void startNewGame(int id, int m,
			ArrayList<Direction> playerPositions) {
		myCorner=playerPositions.get(id);
	}

	@Override
	public Move makeMove(List<MoveResult> previousMoves) {
		
		Move m = new Move(3, 3, myCorner.getOpposite());
		return m;
	}


}
