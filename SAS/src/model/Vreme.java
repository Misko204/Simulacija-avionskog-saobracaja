package model;

public class Vreme implements Comparable<Vreme> {
	private int sati, minuti;	// sati i minuti
	
	public Vreme(int sati, int minuti) throws IllegalArgumentException {
		// Ako su sati manji od 0 ili veći od 23, "baca" se greška
		if (sati < 0 || sati > 23) {
			throw new IllegalArgumentException("Sati moraju biti između 0 i 23");
		}
		
		// Ako su minuti manji od 0 ili veći od 59, "baca" se greška
		if (minuti < 0 || minuti > 59) {
			throw new IllegalArgumentException("Minuti moraju biti između 0 i 59");
		}
		
		this.sati = sati;
		this.minuti = minuti;
	}
	
	public int getSati() {
		return sati;
	}
	
	public int getMinuti() {
		return minuti;
	}
	
	// Dodaje minute i računa novo vreme
	public Vreme dodajMinute(int minuti) {
		int ukupnoMinuta = this.ukupnoMinuta() + minuti;
		
		ukupnoMinuta = ukupnoMinuta % (24 * 60);
		
		if (ukupnoMinuta < 0) {
			ukupnoMinuta += (24 * 60);
		}
		
		return new Vreme(ukupnoMinuta / 60, ukupnoMinuta % 60);
	}
	
	// Koliko je ukupno minuta prošlo od ponoći
	public int ukupnoMinuta() {
		return sati * 60 + minuti;
	}
	
	@Override
	public String toString() {
		return String.format("%02d:%02d", sati, minuti);
	}
	
	// Objekti vreme su jednaki ako su minuti i sati jednaki
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || !(obj instanceof Vreme)) return false;
		Vreme vreme = (Vreme) obj;
		return this.sati == vreme.sati && this.minuti == vreme.minuti;
	}
	
	@Override
	public int compareTo(Vreme vreme) {
		return Integer.compare(this.ukupnoMinuta(), vreme.ukupnoMinuta());
	}
	
}
