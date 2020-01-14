package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class EdgeTraversedGraphListener implements TraversalListener<Fermata, DefaultEdge> {

	Graph<Fermata, DefaultEdge> grafo;
	Map<Fermata, Fermata> back;
	
	
	public EdgeTraversedGraphListener(Graph<Fermata, DefaultEdge> grafo, Map<Fermata, Fermata> back) {
		super();
		this.grafo = grafo;
		this.back = back;
	}

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {

		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {

		
	}

	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> ev) {

	/*
	 * Back codifica relazioni del tipo child -> parent (dal basso verso l'alto)
	 * 
	 * Per un nuovo vertice 'child' scoperto devo avere che:
	 * 
	 * 		- child è ancora sconosciuto (non ancora trovato)
	 * 		- parent è gia stato visitato
	 * 
	 */
		
		//trovo gli estremi dell'arco
		Fermata sourceVertex = grafo.getEdgeSource(ev.getEdge()); //parent
		Fermata targetVertex = grafo.getEdgeTarget(ev.getEdge()); //child
		
		//Soddisfano le condizioni sopra?
		
		/*
		 * Se il grafo è orientato source==parent, target==child
		 * Se il grafo non è orientato potrebbe anche essere il contrario
		 * 
		 */
		
		//Child è nuovo? Controllo se è nella mappa
		
		if( !back.containsKey(targetVertex) && back.containsKey(sourceVertex)) {			
			back.put(targetVertex, sourceVertex);
			
		} else if(back.containsKey(targetVertex) && !back.containsKey(sourceVertex)) {			
			back.put(sourceVertex, targetVertex);
		}
			
		
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
		
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {

		
	}

	
	
}
