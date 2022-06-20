package it.polito.tdp.yelp.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.yelp.model.Evento.EventType;

public class Simulatore {

	//Dati in ingresso
	private int x1;
	private int x2;
	
	//Dati in uscita
	private List<Giornalista> giornalisti;
	private int numGiorni;
	
	//Modello del mondo
	private Set<User> intervistati;
	private Graph<User, DefaultWeightedEdge> grafo;
	
	//Coda eventi
	private PriorityQueue<Evento> queue;
	

	public List<Giornalista> getGiornalisti() {
		return giornalisti;
	}

	public int getNumGiorni() {
		return numGiorni;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	
	
	
	public Simulatore(Graph<User, DefaultWeightedEdge> grafo) {
		this.grafo=grafo;
		
	}
	
	public void init(int x1,int x2) {
		this.x1=x1;
		this.x2=x2;
		
		this.intervistati= new HashSet<User>();
		
		this.numGiorni=0;
		
		this.giornalisti= new ArrayList<Giornalista>();
		for(int id=0; id<this.x1; id++) {
			this.giornalisti.add(new Giornalista(id));
		}
		
		//Precarico la coda;
		for(Giornalista g: giornalisti) {
			User intervistato= selezionaIntervistato(this.grafo.vertexSet());
			
			this.intervistati.add(intervistato);
			g.incrementaNumIntervistati();
			this.queue= new PriorityQueue<>();
			this.queue.add(new Evento(1, EventType.DA_INTERVISTARE, intervistato, g));
		}
		                            
		
	}
	
	public void run() {
		
		
		while(!this.queue.isEmpty() && this.intervistati.size()<this.x2) {
			Evento e= this.queue.poll();
			this.numGiorni= e.getGiorno();
			
			
			//Elaboro
			processEvent(e);
		}
	}
	
	
	private void processEvent(Evento e) {
		switch(e.getType()) {
		case DA_INTERVISTARE:
			
			double caso= Math.random();
			
			if(caso<0.6) {
				//CASO 1
				User vicino= selezionaAdiacente(e.getIntervistato());
				if(vicino==null) {
					vicino= selezionaIntervistato(this.grafo.vertexSet());
				}
				this.queue.add(new Evento(e.getGiorno()+1, EventType.DA_INTERVISTARE,vicino, e.getGiornalista()));
				
				this.intervistati.add(vicino);
				e.getGiornalista().incrementaNumIntervistati();
				
				
			}else if(caso<0.8) {
				//CASO 2
				this.queue.add(new Evento(e.getGiorno()+1, EventType.FERIE, 
						e.getIntervistato(), e.getGiornalista()));
				
			}else {
				//CASO 3 : domani continuo con stesso utente
				this.queue.add(new Evento(e.getGiorno()+1, EventType.DA_INTERVISTARE, 
						e.getIntervistato(), e.getGiornalista()));
				
			}
			
			break;
			
		case FERIE:
			
			User vicino = selezionaAdiacente(e.getIntervistato());
			if(vicino == null) {
				vicino = selezionaIntervistato(this.grafo.vertexSet());
			}
			
			this.queue.add( new Evento(
					e.getGiorno()+1,
					EventType.DA_INTERVISTARE,
					vicino,
					e.getGiornalista()
					)) ;
			
			this.intervistati.add(vicino);
			e.getGiornalista().incrementaNumIntervistati();;

			break;
		}
		
	}

	private User selezionaIntervistato(Collection<User> lista) {
		//Seleziona intervistato dalla lista, evitando quelli gia intervistati
		List<User> candidati= new ArrayList<User>(lista);
		candidati.removeAll(this.intervistati);
		
		int scelto = (int)Math.random()*candidati.size();
		return candidati.get(scelto);
	}
	
	private User selezionaAdiacente(User u) {
		List<User> vicini= Graphs.neighborListOf(this.grafo, u);
		vicini.removeAll(this.intervistati);
		if(vicini.size()==0) {
			//Vertice   isolato o tutti i vicini sono già stati intervistati
			return null;
		}
		
		double max=0;
		for(User v: vicini) {
			double peso=this.grafo.getEdgeWeight(this.grafo.getEdge(u, v));
			if(peso>max) {
				max=peso;
			}
		}
		
		List<User> migliori= new ArrayList<>();
		for(User v: vicini) {
			double peso=this.grafo.getEdgeWeight(this.grafo.getEdge(u, v));
			if(peso==max) {
				migliori.add(v);
			}
		}
		
		int scelto= (int) Math.random()*migliori.size();
		return migliori.get(scelto);
	}
}
