package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import logic.SistemManager;
import model.Aerodrom;
import model.Let;
import model.Vreme;

public class CSVManager {
	// Učitava podatke iz fajla
	// Format: AERODROM,Naziv,Kod,x,y
	// Format: LET,Polazni,Odredišni,Sati,Minuti,Trajanje
	public static void ucitajIzFajla(String putanja, SistemManager manager) 
			throws FileNotFoundException, IOException, InvalidFormatException {
		
		File file = new File(putanja);
		if (!file.exists()) {
			throw new FileNotFoundException("Fajl ne postoji: " + putanja + "\n"
					+ "Proverite putanju i pokušajte ponovo");
		}
		
		List<Aerodrom> aerodromi = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String linija;
			int brojLinije = 0;
			
			while ((linija = br.readLine()) != null) {
				brojLinije++;
				linija = linija.trim();
				
				if (linija.isEmpty() || linija.startsWith("#")) {
					continue;
				}
				
				String[] delovi = linija.split(",");
				if (delovi.length == 0) {
					continue;
				}
				
				String tip = delovi[0].trim().toUpperCase();
				
				try {
					if (tip.equals("AERODROM")) {
						if (delovi.length != 5) {
							throw new InvalidFormatException("Linija " + brojLinije + ": Aerodrom"
									+ " mora imati tačno 5 polja.\n "
									+ "Format: AERODROM,naziv,kod,x,y\n Pronađeno: " + linija);
						}
						
						Aerodrom a = parseAerodrom(delovi, brojLinije);
						aerodromi.add(a);
					} else if (tip.equals("LET")) {
						if (delovi.length != 6) {
							throw new InvalidFormatException("Linija " + brojLinije + ": Aerodrom"
									+ " mora imati tačno 5 polja.\n "
									+ "Format: LET,polazniKod,odredisniKod,sati,minuti,trajanje\n "
									+ "Pronađeno: " + linija);
						}
						
					} else {
						throw new InvalidFormatException("Linija " + brojLinije + ": Nepoznat tip reda"
								+ " '" + tip + "'.\n" + "Dozvoljeni tipovi: AERODROM, LET");
					}
				} catch (IllegalArgumentException e) {
					throw new InvalidFormatException("Linija " + brojLinije + ": " + e.getMessage());
				}
			}
		}
		
		manager.ocistiSve();
		for (Aerodrom a : aerodromi) {
			manager.dodajAerodrom(a);
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String linija;
			int brojLinije = 0;
			
			while ((linija = br.readLine()) != null) {
				brojLinije++;
				linija = linija.trim();
				
				if (linija.isEmpty() || linija.startsWith("#")) {
					continue;
				}
				
				String[] delovi = linija.split(",");
				if (delovi.length == 0) {
					continue;
				}
				
				String tip = delovi[0].trim().toUpperCase();
				
				if (tip.equals("LET")) {
					try {
						Let let = parseLet(delovi, manager, brojLinije);
						manager.dodajLet(let);
					} catch (IllegalArgumentException e) {
						throw new InvalidFormatException("Linija " + brojLinije + ": " + e.getMessage());
					}
				}
			}
		}
	}
	
	// Parsiranje aerodroma i kreiranje novog aerodroma
	private static Aerodrom parseAerodrom(String[] delovi, int brojLinije) 
			throws InvalidFormatException {
		
		try {
			String naziv = delovi[1].trim();
			String kod = delovi[2].trim().toUpperCase();
			double x = Double.parseDouble(delovi[3].trim());
			double y = Double.parseDouble(delovi[4].trim());
			
			return new Aerodrom(naziv, kod, x, y);
			
		} catch (NumberFormatException e) {
			throw new InvalidFormatException("Linija " + brojLinije + ": Koordinate moraju biti brojevi");
		} catch (IllegalArgumentException e) {
			throw new InvalidFormatException(e.getMessage());
		}
	}
	
	// Parsiranje leta i kreiranje novog leta
	private static Let parseLet(String[] delovi, SistemManager manager, int brojLinije) 
			throws InvalidFormatException {
		
		try {
			String polazniKod = delovi[1].trim().toUpperCase();
			String odredisniKod = delovi[2].trim().toUpperCase();
			int sati = Integer.parseInt(delovi[3].trim());
			int minuti = Integer.parseInt(delovi[4].trim());
			int trajanje = Integer.parseInt(delovi[5].trim());
			
			Aerodrom polazni = manager.nadjiAerodrom(polazniKod);
			if (polazni == null) {
				throw new InvalidFormatException("Aerodrom sa kodom " + polazniKod 
						+ "ne postoji u sistemu");
			}
			
			Aerodrom odredisni = manager.nadjiAerodrom(odredisniKod);
			if (odredisni == null) {
				throw new InvalidFormatException("Aerodrom sa kodom " + odredisniKod
						+ "ne postoji u sistemu");
			}
			
			Vreme vreme = new Vreme(sati, minuti);
			
			return new Let(polazni, odredisni, vreme, trajanje);
			
		} catch (IllegalArgumentException e) {
			throw new InvalidFormatException(e.getMessage());
		}
	}
	
	// Čuvanje podataka u fajl
	// Format: AERODROM,Naziv,Kod,x,y
	// Format: LET,Polazni,Odredišni,Sati,Minuti,Trajanje
	public static void sacuvajUFajl(String putanja, SistemManager manager) throws IOException {
		try (PrintWriter pw = new PrintWriter(new FileWriter(putanja))) {
			pw.println("# Format: AERODROM,naziv,kod,x,y");
			pw.println("# Format: LET,polazniKod,odredisniKod,sati,minuti,trajanje");
			pw.println();
			
			for (Aerodrom a : manager.getAerodromi()) {
				pw.printf("AERODROM,%s,%s,%.2f,%.2f%n", a.getNaziv(), a.getKod(), a.getX(), a.getY());
			}
			
			pw.println();
			
			for (Let l : manager.getLetovi()) {
				pw.printf("LET,%s,%s,%d,%d,%d%n", l.getPolazniAerodrom().getKod(), 
						l.getOdredisniAerodrom().getKod(),
						l.getVremePoletanja().getSati(), l.getVremePoletanja().getMinuti(),
						l.getTrajanjeMinuta());
			}
		}
		
	}
	
}
