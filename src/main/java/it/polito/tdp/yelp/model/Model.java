package it.polito.tdp.yelp.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private YelpDao dao;
	private Graph<User, DefaultWeightedEdge> grafo;
	private Map<String, User> idMap;
	private List<User> vertici;
	private int max;
	
	// risultati del simulatore
	private int numeroGiorni ;
	private List<Giornalista> giornalisti ;
		
	
	public Model() {
		this.dao= new YelpDao();
		
	}
	
	public void CreaGrafo(int n, int anno) {
		this.grafo= new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.idMap= new HashMap<String, User>();
		this.vertici= new ArrayList<User>();
		
		//Popolo idMap
		for(User u: dao.getAllUsers()) {
			idMap.put(u.getUserId(), u);
		}
		
		//Aggiungo i vertici
		this.vertici.addAll(dao.getUserConNRecensioni(n, idMap));
		Graphs.addAllVertices(this.grafo, dao.getUserConNRecensioni(n, idMap) );
		
		//Aggiungo gli archi
		for(User u1:this.vertici ) {
			for(User u2:this.vertici) {
				if(u2.getUserId().compareTo(u1.getUserId())<0 && !u1.equals(u2)) {
					int peso=dao.getPesoArco(u1, u2, anno);
					if(peso>0) {
						Graphs.addEdgeWithVertices(this.grafo, u1, u2, peso);
					}
				}
			}
		}
		
		System.out.println("Grafo creato.");
		System.out.println("#VERTICI: "+grafo.vertexSet().size());
		System.out.println("#ARCHI: "+grafo.edgeSet().size());
	}
	
	public List<User> getVertici(){
		
		return this.vertici;
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<User> calcolaSimile(User utente){
		max=0;
		for(DefaultWeightedEdge e: this.grafo.edgesOf(utente)) {
			if(this.grafo.getEdgeWeight(e)>max) {
				max= (int)this.grafo.getEdgeWeight(e);
			}
		}
		List<User> result= new ArrayList<User>();
		for(DefaultWeightedEdge e: this.grafo.edgesOf(utente)) {
			if(this.grafo.getEdgeWeight(e)==max) {
				result.add(Graphs.getOppositeVertex(this.grafo, e, utente));
			}
		}
		return result;
	}
	
	public int getMax() {
		return this.max;
	}
	
	
	public void simula(int intervistatori, int utenti) {
		Simulatore sim = new Simulatore(this.grafo);
		sim.init(intervistatori, utenti);
		sim.run();
		this.giornalisti = sim.getGiornalisti();
		this.numeroGiorni = sim.getNumGiorni();
	}
	
	public List<User> getUsers() {
		return this.vertici;
	}

	public int getNumeroGiorni() {
		return numeroGiorni;
	}

	public List<Giornalista> getGiornalisti() {
		return giornalisti;
	}
}
