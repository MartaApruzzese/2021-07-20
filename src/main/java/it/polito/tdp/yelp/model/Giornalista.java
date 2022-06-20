package it.polito.tdp.yelp.model;

public class Giornalista {

	private int id;
	private int numIntevistati=0;
	
	public void setId(int id) {
		this.id = id;
	}

	public void setNumIntevistati(int numIntevistati) {
		this.numIntevistati = numIntevistati;
	}

	public Giornalista(int id) {
		super();
		this.id = id;
	}
	
	public void incrementaNumIntervistati() {
		this.numIntevistati++;
	}

	public int getId() {
		return id;
	}

	public int getNumIntevistati() {
		return numIntevistati;
	}
	
	
}
