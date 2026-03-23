package gui;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import logic.InactivityTimer;
import logic.SimulacijaManager;
// Panel sa kontrolama za simulaciju
public class SimulacijaKontrolePanel extends Panel {
	private SimulacijaManager simManager;
    private InactivityTimer inactivityTimer;
    private Button pokreniBtn;
    private Button pauzirajBtn;
    private Button resetujBtn;
    private Label vreme;
    private Timer vremeTimer;
    private MapaPanel mapaPanel;
    
    public SimulacijaKontrolePanel(SimulacijaManager manager, 
    		InactivityTimer timer, MapaPanel mapaPanel) {
    	this.simManager = manager;
    	this.inactivityTimer = timer;
    	this.mapaPanel = mapaPanel;
    	
    	setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
    	
    	populateWindow();
    }

    // dodaju se dugmići za pokretanje, pauziranje i resetovanje simulacije
	private void populateWindow() {
		pokreniBtn = new Button("Pokreni simulaciju");
		pokreniBtn.addActionListener((ae) -> {
            inactivityTimer.resetTimer();
            pokreniSimulaciju();
        });
		
		// dugme za pauziranje je u početku onemogućeno
		pauzirajBtn = new Button("Pauziraj simulaciju");
		pauzirajBtn.setEnabled(false);
		pauzirajBtn.addActionListener((ae) -> {
            inactivityTimer.resetTimer();
            pauzirajSimulaciju();
        });
		
		resetujBtn = new Button("Resetuj simulaciju");
		resetujBtn.addActionListener(e -> {
            inactivityTimer.resetTimer();
            resetujSimulaciju();
        });
		
		vreme = new Label("Vreme: 00:00");
		vreme.setFont(new Font("Arial", Font.BOLD, 14));
		
		add(pokreniBtn);
		add(pauzirajBtn);
		add(resetujBtn);
		add(new Label("  |  "));
		add(vreme);
		
		vremeTimer = new Timer(true);
		vremeTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				EventQueue.invokeLater(() -> azurirajPrikaz());
			}
		}, 0, 200);
	}
	
	// pokreće se simulacija
	private void pokreniSimulaciju() {
		if (simManager.isPokrenuta()) {
			return;
		}
		
		// kada se pokrene simulacija, omogućuje se dugme za pauziranje simulacije
		simManager.pokreni();
        pauzirajBtn.setEnabled(true);
        pokreniBtn.setEnabled(false);
		inactivityTimer.pauziraj();
	}
	
	// pauzira se simulacija
	private void pauzirajSimulaciju() {
		// ako je simulacija pauzirana, odpauzira se i dugme postaje dugme za pauziranje
		if (simManager.isPauzirana()) {
            simManager.nastavi();
            pauzirajBtn.setLabel("Pauziraj simulaciju");
        } 
		// ako nije pauzirana, pauzira se i dugme postaje dugme za nastavak 
		else {
            simManager.pauziraj();
            pauzirajBtn.setLabel("Nastavi simulaciju");
        }
	}
	
	// simulacija se resetuje
	private void resetujSimulaciju() {
        simManager.resetuj();
        // omogućava se dugme za pokretanje
        pokreniBtn.setEnabled(true);
        pauzirajBtn.setLabel("Pauziraj simulaciju");
        // onemogućava se dugme za pauziranje
        pauzirajBtn.setEnabled(false);
        vreme.setText("Vreme: 00:00");
        inactivityTimer.nastavi();
        
        if (mapaPanel != null) {
            mapaPanel.repaint();
        }
    }
	
	// ažurira se prikaz, teče vreme
	public void azurirajPrikaz() {
        if (simManager.isPokrenuta()) {
            vreme.setText("Vreme: " + simManager.getTrenutnoVreme().toString());
        }
    }
	
	// tajmer simulacije se zaustavlja
	public void zaustaviTimer() {
        if (vremeTimer != null) {
            vremeTimer.cancel();
        }
    }
	
}
