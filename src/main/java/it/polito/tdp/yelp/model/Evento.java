package it.polito.tdp.yelp.model;

public class Evento implements Comparable<Evento>{
	
	public enum EventType {
		DA_INTERVISTARE,
		FERIE,
	}
	
	private int giorno;
	private User intervistato;
	private Giornalista giornalista;
	private EventType type;
	
	
	public Evento(int giorno, EventType type, User intervistato, Giornalista giornalista) {
		super();
		this.giorno = giorno;
		this.type=type;
		this.intervistato = intervistato;
		this.giornalista = giornalista;
	}
	
	
	public int getGiorno() {
		return giorno;
	}
	public User getIntervistato() {
		return intervistato;
	}
	public Giornalista getGiornalista() {
		return giornalista;
	}
	public EventType getType() {
		return type;
	}


	public int compareTo(Evento other) {
		return this.giorno-other.giorno;
	}
	
	
}
