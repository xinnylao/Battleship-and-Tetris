package src.labs.stealth.agents;

// will need for bfs
// will need for bfs
// will need for bfs
// will need for bfs
import java.util.*;

// SYSTEM IMPORTS
import edu.bu.labs.stealth.agents.MazeAgent;
import edu.bu.labs.stealth.graph.Vertex;
import edu.bu.labs.stealth.graph.Path;


import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.ResourceNode.ResourceView; 


// JAVA PROJECT IMPORTS


public class BFSMazeAgent
    extends MazeAgent
{

    public BFSMazeAgent(int playerNum)
    {
        super(playerNum);
    }

    @Override
    public Path search(Vertex src,
                       Vertex goal,
                       StateView state)
    {
        HashSet<Vertex> visited = new HashSet<>();
        Queue<Path> queue = new LinkedList<>();
        Path initialPath = new Path(src);
        queue.add(initialPath);

        List<ResourceView> resources = state.getAllResourceNodes();
        HashSet<Vertex> avoidCoords = new HashSet<>();

        for (ResourceView resource : resources) {
            avoidCoords.add(new Vertex(resource.getXPosition(), resource.getYPosition()));
        }

        while(!queue.isEmpty()) {
            Path oldPath = queue.poll();
            Vertex target = oldPath.getDestination();
            visited.add(target);
            Vertex east = new Vertex(target.getXCoordinate()+1, target.getYCoordinate());
            if (!visited.contains(east) && !avoidCoords.contains(east) && state.inBounds(target.getXCoordinate()+1, target.getYCoordinate())) {
                if (east.equals(goal)) return oldPath;
                visited.add(east);
                queue.add(new Path(east, 1f, oldPath));
            }
            Vertex west = new Vertex(target.getXCoordinate()-1, target.getYCoordinate());
            if (!visited.contains(west) && !avoidCoords.contains(west) && state.inBounds(target.getXCoordinate()-1, target.getYCoordinate())) {
                if (west.equals(goal)) return oldPath;
                visited.add(west);
                queue.add(new Path(west, 1f, oldPath));
            }
            Vertex north = new Vertex(target.getXCoordinate(), target.getYCoordinate()-1);
            if (!visited.contains(north) && !avoidCoords.contains(north) && state.inBounds(target.getXCoordinate(), target.getYCoordinate()-1))  {
                if (north.equals(goal)) return oldPath;
                visited.add(north);
                queue.add(new Path(north, 1f, oldPath));
            }
            Vertex south = new Vertex(target.getXCoordinate(), target.getYCoordinate()+1);
            if (!visited.contains(south) && !avoidCoords.contains(south) && state.inBounds(target.getXCoordinate(), target.getYCoordinate()+1))  {
                if (south.equals(goal)) return oldPath;
                visited.add(south);
                queue.add(new Path(south, 1f, oldPath));
            }
            Vertex northeast = new Vertex(target.getXCoordinate()+1, target.getYCoordinate()-1);
            if (!visited.contains(northeast) && !avoidCoords.contains(northeast) && state.inBounds(target.getXCoordinate()+1, target.getYCoordinate()-1)) {
                if (northeast.equals(goal)) return oldPath;
                visited.add(northeast);
                queue.add(new Path(northeast, 1f, oldPath));
            }
            Vertex southeast = new Vertex(target.getXCoordinate()+1, target.getYCoordinate()+1);
            if (!visited.contains(southeast) && !avoidCoords.contains(southeast) && state.inBounds(target.getXCoordinate()+1, target.getYCoordinate()+1)) {
                if (southeast.equals(goal)) return oldPath;
                visited.add(southeast);
                queue.add(new Path(southeast, 1f, oldPath));
            }
            Vertex northwest = new Vertex(target.getXCoordinate()-1, target.getYCoordinate()-1);
            if (!visited.contains(northwest) && !avoidCoords.contains(northwest) && state.inBounds(target.getXCoordinate()-1, target.getYCoordinate()-1)) {
                if (northwest.equals(goal)) return oldPath;
                visited.add(northwest);
                queue.add(new Path(northwest, 1f, oldPath));
            }
            Vertex southwest = new Vertex(target.getXCoordinate()-1, target.getYCoordinate()+1);
            if (!visited.contains(southwest) && !avoidCoords.contains(southwest) && state.inBounds(target.getXCoordinate()-1, target.getYCoordinate()+1)) {
                if (southwest.equals(goal)) return oldPath;
                visited.add(southwest);
                queue.add(new Path(southwest, 1f, oldPath));
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
            if (avoidCoords.contains(vertex)) return true;
        }
        return false; 
    }
}
