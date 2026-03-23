package logic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import model.*;

public class SimulacijaManager {
	private SistemManager manager;
	private List<Avion> aktivniAvioni;
	private Vreme trenutnoVreme;
	private boolean pokrenuta;
	private boolean pauzirana;
	private Timer simulacijaTimer;
	
	private List<AerodromRed> redoviCekanja;
	
	private List<AerodromPoletanje> poslednjaPoletanja;
	
	private static final int UPDATE_INTERVAL = 200;
	
	public SimulacijaManager(SistemManager manager) {
		this.manager = manager;
		this.aktivniAvioni = new ArrayList<>();
		this.trenutnoVreme = new Vreme(0, 0);
		this.pokrenuta = false;
		this.pauzirana = false;
		this.redoviCekanja = new ArrayList<>();
		this.poslednjaPoletanja = new ArrayList<>();
	}
	
	// pokretanje simulacije
	public void pokreni() {
		if (pokrenuta) {
			return;
		}
		
		// ako postoji tajmer simulacije, on se zaustavlja i postavlja na null, kasnije se kreira novi
		if (simulacijaTimer != null) {
	        simulacijaTimer.cancel();
	        simulacijaTimer = null;
	    }
		
		// simulacija se resetuje ako postoji
		resetuj();
		pokrenuta = true;
		pauzirana = false;
		
		inicijalizujRedove();
		
		simulacijaTimer = new Timer(true);
		simulacijaTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				if (!pauzirana) {
					azurirajSimulaciju();
				}
			}
		}, UPDATE_INTERVAL, UPDATE_INTERVAL);
	}
	
	// simulacija se pauzira
	public void pauziraj() {
		pauzirana = true;
	}
	
	// simulacija se nastavlja
	public void nastavi() {
		pauzirana = false;
	}
	
	// simulacija se resetuje
	public void resetuj() {
		if (simulacijaTimer != null) {
			simulacijaTimer.cancel();
			simulacijaTimer = null;
		}
		
		aktivniAvioni.clear();
		trenutnoVreme = new Vreme(0, 0);
		pokrenuta = false;
		pauzirana = false;
		redoviCekanja.clear();
		poslednjaPoletanja.clear();
	}
	
	// inicijalizacija redova za čekanja
	private void inicijalizujRedove() {
		redoviCekanja.clear();
		poslednjaPoletanja.clear();
		
		for (Aerodrom a : manager.getAerodromi()) {
			redoviCekanja.add(new AerodromRed(a));
			poslednjaPoletanja.add(new AerodromPoletanje(a));
		}
		
		for (Let l : manager.getLetovi()) {
			AerodromRed red = nadjiRed(l.getPolazniAerodrom());
			if (red != null) {
				Avion a = new Avion(l, l.getVremePoletanja());
				red.dodajAvion(a);
			}
		}
	}

	// simulacija se ažurira na svakih 200ms
	private void azurirajSimulaciju() {
		synchronized (this) {
			trenutnoVreme = trenutnoVreme.dodajMinute(2);
		}
		obradiPoletanja();
		azurirajAvione();
	}
	
	// obrađuju se poletanja
	private void obradiPoletanja() {
		for (AerodromRed red : redoviCekanja) {
			while (!red.redCekanja.isEmpty()) {
				Avion avion = red.redCekanja.peek();
				
				if (trenutnoVreme.compareTo(avion.getVremePoletanja()) < 0) {
					break;
				}
				
				// pronalazi se poslednje poletanje za dati aerodrom
				AerodromPoletanje poletanje = 
						nadjiPoslednjePoletanje(avion.getLet().getPolazniAerodrom());
				if (poletanje != null && poletanje.poslednje != null) {
					int razlika = trenutnoVreme.ukupnoMinuta() - 
							poletanje.poslednje.ukupnoMinuta();
					
					if (razlika < 0) {
						razlika += 24 * 60;
					}
					
					// ako postoji poletanje u poslednjih 10 minuta, preskače se za sada i poleće kada bude mogao
					if (razlika < 10) {
						avion.setVremePoletanja(poletanje.poslednje.dodajMinute(10));
						break;
					}
				}
				
				// izbacuje se avion iz reda za čekanje
				red.redCekanja.poll();
				aktivniAvioni.add(avion);
				avion.pokreniLet();
				
				// ako trenutno poleće avion, vreme poslednjeg poletanja je upravo
				if (poletanje != null) {
					poletanje.poslednje = trenutnoVreme;
				}
			}
		}
	}

	// ako su avioni završili let, brišu se iz liste aktivnih aviona
	private void azurirajAvione() {
		List<Avion> zaUklanjanje = new ArrayList<>();
		
		for (Avion a : aktivniAvioni) {
			a.azurirajPoziciju(trenutnoVreme);
			
			// ako je avion sleteo, treba ga ukloniti
			if (a.sleteo()) {
				zaUklanjanje.add(a);
			}
		}
		
		aktivniAvioni.removeAll(zaUklanjanje);
	}

	// pronalazi se red vezan za određeni aerodrom
	private AerodromRed nadjiRed(Aerodrom aerodrom) {
		for (AerodromRed red : redoviCekanja) {
			if (red.aerodrom.equals(aerodrom)) {
				return red;
			}
		}
		return null;
	}
	
	// pronalazi se poslednje poletanje
	private AerodromPoletanje nadjiPoslednjePoletanje(Aerodrom aerodrom) {
		for (AerodromPoletanje ap : poslednjaPoletanja) {
            if (ap.aerodrom.equals(aerodrom)) {
                return ap;
            }
        }
        return null;
	}
	
	public synchronized Vreme getTrenutnoVreme() {
        return trenutnoVreme;
    }
    
    public List<Avion> getAktivniAvioni() {
        return new ArrayList<>(aktivniAvioni);
    }
    
    public boolean isPokrenuta() {
        return pokrenuta;
    }
    
    public boolean isPauzirana() {
        return pauzirana;
    }
	
    // par {aerodrom, avioni koji cekaju poletanje}
	private static class AerodromRed {
		Aerodrom aerodrom;
		Queue<Avion> redCekanja;
		
		AerodromRed(Aerodrom aerodrom) {
			this.aerodrom = aerodrom;
			this.redCekanja = new LinkedList<>();
		}
		
		// dodaje se let u red za čekanje
		void dodajAvion(Avion avion) {
			redCekanja.offer(avion);
		}
	}
	
	// pamte se vremena poslednjih poletanja za aerodrome
	private static class AerodromPoletanje {
		Aerodrom aerodrom;
		Vreme poslednje;
		
		AerodromPoletanje(Aerodrom aerodrom) {
			this.aerodrom = aerodrom;
			this.poslednje = null;
		}
	}
}
