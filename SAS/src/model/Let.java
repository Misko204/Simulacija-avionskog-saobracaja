package model;

public class Let {
	private Aerodrom polazniAerodrom;		// polazni aerodrom
	private Aerodrom odredisniAerodrom;		// odredišni aerodrom
	private Vreme vremePoletanja;			// zakazano vreme za poletanje
	private int trajanjeMinuta;				// trajanje leta u minutima
	
	public Let(Aerodrom polazniAerodrom, Aerodrom odredisniAerodrom, 
			Vreme vremePoletanja, int trajanjeMinuta) throws IllegalArgumentException {
		// Ako je polazni aerodrom null, "baca" se greška
		if (polazniAerodrom == null) {
			throw new IllegalArgumentException("Mora da postoji polazni aerodrom");
		}
		
		// Ako je odredišni aerodrom null, "baca" se greška
		if (odredisniAerodrom == null) {
			throw new IllegalArgumentException("Mora da postoji odredišni aerodrom");
		}
		
		// Ako su polazni i odredišni aerodromi isti, "baca" se greška
		if (polazniAerodrom.equals(odredisniAerodrom)) {
			throw new IllegalArgumentException("Polazni i odredišni aerodrom ne mogu biti isti");
		}
		
		// Ako je vreme poletanja null, "baca" se greška
		if (vremePoletanja == null) {
			throw new IllegalArgumentException("Mora da postoji vreme poletanja");
		}
		
		// Ako je trajanje leta manje od 0, "baca" se greška
		if (trajanjeMinuta <= 0) {
			throw new IllegalArgumentException("Trajanje leta mora biti pozitivno (uneseno: "
					+ trajanjeMinuta + ")");
		}
		
		this.polazniAerodrom = polazniAerodrom;
		this.odredisniAerodrom = odredisniAerodrom;
		this.vremePoletanja = vremePoletanja;
		this.trajanjeMinuta = trajanjeMinuta;
	}

	public Aerodrom getPolazniAerodrom() {
		return polazniAerodrom;
	}

	public Aerodrom getOdredisniAerodrom() {
		return odredisniAerodrom;
	}

	public Vreme getVremePoletanja() {
		return vremePoletanja;
	}

	public int getTrajanjeMinuta() {
		return trajanjeMinuta;
	}
	
	public Vreme getVremeSletanja() {
		return vremePoletanja.dodajMinute(trajanjeMinuta);
	}
	
	@Override
	public String toString() {
		return polazniAerodrom.getKod() + " -> " + odredisniAerodrom.getKod() + " | Poletanje: "
				+ vremePoletanja + " | Trajanje: " + trajanjeMinuta + "min" + " | Sletanje: "
				+ getVremeSletanja();
	}
	
	// Letovi su jednaki ako imaju sve parametre iste
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || !(obj instanceof Let)) return false;
		Let let = (Let) obj;
		return this.polazniAerodrom.equals(let.polazniAerodrom) 
				&& this.odredisniAerodrom.equals(let.odredisniAerodrom)
				&& this.vremePoletanja.equals(let.vremePoletanja) 
				&& this.trajanjeMinuta == let.trajanjeMinuta;
	}
}
