package src.pas.battleship.agents;


// SYSTEM IMPORTS


// JAVA PROJECT IMPORTS
import edu.bu.battleship.agents.Agent;
import edu.bu.battleship.game.Game.GameView;
import edu.bu.battleship.game.EnemyBoard;
import edu.bu.battleship.game.EnemyBoard.Outcome;
import edu.bu.battleship.utils.Coordinate;
import edu.bu.battleship.game.Constants;


public class ProbabilisticAgent
    extends Agent
{
    public boolean targetMode = false;
    public Coordinate lastCoord = null;
    public Coordinate lastTargetCoord = null;
    public String foundDirection = "";

    public ProbabilisticAgent(String name)
    {
        super(name);
        System.out.println("[INFO] ProbabilisticAgent.ProbabilisticAgent: constructed agent");
    }

    @Override
    public Coordinate makeMove(final GameView game)
    {
        EnemyBoard.Outcome[][] gameBoard = game.getEnemyBoardView();
        Coordinate guess = hunt(game);
        while (gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
            guess = hunt(game);
        }
        if (lastCoord != null && gameBoard[lastCoord.getXCoordinate()][lastCoord.getYCoordinate()] == Outcome.valueOf("HIT")) {
            targetMode = true;
            if (lastTargetCoord != null) {
                foundDirection = getDirection(lastTargetCoord, lastCoord);
                if (foundDirection == "right") {
                    guess = new Coordinate(lastCoord.getXCoordinate()+1, lastCoord.getYCoordinate());
                    if (!game.isInBounds(guess)) {
                        foundDirection = "left";
                        guess = new Coordinate(lastTargetCoord.getXCoordinate()-1, lastTargetCoord.getYCoordinate());
                    }
                    if (guess != null && gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                        foundDirection = "";
                        guess = hunt(game);
                        while (guess != null && gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                            guess = hunt(game);
                        }
                    }
                    lastCoord = guess;
                    return guess;
                }
                else if (foundDirection == "left") {
                    guess = new Coordinate(lastCoord.getXCoordinate()-1, lastCoord.getYCoordinate());
                    if (!game.isInBounds(guess)) {
                        foundDirection = "right";
                        guess = new Coordinate(lastTargetCoord.getXCoordinate()-1, lastTargetCoord.getYCoordinate());
                    }
                    if (guess != null && gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                        foundDirection = "";
                        guess = hunt(game);
                        while (guess != null && gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                            guess = hunt(game);
                        }
                    }
                    lastCoord = guess;
                    return guess;
                }
                else if (foundDirection == "up") {
                    guess = new Coordinate(lastCoord.getXCoordinate(), lastCoord.getYCoordinate()-1);
                    if (!game.isInBounds(guess)) {
                        foundDirection = "down";
                        guess = new Coordinate(lastTargetCoord.getXCoordinate(), lastTargetCoord.getYCoordinate()+1);
                    }
                    if (guess != null && gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                        foundDirection = "";
                        guess = hunt(game);
                        while (guess != null && gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                            guess = hunt(game);
                        }
                    }
                    lastCoord = guess;
                    return guess;
                }
                else {
                    guess = new Coordinate(lastCoord.getXCoordinate(), lastCoord.getYCoordinate()+1);
                    if (!game.isInBounds(guess)) {
                        foundDirection = "up";
                        guess = new Coordinate(lastTargetCoord.getXCoordinate(), lastTargetCoord.getYCoordinate()-1);
                    }
                    if (guess != null && gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                        foundDirection = "";
                        guess = hunt(game);
                        while (guess != null && gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                            guess = hunt(game);
                        }
                    }
                    lastCoord = guess;
                    return guess;
                }
            }
            else {
                lastTargetCoord = lastCoord;
                int dirs[][] = new int[][]{{-1, 0}, {0, -1}, {+1, 0}, {0, +1}};
                for(int dir[] : dirs)
                {
                    int x = lastTargetCoord.getXCoordinate() + dir[0];
                    int y = lastTargetCoord.getYCoordinate() + dir[1];
                    Coordinate checkCoord = new Coordinate(x, y);
                    if (game.isInBounds(checkCoord) && gameBoard[x][y] == Outcome.valueOf("UNKNOWN")) {
                        guess = checkCoord;
                        lastCoord = guess;
                        return guess;
                    }
                }
                targetMode = false;
                foundDirection = "";
                lastCoord = guess;
                return guess;
            }
        }
        else {
            if (targetMode == true && lastCoord != null && gameBoard[lastCoord.getXCoordinate()][lastCoord.getYCoordinate()] == Outcome.valueOf("MISS")) {
                int dirs[][] = new int[][]{{-1, 0}, {+1, 0}, {0, -1}, {0, +1}};
                for(int dir[] : dirs)
                {
                    int x = lastTargetCoord.getXCoordinate() + dir[0];
                    int y = lastTargetCoord.getYCoordinate() + dir[1];
                    Coordinate checkCoord = new Coordinate(x, y);
                    if (game.isInBounds(checkCoord) && gameBoard[x][y] == Outcome.valueOf("UNKNOWN")) {
                        guess = checkCoord;
                        lastCoord = guess;
                        return guess;
                    }
                }
            }
            else {
                if (lastCoord != null && gameBoard[lastCoord.getXCoordinate()][lastCoord.getYCoordinate()] == Outcome.valueOf("SUNK")) {
                    lastTargetCoord = null;
                    foundDirection = "";
                    targetMode = false;
                }
                guess = hunt(game);
                while (gameBoard[guess.getXCoordinate()][guess.getYCoordinate()] != Outcome.valueOf("UNKNOWN")) {
                    guess = hunt(game);
                }
            }
        }
        lastCoord = guess;
        return guess;
    }

    public Coordinate hunt(final GameView game) {
        EnemyBoard.Outcome[][] gameBoard = game.getEnemyBoardView();
        Constants constants = game.getGameConstants();
        int dimRows = constants.getNumRows();
        int dimCols = constants.getNumCols();
        Coordinate guess = new Coordinate(0, 1);
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j] == Outcome.valueOf("HIT")) {
                    int dirs[][] = new int[][]{{-1, 0}, {+1, 0}, {0, -1}, {0, +1}};
                    for(int dir[] : dirs) {
                        int x = i + dir[0];
                        int y = j + dir[1];
                        Coordinate checkCoord = new Coordinate(x, y);
                        if (game.isInBounds(checkCoord) && gameBoard[x][y] == Outcome.valueOf("UNKNOWN")) {
                            guess = checkCoord;
                            return guess;
                        }
                    }
                }
            }
        }
        while ((guess.getXCoordinate() + guess.getYCoordinate()) % 2 != 0) {
            int guessY = (int)(Math.random()*(dimRows));
            int guessX = (int)(Math.random()*(dimCols));
            guess = new Coordinate(guessX, guessY);
        }
        return guess;
    }
    
    public String getDirection(Coordinate initial, Coordinate next) {
        if (initial.getXCoordinate() == next.getXCoordinate()) {
            if (initial.getYCoordinate() < next.getYCoordinate()) return "down";
            else return "up";
        }
        else {
            if (initial.getXCoordinate() < next.getXCoordinate()) return "right";
            else return "left";
        }
    }

    @Override
    public void afterGameEnds(final GameView game) {}

}
