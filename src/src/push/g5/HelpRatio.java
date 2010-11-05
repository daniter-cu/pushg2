package push.g5;

import org.apache.log4j.Logger;

import push.sim.GameController;

public class HelpRatio {
	static Logger log;
	static
	{
		log = Logger.getLogger(GameController.class);
	}
	
	private int bestPointChangePossible = 0;
	private int worstPointChangePossible = 0;
	private int pointChange = 0;
	private int netHelpfulMoves = 0;
	private double helpRatio=0;

	public void addRound(int currentPointChange, int currentBestPointChangePossible, int currentWorstPointChangePossible)
	{
		if(currentPointChange > currentBestPointChangePossible || currentPointChange < currentWorstPointChangePossible || currentBestPointChangePossible < currentWorstPointChangePossible)
			log.info("change: "+currentPointChange+" best: "+currentBestPointChangePossible+" worst: "+currentWorstPointChangePossible);
		
		pointChange += currentPointChange;
		bestPointChangePossible += currentBestPointChangePossible;
		worstPointChangePossible += currentWorstPointChangePossible;
		
		helpRatio = 2.0 * ( pointChange - worstPointChangePossible ) / ( bestPointChangePossible - worstPointChangePossible ) - 1;
		
		if( currentPointChange > 0 )
			netHelpfulMoves++;
		else if( currentPointChange < 0 )
			netHelpfulMoves--;
			
	}
	
	public int getPositivePointsPossible() {
		return bestPointChangePossible;
	}

	public int getNegativePointsPossible() {
		return worstPointChangePossible;
	}

	public int getPointChange() {
		return pointChange;
	}

	public int getNetHelpfulMoves() {
		return netHelpfulMoves;
	}

	public double getHelpRatio()
	{
		return helpRatio;
	}
	
	public String toString()
	{
		return "" + helpRatio;
	}
}
