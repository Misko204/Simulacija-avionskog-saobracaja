package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;

import logic.InactivityTimer;
import logic.SimulacijaManager;
import logic.SistemManager;
import model.Aerodrom;
import model.Let;
import util.CSVManager;
import util.InvalidFormatException;

public class MainFrame extends Frame {
	
	private SistemManager sisManager;
	private InactivityTimer inactivityTimer;
	private List listaAerodroma;
	private List listaLetova;
	private CardLayout cardLayout;
	private Panel karticaPanel;
	private MapaPanel mapaPanel;
	private FilterPanel filterPanel;
	private SimulacijaManager simManager;
    private SimulacijaKontrolePanel kontrolePanel;
	
	// Glavni prozor
	public MainFrame() {
		super("Simulacija avionskog saobraćaja - Faza A i B");
		
		this.sisManager = new SistemManager();
		this.simManager = new SimulacijaManager(sisManager);
		this.inactivityTimer = new InactivityTimer(this);
		
		setSize(1200, 700);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				inactivityTimer.zaustavi();
				if (mapaPanel != null) {
					mapaPanel.zaustaviAnimaciju();
				}
				if (kontrolePanel != null) {
			        kontrolePanel.zaustaviTimer();
			    }
			    if (simManager != null) {
			        simManager.resetuj();
			    }
				dispose();
				System.exit(0);
			}
			
		});
		
		populateWindow();
	}

	private void populateWindow() {
		kreirajMeni();
		
		Panel toolbar = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		// Prikazuje panel sa listom aerodroma
		Button aerodromiBtn = new Button("Aerodromi");
		aerodromiBtn.addActionListener((ae) -> {
			inactivityTimer.resetTimer();
			cardLayout.show(karticaPanel, "AERODROMI");
		});
		
		// Prikazuje panel sa listom letova
		Button letoviBtn = new Button("Letovi");
		letoviBtn.addActionListener((ae) -> {
			inactivityTimer.resetTimer();
			cardLayout.show(karticaPanel, "LETOVI");
		});
		
		// Prikazuje panel sa mapom
		Button mapaBtn = new Button("Mapa");
		   mapaBtn.addActionListener(e -> {
		       inactivityTimer.resetTimer();
		       cardLayout.show(karticaPanel, "MAPA");
		   });
		
		toolbar.add(aerodromiBtn);
		toolbar.add(letoviBtn);
		toolbar.add(mapaBtn);
		add(toolbar, BorderLayout.NORTH);
		
		cardLayout = new CardLayout();
		karticaPanel = new Panel(cardLayout);
		
		karticaPanel.add(kreirajAerodromPanel(), "AERODROMI");
		karticaPanel.add(kreirajLetPanel(), "LETOVI");
		karticaPanel.add(kreirajMapaPanel(), "MAPA");
		
		add(karticaPanel, BorderLayout.CENTER);
		
		Panel infoPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		Label infoLabel = new Label("Simulacija avionskog saobraćaja   |   "
				+ "Automatsko zatvaranje nakon 60s neaktivnosti.");
		infoPanel.add(infoLabel);
		add(infoPanel, BorderLayout.SOUTH);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				inactivityTimer.resetTimer();
			}
		});
	}
	
	// Kreira se panel sa listom aerodroma
	private Panel kreirajAerodromPanel() {
		Panel panel = new Panel(new BorderLayout(10, 10));
		listaAerodroma = new List(15, false);
		listaAerodroma.setBackground(Color.LIGHT_GRAY);
		listaAerodroma.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				inactivityTimer.resetTimer();
			}
		});
		panel.add(listaAerodroma);
		
		Panel btnPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		Button dodajBtn = new Button("Dodaj aerodrom");
		dodajBtn.addActionListener((ae) -> {
			inactivityTimer.resetTimer();
			dodajAerodrom();
		});
		
		Button obrisiBtn = new Button("Obriši aerodrom");
		obrisiBtn.addActionListener((ae) -> {
			inactivityTimer.resetTimer();
			obrisiAerodrom();
		});
		
		btnPanel.add(dodajBtn);
		btnPanel.add(obrisiBtn);
		panel.add(btnPanel, BorderLayout.SOUTH);
		
		osveziAerodrome();
		
		return panel;
	}
	
	// Kreira se panel sa listom letova
	private Panel kreirajLetPanel() {
		Panel panel = new Panel(new BorderLayout(10, 10));
		
		listaLetova = new List(15, false);
		listaLetova.setBackground(Color.LIGHT_GRAY);
		listaLetova.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				inactivityTimer.resetTimer();
			}
		});
		panel.add(listaLetova);
		
		Panel btnPanel = new Panel(new FlowLayout(FlowLayout.LEFT));
		
		Button dodajBtn = new Button("Dodaj let");
		dodajBtn.addActionListener((ae) -> {
			inactivityTimer.resetTimer();
			dodajLet();
		});
		
		Button obrisiBtn = new Button("Obriši let");
		obrisiBtn.addActionListener((ae) -> {
			inactivityTimer.resetTimer();
			obrisiLet();
		});
		
		btnPanel.add(dodajBtn);
		btnPanel.add(obrisiBtn);
		panel.add(btnPanel, BorderLayout.SOUTH);
		
		osveziLetove();
		
		return panel;
	}
	
	// Kreira se panel sa mapom
	private Panel kreirajMapaPanel() {
	       Panel panel = new Panel(new BorderLayout(10, 10));
	       
	       mapaPanel = new MapaPanel(sisManager, inactivityTimer, simManager);
	       panel.add(mapaPanel, BorderLayout.CENTER);
	       
	       filterPanel = new FilterPanel(sisManager, mapaPanel, inactivityTimer);
	       filterPanel.setPreferredSize(new Dimension(300, 600));
	       panel.add(filterPanel, BorderLayout.EAST);
	       
	       kontrolePanel = new SimulacijaKontrolePanel(simManager, inactivityTimer, mapaPanel);
	       panel.add(kontrolePanel, BorderLayout.SOUTH);
	       
	       return panel;
	   }
	
	// Kreiranje menija
	private void kreirajMeni() {
		MenuBar menuBar = new MenuBar();
		
		Menu fajlMenu = new Menu("Fajl");
		
		MenuItem ucitaj = new MenuItem("Učitaj iz CSV fajla");
		ucitaj.addActionListener((ae) -> {
			inactivityTimer.resetTimer();
            ucitajIzFajla();
		});
		
		MenuItem sacuvaj = new MenuItem("Sačuvaj u CSV fajl");
		sacuvaj.addActionListener((ae) -> {
			inactivityTimer.resetTimer();
            sacuvajUFajl();
		});
		
		MenuItem izlaz = new MenuItem("Izlaz");
		izlaz.addActionListener((ae) -> {
			inactivityTimer.zaustavi();
			if (mapaPanel != null) {
		        mapaPanel.zaustaviAnimaciju();
		    }
			if (kontrolePanel != null) {
		        kontrolePanel.zaustaviTimer();
		    }
		    if (simManager != null) {
		        simManager.resetuj();
		    }
            dispose();
            System.exit(0);
		});
		
		fajlMenu.add(ucitaj);
		fajlMenu.add(sacuvaj);
		fajlMenu.addSeparator();
		fajlMenu.add(izlaz);
		
		menuBar.add(fajlMenu);
		setMenuBar(menuBar);
	}
	
	// Prikazivanje dialoga za kreiranje aerodroma
	private void dodajAerodrom() {
		AerodromDialog dialog = new AerodromDialog(this, sisManager);
		dialog.setVisible(true);
		
		Aerodrom novi = dialog.getAerodrom();
		if (novi != null) {
			try {
				sisManager.dodajAerodrom(novi);
				osveziAerodrome();
				filterPanel.osveziListu();
				mapaPanel.repaint();
				prikaziPoruku("Uspeh", "Aerodrom uspešno dodat: " + novi.getKod());
			} catch (Exception e) {
				prikaziGresku("Greška pri dodavanju aerodroma:\n"
						+ e.getMessage());
			}
		}
	}
	
	// Briše se selektovan aerodrom ako za njega nema letova
	private void obrisiAerodrom() {
		int selectedIndex = listaAerodroma.getSelectedIndex();
		if (selectedIndex == -1) {
			prikaziUpozorenje("Morate prvo selektovati aerodrom za brisanje.");
            return;
		}
		
		Aerodrom aerodrom = sisManager.getAerodromi().get(selectedIndex);
		
		if (prikaziPotvrdu("Da li ste sigurni da želite da obrišete"
				+ " aerodrom " + aerodrom.getKod() + "?")) {
			try {
				sisManager.obrisiAerodrom(aerodrom);
				osveziAerodrome();
				osveziLetove();
				filterPanel.osveziListu();
				mapaPanel.repaint();
				prikaziPoruku("Uspeh", "Aerodrom uspešno obrisan.");
			} catch (Exception e) {
				prikaziGresku("Greška pri brisanju:\n" + e.getMessage());
			}
		}
	}
	
	// Prikazivanje dialoga za dodavanje letova
	private void dodajLet() {
		if (sisManager.getAerodromi().isEmpty()) {
			prikaziUpozorenje("Nema aerodroma u sistemu.\n"
					+ "Prvo dodajte najmanje 2 aerodroma.");
			return;
		}
		
		if (sisManager.getAerodromi().size() < 2) {
			prikaziUpozorenje("Za kreiranje leta potrebna su najmanje 2 aerodroma.\n"
					+ "Trenutno imate samo " + sisManager.getAerodromi().size()
					+ " aerodrom.");
			return;
		}
		
		LetDialog dialog = new LetDialog(this, sisManager);
		dialog.setVisible(true);
		
		Let novi = dialog.getLet();
		if (novi != null) {
			try {
				sisManager.dodajLet(novi);
				osveziLetove();
				prikaziPoruku("Uspeh", "Let uspešno dodat: " + 
				novi.getPolazniAerodrom().getKod() + " -> " + 
						novi.getOdredisniAerodrom().getKod());
			} catch (Exception e) {
				prikaziGresku("Greška pri dodavanju leta:\n" + e.getMessage());
			}
		}
	}
	
	// Briše se selektovan let
	private void obrisiLet() {
		int selectedIndex = listaLetova.getSelectedIndex();
		if (selectedIndex == -1) {
			prikaziUpozorenje("Morate prvo selektovati let za brisanje.");
            return;
		}
		
		Let let = sisManager.getLetovi().get(selectedIndex);
		
		if (prikaziPotvrdu("Da li ste sigurni da želite da obrišete"
				+ " let " + let.getPolazniAerodrom().getKod() + " -> " + 
				let.getOdredisniAerodrom().getKod() + "?")) {
			try {
				sisManager.obrisiLet(let);
				osveziLetove();
				prikaziPoruku("Uspeh", "Let uspešno obrisan.");
			} catch (Exception e) {
				prikaziGresku("Greška pri brisanju:\n" + e.getMessage());
			}
		}
	}
	
	// Prikazuju se aerodromi ponovo
	private void osveziAerodrome() {
		listaAerodroma.removeAll();
		for (Aerodrom a : sisManager.getAerodromi()) {
			listaAerodroma.add(a.toString());
		}
	}
	
	// Prikazuju se letovi ponovo
	private void osveziLetove() {
		listaLetova.removeAll();
		for (Let l : sisManager.getLetovi()) {
			listaLetova.add(l.toString());
		}
	}
	
	// Prikazuje se dialog za učitavanje iz fajla
	private void ucitajIzFajla() {
		FileDialog fd = new FileDialog(this, "Učitaj iz CSV fajla", FileDialog.LOAD);
		fd.setFile("*.csv");
		fd.setVisible(true);
		
		String direktorijum = fd.getDirectory();
		String fajl = fd.getFile();
		
		if (direktorijum != null && fajl != null) {
			String putanja = direktorijum + fajl;
			
			try {
				CSVManager.ucitajIzFajla(putanja, sisManager);
				osveziAerodrome();
				osveziLetove();
				filterPanel.osveziListu();
				mapaPanel.repaint();
				
				prikaziPoruku("Uspeh", "Podaci uspešno učitani!\n" + "Aerodromi: " 
						+ sisManager.getAerodromi().size() + "\n" +
	                    "Letovi: " + sisManager.getLetovi().size());
				
			} catch (FileNotFoundException e) {
				prikaziGresku("Fajl nije pronađen:\n" + putanja + "\n\n" 
						+"Proverite da li fajl postoji i pokušajte ponovo.");
			} catch (InvalidFormatException e) {
				prikaziGresku("Format fajla je neispravan:\n\n" + e.getMessage() + 
						"\n\n" + "Očekivani format:\n" + "AERODROM,naziv,kod,x,y\n"
						+ "LET,polazniKod,odredisniKod,sati,minuti,trajanje");
			} catch (IOException e) {
				prikaziGresku("Greška pri čitanju fajla:\n" + e.getMessage() + 
						"\n\n" + "Fajl možda koristi druga aplikacija "
								+ "ili nemate dozvolu za čitanje.");
			} catch (Exception e) {
				prikaziGresku("Neočekivana greška:\n" + e.getMessage());
			}
		}
	}
	
	// Prikazuje se dialog za čuvanje u fajlu
	private void sacuvajUFajl() {
		if (sisManager.getAerodromi().isEmpty() && 
				sisManager.getLetovi().isEmpty()) {
            prikaziUpozorenje("Nema podataka za čuvanje.\n"
            		+ "Prvo dodajte aerodrome ili letove.");
            return;
        }
		
		FileDialog fd = new FileDialog(this, "Sačuvaj u CSV fajl", FileDialog.SAVE);
        fd.setFile("podaci.csv");
        fd.setVisible(true);
        
        String direktorijum = fd.getDirectory();
        String fajl = fd.getFile();
        
        if (direktorijum != null && fajl != null) {
            String putanja = direktorijum + fajl;
            
            if (!putanja.toLowerCase().endsWith(".csv")) {
                putanja += ".csv";
            }
            
            try {
                CSVManager.sacuvajUFajl(putanja, sisManager);
                
                prikaziPoruku("Uspeh", "Podaci uspešno sačuvani u:\n" + 
                		putanja + "\n\n" + "Aerodromi: " + 
                		sisManager.getAerodromi().size() + "\n" + "Letovi: " + 
                		sisManager.getLetovi().size());
                    
            } catch (IOException e) {
                prikaziGresku("Greška pri čuvanju fajla:\n" + e.getMessage() + 
                		"\n\n" + "Proverite da li imate dozvolu za pisanje "
                				+ "u izabranu lokaciju.");
                    
            } catch (Exception e) {
                prikaziGresku("Neočekivana greška:\n" + e.getMessage());
            }
        }
	}
	
	// Prikazuje se poruka
	private void prikaziPoruku(String naslov, String poruka) {
		Dialog dialog = new Dialog(this, naslov, true);
		dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);
        
        TextArea txtPoruka = new TextArea(poruka, 5, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        txtPoruka.setEditable(false);
        dialog.add(txtPoruka, BorderLayout.CENTER);
        
        Button btnOK = new Button("OK");
        btnOK.addActionListener((ae) -> {
        	dialog.dispose();
        });
        Panel panelBtn = new Panel();
        panelBtn.add(btnOK);
        dialog.add(panelBtn, BorderLayout.SOUTH);
        
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.dispose();
            }
        });
        
        dialog.setVisible(true);
	}
	
	// Prikazuje se dialog sa greškom
	private void prikaziGresku(String poruka) {
		prikaziPoruku("Greška", poruka);
	}
	
	// Prikazuje se dialog sa upozorenjem
	private void prikaziUpozorenje(String poruka) {
		prikaziPoruku("Upozorenje", poruka);
	}
	
	// Prikazuje se dialog za potvrdu
	private boolean prikaziPotvrdu(String poruka) {
		final boolean[] rezultat = {false};
		
		Dialog dialog = new Dialog(this, "Potvrda", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);
        
        TextArea txtPoruka = new TextArea(poruka, 4, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);
        txtPoruka.setEditable(false);
        dialog.add(txtPoruka, BorderLayout.CENTER);
        
        Panel btnPanel = new Panel(new FlowLayout());
        
        Button daBtn = new Button("Da");
        daBtn.addActionListener(e -> {
            rezultat[0] = true;
            dialog.dispose();
        });
        
        Button neBtn = new Button("Ne");
        neBtn.addActionListener(e -> {
            rezultat[0] = false;
            dialog.dispose();
        });
        
        btnPanel.add(daBtn);
        btnPanel.add(neBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		rezultat[0] = false;
        		dialog.dispose();
        	}
        });
        
        dialog.setVisible(true);
        return rezultat[0];
	}
	
	public SistemManager getSistemManager() {
		return sisManager;
	}
	
}
