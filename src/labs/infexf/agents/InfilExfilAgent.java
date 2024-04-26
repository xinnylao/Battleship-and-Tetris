package src.labs.infexf.agents;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.w3c.dom.ranges.Range;

// SYSTEM IMPORTS
import edu.bu.labs.infexf.agents.SpecOpsAgent;
import edu.bu.labs.infexf.distance.DistanceMetric;
import edu.bu.labs.infexf.graph.Vertex;
import edu.bu.labs.infexf.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView; 
import edu.cwru.sepia.environment.model.state.Unit.UnitView;


// JAVA PROJECT IMPORTS

public class InfilExfilAgent
    extends SpecOpsAgent
{

    public InfilExfilAgent(int playerNum)
    {
        super(playerNum);
    }

    public static class Range<T extends Comparable<T>> {
        private T min;
        private T max;
    
        public Range(T min, T max) {
            this.min = min;
            this.max = max;
        }
    
        public boolean contains(T value) {
            return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
        }
    }

    // if you want to get attack-radius of an enemy, you can do so through the enemy unit's UnitView
    // Every unit is constructed from an xml schema for that unit's type.
    // We can lookup the "range" of the unit using the following line of code (assuming we know the id):
    //     int attackRadius = state.getUnit(enemyUnitID).getTemplateView().getRange();
    @Override
    public float getEdgeWeight(Vertex src,
                               Vertex dst,
                               StateView state)
    {
        int x_i = src.getXCoordinate();
        int y_i = src.getYCoordinate();
        int x_f = dst.getXCoordinate();
        int y_f = dst.getYCoordinate();

        int weight = 0;

        Set<Integer> enemyIDs = getOtherEnemyUnitIDs();
        for (Integer id : enemyIDs) {
            System.out.println("this is ran");
            UnitView enemyView = state.getUnit(id);
            int attackRadius = enemyView.getTemplateView().getRange();
            //System.out.println("enemy is at (" + enemyView.getXPosition() + ", " + enemyView.getYPosition()+")");
            Range<Integer> xRange = new Range<>(enemyView.getXPosition()-attackRadius, enemyView.getXPosition()+attackRadius);
            Range<Integer> yRange = new Range<>(enemyView.getYPosition()-attackRadius, enemyView.getYPosition()+attackRadius);
            if (xRange.contains(x_f) && yRange.contains(y_f)) {
                weight = Integer.MAX_VALUE-200;
            }
            if ((xRange.contains(x_f+1) || xRange.contains(x_f-1)) && (yRange.contains(y_f+1) || yRange.contains(y_f-1))) {
                weight = Integer.MAX_VALUE-200;
            }
            if ((xRange.contains(x_f+2) || xRange.contains(x_f-2)) && (yRange.contains(y_f+2) || yRange.contains(y_f-2))) {
                if (10000000 > weight) weight = 10000000;
            }
            if ((xRange.contains(x_f+3) || xRange.contains(x_f-3)) && (yRange.contains(y_f+3) || yRange.contains(y_f-3))) {
                if (100000 > weight) weight = 100000;
            }
            if ((xRange.contains(x_f+4) || xRange.contains(x_f-4)) && (yRange.contains(y_f+4) || yRange.contains(y_f-4))) {
                if (1000 > weight) weight = 1000;
            }
        }
        float result = (float)(weight);
        //System.out.println("cost is "+result);
        return result;
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        Stack<Vertex> currentPlan = getCurrentPlan();
        HashSet<Vertex> avoidCoords = new HashSet<>();
        List<ResourceView> resources = state.getAllResourceNodes();
        Set<Integer> enemyIDs = getOtherEnemyUnitIDs();
        for (ResourceView resource : resources) {
            avoidCoords.add(new Vertex(resource.getXPosition(), resource.getYPosition()));
        }
        for (Vertex vertex : currentPlan) {
            if (avoidCoords.contains(vertex)) return true;
            int x = vertex.getXCoordinate();
            int y = vertex.getYCoordinate();
            for (Integer id : enemyIDs) {
                UnitView enemyView = state.getUnit(id);
                if (enemyView != null) {
                    int attackRadius = enemyView.getTemplateView().getRange();
                    System.out.println("enemy is at (" + enemyView.getXPosition() + ", " + enemyView.getYPosition()+")");
                    Range<Integer> xRange = new Range<>(enemyView.getXPosition()-attackRadius, enemyView.getXPosition()+attackRadius);
                    Range<Integer> yRange = new Range<>(enemyView.getYPosition()-attackRadius, enemyView.getYPosition()+attackRadius);
                    if (xRange.contains(x+1) && yRange.contains(y+1)) {
                        System.out.println("replace plan");
                        return true;
                    }
                }
            }
        }
        System.out.println("dont replace plan");
        return false; 
    }
}
