package model;

public class Avion {
	private Let let;				// za koji let je ovaj avion vezan
	private double trenutnaX;		// trenutna x koordinata aviona
	private double trenutnaY;		// trenutna y koordinata aviona
	private double pocetnaX;		// pocetna x koordinata aviona
	private double pocetnaY;		// pocetna y koordinata aviona
	private double krajnjaX;		// krajnja x koordinata aviona
	private double krajnjaY;		// krajnja y koordinata aviona
	private Vreme vremePoletanja;	// vreme kada je zapravo avion poleteo
	private Vreme vremeSletanja;	// vreme kada avion sleće
	private boolean uLetu;			// da li je avion trenutno u letu
	private boolean prikazan;		// da li je avion prikazan na mapi
	private boolean sleteo;			// da li je avion sleteo
	
	public Avion(Let let, Vreme vremePoletanja) {
		if (let == null) {
			throw new IllegalArgumentException("Let ne može biti null");
		}
		
		if (vremePoletanja == null) {
			throw new IllegalArgumentException("Vreme poletanja ne može biti null");
		}
		
		this.let = let;
		this.pocetnaX = let.getPolazniAerodrom().getX();
		this.pocetnaY = let.getPolazniAerodrom().getY();
		this.krajnjaX = let.getOdredisniAerodrom().getX();
		this.krajnjaY = let.getOdredisniAerodrom().getY();
		this.vremePoletanja = vremePoletanja;
		this.vremeSletanja = vremePoletanja.dodajMinute(let.getTrajanjeMinuta());
		this.uLetu = false;
		this.prikazan = false;
		this.sleteo = false;
		
		this.trenutnaX = let.getPolazniAerodrom().getX();
		this.trenutnaX = let.getPolazniAerodrom().getY();
	}
	
	public Let getLet() {
		return this.let;
	}
	
	public double getTrenutnaX() {
		return this.trenutnaX;
	}
	
	public double getTrenutnaY() {
		return this.trenutnaY;
	}
	
	public void setVremePoletanja(Vreme novo) {
		this.vremePoletanja = novo;
		this.vremeSletanja = vremePoletanja.dodajMinute(let.getTrajanjeMinuta());
	}
	
	public Vreme getVremePoletanja() {
		return this.vremePoletanja;
	}
	
	public Vreme getVremeSletanja() {
		return this.vremeSletanja;
	}
	
	public boolean isULetu() {
		return this.uLetu;
	}
	
	public boolean sleteo() {
		return this.sleteo;
	}
	
	public boolean prikazan() {
		return prikazan;
	}
	
	public void pokreniLet() {
		this.prikazan = true;
		this.uLetu = true;
	}
	
	// završava se let
	public void zavrsiLet() {
		this.uLetu = false;
		this.prikazan = false;
		this.sleteo = true;
		this.trenutnaX = this.let.getOdredisniAerodrom().getX();
		this.trenutnaY = this.let.getOdredisniAerodrom().getY();
	}
	
	// ažurira se pozicija aviona
	public void azurirajPoziciju(Vreme trenutnoVreme) {
		if (!this.uLetu) {
			return;
		}
		
		double x1 = pocetnaX;
		double y1 = pocetnaY;
		double x2 = krajnjaX;
		double y2 = krajnjaY;
		
		// koliko minuta je proteklo od početka leta
		int protekloMinuta = trenutnoVreme.ukupnoMinuta() - vremePoletanja.ukupnoMinuta();
		
		// ako je let pređe u sledeći dan, gornja razlika će biti manja od 0
		if (protekloMinuta < 0) {
			protekloMinuta += 24 * 60;
		}
		
		// koliko posto leta obavljeno
		double procenat = (double) protekloMinuta / this.let.getTrajanjeMinuta();
		
		// let se završava kada je procenat 100%
		if (procenat >= 1.00) {
			zavrsiLet();
			return;
		}
        
        this.trenutnaX = x1 + (x2 - x1) * procenat;
        this.trenutnaY = y1 + (y2 - y1) * procenat;
	}
	
	@Override
	public String toString() {
		return "Avion[" + let.getPolazniAerodrom().getKod() + "->" + 
				let.getOdredisniAerodrom().getKod() + ", poletanje: " + 
	            vremePoletanja + ", status: " + (uLetu ? "u letu" : "na zemlji") + "]";
	}
	
}
