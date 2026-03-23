package gui;

import java.awt.*;
import java.awt.event.*;

import logic.SistemManager;
import model.Aerodrom;
// Prozor za kreiranje aerodroma
public class AerodromDialog extends Dialog {
	private TextField txtNaziv;
	private TextField txtKod;
	private TextField txtX;
	private TextField txtY;
	private Aerodrom rezultat;
	private SistemManager manager;
	
	public AerodromDialog(Frame parent, SistemManager manager) {
		super(parent, "Dodaj aerodrom", true);
		this.manager = manager;
		this.rezultat = null;
		
		setLayout(new BorderLayout(10, 10));
		setSize(400, 200);
		setLocationRelativeTo(parent);
		setBackground(new Color(173, 216, 230));
		
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
		Panel formaPanel = new Panel(new GridLayout(4, 2, 10, 10));
		
		formaPanel.add(new Label("Naziv:"));
		txtNaziv = new TextField(20);
		txtNaziv.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(txtNaziv);
		
		formaPanel.add(new Label("Kod (3 velika slova):"));
		txtKod = new TextField(3);
		txtKod.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(txtKod);
		
		formaPanel.add(new Label("X koordinata [-90, 90]"));
		txtX = new TextField(10);
		txtX.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(txtX);
		
		formaPanel.add(new Label("Y koordinata [-90, 90]"));
		txtY = new TextField(10);
		txtY.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					potvrdi();
				}
			}
		});
		formaPanel.add(txtY);
		
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
			String naziv = txtNaziv.getText().trim();
			String kod = txtKod.getText().trim();
			double x = Double.parseDouble(txtX.getText().trim());
			double y = Double.parseDouble(txtY.getText().trim());
			
			// Da li aerodrom već postoji
			if (manager.postojiAerodrom(kod)) {
				throw new IllegalArgumentException("Aerodrom sa kodom " + kod + " već postoji u sistemu");
			}
			
			if (manager.postojeKoordinate(x, y)) {
	            throw new IllegalArgumentException(
	                "Aerodrom na koordinatama (" + x + ", " + y + ") već postoji.\n" +
	                "Izaberite druge koordinate."
	            );
	        }
			
			this.rezultat = new Aerodrom(naziv, kod, x, y);
			dispose();
			
		} catch (NumberFormatException e) {
			prikaziGresku("Koordinate moraju biti brojevi");
		} catch (IllegalArgumentException e) {
			prikaziGresku(e.getMessage());
		}
	}
	
	// Prikazuje se greška nastala prilikom dodavanja novog aerodroma
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
		Panel buttonPanel = new Panel();
		buttonPanel.add(okBtn);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		
		dialog.setVisible(true);
	}
	
	public Aerodrom getAerodrom() {
		return rezultat;
	}
	
}
