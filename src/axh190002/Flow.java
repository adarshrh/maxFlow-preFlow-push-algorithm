/**
 * IDSA Long Project 5
 * Group members:
 * Adarsh Raghupati   axh190002
 * Akash Akki         apa190001
 * Keerti Keerti      kxk190012
 * Stewart cannon     sjc160330
 */

package axh190002;

import axh190002.Graph.Edge;
import axh190002.Graph.Factory;
import axh190002.Graph.GraphAlgorithm;
import axh190002.Graph.Vertex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Flow extends GraphAlgorithm<Flow.FlowVertex> {

    Graph flowGraph;
    Vertex s,t;
    HashMap<Edge,Integer> capacity;
    HashMap<Edge,Integer> flow;  //Keeps track of flow in each edge
    Queue<Vertex> queue;

    /**
     * Initialize Graph, Source, Target and Capacity of edges
     * @param g
     * @param s
     * @param t
     * @param capacity
     */
    public Flow(Graph g, Vertex s, Vertex t, HashMap<Edge, Integer> capacity) {
        super(g,new FlowVertex((Vertex) null));
        flowGraph=g;
        this.s=s;
        this.t=t;
        this.capacity=capacity;
        this.flow= new HashMap<>(capacity.size());
        queue = new LinkedList<>();

    }


    public static class FlowVertex implements  Factory {
        int height; //Distance of target/sink from the vertex
        int excess;
        boolean seen;
        boolean inQueue; //To keep track of nodes present in the queue

        FlowVertex(Vertex u) {
            height = 0;
            excess=0;
            seen=false;
            inQueue=false;
        }

        /**
         * Used to create FlowVertex by  GraphAlgorithm
         * @param u
         * @return
         */
        public FlowVertex make(Vertex u) { return new FlowVertex(u); }

    }


    /**
     * Calculates Maximum flow of the graph using FIFO preflow-push algorithm
     *
     * @return
     */
    public int preflowPush() {
        initialize();
        while (queue.size()>0){
            Vertex u = queue.poll();
            get(u).inQueue=false;
            discharge(u);
            if(get(u).excess>0){
                relabel(u);
            }
        }

	return get(t).excess;
    }


    /**
     * Updates the flow going out of the vertex u
     * @param u
     */
    private void discharge(Vertex u) {
        FlowVertex cur = get(u);
        boolean flag = false;
        //For all outgoing edges (u,v)
        for(Edge edge: flowGraph.outEdges(u)){
            Vertex v = edge.to;
            if(cur.height == get(v).height+1 && (capacity(edge)-flow(edge))>0){
                int delta = Math.min(cur.excess,capacity(edge)-flow(edge));
                if(delta > 0){
                    flag=true;
                    flow.put(edge,flow(edge)+delta);
                    cur.excess = cur.excess-delta;
                    get(v).excess = get(v).excess+delta;
                    if(!get(v).inQueue && v.name!=s.name && v.name!=t.name){
                        get(v).inQueue=true;
                        queue.add(v);
                    }
                    if(cur.excess==0){
                        return;
                    }
                }

            }
        }
        //If there are no outgoing edges; check for outgoing edges in residual graph
        if(!flag){
            //In edges in the given graph will be outgoing edges in residual graph provided some flow exists in the edge
            for(Edge edge: flowGraph.inEdges(u)){
                Vertex v = edge.from;
                if(cur.height == get(v).height+1 && flow(edge)>0){
                    int delta = Math.min(cur.excess,flow(edge));
                    if(delta > 0){
                        //push back the excess flow in the residual edge
                        flow.put(edge,flow(edge)-delta);
                        cur.excess = cur.excess-delta;
                        get(v).excess = get(v).excess+delta;
                        if(!get(v).inQueue && v.name!=s.name && v.name!=t.name){
                            get(v).inQueue=true;
                            queue.add(v);
                        }
                        if(cur.excess==0){
                            return;
                        }
                    }

                }
            }
        }


    }


    /**
     * Updates the height of the vertex and adds the vertex to the queue
     * @param u
     */
    private void relabel(Vertex u) {
        int ht=Integer.MAX_VALUE;
        //Check outgoing edges
        for(Edge edge: flowGraph.outEdges(u)){
            if((capacity(edge)-flow(edge))>0) {
                ht = Math.min(ht, get(edge.to).height);
            }
        }
        //Check outgoing edges in residual graph
            for(Edge edge: flowGraph.inEdges(u)){
                if(flow(edge)>0) {
                    ht = Math.min(ht, get(edge.from).height);
                }
            }
        get(u).height = 1+ht;
        get(u).inQueue=true;
        queue.add(u);
    }

    /**
     * Initialize all vertex with height,excess and edges with flow 0
     */
    public void initialize(){
        //Set initial flow of all edges to 0
       for(Edge edge : flowGraph.getEdgeArray()){
           flow.put(edge,0);
       }
       initializeHeight();
       for(Edge edge: flowGraph.outEdges(s)){
           int cp = capacity(edge);
           flow.put(edge,cp);
           get(s).excess = get(s).excess-cp;
           get(edge.to).excess = get(edge.to).excess + cp;
           get(edge.to).inQueue=true;
           this.queue.add(edge.to);
       }
       get(s).height = flowGraph.size();

    }

    /**
     * Calculates height of the vertex from the sink/target
     */
    public void initializeHeight(){
        get(t).height = 0;
        Queue<Vertex> queue = new LinkedList<>();
        queue.add(t);
        while (queue.size()>0){
            Vertex current = queue.poll();
            int hop = get(current).height;
            for(Edge edge : flowGraph.inEdges(current)){
                FlowVertex v = get(edge.from);
                if(!v.seen){
                    v.height = hop+1;
                    v.seen = true;
                    queue.add(edge.from);
                }
            }
        }
    }


    /**
     * Returns flow through the given edge
     * @param e
     * @return
     */
    public int flow(Edge e) {
	return flow.get(e);
    }

    /**
     * Returns the capacity of the given edge
     * @param e
     * @return
     */
    public int capacity(Edge e) {
	return capacity.get(e);
    }

    /* After maxflow has been computed, this method can be called to
       get the "S"-side of the min-cut found by the algorithm
    */
    public Set<Vertex> minCutS() {
	return null;
    }

    /* After maxflow has been computed, this method can be called to
       get the "T"-side of the min-cut found by the algorithm
    */
    public Set<Vertex> minCutT() {
	return null;
    }
}
