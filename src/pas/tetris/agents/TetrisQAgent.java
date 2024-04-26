package src.pas.tetris.agents;

import java.util.List;
import java.util.Random;
import java.util.Iterator;

import edu.bu.tetris.agents.QAgent;
import edu.bu.tetris.agents.TrainerAgent.GameCounter;
import edu.bu.tetris.game.Board;
import edu.bu.tetris.game.Game.GameView;
import edu.bu.tetris.game.minos.Mino;
import edu.bu.tetris.linalg.Matrix;
import edu.bu.tetris.nn.Model;
import edu.bu.tetris.nn.LossFunction;
import edu.bu.tetris.nn.Optimizer;
import edu.bu.tetris.nn.models.Sequential;
import edu.bu.tetris.nn.layers.Dense;
import edu.bu.tetris.nn.layers.Tanh;
import edu.bu.tetris.training.data.Dataset;
import edu.bu.tetris.utils.Pair;

public class TetrisQAgent extends QAgent {

    private static final double INITIAL_EXPLORATION_RATE = 1.0;
    private static final double FINAL_EXPLORATION_RATE = 0.1;
    private static final double EXPLORATION_DECAY_RATE = 0.995;

    private double explorationRate;
    private Random random;

    public TetrisQAgent(String name) {
        super(name);
        this.explorationRate = INITIAL_EXPLORATION_RATE;
        this.random = new Random(12345);
    }

    @Override
    public Matrix getQFunctionInput(final GameView game, final Mino potentialAction) {
        Matrix flattenedImage = null;
        try {
            flattenedImage = game.getGrayscaleImage(potentialAction).flatten();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        int pivotRow = potentialAction.getPivotBlockCoordinate().getXCoordinate();
        int pivotCol = potentialAction.getPivotBlockCoordinate().getYCoordinate();
        
        int numRows = 1;
        int numCols = flattenedImage.numel() + 2;
        Matrix featureMatrix = Matrix.zeros(numRows, numCols);
        for (int i = 0; i < flattenedImage.numel(); i++) {
            featureMatrix.set(0, i, flattenedImage.get(0, i));
        }
        featureMatrix.set(0, flattenedImage.numel(), pivotRow);
        featureMatrix.set(0, flattenedImage.numel() + 1, pivotCol);
    
        return featureMatrix;
    }

    @Override
    public boolean shouldExplore(final GameView game, final GameCounter gameCounter) {
        if (gameCounter.getTotalGamesPlayed() % 100 == 0) {
            explorationRate *= EXPLORATION_DECAY_RATE;
            explorationRate = Math.max(explorationRate, FINAL_EXPLORATION_RATE);
        }
        return this.random.nextDouble() <= explorationRate;
    }

    @Override
    public Mino getExplorationMove(final GameView game) {
        List<Mino> possibleActions = game.getFinalMinoPositions();
        return possibleActions.get(random.nextInt(possibleActions.size()));
    }

    @Override
    public double getReward(final GameView game) {
        double reward = game.getTotalScore();
        return reward;
    }

    @Override
    public Model initQFunction() {
        final int numPixelsInImage = Board.NUM_ROWS * Board.NUM_COLS;
        final int hiddenDim = 2 * numPixelsInImage;
        final int outDim = 1;

        Sequential qFunction = new Sequential();
        qFunction.add(new Dense(numPixelsInImage + 2, hiddenDim));
        qFunction.add(new Tanh());
        qFunction.add(new Dense(hiddenDim, outDim));

        return qFunction;
    }

    @Override
    public void trainQFunction(Dataset dataset, LossFunction lossFunction, Optimizer optimizer, long numUpdates) {
        for (int epochIdx = 0; epochIdx < numUpdates; ++epochIdx) {
            dataset.shuffle();
            Iterator<Pair<Matrix, Matrix>> batchIterator = dataset.iterator();
            while (batchIterator.hasNext()) {
                Pair<Matrix, Matrix> batch = batchIterator.next();
                try {
                    optimizer.reset();
                    Matrix YHat = this.getQFunction().forward(batch.getFirst());
                    this.getQFunction().backwards(batch.getFirst(), lossFunction.backwards(YHat, batch.getSecond()));
                    optimizer.step();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }
}