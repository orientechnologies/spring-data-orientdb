package org.springframework.data.orientdb.graph;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrepareGraphDatabase {

    private static final Logger logger = LoggerFactory.getLogger(PrepareGraphDatabase.class);

    public static void main(String[] args) {
        
        OrientGraphFactory factory = new OrientGraphFactory("memory:spring-data-orientdb-graph");
        
        OrientGraph graph = factory.getTx();
        
        try {
            //graph.createVertexType("Person");
//            Vertex person = graph.addVertex("Person", "Person");
//            person.setProperty("name", "Dzmitry");
//            graph.commit();

            for (Vertex vertex : graph.getVertices()) {
                logger.debug("Vertex: {}", vertex);
                for (Edge edge : vertex.getEdges(Direction.BOTH)) {
                    logger.debug("Edge: {}", edge);
                }

                logger.debug("Property keys: {}", vertex.getPropertyKeys());
            }

            for (Edge edge : graph.getEdges()) {
                logger.debug("Edge: {}", edge);
            }
            
            //graph.addEdge(null, graph.getVertex("#13:0"), graph.getVertex("#13:0"), "link");
        } finally {
            graph.shutdown();
        }
    }
}
