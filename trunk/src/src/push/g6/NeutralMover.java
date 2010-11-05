package push.g6;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import push.sim.GameConfig;
import push.sim.GameEngine;
import push.sim.Move;
import push.sim.MoveResult;
import push.sim.Player;

public class NeutralMover extends Player {
    int[][] board;
    ArrayList<Point2D> whiteSpots = new ArrayList<Point2D>();

    @Override
    public void updateBoardState(int[][] board) {
        this.board = board;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return "Neutral Mover";
    }

    public Direction myCorner;
    int id;

    @Override
    public void startNewGame(int id, int m, ArrayList<Direction> playerPositions) {
        myCorner = playerPositions.get(id);
        this.id = id;
        if (myCorner.equals(Direction.E)) {
            whiteSpots.add(new Point2D.Double(8, 0));
            whiteSpots.add(new Point2D.Double(8, 2));
            whiteSpots.add(new Point2D.Double(8, 4));
            whiteSpots.add(new Point2D.Double(8, 6));
            whiteSpots.add(new Point2D.Double(8, 8));
            whiteSpots.add(new Point2D.Double(5, 3));
            whiteSpots.add(new Point2D.Double(5, 5));
            whiteSpots.add(new Point2D.Double(2, 2));
            whiteSpots.add(new Point2D.Double(2, 6));
        }
        if (myCorner.equals(Direction.NE)) {
            whiteSpots.add(new Point2D.Double(11, 5));
            whiteSpots.add(new Point2D.Double(14, 6));
            whiteSpots.add(new Point2D.Double(8, 4));
            whiteSpots.add(new Point2D.Double(8, 6));
            whiteSpots.add(new Point2D.Double(8, 8));
            whiteSpots.add(new Point2D.Double(5, 3));
            whiteSpots.add(new Point2D.Double(5, 5));
            whiteSpots.add(new Point2D.Double(2, 2));
            whiteSpots.add(new Point2D.Double(2, 6));
        }
        if (myCorner.equals(Direction.SE)) {
            whiteSpots.add(new Point2D.Double(11, 3));
            whiteSpots.add(new Point2D.Double(14, 2));
            whiteSpots.add(new Point2D.Double(8, 4));
            whiteSpots.add(new Point2D.Double(8, 6));
            whiteSpots.add(new Point2D.Double(8, 0));
            whiteSpots.add(new Point2D.Double(5, 3));
            whiteSpots.add(new Point2D.Double(5, 5));
            whiteSpots.add(new Point2D.Double(2, 2));
            whiteSpots.add(new Point2D.Double(2, 6));
            whiteSpots.add(new Point2D.Double(8, 2));
        }
        if (myCorner.equals(Direction.W)) {
            whiteSpots.add(new Point2D.Double(8, 0));
            whiteSpots.add(new Point2D.Double(8, 4));
            whiteSpots.add(new Point2D.Double(8, 6));
            whiteSpots.add(new Point2D.Double(8, 8));
            whiteSpots.add(new Point2D.Double(8, 2));
            whiteSpots.add(new Point2D.Double(11, 3));
            whiteSpots.add(new Point2D.Double(11, 5));
            whiteSpots.add(new Point2D.Double(14, 2));
            whiteSpots.add(new Point2D.Double(14, 6));

        }
        if (myCorner.equals(Direction.SW)) {
            whiteSpots.add(new Point2D.Double(11, 5));
            whiteSpots.add(new Point2D.Double(14, 6));
            whiteSpots.add(new Point2D.Double(8, 4));
            whiteSpots.add(new Point2D.Double(14, 2));
            whiteSpots.add(new Point2D.Double(8, 0));
            whiteSpots.add(new Point2D.Double(5, 3));
            whiteSpots.add(new Point2D.Double(11, 3));
            whiteSpots.add(new Point2D.Double(2, 2));
            whiteSpots.add(new Point2D.Double(8, 2));
        }
        if (myCorner.equals(Direction.NW)) {
            whiteSpots.add(new Point2D.Double(11, 3));
            whiteSpots.add(new Point2D.Double(14, 2));
            whiteSpots.add(new Point2D.Double(8, 4));
            whiteSpots.add(new Point2D.Double(8, 6));
            whiteSpots.add(new Point2D.Double(14, 6));
            whiteSpots.add(new Point2D.Double(11, 5));
            whiteSpots.add(new Point2D.Double(5, 5));
            whiteSpots.add(new Point2D.Double(8, 8));
            whiteSpots.add(new Point2D.Double(2, 6));
        }

    }

    public Move makeMoveFromSouth() {
        for (Point2D point : whiteSpots) {
            int y = (int) point.getY();
            int x = (int) point.getX();
            if (x - 1 > 0 && y + 1 < 9 && board[y + 1][x - 1] != 0) {
                return new Move(x - 1, y + 1, Direction.NE);
            }
            if (x + 1 < 16 && y + 1 < 9 && board[y + 1][x + 1] != 0) {
                return new Move(x + 1, y + 1, Direction.NW);
            }
            if (x - 2 > 0 && board[y + 1][x - 1] != 0) {
                return new Move(x - 2, y, Direction.E);
            }
        }
        return generateRandomMove(0);
    }

    public Move makeMove() {
        for (Point2D point : whiteSpots) {
           
            // System.out.println("MX "+myCorner.getHome().x+" "+myCorner.getHome().y);
            int x = (int) point.getX() - myCorner.getRelative(0).getDx();
            int y = (int) point.getY() - myCorner.getRelative(0).getDy();
            // System.out.println("X="+x+"Y="+y+"ff "+point.getX()+"ff2 "+point.getY());
            if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
                    && board[y][x] != 0)
                return new Move(x, y, myCorner.getOpposite());

            x = (int) point.getX() - myCorner.getRelative(1).getDx();
            y = (int) point.getY() - myCorner.getRelative(1).getDy();

            // System.out.println("X="+x+"Y="+y);
            if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
                    && board[y][x] != 0)
                return new Move(x, y, myCorner.getOpposite().getRight());

            x = (int) point.getX() - myCorner.getRelative(-1).getDx();
            y = (int) point.getY() - myCorner.getRelative(-1).getDy();

            // System.out.println("X="+x+"Y="+y);
            if ((x >= 0 && y >= 0) && (x < board[0].length && y < board.length)
                    && board[y][x] != 0)
                return new Move(x, y, myCorner.getOpposite().getLeft());

        }
        return generateRandomMove(0);

    }

    @Override
    public Move makeMove(List<MoveResult> previousMoves) {
        this.updateBoardState(board);
        if (myCorner.equals(Direction.SW) || myCorner.equals(Direction.SE)) {
            return makeMoveFromSouth();
        }
        return makeMove();
        // return generateRandomMove(0);
    }

    public Move generateRandomMove(int depth) {
        if (depth > 300) {
            return new Move(0, 0, Direction.NE);
        }
        int n2 = GameConfig.random.nextInt(9);
        int length = n2;
        if (length > 4)
            length = 8 - length;
        int offset = 4 - length;
        length += 5;
        int n1 = GameConfig.random.nextInt(length);
        n1 *= 2;
        n1 += offset;
        if (!GameEngine.isInBounds(n1, n2))
            return generateRandomMove(depth + 1);

        if (board != null && board[n2][n1] == 0)
            return generateRandomMove(depth + 1);
        Direction d = myCorner.getRelative(GameConfig.random.nextInt(3) - 1);
        int tries = 0;
        while (!GameEngine.isValidDirectionForCellAndHome(d, myCorner)
                && tries < 10) {
            d = myCorner.getRelative(GameConfig.random.nextInt(2) - 1);

            tries++;
        }
        if (!GameEngine.isValidDirectionForCellAndHome(d, myCorner))
            return generateRandomMove(depth + 1);

        if (!GameEngine.isInBounds(n1 + d.getDx(), n2 + d.getDy()))
            return generateRandomMove(depth + 1);

        Move m = new Move(n1, n2, d);
        return m;
    }

    private int maximum(int a, int b, int c) {
        if (a > b && a > c) {
            return a;
        }
        if (b > a && b > c) {
            return b;
        }
        if (c > a && c > b) {
            return c;
        }
        return 0;
    }
}

