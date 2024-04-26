package src.labs.stealth.agents;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView; 

import java.util.*;

import java.util.HashSet;   // will need for dfs
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;     // will need for dfs
import java.util.Set;       // will need for dfs


// JAVA PROJECT IMPORTS


public class DFSMazeAgent
    extends MazeAgent
{

    public DFSMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {
        HashSet<Vertex> visited = new HashSet<>();
        Stack<Path> stack = new Stack<>();
        Path initialPath = new Path(src);
        stack.add(initialPath);

        List<ResourceView> resources = state.getAllResourceNodes();
        HashSet<Vertex> avoidCoords = new HashSet<>();

        for (ResourceView resource : resources) {
            avoidCoords.add(new Vertex(resource.getXPosition(), resource.getYPosition()));
        }

        while(!stack.isEmpty()) {
            Path oldPath = stack.pop();
            Vertex target = oldPath.getDestination();
            visited.add(target);
            Vertex east = new Vertex(target.getXCoordinate()+1, target.getYCoordinate());
            if (!visited.contains(east) && !avoidCoords.contains(east) && state.inBounds(target.getXCoordinate()+1, target.getYCoordinate())) {
                if (east.equals(goal)) return oldPath;
                visited.add(east);
                stack.add(new Path(east, 1f, oldPath));
            }
            Vertex west = new Vertex(target.getXCoordinate()-1, target.getYCoordinate());
            if (!visited.contains(west) && !avoidCoords.contains(west) && state.inBounds(target.getXCoordinate()-1, target.getYCoordinate())) {
                if (west.equals(goal)) return oldPath;
                visited.add(west);
                stack.add(new Path(west, 1f, oldPath));
            }
            Vertex north = new Vertex(target.getXCoordinate(), target.getYCoordinate()-1);
            if (!visited.contains(north) && !avoidCoords.contains(north) && state.inBounds(target.getXCoordinate(), target.getYCoordinate()-1))  {
                if (north.equals(goal)) return oldPath;
                visited.add(north);
                stack.add(new Path(north, 1f, oldPath));
            }
            Vertex south = new Vertex(target.getXCoordinate(), target.getYCoordinate()+1);
            if (!visited.contains(south) && !avoidCoords.contains(south) && state.inBounds(target.getXCoordinate(), target.getYCoordinate()+1))  {
                if (south.equals(goal)) return oldPath;
                visited.add(south);
                stack.add(new Path(south, 1f, oldPath));
            }
            Vertex northeast = new Vertex(target.getXCoordinate()+1, target.getYCoordinate()-1);
            if (!visited.contains(northeast) && !avoidCoords.contains(northeast) && state.inBounds(target.getXCoordinate()+1, target.getYCoordinate()-1)) {
                if (northeast.equals(goal)) return oldPath;
                visited.add(northeast);
                stack.add(new Path(northeast, 1f, oldPath));
            }
            Vertex southeast = new Vertex(target.getXCoordinate()+1, target.getYCoordinate()+1);
            if (!visited.contains(southeast) && !avoidCoords.contains(southeast) && state.inBounds(target.getXCoordinate()+1, target.getYCoordinate()+1)) {
                if (southeast.equals(goal)) return oldPath;
                visited.add(southeast);
                stack.add(new Path(southeast, 1f, oldPath));
            }
            Vertex northwest = new Vertex(target.getXCoordinate()-1, target.getYCoordinate()-1);
            if (!visited.contains(northwest) && !avoidCoords.contains(northwest) && state.inBounds(target.getXCoordinate()-1, target.getYCoordinate()-1)) {
                if (northwest.equals(goal)) return oldPath;
                visited.add(northwest);
                stack.add(new Path(northwest, 1f, oldPath));
            }
            Vertex southwest = new Vertex(target.getXCoordinate()-1, target.getYCoordinate()+1);
            if (!visited.contains(southwest) && !avoidCoords.contains(southwest) && state.inBounds(target.getXCoordinate()-1, target.getYCoordinate()+1)) {
                if (southwest.equals(goal)) return oldPath;
                visited.add(southwest);
                stack.add(new Path(southwest, 1f, oldPath));
            }
        }
        return null;
    }

    @Override
    public boolean shouldReplacePlan(StateView state)
    {
        Stack<Vertex> currentPlan = getCurrentPlan();
        Set<Integer> enemyIDs = getOtherEnemyUnitIDs();
        HashSet<ResourceView> enemyViews = new HashSet<>();
        HashSet<Vertex> avoidCoords = new HashSet<>();
        for (Integer id : enemyIDs) {
            enemyViews.add(state.getResourceNode(id));
        }
        for (ResourceView enemyView : enemyViews) {
            avoidCoords.add(new Vertex(enemyView.getXPosition(), enemyView.getYPosition()));
        }
        for (Vertex vertex : currentPlan) {
            if (avoidCoords.contains(vertex)) {
                return true;
            }
        }
        return false; 
    }

}
