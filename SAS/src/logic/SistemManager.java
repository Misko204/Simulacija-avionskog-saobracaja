package logic;

import java.util.ArrayList;
import java.util.List;

import model.Aerodrom;
import model.Let;
// SistemManager, čuva listu aerodroma i letova i obavlja akcije vezane za njih
public class SistemManager {
	private List<Aerodrom> aerodromi;
	private List<Let> letovi;
	
	public SistemManager() {
		this.aerodromi = new ArrayList<>();
		this.letovi = new ArrayList<>();
	}
	
	// dodavanje aerodroma
	public void dodajAerodrom(Aerodrom aerodrom) throws IllegalArgumentException, IllegalStateException {
		if (aerodrom == null) {
			throw new IllegalArgumentException("Aerodrom ne može biti null");
		}
		
		if (postojiAerodrom(aerodrom.getKod())) {
			throw new IllegalStateException("Aerodrom sa kodom " + aerodrom.getKod() 
					+ " ili sa koordinatama (" + aerodrom.getX() + ", " + aerodrom.getY() 
					+ ") već postoji u sistemu");
		}
		
		if (postojeKoordinate(aerodrom.getX(), aerodrom.getY())) {
	        throw new IllegalArgumentException(
	            "Aerodrom na koordinatama (" + aerodrom.getX() + ", " + 
	            aerodrom.getY() + ") već postoji u sistemu.\n" +
	            "Izaberite druge koordinate."
	        );
	    }
		
		aerodromi.add(aerodrom);
	}
	
	// brisanje aerodroma
	public void obrisiAerodrom(Aerodrom aerodrom) throws IllegalStateException {
		for (Let let : letovi) {
			if (let.getPolazniAerodrom().equals(aerodrom) || 
				let.getOdredisniAerodrom().equals(aerodrom)) {
					throw new IllegalStateException("Ne možete obrisati aerodrom " + aerodrom.getKod() 
							+ " jer postoje letovi povezani sa njim.\n "
							+ "Prvo obrišite sve povezane letove");
				}
		}
		
		aerodromi.remove(aerodrom);
	}
	
	// dodavanje letova
	public void dodajLet(Let let) throws IllegalStateException {
		if (let == null) {
			throw new IllegalArgumentException("Let ne može biti null");
		}
		
		if (postojiLet(let)) {
			throw new IllegalStateException("Let koji ide od aerodroma " + let.getPolazniAerodrom().getKod() 
					+ " do aerodroma " + let.getOdredisniAerodrom().getKod() + " sa vremenom poletanja "  
					+ let.getVremePoletanja() + " i trajanjem od " + let.getTrajanjeMinuta() + "min "
					+ "već postoji u sistemu");
		}
		
		letovi.add(let);
		sortirajLetove();
	}
	
	// sortiranje letova po vremenu poletanja
	private void sortirajLetove() {
		this.letovi.sort((l1, l2) -> l1.getVremePoletanja().compareTo(l2.getVremePoletanja()));
	}
	
	// da li postoji aerodrom sa datim koordinatama
	public boolean postojeKoordinate(double x, double y) {
	    for (Aerodrom a : aerodromi) {
	        if (a.getX() == x && a.getY() == y) {
	            return true;
	        }
	    }
	    return false;
	}
	
	// brisanje letova
	public void obrisiLet (Let let) {
		letovi.remove(let);
	}

	public List<Aerodrom> getAerodromi() {
		return new ArrayList<Aerodrom>(aerodromi);
	}

	public List<Let> getLetovi() {
		return new ArrayList<Let>(letovi);
	}
	
	public Aerodrom nadjiAerodrom(String kod) {
		for (Aerodrom a : aerodromi) {
			if (a.getKod().equals(kod)) return a;
		}
		
		return null;
	}

	// da li postoji aerodrom sa određenim kodom
	public boolean postojiAerodrom(String kod) {
		return nadjiAerodrom(kod) != null;
	}
	
	// pronalazak leta
	public Let nadjiLet(Let let) {
		for (Let l : letovi) {
			if (l.equals(let)) return l;
		}
		
		return null;
	}
	
	public boolean postojiLet(Let let) {
		return nadjiLet(let) != null;
	}
	
	// pražnjenje lista aerodroma i letova
	public void ocistiSve () {
		letovi.clear();
		aerodromi.clear();
	}
}
