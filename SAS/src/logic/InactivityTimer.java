package logic;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import gui.InactivityDialog;

public class InactivityTimer {
	private static final int TIMEOUT_SECONDS = 60;		// timeout tajmer
	private static final int WARNING_THRESHOLD = 5;		// na koliko sekundi do kraja rada se prikazuje upozorenje
	
	private Timer timer;
	private long poslednjuAktivnost;
	private boolean pauziran;
	private Frame mainFrame;
	
	// Pokreće se tajmer pri pokretanju aplikacije
	public InactivityTimer(Frame mainFrame) {
		this.mainFrame = mainFrame;
		this.poslednjuAktivnost = System.currentTimeMillis();
		this.pauziran = false;
		
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!pauziran) {
					proveriNeaktivnost();
				}
				
			}
		}, 1000, 1000);
	}
	
	// Resetuje se tajmer, trenutno vreme se uzima za poslednju aktivnost
	public void resetTimer() {
		poslednjuAktivnost = System.currentTimeMillis();
	}
	
	// pauzira se tajmer
	public void pauziraj () {
		pauziran = true;
	}
	
	// pauziran tajmer se resetuje i opet pokreće
	public void nastavi() {
		pauziran = false;
		resetTimer();
	}
	
	// zaustavlja se tajmer
	public void zaustavi() {
		if (timer != null) {
			timer.cancel();
		}
	}
	
	// proverava se koliko je dugo aplikacija neaktivna
	private void proveriNeaktivnost() {
		long protekloVreme = (System.currentTimeMillis() - poslednjuAktivnost) / 1000;
		
		if (protekloVreme >= TIMEOUT_SECONDS) {
			zaustavi();
			mainFrame.dispose();
			System.exit(0);
		} else if (protekloVreme >= (TIMEOUT_SECONDS - WARNING_THRESHOLD)) {
				int preostalo = (int) (TIMEOUT_SECONDS - protekloVreme);
				prikaziUpozorenje(preostalo);
		}
	}
	
	// prikazuje se upozorenje da će se aplikacija zatvoriti uskoro
	private void prikaziUpozorenje(int preostaloSekundi) {
		pauziraj();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				InactivityDialog dialog = new InactivityDialog(mainFrame, preostaloSekundi);
				dialog.setVisible(true);
				
				if (dialog.zelimNastaviti()) {
					resetTimer();
					nastavi();
				} 
			}
		});
	}

}
