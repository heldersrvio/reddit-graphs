package EDIIGraph;

import org.graphstream.algorithm.AStar;
import org.graphstream.algorithm.ConnectedComponents;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphStreamGraphTest {
    public static void main(String[] args){
        ArrayList<SubRConnection> buffer = new ArrayList<SubRConnection>();
        HashMap<String, ArrayList<SubRConnection>> hm = new HashMap<String, ArrayList<SubRConnection>>();
        SubRConnection bf = new SubRConnection("ZiaFox", SubRConnection.BLUE);
        buffer.add(bf);
        buffer.add(bf);
        bf = new SubRConnection("ukpolitics", SubRConnection.BLUE);
        buffer.add(bf);
        bf = new SubRConnection("glasgow", SubRConnection.BLUE);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        bf = new SubRConnection("help", SubRConnection.BLUE);
        buffer.add(bf);
        bf = new SubRConnection("The_Farage", SubRConnection.RED);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        bf = new SubRConnection("ukpolitics", SubRConnection.RED);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        buffer.add(bf);
        bf = new SubRConnection("Animemes", SubRConnection.RED);
        buffer.add(bf);
        buffer.add(bf);
        bf = new SubRConnection("Sinvicta", SubRConnection.RED);
        buffer.add(bf);
        hm.put("Scotland", buffer);
        GraphStreamGraph gsg = new GraphStreamGraph(hm);
        ConnectedComponents cc = new ConnectedComponents();
        cc.init(gsg.graph);
        System.out.println("Numero de vertices: " + gsg.graph.getNodeCount());
        System.out.println("Numero de arestas: " + gsg.graph.getEdgeCount());
        System.out.println("Componentes conectados: " + cc.getConnectedComponentsCount());
        AStar astar = new AStar(gsg.graph);
        astar.compute("Animemes", "The_Farage");
        System.out.println("Percurso entre r/Animemes e r/The_Farage: " + astar.getShortestPath());
    }
}
