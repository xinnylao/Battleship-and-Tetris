package src.labs.zombayes.agents;


// SYSTEM IMPORTS
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.*;
import java.math.*;


// JAVA PROJECT IMPORTS
import edu.bu.labs.zombayes.agents.SurvivalAgent;
import edu.bu.labs.zombayes.features.Features.FeatureType;
import edu.bu.labs.zombayes.linalg.Matrix;
import edu.bu.labs.zombayes.utils.Pair;




public class NaiveBayesAgent
    extends SurvivalAgent
{

    public static class NaiveBayes
        extends Object
    {

        public static final FeatureType[] FEATURE_HEADER = {FeatureType.CONTINUOUS,
                                                            FeatureType.CONTINUOUS,
                                                            FeatureType.DISCRETE,
                                                            FeatureType.DISCRETE};
        
        private double[] probability;
        private int[] count;
        private double[][] featureTrack;
        private double[][] estimates;
    
        public NaiveBayes() {
            probability = new double[0];
            count = new int[2];
            featureTrack = new double[2][4];
            estimates = new double[2][4];
        }
    
        public void fit(Matrix X, Matrix y_gt) {
            System.out.println("this is called");
            int numSamples = X.getShape().getNumRows();
            int numFeatures = 4;
            int numClasses = 2;

            for (int i = 0; i < numSamples; i++) {
                for (int j = 0; j < numFeatures; j++) {
                    featureTrack[(int)y_gt.get(i, 0)][j] += X.get(i, j);
                    count[(int)y_gt.get(i, 0)]++;
                }
            }

            for (int a = 0; a < 2; a++) {
                for (int b = 0; b < 4; b++) {
                    estimates[a][b] = featureTrack[a][b] / (double)count[a];
                }
            }

            // System.out.println("featureTrack " + Arrays.deepToString(featureTrack));
            // System.out.println("count " + Arrays.toString(count));
            // System.out.println("estimates " + Arrays.deepToString(estimates));
        }
    
        public int predict(Matrix x) {
            // System.out.println("predict for " + x.toString());
            double prob0 = 0;
            double prob1 = 0;
            for (int i = 0; i < 4; i++) {
                double diffTo0 = Math.abs(estimates[0][i] - x.get(0, i));
                double diffTo1 = Math.abs(estimates[1][i] - x.get(0, i));
                if (diffTo0 > diffTo1) {
                    if (diffTo0 != 0 && diffTo1 != 0) {
                        prob1 += (diffTo1/diffTo0)*0.25;
                    }
                    else {
                        prob1 += 0.5;
                    }
                }
                else {
                    if (diffTo0 != 0 && diffTo1 != 0) {
                        prob0 += (diffTo0/diffTo1)*0.25;
                    }
                    else {
                        prob0 += 0.5;
                    }
                }
            }
            if (prob0 > prob1) {
                // System.out.println("predicted 0");
                return 0;
            }
            // System.out.println("predicted 1");
            return 1;
        }

    }

    private NaiveBayes model;

    public NaiveBayesAgent(int playerNum, String[] args)
    {
        super(playerNum, args);
        this.model = new NaiveBayes();
    }

    public NaiveBayes getModel() { return this.model; }

    @Override
    public void train(Matrix X, Matrix y_gt)
    {
        System.out.println(X.getShape() + " " + y_gt.getShape());
        this.getModel().fit(X, y_gt);
    }

    @Override
    public int predict(Matrix featureRowVector)
    {
        return this.getModel().predict(featureRowVector);
    }

}
