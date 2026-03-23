package gui;

import java.awt.*;
import java.awt.event.*;

import logic.SistemManager;
import model.Aerodrom;
import model.Let;
import model.Vreme;
// Prozor za dodavanje novog leta
public class LetDialog extends Dialog {
	private SistemManager manager;
	private Choice chPolazni;
	private Choice chOdredisni;
	private TextField txtSati;
	private TextField txtMinuti;
	private TextField txtTrajanje;
	private Let rezultat;
	
	public LetDialog(Frame parent, SistemManager manager) {
		super(parent, "Dodaj let", true);
		this.manager = manager;
		this.rezultat = null;
		
		setLayout(new BorderLayout(10, 10));
		setSize(450, 250);
		setLocationRelativeTo(parent);
		setBackground(new Color(255, 156, 64));
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		
		setFocusable(true);
		
		populateWindow();
	}

	private void populateWindow() {
		Panel formaPanel = new Panel(new GridLayout(5, 2, 10, 10));
		
		formaPanel.add(new Label("Polazni aerodrom:"));
		chPolazni = new Choice();
		for (Aerodrom a : manager.getAerodromi()) {
			chPolazni.add(a.toString());
		}
		chPolazni.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(chPolazni);
		
		formaPanel.add(new Label("Odredišni aerodrom:"));
		chOdredisni = new Choice();
		for (Aerodrom a : manager.getAerodromi()) {
			chOdredisni.add(a.toString());
		}
		chOdredisni.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(chOdredisni);
		
		formaPanel.add(new Label("Sati poletanja (0 - 23):"));
		txtSati = new TextField(5);
		txtSati.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(txtSati);
		
		formaPanel.add(new Label("Minuti poletanja (0 - 59):"));
		txtMinuti = new TextField(5);
		txtMinuti.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(txtMinuti);
		
		formaPanel.add(new Label("Trajanje leta (minuti):"));
		txtTrajanje = new TextField(10);
		txtTrajanje.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(txtTrajanje);
		
		add(formaPanel, BorderLayout.CENTER);
		
		Panel btnPanel = new Panel(new FlowLayout(FlowLayout.RIGHT));
		
		Button potvrdiBtn = new Button("Potvrdi");
		potvrdiBtn.addActionListener((ae) -> {
			potvrdi();
		});
		
		Button otkaziBtn = new Button("Otkaži");
		otkaziBtn.addActionListener((ae) -> {
			dispose();
		});
		
		btnPanel.add(potvrdiBtn);
		btnPanel.add(otkaziBtn);
		add(btnPanel, BorderLayout.SOUTH);
	}
	
	private void potvrdi() {
		try {
			// Ako ne postoji ni jedan dodat aerodrom, ne može se dodati ni let
			if (manager.getAerodromi().isEmpty()) {
				throw new IllegalArgumentException("Nema aerodroma u sistemu.\n"
						+ "Prvo dodajte aerodrome.");
			}
			
			int indexPolazni = chPolazni.getSelectedIndex();
			int indexOdredisni = chOdredisni.getSelectedIndex();
			
			if (indexPolazni == -1 || indexOdredisni == -1) {
				throw new IllegalArgumentException("Morate izabrati aerodrome.");
			}
			
			Aerodrom polazni = manager.getAerodromi().get(indexPolazni);
			Aerodrom odredisni = manager.getAerodromi().get(indexOdredisni);
			
			int sati = Integer.parseInt(txtSati.getText().trim());
			int minuti = Integer.parseInt(txtMinuti.getText().trim());
			int trajanje = Integer.parseInt(txtTrajanje.getText().trim());
			
			Vreme vreme = new Vreme(sati, minuti);
			rezultat = new Let(polazni, odredisni, vreme, trajanje);
			dispose();
			
		} catch (NumberFormatException e) {
			prikaziGresku("Vreme i trajanje moraju biti celi brojevi.");
		} catch (IllegalArgumentException e) {
			prikaziGresku(e.getMessage());
		}
	}
	
	// Prikazuje se greška nastala prilikom kreirannja novog leta
	private void prikaziGresku(String poruka) {
		Dialog dialog = new Dialog(this, "Greška", true);
		dialog.setLayout(new BorderLayout(10, 10));
		dialog.setSize(350, 120);
		dialog.setLocationRelativeTo(this);
		
		TextArea txtPoruka = new TextArea(poruka, 3, 30, TextArea.SCROLLBARS_VERTICAL_ONLY);
		txtPoruka.setEditable(false);
		dialog.add(txtPoruka, BorderLayout.CENTER);
		
		Button okBtn = new Button("OK");
		okBtn.addActionListener((ae) -> {
			dialog.dispose();
		});
		
		Panel btnPanel = new Panel();
		btnPanel.add(okBtn);
		dialog.add(btnPanel, BorderLayout.SOUTH);
		
		dialog.setVisible(true);
	}
	
	public Let getLet() {
		return rezultat;
	}
}
