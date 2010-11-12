package push.g3;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class ShovePlayer extends Player {

    private int[][] board;
    private int[][] boardLastTurn;
    private int ourID;
    private Direction myCorner;
    private ScoreChart scoreChart;
    private boolean couldAnyoneHelpUsLastTurn;

    private int numberMovesThusFar;
    private int numberMovesPossible;

    private ArrayList<Direction> players;

    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    public String getName()
    {
        return "ShovePlayer";
    }

    @Override
    public void startNewGame(int id, int m, ArrayList<Direction> arrayList)
    {
        PlayerBasicAbility.setUpPlayerPoints();
        this.ourID = id;
        this.myCorner = arrayList.get(id);
        scoreChart = new ScoreChart();
        numberMovesPossible = m;
        numberMovesThusFar = 0;
        players = arrayList;
        couldAnyoneHelpUsLastTurn = true;
    }

    @Override
    public void updateBoardState(int[][] newBoard) {
        if(numberMovesThusFar > 0) {
            boardLastTurn = new int[this.board.length][this.board[0].length];
            for(int i = 0; i < this.board.length; i++) {
                for(int j = 0; j < this.board[0].length; j++) {
                    boardLastTurn[i][j] = this.board[i][j];
                }
            }
        }

        board = new int[newBoard.length][newBoard[0].length];
        for(int i = 0; i < newBoard.length; i++) {
            for(int j = 0; j < newBoard[0].length; j++) {
                board[i][j] = newBoard[i][j];
            }
        }
    }

    private final double PERCENT_OPENING_STRATEGY = .02;
    private final double PERCENT_ENDING_STRATEGY = .02;

    @Override
    public Move makeMove(List<MoveResult> previousMoves)
    {
        Move nextMove = null;
        Direction playerWeWantToHelp = null;

        if(couldAnyoneHelpUsLastTurn && numberMovesThusFar > 0) //If we cannot be helped, do NOT update score (keep friends for longer). 
            for(MoveResult mr : previousMoves)
                scoreChart.updateScore(playerIDToDirection(mr.getPlayerId()), ScoreChart.getScoreGivenMove(myCorner, mr.getMove(), boardLastTurn));

        LinkedList<Move> movesWhichCanHelpUsDirectly = PlayerBasicAbility.doGood(myCorner, myCorner.getOpposite(), board); //opposite player will always be able to help us if there is a move to help us directly this turn.
        couldAnyoneHelpUsLastTurn = movesWhichCanHelpUsDirectly.size() > 0;

        if(numberMovesThusFar < Math.ceil(numberMovesPossible*PERCENT_OPENING_STRATEGY)) { // opening strategy
            nextMove = findBestMoveForPlayer(myCorner.getOpposite(), myCorner);
        } else if((numberMovesPossible - numberMovesThusFar) <= Math.ceil(numberMovesPossible*PERCENT_ENDING_STRATEGY)) { // ending strategy
            // find hurtful move
            LinkedList<Direction> players = PlayerBasicAbility.getPlayerRanking(board);
            Direction playerToHurt = null;
            Move hurtMove = null;
            int hurtMoveScoreDifference = 0;
            int player = -1; //start with player in 1st
            while(hurtMove == null && player < Direction.values().length -1) {
                player++;
                playerToHurt = players.get(player);
                if(playerToHurt.equals(myCorner)) // don't hurt ourselves!!
                    continue;
                
                // make sure we find the most hurtful move.
                Move tempMove = PlayerBasicAbility.doEvil(playerToHurt, myCorner, board);
                if(tempMove != null) {
                    int tempMoveDifference = -1*PlayerBasicAbility.getActualScoreChangeForMoveAndPlayerGivenBoard(tempMove, playerToHurt, board);
                    if(tempMoveDifference > hurtMoveScoreDifference) {
                        hurtMoveScoreDifference = tempMoveDifference;
                        hurtMove = tempMove;
                    }                    
                }
            }

            // find best move to help ourselves
            Move selfHelpMove = findBestMoveForPlayer(myCorner, myCorner);

            if(hurtMove != null || selfHelpMove != null) {
                if(hurtMove != null)
                    nextMove = hurtMove;
                else
                    nextMove = selfHelpMove;
            }
            
            // see which move is more useful
            if(nextMove != null && hurtMove != null && playerToHurt != null && selfHelpMove != null) {
                int ourScoreIncrease = PlayerBasicAbility.getActualScoreChangeForMoveAndPlayerGivenBoard(selfHelpMove, myCorner, board);
                int theirScoreDecrease = hurtMoveScoreDifference;
                if(ourScoreIncrease >= theirScoreDecrease/2) {
                    nextMove = selfHelpMove;
                    logger.error(myCorner + " is taking the self help move because move " + selfHelpMove + " (" + ourScoreIncrease + ") is more helpful to us than " +
                            hurtMove + " (" + theirScoreDecrease + ")");
                } else {
                    nextMove = hurtMove;
                    logger.error(myCorner + " is taking the hurtful move because move " + hurtMove + " (" + theirScoreDecrease + ") is more helpful to us than " +
                            selfHelpMove + " (" + ourScoreIncrease + ")");

                }
            }
        } else { // middle strategy
            nextMove = null;
            int nextRank = 1;
            while (nextMove == null && nextRank < Direction.values().length) {
                playerWeWantToHelp = scoreChart.playerToHelpGivenRank(myCorner, nextRank);
                
                //Find the best possible move to help someone. This signals stronger cooperation and hopefully will result in kind.
                //If we do not get stronger cooperation in return, at least we are creating larger piles (which are better for sabatoging).
                //Moreover, if we do not get strong cooperation in return, we will hopefully find someone better to help us.
                nextMove = findBestMoveForPlayer(playerWeWantToHelp, myCorner);
                nextRank++;
            }
            nextMove = PlayerBasicAbility.getBestMoveForOurPlayerGivenBestMoveFoundSoFar(myCorner, playerWeWantToHelp, nextMove, board);
        }

        //If we couldn't help anyone, help ourselves
        if(nextMove == null) {
            nextMove = findBestMoveForPlayer(myCorner, myCorner);
        }
        
        //If we couldn't find a move, hurt the player who has helped us the least.
        if(nextMove == null) {
            int nextRank = players.size() -1;
            nextMove = null;
            while (nextMove == null && nextRank > 0) {
                Direction playerWeWantToHurt = scoreChart.playerToHelpGivenRank(myCorner, nextRank);
                nextMove = PlayerBasicAbility.doEvil(playerWeWantToHurt, myCorner, board);
                nextRank--;
            }    
        }

        //If we still didn't find anything to do... random move. SHOULD NOT OCCUR!!
        if(nextMove == null) {
            logger.error(myCorner + " is defaulting to generateRandomMove! Bad!");
            nextMove = generateRandomMove(0);                
        }

        numberMovesThusFar++;
        return nextMove;
    }

    public Move findBestMoveForPlayer(Direction toCorner, Direction fromCorner) {
        Move nextMove = null;
        LinkedList<Move> possibleHelpfulMoves = PlayerBasicAbility.doGood(toCorner, fromCorner, board);
        int BestScoreSoFar = 0;

        //Find the best possible move to help us.
        if(possibleHelpfulMoves.size() > 0) {
            for(Move m : possibleHelpfulMoves) {
                int moveScore = ScoreChart.getScoreGivenMove(toCorner, m, board);
                if(moveScore > BestScoreSoFar) {
                    BestScoreSoFar = moveScore;
                    nextMove = m;       
                }
            }                    
        }
        
        return nextMove;
    }
    
    //Returns the home slot of the player whose ID is playerID
    public Direction playerIDToDirection(int playerID){
        Direction result = myCorner;
        int tempID = ourID;
        while(tempID!=playerID){
            result = result.getRight();
            tempID++;
            if(tempID>5){
                tempID=0;
            }
        }
        return result;
    }

    public int directionToPlayerID(Direction d){
        Direction result = myCorner;
        int tempID = ourID;
        while(result.equals(d) == false){
            result = result.getRight();
            tempID++;
            if(tempID>5){
                tempID=0;
            }
        }
        return tempID;
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

}
