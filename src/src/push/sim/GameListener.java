/* 
 * 	$Id: GameListener.java,v 1.1 2007/09/06 14:51:49 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package push.sim;

public interface GameListener {
	public enum GameUpdateType{STARTING, GAMEOVER, MOVEPROCESSED, MOUSEMOVED};
	public void gameUpdated(GameUpdateType type);
}
