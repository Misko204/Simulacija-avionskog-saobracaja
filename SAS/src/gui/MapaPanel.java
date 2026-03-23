package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import logic.InactivityTimer;
import logic.SimulacijaManager;
import logic.SistemManager;
import model.Aerodrom;
import model.Avion;
import util.GeometrijaUtil;
// "Platno" na kome se prikazuje mapa
public class MapaPanel extends Canvas {
	private SistemManager sisManager;
	private InactivityTimer timer;
	private Aerodrom selektovaniAerodrom;
	private boolean crvenaBoja;
	private Timer animacijaTimer;
	private SimulacijaManager simManager;
	
	private static final int AERODROM_SIZE = 20;
	private static final Color AERODROM_COLOR = Color.GRAY;
	private static final Color SELECTED_COLOR = Color.RED;
	private static final Color BACKGROUND_COLOR = new Color(240, 248, 255);
	private static final Color GRID_COLOR = new Color(200, 200, 200);
	private static final int AVION_SIZE = 12;
    private static final Color AVION_COLOR = Color.BLUE;
	
	public MapaPanel(SistemManager sisManager, InactivityTimer timer, SimulacijaManager simManager) {
		this.sisManager = sisManager;
		this.timer = timer;
		this.selektovaniAerodrom = null;
		this.crvenaBoja = false;
		this.simManager = simManager;
		
		setBackground(BACKGROUND_COLOR);
		
		// Kada se klikne mišem na ekran, klik se obrađuje
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				timer.resetTimer();
				obradiKlik(e.getX(), e.getY());
			}
		});
		
		animacijaTimer = new Timer(true);
		
		// Animacija da aerodrom treperi sivo-crveno
		animacijaTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (selektovaniAerodrom != null) {
					crvenaBoja = !crvenaBoja;
					EventQueue.invokeLater(() -> repaint());
				}
			}
		}, 500, 500);
		
		Timer simulacijaRefresh = new Timer(true);
	    simulacijaRefresh.scheduleAtFixedRate(new TimerTask() {
	        @Override
	        public void run() {
	            if (simManager != null && 
	                simManager.isPokrenuta() && 
	                !simManager.isPauzirana()) {
	                EventQueue.invokeLater(() -> repaint());
	            }
	        }
	    }, 200, 200);
	}
	
	// U zavisnosti od toga sta je potrebno nacrtati, poziva se odgovarajuća metoda
	@Override
	public void paint(Graphics g) {
		Dimension size = getSize();
		
		g.setColor(BACKGROUND_COLOR);
		g.fillRect(0, 0, size.width, size.height);
		
		nacrtajGrid(g, size.width, size.height);
		nacrtajOse(g, size.width, size.height);
		
		for (Aerodrom aerodrom : sisManager.getAerodromi()) {
			if (aerodrom.isPrikazan()) {
                nacrtajAerodrom(g, aerodrom, size.width, size.height);
            }
		}
		
		if (simManager != null && simManager.isPokrenuta()) {
            for (Avion avion : simManager.getAktivniAvioni()) {
                if (avion.prikazan()) {
                    nacrtajAvion(g, avion, size.width, size.height);
                }
            }
        }
	}
	
	@Override
	public void update(Graphics g) {
	    Image offscreen = createImage(getSize().width, getSize().height);
	    if (offscreen != null) {
	        Graphics offG = offscreen.getGraphics();
	        paint(offG);
	        g.drawImage(offscreen, 0, 0, this);
	    } else {
	        // Ako double buffering ne radi, crtaj direktno
	        paint(g);  // Imaćeš flickering, ali bar radi
	    }
	}
	
	// Metoda koja iscrtava aerodrom na mapi
	private void nacrtajAerodrom(Graphics g, Aerodrom aerodrom, int width, int height) {
		Point screenPos = GeometrijaUtil.modelToScreen(aerodrom.getX(), 
				aerodrom.getY(), width, height);
		
		// Ako je neki aerodrom selektovan, gleda se da li u tom trenutku treba da bude crven ili siv
		Color boja;
        if (aerodrom.equals(selektovaniAerodrom)) {
            boja = crvenaBoja ? SELECTED_COLOR : AERODROM_COLOR;
        } else {
            boja = AERODROM_COLOR;
        }
        
        // Iscrtava aerodrom kao obojeni pravougaonik odgovarajuće boje
        g.setColor(boja);
        g.fillRect(screenPos.x - AERODROM_SIZE / 2, 
        		screenPos.y - AERODROM_SIZE / 2, AERODROM_SIZE, AERODROM_SIZE);
        
        // Crni okvir oko pravougaonika sive/crvene boje
        g.setColor(Color.BLACK);
        g.drawRect(screenPos.x - AERODROM_SIZE / 2, 
        		screenPos.y - AERODROM_SIZE / 2, AERODROM_SIZE, AERODROM_SIZE);
        
        // Ispisuje kod aerodroma nadomak istog
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String kod = aerodrom.getKod();
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(kod);
        g.drawString(kod, screenPos.x - textWidth / 2, 
        		screenPos.y - AERODROM_SIZE / 2 - 5);
	}
	
	// Metoda koja iscrtava avion na mapi
	private void nacrtajAvion(Graphics g, Avion avion, int width, int height) {
        Point screenPos = GeometrijaUtil.modelToScreen(avion.getTrenutnaX(), 
        		avion.getTrenutnaY(), width, height);
        
        // Iscrtava se plavi krug sa crnim okvirom
        g.setColor(AVION_COLOR);
        g.fillOval(screenPos.x - AVION_SIZE / 2, screenPos.y - AVION_SIZE / 2, 
            AVION_SIZE, AVION_SIZE);
        
        g.setColor(Color.BLACK);
        g.drawOval(screenPos.x - AVION_SIZE / 2, screenPos.y - AVION_SIZE / 2, 
            AVION_SIZE, AVION_SIZE);
    }
	
	// Metoda koja iscrtava oznake na osama
	private void nacrtajOse(Graphics g, int width, int height) {
		g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        
        int margin = 40;
        FontMetrics fm = g.getFontMetrics();
        
        String[] xLabels = {"-90", "-45", "0", "45", "90"};
        for (int i = 0; i < xLabels.length; i++) {
            int x = margin + ((width - 2 * margin) * i) / 4;
            String label = xLabels[i];
            int labelWidth = fm.stringWidth(label);
            g.drawString(label, x - labelWidth / 2, height - margin + 15);
        }
        
        String[] yLabels = {"90", "45", "0", "-45", "-90"};
        for (int i = 0; i < yLabels.length; i++) {
            int y = margin + ((height - 2 * margin) * i) / 4;
            g.drawString(yLabels[i], margin - 25, y + 5);
        }
        
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.drawString("X", width / 2 - 5, height - 10);
        g.drawString("Y", 10, height / 2);
		
	}

	// Metoda koja iscrtava mrežu na mapi
	private void nacrtajGrid(Graphics g, int width, int height) {
		g.setColor(GRID_COLOR);
        
        int margin = 40;
        int gridWidth = width - 2 * margin;
        int gridHeight = height - 2 * margin;
        
        for (int i = 0; i <= 4; i++) {
            int x = margin + (gridWidth * i) / 4;
            g.drawLine(x, margin, x, height - margin);
        }
        
        for (int i = 0; i <= 4; i++) {
            int y = margin + (gridHeight * i) / 4;
            g.drawLine(margin, y, width - margin, y);
        }
		
	}

	// Metoda koja obrađuje klik mišem
	protected void obradiKlik(int x, int y) {
		Dimension size = getSize();
		
		// Preskaču se svi neprikazani aerodromi
		for (Aerodrom aerodrom : sisManager.getAerodromi()) {
            if (!aerodrom.isPrikazan()) {
                continue;
            }
            
            // Koordinate aerodroma se prevode u koordinate ekrana
            Point screenPos = GeometrijaUtil.modelToScreen(aerodrom.getX(), 
            		aerodrom.getY(), size.width, size.height);
            
            // Proverava se da li je klik bio na aerodrom koji je dohvaćen u petlji trenutno
            if (GeometrijaUtil.tackaUKvadratu(x, y, screenPos.x, screenPos.y, 
            		AERODROM_SIZE)) {
            	// Ako je klik bio na već selektovani aerodrom, selekcija se poništava
                if (aerodrom.equals(selektovaniAerodrom)) {
                    selektovaniAerodrom = null;
                    timer.nastavi();
                } 
                // Ako je klik bio na aerodrom koji nije selektovan trenutno, taj aerodrom postaje selektovan
                else {
                    selektovaniAerodrom = aerodrom;
                    crvenaBoja = true;
                    timer.pauziraj();
                }
                
                // Poziva se metoda repaint() kako bi se ponovo prikazala situacija na mapi
                repaint();
                return;
            }
        }
		
		if (selektovaniAerodrom != null) {
            selektovaniAerodrom = null;
            timer.nastavi();
            repaint();
        }
	}
	
	public void zaustaviAnimaciju() {
        if (animacijaTimer != null) {
            animacijaTimer.cancel();
        }
    }

}
