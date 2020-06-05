package EDIIGraph;
import java.util.*;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GraphStreamGraph {

    Graph graph;

    public GraphStreamGraph(HashMap<String, ArrayList<SubRConnection>> hm){
        //System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = new MultiGraph("subredditsgraph");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        Iterator it = hm.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            for (int i = 0; i < ((List) pair.getValue()).size(); i++){
                Edge e = graph.addEdge(pair.getKey() + ((SubRConnection)((List) pair.getValue()).get(i)).getSubreddit() + i, (String)pair.getKey(), ((SubRConnection)((List) pair.getValue()).get(i)).getSubreddit());
                if (((SubRConnection)((List) pair.getValue()).get(i)).isColor())
                    e.addAttribute("ui.class", "blue");
                else
                    e.addAttribute("ui.class", "red");
            }
        }
        for (Node node : graph) {
            double n = node.getDegree()*0.75;
            double p = n;
            if (n > 20)
                n = 20;
            if (p > 15)
                p = 15;
            if (p < 3)
                p = 3;
            node.setAttribute("ui.style", "text-style: bold;text-alignment: center;text-size:" + 1.5*p + ";size:"+ n +"px;");
            node.addAttribute("ui.label", node.getId());
        }
        graph.addAttribute("ui.stylesheet", styleSheet);
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.antialias");

        graph.display();
    }

    protected String styleSheet =
            "edge.blue {" + "	fill-color: blue;" + "}" + "edge.red {" + "	fill-color: red;" + "}";

}
