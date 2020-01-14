package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	private class EdgeTraversedGraphListener implements TraversalListener<Fermata, DefaultEdge>  {

		@Override
		public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
			
		}

		@Override
		public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> ev) {

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
			
			if( !backVisit.containsKey(targetVertex) && backVisit.containsKey(sourceVertex)) {			
				backVisit.put(targetVertex, sourceVertex);
				
			} else if(backVisit.containsKey(targetVertex) && !backVisit.containsKey(sourceVertex)) {			
				backVisit.put(sourceVertex, targetVertex);
			}
				
			
		}

		@Override
		public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
			
		}

		@Override
		public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
			
		}
		
	}
	
	private Graph<Fermata, DefaultEdge> grafo;
	private Map<Integer, Fermata> fermateIdMap;
	private MetroDAO dao;
	private List<Fermata> fermate;
	Map<Fermata, Fermata> backVisit;
	
	
	public void creaGrafo() {
		
		//Creo l'oggetto grafo
		this.grafo = new SimpleDirectedGraph<Fermata, DefaultEdge>(DefaultEdge.class);
		
		dao = new MetroDAO();
		//Aggiungo i vertici
		this.fermate = dao.getAllFermate();
		
		//Creo idMap
		this.fermateIdMap = new HashMap<Integer, Fermata>();
		for(Fermata f: this.fermate) 
			fermateIdMap.put(f.getIdFermata(), f);  //Aggiungo le info anche nella mappa
		
		Graphs.addAllVertices(grafo, fermate);
		
		//Aggiungo gli archi
		
		// -------- OPZIONE 1 --------- //
		
//		for(Fermata partenza: this.grafo.vertexSet()) {
//			for(Fermata arrivo: this.grafo.vertexSet()) {
//				
//				if(dao.esisteConnessione(partenza,arrivo)) {
//					this.grafo.addEdge(partenza, arrivo);
//				}
//			}
//		}
		
		// -------- OPZIONE 2 -------- //
		
		for(Fermata partenza: this.grafo.vertexSet()) {
			
			List<Fermata> arrivi = dao.stazioneArrivo(partenza, fermateIdMap);
			for(Fermata arrivo: arrivi) {
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		
		// -------- OPZIONE 3 -------- //
		
		//tutto col db
		
	}
	
	public List<Fermata> fermateRaggiungibili(Fermata source) {
		
		List<Fermata> result = new ArrayList<Fermata>();
		backVisit = new HashMap<Fermata, Fermata>();
				
		
		//Creo un nuovo iteratore e lo associo al grafo e ha bisogno di un parametro di partenza
		
//		VISITA IN AMPIEZZA
		GraphIterator<Fermata, DefaultEdge> it = new BreadthFirstIterator<Fermata, DefaultEdge>(this.grafo, source);
		
//		 VISITA IN PROFONDITA
//		GraphIterator<Fermata, DefaultEdge> it = new DepthFirstIterator<Fermata, DefaultEdge>(this.grafo, source);
		
		
		it.addTraversalListener(new Model.EdgeTraversedGraphListener());
		
		/* it.addTraversalListener(new TraversalListener<Fermata, DefaultEdge>() {

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
				// TODO Auto-generated method stub
				
			}						
		});
		*/
		
		backVisit.put(source, null);
		
		while(it.hasNext()) {
			result.add(it.next());
		}
		
		return result;
		
	}

	public List<Fermata> percorsoFinoA(Fermata target) {
		
		if(!backVisit.containsKey(target)) {
			//Il target non è raggiungibile dalla source
			return null;			
		}
		
		List<Fermata> percorso = new LinkedList<Fermata>();
		Fermata f = target;
		while(f != null) { //Ripercorro indietro l'albero
			
			percorso.add(0, f);			
			//Torno al padre
			f = backVisit.get(f);
		}
		return percorso;
	}
	
	
	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}
}
